package com.gogoing.workflow.service;


import com.gogoing.workflow.domain.CompleteTaskParam;
import com.gogoing.workflow.domain.PageBean;
import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskUnFinishQuery;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;

import java.util.List;

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

    /**
     *  查询待审批任务
     * @param taskUnFinishQuery 查询条件
     * @return
     */
    PageBean<ProcessTaskResult> queryUnFinishTask(TaskUnFinishQuery taskUnFinishQuery);

}
