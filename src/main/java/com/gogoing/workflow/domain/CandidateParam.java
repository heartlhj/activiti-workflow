package com.gogoing.workflow.domain;

import lombok.Data;

import java.io.Serializable;

/***
 * @description 待审批信息
 * @author LHJ
 * @date 2019/12/10 17:41
 */
@Data
public class CandidateParam implements Serializable {

    /**
     * 类型不能为空
     */
    private String type;

    /**
     * id不能为空
     */
    private String id;

}
