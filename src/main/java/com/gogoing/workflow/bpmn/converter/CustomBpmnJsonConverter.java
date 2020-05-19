package com.gogoing.workflow.bpmn.converter;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import java.util.Map;

/**
 *  自定义BPMN Json 转换器
 * @author lhj
 * @date 2020/5/20  20:35
 * @since V1.0.0
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    public CustomBpmnJsonConverter(){
        CustomUserTaskJsonConverter.fillTypes(convertersToBpmnMap, convertersToJsonMap);
    }

    //通过继承开放convertersToJsonMap的访问
    public static Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> getConvertersToJsonMap(){
        return convertersToJsonMap;
    }

    //通过继承开放convertersToJsonMap的访问
    public static Map<String, Class<? extends BaseBpmnJsonConverter>> getConvertersToBpmnMap(){
        return convertersToBpmnMap;
    }
}
