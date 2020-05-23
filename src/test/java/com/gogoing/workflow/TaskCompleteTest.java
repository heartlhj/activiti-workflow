package com.gogoing.workflow;

import com.gogoing.workflow.domain.CompleteTaskParam;
import com.gogoing.workflow.domain.PageBean;
import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskUnFinishQuery;
import com.gogoing.workflow.service.ProcessTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lhj
 * @version 1.0
 * @description: 流程任务完成测试
 * @date 2020-5-20 22:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TaskCompleteTest {

    private static String USER_ID = "wangwu";

    @Resource
    private ProcessTaskService processTaskService;

    private TaskUnFinishQuery taskUnFinishQuery;

    private CompleteTaskParam completeTaskParam;



    @Test
    public void complete(){

        PageBean<ProcessTaskResult> processTaskResultPageBean = processTaskService.queryUnFinishTask(taskUnFinishQuery);
        List<ProcessTaskResult> content = processTaskResultPageBean.getContent();
        if(!CollectionUtils.isEmpty(content)){
            for (ProcessTaskResult processTaskResult : content) {
                completeTaskParam.setTaskId(processTaskResult.getTaskId());
                processTaskService.complete(completeTaskParam);
                log.info("任务审批完成,任务ID:{}",processTaskResult.getTaskId());
            }
        }

    }

    @Before
    public void createTaskUnFinishQuery() {

        taskUnFinishQuery = new TaskUnFinishQuery();
        taskUnFinishQuery.setUserId(USER_ID);
        taskUnFinishQuery.setIsNotify(true);

        completeTaskParam = new CompleteTaskParam();
        completeTaskParam.setUserId(USER_ID);
        completeTaskParam.setComment("OK");
    }
}
