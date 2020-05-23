package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 启动流程实例（通过流程定义Key来启动）
 * @author lhj
 */
@Data
@ApiModel("启动流程实例（通过流程定义Key来启动）")
@EqualsAndHashCode(callSuper = false)
public class ProcessStartParam extends AbstractParam {

    /**
     * 流程定义key
     */
    @NotBlank(message = "流程定义key不能为空")
    @ApiModelProperty(value = "流程定义key")
    private String processDefineKey;

    /**
     *用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 业务key,跟业务单据关联
     */
    @ApiModelProperty(value = "业务key")
    private String businessKey;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    /**
     * 启动流程变量信息
     */
    @ApiModelProperty(value = "启动流程变量")
    private Map<String, Object> variables;

}
