package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 流程实例取消操作入参对象
 * @author lhj
 * @date 2019/12/13 11:47
 * @since V1.0.0
 */
@Data
@ApiModel(value = "流程实例取消操作入参")
public class ProcInstanceDeleteParam extends AbstractParam {


    @ApiModelProperty(value = "流程实例ID")
    private  String  processInstanceId;

    @ApiModelProperty(value = "删除原因")
    @NotBlank(message = "删除原因不能为空")
    private  String  cancelReason;

    @ApiModelProperty(value = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private  String userId;

    @ApiModelProperty(value = "流程业务KEY")
    private String businessKey;
    @ApiModelProperty(value = "流程定义ID")
    private String processDefineKey;

}
