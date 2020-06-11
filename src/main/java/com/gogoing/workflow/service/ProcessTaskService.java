package com.gogoing.workflow.service;


import com.gogoing.workflow.domain.*;

/**
 * 流程任务管理Service组件
 * @author lhj
 */
public interface ProcessTaskService {

    /**
     *  查询待审批任务,包含抄送
     * @param taskUnFinishQuery 查询条件
     * @return
     */
    PageBean<ProcessTaskResult> queryUnFinishTask(TaskUnFinishQuery taskUnFinishQuery);

    /**
     * 完成任务
     * @param param 完成任务参数
     * @return 是否成功
     */
    Boolean complete(CompleteTaskParam param);

    /**
     * 任务驳回
     * @param processRejectParam 任务驳回入参
     * @return 任务驳回操作结果
     */
    Boolean taskReject(ProcessTaskRejectParam processRejectParam);

    /**
     *  查询待审批任务,包含抄送
     * @param taskFinishQuery 查询条件
     * @return
     */
    PageBean<ProcessTaskResult> queryFinishTask(TaskFinishQuery taskFinishQuery);


    /**
     *  查询抄送任务,不包含审批
     * @param taskQuery 查询条件
     * @return
     */
    PageBean<ProcessTaskResult> queryNotifyTask(TaskQuery taskQuery);

}
