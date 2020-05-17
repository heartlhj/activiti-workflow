package com.gogoing.workflow.service;

import com.gogoing.workflow.domain.ProcessStartParam;
import com.gogoing.workflow.domain.ProcessStartResult;


/**
 * 流程实例管理Service组件
 * @author lhj
 */
public interface ProcessProcInstanceService {

    /**
     * 启动流程实例（通过流程定义key来启动）
     * @param startProcessParam 流程启动入参
     * @return 流程启动结果
     */
    ProcessStartResult startProcInstanceByKey(ProcessStartParam startProcessParam);

}
