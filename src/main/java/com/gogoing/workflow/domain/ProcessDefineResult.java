package com.gogoing.workflow.domain;

import lombok.Data;

/**
 * 流程定义详情
 * @author lhj
 */
@Data
public class ProcessDefineResult extends AbstractParam{

    /**
     * 流程定义ID
     */
    private String processDefId;

    /**
     * 流程定义名称
     */
    private String processDefName;

    /**
     * 流程定义key
     */
    private String processDefKey;

    /**
     * 流程定义分类
     */
    private String category;

    /**
     * 流程部署ID
     */
    private String deploymentId;

    /**
     * 流程文件
     */
    private String resourceName;

    /**
     * 流程图
     */
    private String dgrmResourceName;

    /**
     * 状态
     */
    private Boolean suspensionState;

}
