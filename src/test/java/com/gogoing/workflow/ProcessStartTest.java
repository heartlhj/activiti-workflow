package com.gogoing.workflow;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author lhj
 * @version 1.0
 * @description: 流程发起测试
 * @date 2020-5-20 22:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ProcessStartTest {

    private static String KEY = "test";

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IdentityService identityService;

    @Test
    public void start(){

        //设置流程发起人
        identityService.setAuthenticatedUserId("demo");
        // 启动流程
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(KEY)
                .businessKey(UUID.randomUUID().toString())
                .start();
        log.info("流程发起成功，流程实例ID:" + processInstance.getId());
    }
}
