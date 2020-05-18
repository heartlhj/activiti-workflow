package com.gogoing.workflow.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增流程定义操作的返回结果
 * @author yangxi
 */
@Data
public class ProcessCreateDefineResult implements Serializable {

    private String deployId;

    private String processKey;

    private Boolean code;


}
