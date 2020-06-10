package com.gogoing.workflow.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.util.StringUtil;
import org.activiti.bpmn.model.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.task.Task;

import java.util.*;

/**
 * @author lhj
 * @version 1.0
 * @description: bpmn工具类
 * @date 2020-5-20 21:23
 */
public class BpmnUtil {

    /**
     * @description: 获取开始节点的下一节点
     * @author lhj
     * @param bpmnModel
     * @return org.activiti.bpmn.model.FlowElement
     * @date 2020-5-20 21:53
     */
    public static FlowElement startEventNextTaskId(BpmnModel bpmnModel){
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        SequenceFlow sequenceFlow = null;
        for (FlowElement flowElement : flowElements) {
            if(flowElement instanceof StartEvent){
                StartEvent startEvent = (StartEvent) flowElement;
                List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
                if(CollectionUtil.isNotEmpty(outgoingFlows)){
                    sequenceFlow = outgoingFlows.get(0);
                    break;
                }
            }
        }
        FlowElement targetFlowElement = null;
        if(sequenceFlow != null){
            targetFlowElement = sequenceFlow.getTargetFlowElement();
        }
        return targetFlowElement;
    }

    /**
     * 获取节点的所有出口
     * @param targetFlowElement
     * @param execution
     * @param flowElement
     * @return
     */
    public static FlowElement querySubmitNextTask(FlowElement targetFlowElement, ExecutionEntityImpl execution, List<FlowElement> flowElement) {
        FlowNode flowNode = (FlowNode) targetFlowElement;
        List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
        for (SequenceFlow sequenceFlow : outgoingFlows) {
            FlowElement outFlowElement = sequenceFlow.getTargetFlowElement();
            String conditionExpression = sequenceFlow.getConditionExpression();
            boolean evaluate;
            if(StringUtil.isEmpty(conditionExpression)){
                evaluate = true;
            }else {
                Expression expression = Context.getProcessEngineConfiguration().getExpressionManager().createExpression(conditionExpression);
                Condition condition = new UelExpressionCondition(expression);
                evaluate = condition.evaluate(sequenceFlow.getId(), execution);
            }
            if(evaluate){
                if(outFlowElement instanceof UserTask) {
                    flowElement.add(outFlowElement);
                }
                if(outFlowElement instanceof Gateway  || !(outFlowElement instanceof UserTask)) {
                    querySubmitNextTask(outFlowElement,execution,flowElement);
                }
            }
        }
        return null;
    }

    /**
     * 处理撤回连线 可能存在分支
     * @param bpmnModel
     * @param taskList
     * @param targetFlowElement
     * @return
     */
    public static Map<String,List<SequenceFlow>> invokeSequenceFlows(BpmnModel bpmnModel , List<Task> taskList, FlowElement targetFlowElement) {
        Map<String,List<SequenceFlow>> flowElements = new HashMap<>(2);
        //并行网关
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.setId("parallelGateway" + targetFlowElement.getId());
        parallelGateway.setBehavior(new ParallelGatewayActivityBehavior());
        List<SequenceFlow> parallelSequenceFlowInCome = new ArrayList<>();
        for (Task task : taskList) {
            //当前节点
            FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
            flowElements.put(currentFlowNode.getId(),currentFlowNode.getOutgoingFlows());

            //重新绘制流程图，从当前节点到到并行网关
            List<SequenceFlow> parallelSequenceFlowList = new ArrayList<>();
            SequenceFlow parallelSequenceFlow = new SequenceFlow();
            parallelSequenceFlow.setId("newSequenceFlowId" + System.currentTimeMillis());
            parallelSequenceFlow.setSourceFlowElement(currentFlowNode);
            parallelSequenceFlow.setTargetFlowElement(parallelGateway);
            parallelSequenceFlowList.add(parallelSequenceFlow);
            parallelSequenceFlowInCome.add(parallelSequenceFlow);
            currentFlowNode.setOutgoingFlows(parallelSequenceFlowList);
        }
        //重新绘制流程图，从并行网关到开始节点
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        //绘制连线，加入流程信息，并组装到流程图
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId" + targetFlowElement.getId());
        newSequenceFlow.setSourceFlowElement(parallelGateway);
        newSequenceFlow.setTargetFlowElement(targetFlowElement);
        newSequenceFlowList.add(newSequenceFlow);
        parallelGateway.setIncomingFlows(parallelSequenceFlowInCome);
        parallelGateway.setOutgoingFlows(newSequenceFlowList);

        return flowElements;
    }
}
