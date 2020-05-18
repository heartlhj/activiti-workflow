package com.gogoing.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.util.StringUtil;
import com.gogoing.workflow.bpmn.builder.BpmnBuilder;
import com.gogoing.workflow.bpmn.builder.ImageBpmnModelUtils;
import com.gogoing.workflow.bpmn.builder.ImageGenerator;
import com.gogoing.workflow.constant.ProcessConstants;
import com.gogoing.workflow.domain.ProcessCreateDefineParam;
import com.gogoing.workflow.domain.ProcessCreateDefineResult;
import com.gogoing.workflow.domain.ProcessDefineResult;
import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.service.ProcessDefineService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.ModelEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义管理Service组件
 * @author lhj
 */
@Service
@Slf4j
public class ProcessDefineServiceImpl implements ProcessDefineService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * bpmn与json的转换器
     */
    private BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();


    /**
     * 查询全部的流程定义信息
     *
     * @return
     */
    @Override
    public List<ProcessDefineResult> listProcessDefine() {
        // 查询全部定义的流程
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()
                .list();

        // 返回对象转换
        List<ProcessDefineResult> result = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(list)) {
            result = list.stream().map(this::convertProcessDefResult).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 根据流程定义key查询流程定义信息
     *
     * @param processDefKey 流程定义key
     * @return 流程定义信息
     */
    @Override
    public ProcessDefineResult getProcessDefByKey(String processDefKey) {
        if (StringUtil.isNotEmpty(processDefKey)) {
            // 根据processKey查询流程定义
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processDefKey)
                    .singleResult();
            if (processDefinition != null) {
                return convertProcessDefResult(processDefinition);
            }
        }
        return null;
    }

    /**
     * 新增流程定义
     * @param createProcessDefineParam
     * @return
     */
    @Override
    public ProcessCreateDefineResult createProcessDefine(ProcessCreateDefineParam createProcessDefineParam){
        BpmnBuilder builder = new BpmnBuilder(createProcessDefineParam);
        BpmnModel bpmnModel = builder.build();
        // 将bpmnmodel转成json
        ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addBpmnModel(createProcessDefineParam.getProcessKey()+".bpmn",bpmnModel);
        deployment.key(createProcessDefineParam.getProcessKey());
        deployment.name(bpmnModel.getMainProcess().getName());
        deployment.tenantId(createProcessDefineParam.getTenantId());
        Deployment deploy = deployment.deploy();
        log.info("流程已部署，部署信息：{}", ToStringBuilder.reflectionToString(deploy, ToStringStyle.JSON_STYLE));

        // 构造model的MateInfo数据
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ProcessConstants.MODEL_NAME, createProcessDefineParam.getProcessName());
        modelObjectNode.put(ProcessConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ProcessConstants.MODEL_DESCRIPTION, createProcessDefineParam.getDescription());
        Model model = new ModelEntityImpl();
        model.setMetaInfo(modelObjectNode.toString());
        model.setName(createProcessDefineParam.getProcessName());
        model.setKey(createProcessDefineParam.getProcessKey());
        model.setTenantId(createProcessDefineParam.getTenantId());
        model.setDeploymentId(deploy.getId());
        // 新增模型数据并增加模型的xml和图片
        try {
            this.updateModelAndSource(model, bpmnModel, modelNode);
        } catch (IOException e) {
            throw new ProcessException("模型数据保存异常");
        }

        ProcessCreateDefineResult result = new ProcessCreateDefineResult();
        result.setDeployId(deploy.getId());
        result.setProcessKey(deploy.getKey());
        result.setCode(true);
        return result;
    }

    /**
     * 流程定义类转换类流程结果返回类
     * @param processDefinition 流程定义类对象
     * @return 流程返回类对象
     */
    private ProcessDefineResult convertProcessDefResult(ProcessDefinition processDefinition) {
        ProcessDefineResult processDef = new ProcessDefineResult();
        processDef.setProcessDefId(processDefinition.getId());
        processDef.setProcessDefName(processDefinition.getName());
        processDef.setProcessDefKey(processDefinition.getKey());
        processDef.setDgrmResourceName(processDefinition.getDiagramResourceName());
        processDef.setResourceName(processDefinition.getResourceName());
        processDef.setDeploymentId(processDefinition.getDeploymentId());
        processDef.setSuspensionState(processDefinition.isSuspended());
        processDef.setCategory(processDefinition.getCategory());
        return processDef;
    }

    /**
     * 更新model并更新model对应的资源文件(json和png)
     *
     * @param model 模型对象
     * @param bpmnModel 模型的bpmnModel对象
     * @param jsonNode 模型的json数据
     * @throws IOException
     */

    public void updateModelAndSource(Model model, BpmnModel bpmnModel, JsonNode jsonNode) throws IOException {
        repositoryService.saveModel(model);
        byte[] result = null;
        // 保存流程模型的资源(即json数据保存到act_ge_bytearray表)
        this.repositoryService.addModelEditorSource(model.getId(), jsonNode.toString().getBytes(ProcessConstants.COMMON_CHARACTER_ENCODING_UTF_8));
        // 将图片的大小进行缩小
        double scaleFactor = 1.0;
        GraphicInfo diagramInfo = ImageBpmnModelUtils.calculateDiagramSize(bpmnModel);
        if (diagramInfo.getWidth() > 300f) {
            scaleFactor = diagramInfo.getWidth() / 300f;
            ImageBpmnModelUtils.scaleDiagram(bpmnModel, scaleFactor);
        }
        // 按比例生成图片资源
        BufferedImage modelImage = ImageGenerator.createImage(bpmnModel, scaleFactor);
        if (modelImage != null) {
            result = ImageGenerator.createByteArrayForImage(modelImage, "png");
        }
        // 保存图片资源到act_ge_bytearray表
        if (result != null && result.length > 0) {
            this.repositoryService.addModelEditorSourceExtra(model.getId(), result);
        }
    }

}
