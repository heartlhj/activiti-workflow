package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/***
 * @description 流程撤回操作
 * @author LHJ
 * @date 2020/3/17 19:43
 */
@Data
@ApiModel(value = "流程撤回")
public class ProcessReturnParam extends AbstractParam {


    /**
     * 原来的审批人
     */
    @ApiModelProperty(value = "流程发起人")
    @NotBlank(message = "流程发起人")
    private  String userId;

    @ApiModelProperty(value = "流程定义KEy")
    private String processDefineKey;

    @ApiModelProperty(value = "业务KEy")
    private String businessKey;

    @ApiModelProperty(value = "原因")
    private String comment;
}
