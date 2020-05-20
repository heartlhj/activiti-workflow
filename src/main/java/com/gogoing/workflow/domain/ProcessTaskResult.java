package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务列表信息
 * @author lhj
 */
@Data
@ApiModel(value = "流程任务列表信息")
public class ProcessTaskResult extends AbstractParam {

    @ApiModelProperty(value = "流程任务ID")
    private String taskId;

    @ApiModelProperty(value = "流程任务名称")
    private String taskDefineName;

    @ApiModelProperty(value = "流程任务Key")
    private String taskDefineKey;

    @ApiModelProperty(value = "流程定义KEY")
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "任务审批人")
    private String assignee;

    @ApiModelProperty(value = "流程实例业务key")
    private String businessKey;

    /**
     * 方便查询历史审核记录
     */
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**
     * 审批批注
     */
    @ApiModelProperty(value = "审批批注")
    private String comment;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

}
