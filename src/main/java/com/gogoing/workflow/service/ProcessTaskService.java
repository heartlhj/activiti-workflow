package com.gogoing.workflow.service;


import com.gogoing.workflow.domain.CompleteTaskParam;

/**
 * 流程任务管理Service组件
 * @author yangxi
 */
public interface ProcessTaskService {

    /**
     * 完成任务
     * @param param 完成任务参数
     * @return 是否成功
     */
    Boolean complete(CompleteTaskParam param);

}
