package com.gogoing.workflow;

import com.gogoing.workflow.domain.ProcessStartParam;
import com.gogoing.workflow.domain.ProcessStartResult;
import com.gogoing.workflow.service.ProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    private ProcessStartParam startProcessParam;

    @Autowired
    private ProcessInstanceService processInstanceService;

    @Test
    public void start(){

        // 启动流程
        ProcessStartResult processInstance = processInstanceService.startProcInstanceByKey(startProcessParam);
        log.info("流程发起成功，流程实例ID:" + processInstance.getProcessInstanceId());
    }

    @Before
    public void createProcessStartParam() {
        startProcessParam = new ProcessStartParam();
        startProcessParam.setProcessDefineKey(KEY);
        startProcessParam.setUserId("lisi");
        startProcessParam.setBusinessKey(UUID.randomUUID().toString());
    }
}
