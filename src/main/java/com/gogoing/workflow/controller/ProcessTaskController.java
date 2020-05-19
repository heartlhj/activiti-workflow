package com.gogoing.workflow.controller;

import com.gogoing.workflow.domain.*;
import com.gogoing.workflow.service.ProcessTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 流程任务管理
 *
 * @author lhj
 * @since 2020/04/27 10:42
 */
@RestController
@RequestMapping("/workflow/processTask")
@Api(value = "流程任务管理", tags = "流程任务管理")
public class ProcessTaskController {

    private static final Logger log = LoggerFactory.getLogger(ProcessTaskController.class);

    @Autowired
    private ProcessTaskService processTaskService;

    /**
     * 待办查询（查询用户受理的全部任务）
     * @param processTaskQuery 用户ID
     * @return 待办任务列表
     */
    @ApiOperation(value = "待办任务查询")
    @GetMapping("/listUnFinishTasks")
    public PageBean<ProcessTaskResult> listAssigneeTasks(TaskUnFinishQuery processTaskQuery) {
        PageBean<ProcessTaskResult> taskResults = processTaskService.queryUnFinishTask(processTaskQuery);
        return taskResults;
    }

    /**
     * 完成任务
     * @param param 完成任务参数
     * @return 是否成功
     */
    @ApiOperation(value = "完成任务，没有附件")
    @PostMapping("/complete")
    public Boolean complete(@RequestBody @Validated CompleteTaskParam param) {
        return processTaskService.complete(param);
    }

}
