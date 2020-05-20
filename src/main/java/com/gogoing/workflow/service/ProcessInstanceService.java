package com.gogoing.workflow.service;

import com.gogoing.workflow.domain.*;


/**
 * 流程实例管理Service组件
 * @author lhj
 */
public interface ProcessInstanceService {

    /**
     * 启动流程实例（通过流程定义key来启动）
     * @param startProcessParam 流程启动入参
     * @return 流程启动结果
     */
    ProcessStartResult startProcInstanceByKey(ProcessStartParam startProcessParam);

    /**
     * 查询流程信息
     * @param processInstanceId
     * @return
     */
    ProcessInstanceInfoResult getInstanceInfo(String processInstanceId);

    /**
     * 校验流程撤回
     * @param param
     * @return
     */
    Boolean processReturn(ProcessReturnParam param);

    /**
     * 撤销流程实例（流程实例删除、取消申请）
     * @param deleteProcInstanceParam 撤销流程实例操作请求入参
     * @return
     */
    Boolean procInstanceDelete(ProcInstanceDeleteParam deleteProcInstanceParam);

}
