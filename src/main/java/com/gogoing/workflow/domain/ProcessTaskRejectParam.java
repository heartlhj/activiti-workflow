package com.gogoing.workflow.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author lhj
 * @date 2019/12/13 11:55
 * @since V1.0.0
 */
@Data
public class ProcessTaskRejectParam extends AbstractParam {


    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    @NotBlank(message = "驳回原因不能为空")
    private String rejectComment;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "驳回目标节点ID不能为空")
    private String targetNodeId;

}
