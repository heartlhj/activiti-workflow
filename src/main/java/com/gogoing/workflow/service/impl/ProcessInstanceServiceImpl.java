package com.gogoing.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.util.StringUtil;
import com.gogoing.workflow.cmd.CustomTaskCompleteCmd;
import com.gogoing.workflow.domain.*;
import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.service.ProcessInstanceService;
import com.gogoing.workflow.utils.BpmnUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 流程实例管理Service组件
 * @author yangxi
 */
@Service
@Slf4j
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;

    @Resource
    private HistoryService historyService;

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private ManagementService managementService;

    /**
     * 启动流程实例（通过流程定义key来启动）
     * @param startProcessParam 流程启动入参
     * @return 流程启动结果
     */
    @Override
    public ProcessStartResult startProcInstanceByKey(ProcessStartParam startProcessParam){
        if (StringUtils.isBlank(startProcessParam.getProcessDefineKey())) {
            throw new ProcessException("流程启动失败, 流程定义Key不能为空");
        }
        ProcessInstance oldProcessInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(startProcessParam.getProcessDefineKey())
                .processInstanceBusinessKey(startProcessParam.getBusinessKey())
                .singleResult();

        if (oldProcessInstance != null) {
            throw new ProcessException("流程启动失败, 业务Key[" + startProcessParam.getBusinessKey() + "]已经存在");
        }

        //设置流程发起人
        identityService.setAuthenticatedUserId(startProcessParam.getUserId());

        // 启动流程
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(startProcessParam.getProcessDefineKey())
                .businessKey(startProcessParam.getBusinessKey())
                .variables(startProcessParam.getVariables())
                .tenantId(startProcessParam.getTenantId())
                .start();

        return getStartProcessResult(processInstance);
    }

    /**
     * 查询流程信息
     * @param processInstanceId
     * @return
     */
    @Override
    public ProcessInstanceInfoResult getInstanceInfo(String processInstanceId){
        ProcessInstanceInfoResult result = new ProcessInstanceInfoResult();
        HistoricProcessInstance historicProcessInstance = historyService.
                createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if(historicProcessInstance == null ){
            throw new ProcessException("查询错误，未查询到实例信息！");
        }
        BeanUtils.copyProperties(historicProcessInstance,result);
        List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery().includeTaskLocalVariables().processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime()
                .desc().list();
        if(CollectionUtil.isNotEmpty(list)){
            for (HistoricTaskInstance historicActivityInstance : list) {
                ProcessTaskResult processTaskResult = new ProcessTaskResult();
                BeanUtils.copyProperties(historicActivityInstance, processTaskResult);
                processTaskResult.setTaskDefineKey(historicActivityInstance.getTaskDefinitionKey());
                processTaskResult.setTaskDefineName(historicActivityInstance.getName());
                processTaskResult.setTaskId(historicActivityInstance.getId());
                //批注信息
                if(StringUtil.isNotEmpty(historicActivityInstance.getId())){
                    List<Comment> comment = processEngine.getTaskService().getTaskComments(historicActivityInstance.getId());
                    if(CollectionUtil.isNotEmpty(comment)){
                        processTaskResult.setComment(comment.get(0).getFullMessage());
                    }
                }
                if(processTaskResult.getEndTime() == null){
                    processTaskResult.setAssignee(historicActivityInstance.getAssignee());
                    if(StringUtil.isEmpty(historicActivityInstance.getAssignee())) {
                        processTaskResult.setAssignee(handleAssignee(historicActivityInstance.getId()));
                    }
                }
                result.getTaskResult().add(processTaskResult);
            }
        }
        return result;
    }

    /**
     * 流程撤回
     * @param processReturnParam 流程撤回
     * @return 流程撤回
     */
    @Override
    public Boolean processReturn(ProcessReturnParam processReturnParam){
        String userId = processReturnParam.getUserId();
        if(userId == null || "".equals(userId)) {
            throw new ProcessException("用户ID不能为空");
        }
        String businessKey = processReturnParam.getBusinessKey();
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().
                processInstanceBusinessKey(businessKey).processDefinitionKey(processReturnParam.getProcessDefineKey()).singleResult();
        if(processInstance == null){
            throw new ProcessException("处理失败，失败原因：未查到流程实例");
        }
        //流程发起人
        String startUserId = processInstance.getStartUserId();
        if(!userId.equals(startUserId)){
            throw new ProcessException("撤销失败，失败原因：你没有权限撤销");
        }
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel((processInstance.getProcessDefinitionId()));
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).processInstanceBusinessKey(processInstance.getBusinessKey()).singleResult();

        //获取开始节点的下一节点
        FlowElement targetFlowElement = BpmnUtil.startEventNextTaskId(bpmnModel);
        //满足条件的出口
        List<FlowElement> flowElement = new ArrayList<>();
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration();
        Context.setCommandContext(processEngineConfiguration.getCommandContextFactory().createCommandContext(null));
        Context.setProcessEngineConfiguration(processEngineConfiguration);
        //获取所有出口
        BpmnUtil.querySubmitNextTask(targetFlowElement, execution,flowElement);
        Context.removeCommandContext();
        Context.removeProcessEngineConfiguration();
        String processInstanceId = processInstance.getId();
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).list();
        //当前待审批节点定义Id集合
        List<String> currentTask = taskList.stream().map(Task::getTaskDefinitionKey).collect(Collectors.toList());
        List<String> flowElementList = flowElement.stream().map(FlowElement::getId).collect(Collectors.toList());
        if(currentTask.size() != flowElementList.size() || !currentTask.containsAll(flowElementList)) {
            throw new ProcessException("流程已在审批中，不能进行撤回");
        }

        if(CollectionUtil.isNotEmpty(taskList)){
            BpmnModel newBpmnModel = bpmnModel;
            Map<String, List<SequenceFlow>> stringListMap = BpmnUtil.invokeSequenceFlows(newBpmnModel, taskList, targetFlowElement);

            for (Task task : taskList) {
                //记录原活动方向
                List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
                //当前节点
                oriSequenceFlows.addAll(stringListMap.get(task.getTaskDefinitionKey()));
                FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
                //处理连线
                //handleSequenceFlow(currentFlowNode,targetFlowElement);
                //设置当前审批人为提交人
                processEngine.getTaskService().setAssignee(task.getId(),userId);
                // 保存任务评价
                if (StringUtil.isNotEmpty(processReturnParam.getComment())) {
                    processEngine.getTaskService().addComment(task.getId(), task.getProcessInstanceId(), processReturnParam.getComment());
                }
                Map<String,Object> variables = new HashMap<>();
                //设置节点状态
                processEngine.getTaskService().setVariablesLocal(task.getId(),variables);
                try {
                    //完成
                    managementService.executeCommand(new CustomTaskCompleteCmd(task.getId(),variables,true));
                    //删除任务
                    /*撤回记录保存，不用删除历史信息*/
                    //historyService.deleteHistoricTaskInstance(task.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ProcessException("流程撤回异常，异常原因：" + e.getMessage());
                } finally {
                    //恢复原方向
                    currentFlowNode.setOutgoingFlows(oriSequenceFlows);
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 撤销流程实例（流程实例删除、取消申请）
     *
     * @param deleteProcInstanceParam 撤销流程实例操作请求入参
     * @return 撤销流程实例操作返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean procInstanceDelete(ProcInstanceDeleteParam deleteProcInstanceParam) {
        String processInstanceId = deleteProcInstanceParam.getProcessInstanceId();
        String businessKey = deleteProcInstanceParam.getBusinessKey();
        String processDefineKey = deleteProcInstanceParam.getProcessDefineKey();

        ProcessInstanceQuery processInstanceQuery = processEngine.getRuntimeService().createProcessInstanceQuery();

        if(StringUtil.isNotEmpty(processInstanceId)){
            processInstanceQuery.processInstanceId(processInstanceId);
        }else if(StringUtil.isEmpty(businessKey) && StringUtil.isEmpty(processDefineKey)){
            throw new ProcessException("流程实例ID或业务ID和流程定义Key不能为空");
        }else{
            processInstanceQuery.processInstanceBusinessKey(businessKey).processDefinitionKey(processDefineKey);
        }
        ProcessInstance processInstance = processInstanceQuery.startedBy(deleteProcInstanceParam.getUserId())
                .singleResult();
        if(processInstance == null){
            throw new ProcessException("您没有权限对该流程进行撤销操作！");
        }
        if(processInstance.isSuspended()){
            throw new ProcessException("该流程被冻结，无法对该流程进行撤销操作！");
        }
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel((processInstance.getProcessDefinitionId()));
        //获取开始节点的下一节点
        FlowElement targetFlowElement = BpmnUtil.startEventNextTaskId(bpmnModel);
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
        //排除提交人自己审核
        List<Task> list = taskList.stream().filter(item -> !item.getTaskDefinitionKey().equals(targetFlowElement.getId())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(list)){
            throw new ProcessException("流程已在审核中，无法撤销！");
        }
        processEngine.getRuntimeService()
                .deleteProcessInstance(processInstance.getProcessInstanceId(), deleteProcInstanceParam.getCancelReason());

        return Boolean.TRUE;
    }

    /**
     * 根据启动流程实例转换启动流程结果
     *
     * @param processInstance 启动实例对象
     * @return 启动流程结果
     */
    private ProcessStartResult getStartProcessResult(ProcessInstance processInstance) {
        // 设置返回参数
        ProcessStartResult result = new ProcessStartResult();
        result.setBusinessKey(processInstance.getBusinessKey());
        result.setDeploymentId(processInstance.getDeploymentId());
        result.setStartUserId(processInstance.getStartUserId());
        result.setProcessDefineId(processInstance.getProcessDefinitionId());
        result.setProcessDefineKey(processInstance.getProcessDefinitionKey());
        result.setProcessDefineName(processInstance.getProcessDefinitionName());
        result.setProcessDefineVersion(processInstance.getProcessDefinitionVersion());
        result.setStartTime(processInstance.getStartTime());
        result.setProcessInstanceId(processInstance.getProcessInstanceId());
        return result;
    }
    /**
     * 查询审批人
     * @param taskId
     * @return
     */
    private String handleAssignee(String taskId) {
        List<IdentityLink> identityLinksForTask = processEngine.getTaskService().getIdentityLinksForTask(taskId);
        if(CollectionUtil.isNotEmpty(identityLinksForTask)){
            List<String> candidateUsersList = identityLinksForTask.stream().filter(item->StringUtil.isNotEmpty(item.getUserId())).map(IdentityLink::getUserId).collect(Collectors.toList());
            candidateUsersList = candidateUsersList.stream().distinct().collect(Collectors.toList());
            String[] candidateUsers = candidateUsersList.toArray(new String[candidateUsersList.size()]);
            return StringUtils.join(candidateUsers, ",");
        }
        return null;
    }
}
