package com.gogoing.workflow.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lhj
 * @since 2019/12/26 11:38
 */
@Data
public abstract class AbstractParam implements Serializable {

    private static final long serialVersionUID = 6067858305735262573L;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
}
