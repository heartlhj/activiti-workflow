package com.gogoing.workflow.controller;

import com.gogoing.workflow.domain.ProcessStartParam;
import com.gogoing.workflow.domain.ProcessStartResult;
import com.gogoing.workflow.service.ProcessInstanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lhj
 * @version 1.0
 * @description: 流程实例管理
 * @date 2020-5-17 20:35
 */

@RestController
@RequestMapping("/workflow/procInstance")
@Api(value = "流程实例管理", tags = "流程实例管理")
public class ProcessProcInstanceController {

    @Autowired
    private ProcessInstanceService processInstanceService;
    /**
     * 启动流程实例（通过流程定义key来启动）
     * @return 启动结果
     */
    @ApiOperation(value = "启动流程实例")
    @PostMapping("/startProcInstance")
    public ProcessStartResult startProcInstance(ProcessStartParam startProcessParam) {
        return processInstanceService.startProcInstanceByKey(startProcessParam);
    }
}
