package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 完成任务
 * @author lhj
 * @since 2019/12/26 20:29
 */
@Data
@ApiModel("完成任务信息")
@EqualsAndHashCode(callSuper = false)
public class CompleteTaskParam extends AbstractParam {

    @ApiModelProperty(value = "任务ID")
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    @ApiModelProperty(value = "评价内容")
    private String comment;

    @ApiModelProperty(value = "用户Id")
    private String userId;
    @ApiModelProperty(value = "完成参数")
    private Map<String, Object> variables;

    private List<TaskAttachmentParam> attachments;
}
