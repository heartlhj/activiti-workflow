package com.gogoing.workflow.domain;

import lombok.Data;

import java.util.List;

/***
 * @description 新增流程定义参数
 * @author LHJ
 * @date 2020/3/13 14:44
 */
@Data
public class ProcessCreateDefineParam extends AbstractParam {
    /**
     * 流程唯一标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;


    /**
     * 流程描述
     */
    private String description;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 表单Key
     */
    private String formKey;



    /**
     * 流程节点
     */
    private List<ProcessCreateTaskParam> processNode;



}
