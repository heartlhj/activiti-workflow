package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 用户任务列表查询条件
 * @author lhj
 */
@Data
@ApiModel(value = "用户任务列表查询条件")
public class TaskQuery extends WorkflowPage {

    /**
     * 流程发起人
     */
    @ApiModelProperty(value = "流程发起人")
    private String startUserId;
    /**
     * 任务审批人
     */
    @ApiModelProperty(value = "任务审批人")
    @NotEmpty(message = "任务审批人不能为空")
    private String userId;

    /**
     * 流程定义KEY
     */
    @ApiModelProperty(value = "流程定义KEY")
    private String processDefinitionKey;

    /**
     * 业务KEY
     */
    @ApiModelProperty(value = "业务KEY")
    private List<String> businessKeys;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

}
