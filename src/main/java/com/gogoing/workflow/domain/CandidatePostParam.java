package com.gogoing.workflow.domain;

import lombok.Data;

import java.io.Serializable;

/***
 * @description 待审批岗位信息
 * @author LHJ
 * @date 2019/12/10 17:41
 */
@Data
public class CandidatePostParam implements Serializable {

    /**
     * 岗位类型不能为空
     */
    private String postType;

    /**
     * 岗位Id不能为空
     */
    private String postId;

}
