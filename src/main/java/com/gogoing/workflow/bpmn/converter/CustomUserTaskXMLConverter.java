
package com.gogoing.workflow.bpmn.converter;

import com.gogoing.workflow.bpmn.model.CustomAlfrescoUserTask;
import com.gogoing.workflow.bpmn.model.CustomUserTask;
import org.activiti.bpmn.converter.UserTaskXMLConverter;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义解析BPMN的XML
 * @author lhj
 */
public class CustomUserTaskXMLConverter extends UserTaskXMLConverter {
  public static final String ATTRIBUTE_TASK_USER_CANDIDATE_NOTIFY_USERS = "candidateNotifyUsers";


  /**
   * 用户节点默认通用属性
   */
  protected static final List<ExtensionAttribute> defaultUserTaskAttributes = Arrays.asList(
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_FORM_FORMKEY),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_DUEDATE),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_BUSINESS_CALENDAR_NAME),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_ASSIGNEE),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_OWNER),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_PRIORITY),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEUSERS),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEGROUPS),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CATEGORY),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SERVICE_EXTENSIONID),
          //设置抄送为默认通用标签
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATE_NOTIFY_USERS),
          new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_SKIP_EXPRESSION)
  );


  public Class<? extends BaseElement> getBpmnElementType() {
    return CustomUserTask.class;
  }
  
  @Override
  protected String getXMLElementName() {
    return ELEMENT_TASK_USER;
  }
  
  @Override
  protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
    String formKey = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_FORM_FORMKEY);
    CustomUserTask userTask = null;
    if (StringUtils.isNotEmpty(formKey)) {
      if (model.getUserTaskFormTypes() != null && model.getUserTaskFormTypes().contains(formKey)) {
        userTask = new CustomAlfrescoUserTask();
      }
    }
    if (userTask == null) {
      userTask = new CustomUserTask();
    }
    BpmnXMLUtil.addXMLLocation(userTask, xtr);
    userTask.setDueDate(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_DUEDATE));
    userTask.setBusinessCalendarName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_BUSINESS_CALENDAR_NAME));
    userTask.setCategory(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CATEGORY));
    userTask.setFormKey(formKey);
    userTask.setAssignee(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_ASSIGNEE)); 
    userTask.setOwner(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_OWNER));
    userTask.setPriority(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_PRIORITY));
    if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEUSERS))) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEUSERS);
      userTask.getCandidateUsers().addAll(parseDelimitedList(expression));
    } 
    
    if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEGROUPS))) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATEGROUPS);
      userTask.getCandidateGroups().addAll(parseDelimitedList(expression));
    }

    if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATE_NOTIFY_USERS))) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_CANDIDATE_NOTIFY_USERS);
      userTask.getCandidateNotifyUsers().addAll(parseDelimitedList(expression));
    }
    
    userTask.setExtensionId(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_SERVICE_EXTENSIONID));

    if (StringUtils.isNotEmpty(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_SKIP_EXPRESSION))) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, ATTRIBUTE_TASK_USER_SKIP_EXPRESSION);
      userTask.setSkipExpression(expression);
    }

    BpmnXMLUtil.addCustomAttributes(xtr, userTask, defaultElementAttributes,
        defaultActivityAttributes, defaultUserTaskAttributes);

    parseChildElements(getXMLElementName(), userTask, childParserMap, model, xtr);
    
    return userTask;
  }


  @Override
  @SuppressWarnings("unchecked")
  protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
    CustomUserTask userTask = (CustomUserTask) element;
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_ASSIGNEE, userTask.getAssignee(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_OWNER, userTask.getOwner(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_CANDIDATEUSERS, convertToDelimitedString(userTask.getCandidateUsers()), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_CANDIDATEGROUPS, convertToDelimitedString(userTask.getCandidateGroups()), xtw);
    //设置抄送标签属性
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_CANDIDATE_NOTIFY_USERS, convertToDelimitedString(userTask.getCandidateNotifyUsers()), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_DUEDATE, userTask.getDueDate(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_BUSINESS_CALENDAR_NAME, userTask.getBusinessCalendarName(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_TASK_USER_CATEGORY, userTask.getCategory(), xtw);
    writeQualifiedAttribute(ATTRIBUTE_FORM_FORMKEY, userTask.getFormKey(), xtw);
    if (userTask.getPriority() != null) {
      writeQualifiedAttribute(ATTRIBUTE_TASK_USER_PRIORITY, userTask.getPriority().toString(), xtw);
    }
    if (StringUtils.isNotEmpty(userTask.getExtensionId())) {
      writeQualifiedAttribute(ATTRIBUTE_TASK_SERVICE_EXTENSIONID, userTask.getExtensionId(), xtw);
    }
    if (userTask.getSkipExpression() != null) {
      writeQualifiedAttribute(ATTRIBUTE_TASK_USER_SKIP_EXPRESSION, userTask.getSkipExpression(), xtw);
    }
    // write custom attributes
    BpmnXMLUtil.writeCustomAttributes(userTask.getAttributes().values(), xtw, defaultElementAttributes,
        defaultActivityAttributes, defaultUserTaskAttributes);
  }

}
