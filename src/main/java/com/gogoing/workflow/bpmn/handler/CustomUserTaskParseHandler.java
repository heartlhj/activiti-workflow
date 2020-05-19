
package com.gogoing.workflow.bpmn.handler;

import com.gogoing.workflow.bpmn.behavior.CustomUserTaskActivityBehavior;
import com.gogoing.workflow.bpmn.model.CustomUserTask;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.AbstractActivityBpmnParseHandler;

/**
 * 用户节点处理器
 * @author lhj
 */
public class CustomUserTaskParseHandler extends AbstractActivityBpmnParseHandler<CustomUserTask> {

  public Class<? extends BaseElement> getHandledType() {
    return CustomUserTask.class;
  }

  @Override
  protected void executeParse(BpmnParse bpmnParse, CustomUserTask userTask) {
    userTask.setBehavior(new CustomUserTaskActivityBehavior(userTask));
  }

}
