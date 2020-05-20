package com.gogoing.workflow.domain;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/*** 流程实例信息返回对象
 * @description
 * @author LHJ
 * @date 2019/12/25 10:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessInstanceInfoResult extends AbstractParam{

    /**
     * 实例Id
     */
    @ApiModelProperty(value = "实例Id")
    private String  id;
    /**
     * 业务主键
     */
    @ApiModelProperty(value = "业务主键")
    private String  businessKey;
    /**
     * 流程定义id
     */
    @ApiModelProperty(value = "流程定义id")
    private String  processDefinitionId;


    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义Key")
    private String processDefinitionKey;

    @ApiModelProperty(value = "部署ID")
    private String deploymentId;
    @ApiModelProperty(value = "实例ID")
    private String processInstanceId;
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

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String  tenantId;
    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private Integer status;


    /**
     * 节点
     */
    @ApiModelProperty(value = "节点")
    private List<ProcessTaskResult> taskResult = Lists.newArrayList();



}
