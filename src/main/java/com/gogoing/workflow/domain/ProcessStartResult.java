package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 启动流程操作结果
 *
 * @author lhj
 */
@Data
@ApiModel(value = "流程启动信息")
@EqualsAndHashCode(callSuper = true)
public class ProcessStartResult extends AbstractParam {
    /**
     * 流程定义ID
     */
    @ApiModelProperty(value = "流程定义ID")
    private String processDefineId;

    /**
     * 流程定义名称
     */
    @ApiModelProperty(value = "流程定义名称")
    private String processDefineName;

    /**
     * 流程定义Key
     */
    @ApiModelProperty(value = "流程定义Key")
    private String processDefineKey;

    /**
     * 流程定义版本
     */
    @ApiModelProperty(value = "流程定义版本")
    private Integer processDefineVersion;

    /**
     * 流程运行实例
     */
    @ApiModelProperty(value = "流程运行实例")
    private String processInstanceId;

    /**
     * 部署ID
     */
    @ApiModelProperty(value = "部署ID")
    private String deploymentId;

    /**
     * 业务Key
     */
    @ApiModelProperty(value = "业务Key")
    private String businessKey;

    /**
     * 流程启动时间
     */
    @ApiModelProperty(value = "流程启动时间")
    private Date startTime;

    /**
     * 流程启动人
     */
    @ApiModelProperty(value = "流程启动人")
    private String startUserId;
}
