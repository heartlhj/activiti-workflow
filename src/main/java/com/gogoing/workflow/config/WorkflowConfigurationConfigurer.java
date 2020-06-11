package com.gogoing.workflow.config;

import com.gogoing.workflow.bpmn.converter.CustomUserTaskXMLConverter;
import com.gogoing.workflow.bpmn.entity.CustomIdentityLinkEntityManagerImpl;
import com.gogoing.workflow.bpmn.handler.CustomUserTaskParseHandler;
import com.gogoing.workflow.mapper.CustomActivitiDatabaseMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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


        Set<Class<?>> mappers = processEngineConfiguration.getCustomMybatisMappers();
        if (mappers == null) {
            mappers = new HashSet<>();
        }
        mappers.add(CustomActivitiDatabaseMapper.class);
        processEngineConfiguration.setCustomMybatisMappers(mappers);
        Set<String> mapperXmls = processEngineConfiguration.getCustomMybatisXMLMappers();
        if (mapperXmls == null) {
            mapperXmls = new HashSet<>();
        }
        mapperXmls.add("mapper/CustomActivitiDatabaseMapper.xml");
        processEngineConfiguration.setCustomMybatisXMLMappers(mapperXmls);

        List<BpmnParseHandler> postBpmnParseHandlers = new ArrayList<>();
        postBpmnParseHandlers.add(new CustomUserTaskParseHandler());
        processEngineConfiguration.setPostBpmnParseHandlers(postBpmnParseHandlers);
        processEngineConfiguration.init();
        processEngineConfiguration.setIdentityLinkEntityManager(new CustomIdentityLinkEntityManagerImpl(processEngineConfiguration, processEngineConfiguration.getIdentityLinkDataManager()));

        BpmnXMLConverter.addConverter(new CustomUserTaskXMLConverter());
    }



}
