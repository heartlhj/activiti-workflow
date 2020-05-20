package com.gogoing.workflow;

import com.gogoing.workflow.domain.ProcessInstanceInfoResult;
import com.gogoing.workflow.service.impl.ProcessInstanceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
public class ProcessInfoTest {

    private static String PROCESSINSTANCEID = "2501";

    @Resource
    private ProcessInstanceServiceImpl processInstanceService;


    @Test
    public void detail(){
        ProcessInstanceInfoResult instanceInfo = processInstanceService.getInstanceInfo(PROCESSINSTANCEID);
        Assert.assertEquals(1,instanceInfo.getTaskResult().size());
    }
}
