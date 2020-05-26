package com.gogoing.workflow.bpmn.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.gogoing.workflow.bpmn.CustomStencilConstants;
import com.gogoing.workflow.bpmn.model.CustomUserTask;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 自定义用户任务 JSON 转换器
 * @author lhj
 * @date 2020/4/21  11:36
 * @since V1.0.0
 */
public class CustomUserTaskJsonConverter extends UserTaskJsonConverter implements CustomStencilConstants {

    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }


    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put("UserTask", CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        CustomUserTask task = new CustomUserTask();

        task.setPriority(getPropertyValueAsString(PROPERTY_USERTASK_PRIORITY, elementNode));
        String formKey = getPropertyValueAsString(PROPERTY_FORMKEY, elementNode);
        if (StringUtils.isNotEmpty(formKey)) {
            task.setFormKey(formKey);
        } else {
            JsonNode formReferenceNode = getProperty(PROPERTY_FORM_REFERENCE, elementNode);
            if (formReferenceNode != null && formReferenceNode.get("id") != null) {

                if (formMap != null && formMap.containsKey(formReferenceNode.get("id").asText())) {
                    task.setFormKey(formMap.get(formReferenceNode.get("id").asText()));
                }
            }
        }

        task.setDueDate(getPropertyValueAsString(PROPERTY_USERTASK_DUEDATE, elementNode));
        task.setCategory(getPropertyValueAsString(PROPERTY_USERTASK_CATEGORY, elementNode));

        JsonNode assignmentNode = getProperty(PROPERTY_USERTASK_ASSIGNMENT, elementNode);
        if (assignmentNode != null) {
            JsonNode assignmentDefNode = assignmentNode.get("assignment");
            if (assignmentDefNode != null) {

                JsonNode typeNode = assignmentDefNode.get("type");
                JsonNode canCompleteTaskNode = assignmentDefNode.get("initiatorCanCompleteTask");
                if (typeNode == null || "static".equalsIgnoreCase(typeNode.asText())) {
                    JsonNode assigneeNode = assignmentDefNode.get(PROPERTY_USERTASK_ASSIGNEE);
                    if (assigneeNode != null && assigneeNode.isNull() == false) {
                        task.setAssignee(assigneeNode.asText());
                    }

                    task.setCandidateUsers(getValueAsList(PROPERTY_USERTASK_CANDIDATE_USERS, assignmentDefNode));
                    task.setCandidateGroups(getValueAsList(PROPERTY_USERTASK_CANDIDATE_GROUPS, assignmentDefNode));
                    task.setCandidateNotifyUsers(getValueAsList(CANDIDATE_NOTIFY_USERS, assignmentDefNode));

                    if (StringUtils.isNotEmpty(task.getAssignee()) && "$INITIATOR".equalsIgnoreCase(task.getAssignee()) == false) {

                        if (canCompleteTaskNode != null && canCompleteTaskNode.isNull() == false) {
                            addInitiatorCanCompleteExtensionElement(Boolean.valueOf(canCompleteTaskNode.asText()), task);
                        } else {
                            addInitiatorCanCompleteExtensionElement(false, task);
                        }

                    } else if (StringUtils.isNotEmpty(task.getAssignee()) && "$INITIATOR".equalsIgnoreCase(task.getAssignee())) {
                        addInitiatorCanCompleteExtensionElement(true, task);
                    }

                } else if ("idm".equalsIgnoreCase(typeNode.asText())) {
                    JsonNode idmDefNode = assignmentDefNode.get("idm");
                    if (idmDefNode != null && idmDefNode.has("type")) {
                        JsonNode idmTypeNode = idmDefNode.get("type");
                        if (idmTypeNode != null && "user".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("assignee") || idmDefNode.has("assigneeField"))) {

                            fillAssigneeInfo(idmDefNode, canCompleteTaskNode, task);

                        } else if (idmTypeNode != null && "users".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("candidateUsers") || idmDefNode.has("candidateUserFields"))) {

                            fillCandidateUsers(idmDefNode, canCompleteTaskNode, task);

                        } else if (idmTypeNode != null && "groups".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("candidateGroups") || idmDefNode.has("candidateGroupFields"))) {

                            fillCandidateGroups(idmDefNode, canCompleteTaskNode, task);

                        } else {
                            task.setAssignee("$INITIATOR");
                            addExtensionElement("activiti-idm-initiator", String.valueOf(true), task);
                        }
                    }
                }
            }
        }
        convertJsonToFormProperties(elementNode, task);
        return task;
    }
}
