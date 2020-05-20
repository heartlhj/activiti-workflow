package com.gogoing.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.util.StringUtil;
import com.gogoing.workflow.domain.*;
import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.mapper.CustomActivitiDatabaseMapper;
import com.gogoing.workflow.service.ProcessTaskService;
import com.gogoing.workflow.utils.BpmnUtil;
import com.gogoing.workflow.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author lhj
 * @version 1.0
 * @description: 任务服务
 * @date 2020-5-18 22:28
 */
@Slf4j
@Service
public class ProcessTaskServiceImpl implements ProcessTaskService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private ManagementService managementService;

    /**
     *  查询待审批任务
     * @param taskUnFinishQuery 查询条件
     * @return
     */
    @Override
    public PageBean<ProcessTaskResult> queryUnFinishTask(TaskUnFinishQuery taskUnFinishQuery){
        PageUtil<ProcessTaskResult, TaskUnFinishQuery> pageUtil = new PageUtil<>();
        Long count = managementService.executeCustomSql(new AbstractCustomSqlExecution<CustomActivitiDatabaseMapper, Long>(CustomActivitiDatabaseMapper.class) {
            @Override
            public Long execute(CustomActivitiDatabaseMapper customActivitiDatabaseMapper) {
                return customActivitiDatabaseMapper.selectUnFinishTaskCount(taskUnFinishQuery);
            }
        });
        //没有查询到，就直接返回空
        if(count <= 0){
            return pageUtil.buildPage(Collections.emptyList(), taskUnFinishQuery, 0);
        }
        List<ProcessTaskResult> list = managementService.executeCustomSql(new AbstractCustomSqlExecution<CustomActivitiDatabaseMapper, List<ProcessTaskResult>>(CustomActivitiDatabaseMapper.class) {
            @Override
            public List<ProcessTaskResult> execute(CustomActivitiDatabaseMapper customActivitiDatabaseMapper) {
                return customActivitiDatabaseMapper.selectUnFinishTask(taskUnFinishQuery);
            }
        });

        return pageUtil.buildPage(list,taskUnFinishQuery,count);
    }

    /**
     * 完成任务
     *
     * @param param       完成任务参数
     * @return 是否成功
     */
    @Override
    public Boolean complete(CompleteTaskParam param) {
        List<TaskAttachmentParam> attachments = param.getAttachments();

        Task task = taskService.createTaskQuery()
                .taskId(param.getTaskId())
                .taskCandidateOrAssigned(param.getUserId())
                .singleResult();
        if (task == null) {
            throw new ProcessException("没有权限执行该任务！");
        }

        // 任务暂停不能完成
        if (task.isSuspended()) {
            throw new ProcessException("流程任务已冻结，无法执行审批操作！");
        }
        // 保存任务评价
        if (StringUtil.isNotEmpty(param.getComment())) {
            taskService.addComment(param.getTaskId(), task.getProcessInstanceId(), param.getComment());
        }

        // 保存任务文件
        if (CollectionUtil.isNotEmpty(attachments)) {
            for (TaskAttachmentParam attachment : attachments) {
                taskService.createAttachment(attachment.getType(), param.getTaskId(),
                        task.getProcessInstanceId(), attachment.getName(), attachment.getDescription(),
                        new ByteArrayInputStream(attachment.getFileBytes()));
            }
        }
        //设置节点审批人
        taskService.setAssignee(param.getTaskId(),param.getUserId());
        // 完成任务
        Map<String, Object> variables = CollectionUtil.isEmpty(param.getVariables()) ? null : param.getVariables();
        taskService.complete(param.getTaskId(), variables);

        return Boolean.TRUE;
    }

    /**
     * 任务驳回
     * @param processRejectParam 任务驳回入参
     * @return 任务驳回操作结果
     */
    @Override
    public Boolean taskReject(ProcessTaskRejectParam processRejectParam){
        String taskId = processRejectParam.getTaskId();
        if (taskId == null || "".equals(taskId)) {
            throw new ProcessException("任务ID不能为空");
        }

        String userId = processRejectParam.getUserId();
        if (userId == null || "".equals(userId)) {
            throw new ProcessException("操作用户ID不能为空");
        }
        String statusDesc = "驳回";
        String rejectComment = statusDesc + "" +processRejectParam.getRejectComment();

        Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateOrAssigned(userId).singleResult();
        if (null == task) {
            throw new ProcessException("驳回失败，失败原因：未查到任务信息或者你没有权限驳回该任务");
        }

        if (task.isSuspended()) {
            throw new ProcessException("驳回失败，失败原因：任务被冻结,无法执行驳回该任务");
        }
        //获取流程模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        if (null == bpmnModel) {
            throw new ProcessException("驳回失败，失败原因：未查到流程信息！");
        }


        String processInstanceId = task.getProcessInstanceId();
        FlowElement targetFlowElement = null;
        if(StringUtil.isNotEmpty(processRejectParam.getTargetNodeId())){
            //找到目标节点元素
            targetFlowElement = bpmnModel.getMainProcess().getFlowElement(processRejectParam.getTargetNodeId());
        }else{
            targetFlowElement = BpmnUtil.startEventNextTaskId(bpmnModel);
        }
        //查找当前任务的元素ID
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        log.warn("当前任务所在流程元素ID:{}", activityId);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
        oriSequenceFlows.addAll(currentFlowNode.getOutgoingFlows());

        //清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //重新绘制流程图，从当前节点到开始节点
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();

        //绘制连线，加入流程信息，并组装到流程图
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId-" + System.currentTimeMillis());
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(targetFlowElement);
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        try {
            taskService.addComment(task.getId(), processInstanceId, rejectComment);

            //将处理人加入流程变量
            Map<String, Object> currentVariables = new HashMap<>(1);
            //设置节点状态
            taskService.setVariablesLocal(task.getId(),currentVariables);
            taskService.setAssignee(task.getId(),userId);
            //完成任务
            taskService.complete(task.getId(), currentVariables);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProcessException("驳回失败，失败原因：完成任务时失败！");
        } finally {
            //恢复原方向
            currentFlowNode.setOutgoingFlows(oriSequenceFlows);
        }

        return Boolean.TRUE;
    }

    /**
     *  查询待审批任务
     * @param taskFinishQuery 查询条件
     * @return
     */
    @Override
    public PageBean<ProcessTaskResult> queryFinishTask(TaskFinishQuery taskFinishQuery){
        PageUtil<ProcessTaskResult, TaskFinishQuery> pageUtil = new PageUtil<>();
        Long count = managementService.executeCustomSql(new AbstractCustomSqlExecution<CustomActivitiDatabaseMapper, Long>(CustomActivitiDatabaseMapper.class) {
            @Override
            public Long execute(CustomActivitiDatabaseMapper customActivitiDatabaseMapper) {
                return customActivitiDatabaseMapper.selectFinishTaskCount(taskFinishQuery);
            }
        });
        //没有查询到，就直接返回空
        if(count <= 0){
            return pageUtil.buildPage(Collections.emptyList(), taskFinishQuery, 0);
        }
        List<ProcessTaskResult> list = managementService.executeCustomSql(new AbstractCustomSqlExecution<CustomActivitiDatabaseMapper, List<ProcessTaskResult>>(CustomActivitiDatabaseMapper.class) {
            @Override
            public List<ProcessTaskResult> execute(CustomActivitiDatabaseMapper customActivitiDatabaseMapper) {
                return customActivitiDatabaseMapper.selectFinishTask(taskFinishQuery);
            }
        });

        return pageUtil.buildPage(list,taskFinishQuery,count);
    }
}
