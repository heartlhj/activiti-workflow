package com.gogoing.workflow.cmd;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEventDispatcher;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.cmd.CompleteTaskCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLinkType;

import java.util.Map;

/**
 * 用于处理驳回，任务完成事件不执行
 */
public class CustomTaskCompleteCmd extends CompleteTaskCmd {

    protected boolean eventDispatcher;

    public CustomTaskCompleteCmd(String taskId, Map<String, Object> variables, boolean eventDispatcher) {
        super(taskId,variables);
        this.eventDispatcher = eventDispatcher;
    }
    public CustomTaskCompleteCmd(String taskId, Map<String, Object> variables, boolean localScope, boolean eventDispatcher) {
        super(taskId, variables,localScope);
        this.eventDispatcher = eventDispatcher;
    }

    public CustomTaskCompleteCmd(String taskId, Map<String, Object> variables, Map<String, Object> transientVariables, boolean eventDispatcher) {
        super(taskId, variables,transientVariables);
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    protected void executeTaskComplete(CommandContext commandContext, TaskEntity taskEntity, Map<String, Object> variables, boolean localScope) {
        if (taskEntity.getDelegationState() != null && taskEntity.getDelegationState().equals(DelegationState.PENDING)) {
            throw new ActivitiException("A delegated task cannot be completed, but should be resolved instead.");
        }
        //移除任务完成监听器执行
        //commandContext.getProcessEngineConfiguration().getListenerNotificationHelper().executeTaskListeners(taskEntity, TaskListener.EVENTNAME_COMPLETE);
        if (Authentication.getAuthenticatedUserId() != null && taskEntity.getProcessInstanceId() != null) {
            ExecutionEntity processInstanceEntity = commandContext.getExecutionEntityManager().findById(taskEntity.getProcessInstanceId());
            commandContext.getIdentityLinkEntityManager().involveUser(processInstanceEntity, Authentication.getAuthenticatedUserId(), IdentityLinkType.PARTICIPANT);
        }
        if(!eventDispatcher){
            ActivitiEventDispatcher eventDispatcher = Context.getProcessEngineConfiguration().getEventDispatcher();
            if (eventDispatcher.isEnabled()) {
                if (variables != null) {
                    eventDispatcher.dispatchEvent(ActivitiEventBuilder.createEntityWithVariablesEvent(ActivitiEventType.TASK_COMPLETED, taskEntity, variables, localScope));
                } else {
                    eventDispatcher.dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEventType.TASK_COMPLETED, taskEntity));
                }
            }
        }
        commandContext.getTaskEntityManager().deleteTask(taskEntity, null, false, false);

        // Continue process (if not a standalone task)
        if (taskEntity.getExecutionId() != null) {
            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(taskEntity.getExecutionId());
            Context.getAgenda().planTriggerExecutionOperation(executionEntity);
        }
    }
}
