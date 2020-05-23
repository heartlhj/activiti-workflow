package com.gogoing.workflow;

import com.gogoing.workflow.domain.PageBean;
import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskFinishQuery;
import com.gogoing.workflow.service.impl.ProcessTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author lhj
 * @version 1.0
 * @description: 流程详情测试
 * @date 2020-5-20 22:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TaskFinishTest {

    private static String USER_ID = "wangwu";

    @Resource
    private ProcessTaskServiceImpl processTaskService;

    private TaskFinishQuery taskFinishQuery;

    @Test
    public void unFinish(){
        PageBean<ProcessTaskResult> processTaskResultPageBean = processTaskService.queryFinishTask(taskFinishQuery);
        log.info("查询已审批，数量为:" +processTaskResultPageBean.getTotalElements());
    }

    @Before
    public void createTaskUnFinishQuery() {
        taskFinishQuery = new TaskFinishQuery();
        taskFinishQuery.setUserId(USER_ID);
        taskFinishQuery.setIsNotify(true);
    }
}
