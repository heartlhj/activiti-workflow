package com.gogoing.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.util.StringUtil;
import com.gogoing.workflow.domain.CompleteTaskParam;
import com.gogoing.workflow.domain.TaskAttachmentParam;
import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.service.ProcessTaskService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author lhj
 * @version 1.0
 * @description: 任务服务
 * @date 2020-5-18 22:28
 */
public class ProcessTaskServiceImpl implements ProcessTaskService {


    @Autowired
    private TaskService taskService;
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
}
