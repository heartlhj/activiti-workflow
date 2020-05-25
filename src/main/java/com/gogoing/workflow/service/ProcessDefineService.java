package com.gogoing.workflow.service;

import com.gogoing.workflow.domain.ProcessCreateDefineParam;
import com.gogoing.workflow.domain.ProcessCreateDefineResult;
import com.gogoing.workflow.domain.ProcessDefineResult;
import org.activiti.bpmn.model.BpmnModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 流程定义管理Service组件
 * @author lhj
 */
public interface ProcessDefineService {


    /**
     * 查询全部的流程定义信息
     * @return
     */
    List<ProcessDefineResult> listProcessDefine();

    /**
     * 根据流程定义key查询流程定义信息
     * @param processDefKey 流程定义key
     * @return 流程定义信息
     */
    ProcessDefineResult getProcessDefByKey(String processDefKey);



    /**
     * 新增流程定义
     * @param createProcessDefineParam
     * @return
     */
    ProcessCreateDefineResult createProcessDefine(ProcessCreateDefineParam createProcessDefineParam);


    /**
     * 根据bpmn文件导入增加模型
     *
     * @param file 上传的文件
     * @return 增加的模型信息
     */
    Boolean importModel(MultipartFile file);



    /**
     * 获取模型数据
     * @param processDefinitionId
     */
    BpmnModel export(String processDefinitionId);


}
