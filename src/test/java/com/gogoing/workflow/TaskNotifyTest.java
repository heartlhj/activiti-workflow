package com.gogoing.workflow;

import com.gogoing.workflow.domain.PageBean;
import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskQuery;
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
 * @description: 抄送查询测试
 * @date 2020-5-20 22:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TaskNotifyTest {

    private static String USER_ID = "wangwu";

    @Resource
    private ProcessTaskServiceImpl processTaskService;

    private TaskQuery taskhQuery;

    @Test
    public void notifyQuery(){
        PageBean<ProcessTaskResult> processTaskResultPageBean = processTaskService.queryNotifyTask(taskhQuery);
        log.info("查询抄送，数量为:" +processTaskResultPageBean.getTotalElements());
    }

    @Before
    public void createTaskUnFinishQuery() {
        taskhQuery = new TaskQuery();
        taskhQuery.setUserId(USER_ID);
    }
}
