package com.gogoing.workflow.config;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


/**
 * 针对SpringProcessEngineConfiguration的扩展配置
 * @author lhj
 */
@Component
public class WorkflowConfigurationConfigurer implements ProcessEngineConfigurationConfigurer {

    @Autowired
    private DataSource dataSource;

//    @Autowired
//    private WorkflowEventListener workflowEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {

        //配置全局监听器
        List<ActivitiEventListener> eventListeners =new ArrayList<>();
//        eventListeners.add(workflowEventListener);
        processEngineConfiguration.setEventListeners(eventListeners);

    }



}
