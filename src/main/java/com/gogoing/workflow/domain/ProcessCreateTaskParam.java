package com.gogoing.workflow.domain;

import lombok.Data;

import java.util.List;

/**
 *
 * @author lhj
 * @date 2019/11/27 19:48
 * @since V1.0.0
 */
@Data
public class ProcessCreateTaskParam extends AbstractParam {

    private String id;
    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点排序号
     */
    private Integer orderNum;

    /**
     * 指定审批人
     */
    private String assignee;


    /**
     * 优先级，取值区间[0~100]
     */
    private String priority;

    /**
     * 外置表单（预留）
     */
    private String formKey;

    /**
     * 截止日期
     */
    private String dueDate;

    /**
     * 类型 会签 或签 知会
     * "1","会签审批",
     * "2","或签审批",
     * "3","知会";
     *
     */
    private String category;

    /**
     * 审批人岗位
     */
    private List<CandidateParam> candidateParams;



    /**
     * 节点执行监听器，
     */
    private List<ExecutionListenerParam> executionListenerParams;



}
