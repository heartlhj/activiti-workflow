package com.gogoing.workflow.service.impl;

import com.gogoing.workflow.domain.ProcessStartParam;
import com.gogoing.workflow.domain.ProcessStartResult;
import com.gogoing.workflow.exception.ProcessException;
import com.gogoing.workflow.service.ProcessProcInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 流程实例管理Service组件
 * @author yangxi
 */
@Service
@Slf4j
public class ProcessProcInstanceServiceImpl implements ProcessProcInstanceService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private IdentityService identityService;

    @Resource
    private ProcessEngine processEngine;

    @Override
    public ProcessStartResult startProcInstanceByKey(ProcessStartParam startProcessParam){
        if (StringUtils.isBlank(startProcessParam.getProcessDefineKey())) {
            throw new ProcessException("流程启动失败, 流程定义Key不能为空");
        }
        ProcessInstance oldProcessInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(startProcessParam.getProcessDefineKey())
                .processInstanceBusinessKey(startProcessParam.getBusinessKey())
                .singleResult();

        if (oldProcessInstance != null) {
            throw new ProcessException("流程启动失败, 业务Key[" + startProcessParam.getBusinessKey() + "]已经存在");
        }

        // 设置登录用户ID
        identityService.setAuthenticatedUserId(startProcessParam.getUserId());

        // 启动流程
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(startProcessParam.getProcessDefineKey())
                .businessKey(startProcessParam.getBusinessKey())
                .variables(startProcessParam.getVariables())
                .tenantId(startProcessParam.getTenantId())
                .start();

        return getStartProcessResult(processInstance);
    }

    /**
     * 根据启动流程实例转换启动流程结果
     *
     * @param processInstance 启动实例对象
     * @return 启动流程结果
     */
    private ProcessStartResult getStartProcessResult(ProcessInstance processInstance) {
        // 设置返回参数
        ProcessStartResult result = new ProcessStartResult();
        result.setBusinessKey(processInstance.getBusinessKey());
        result.setDeploymentId(processInstance.getDeploymentId());
        result.setStartUserId(processInstance.getStartUserId());
        result.setProcessDefineId(processInstance.getProcessDefinitionId());
        result.setProcessDefineKey(processInstance.getProcessDefinitionKey());
        result.setProcessDefineName(processInstance.getProcessDefinitionName());
        result.setProcessDefineVersion(processInstance.getProcessDefinitionVersion());
        result.setStartTime(processInstance.getStartTime());
        result.setProcessInstanceId(processInstance.getProcessInstanceId());
        return result;
    }

}
