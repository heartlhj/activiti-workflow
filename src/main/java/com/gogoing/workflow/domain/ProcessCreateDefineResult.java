package com.gogoing.workflow.domain;

import lombok.Data;

/**
 * 新增流程定义操作的返回结果
 * @author lhj
 */
@Data
public class ProcessCreateDefineResult extends AbstractParam {

    private String deployId;

    private String processKey;

    private Boolean code;


}
