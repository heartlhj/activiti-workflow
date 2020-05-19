package com.gogoing.workflow.bpmn.builder;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.util.StringUtil;
import com.gogoing.workflow.bpmn.model.CustomUserTask;
import com.gogoing.workflow.domain.CandidateParam;
import com.gogoing.workflow.domain.ExecutionListenerParam;
import com.gogoing.workflow.domain.ProcessCreateDefineParam;
import com.gogoing.workflow.domain.ProcessCreateTaskParam;
import com.gogoing.workflow.enums.CandidateTypeEnum;
import com.gogoing.workflow.enums.ProcessTaskAuditTypeEnum;
import com.gogoing.workflow.exception.ProcessException;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/***
 * @description 模型组装
 * @author LHJ
 * @date 2020/3/13 16:24
 */
@Slf4j
public class BpmnBuilder {

    private final String ASSIGNEE = "assignee";

    private final String ASSIGNEE_LIST = "assigneeList";

    private final String ASSIGNEE_EXP = "${" + ASSIGNEE + "}";

    private List<Event> event = new ArrayList<>();

    private List<Task> task = new ArrayList<>();

    private List<Gateway> gateway = new ArrayList<>();

    private List<SequenceFlow> sequenceFlow = new ArrayList<>();

    private List<SubProcess> subProcesses = new ArrayList<>();

    private ProcessCreateDefineParam processDefine;

    public BpmnBuilder(ProcessCreateDefineParam processDefine) {
        this.processDefine = processDefine;
    }


    public BpmnModel build() {
        createElement(this.processDefine);

        BpmnModel model = new BpmnModel();
        Process process = new Process();
        process.setName(this.processDefine.getProcessName());
        process.setId(this.processDefine.getProcessKey());
        builderProcess(this.event, process);
        builderProcess(this.task, process);
        builderProcess(this.gateway, process);
        builderProcess(this.sequenceFlow, process);
        builderProcess(this.subProcesses, process);
        model.addProcess(process);
        //自动生成坐标
        new BpmnAutoLayout(model).execute();
        return model;
    }


    /**
     * 建造流程定义信息
     * @param processDefine
     */
    private void createElement(ProcessCreateDefineParam processDefine) {
        //组装开始事件（表单信息）
        createStartEventElement(processDefine);
        //组装结束事件
        createEndEventElement(processDefine);
        //组装节点任务（表单信息，处理多实例的生成规则）
        createUserTaskElement(processDefine);
        //组装连线
        createSequenceFlowElement(processDefine);


    }


    /**
     * 建造开始事件节点
     * @param processDefine
     */
    private void createStartEventElement(ProcessCreateDefineParam processDefine) {

        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        startEvent.setName("开始");
        //设置流程发起人，也就是权限所属人
        startEvent.setInitiator("initiator");
        startEvent.setFormKey(processDefine.getFormKey());
        this.event.add(startEvent);
    }


    /**
     * 建造结束事件节点
     * @param processDefine
     */
    private void createEndEventElement(ProcessCreateDefineParam processDefine) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        endEvent.setName("结束");
        this.event.add(endEvent);

    }


    /**
     * 建造用户任务节点
     * @param processDefine
     */
    private void createUserTaskElement(ProcessCreateDefineParam processDefine) {

        List<ProcessCreateTaskParam> processNode = processDefine.getProcessNode();
        if (CollectionUtil.isNotEmpty(processNode)) {
            for (ProcessCreateTaskParam node : processNode) {

                String category = node.getCategory();
                UserTask taskNode =  baseTask(node);
                //签收类型
                if (StringUtil.isNotEmpty(category)) {
                    switch (Objects.requireNonNull(ProcessTaskAuditTypeEnum.gain(category))) {
                        case NOTIFY:
                            taskNode.setTaskListeners(taskListener(node));
                            break;
                        case OR_SIGN:
                            orSign(taskNode,node);
                            break;
                        case COUNTERSIGN:
                            countersign(taskNode,node);
                            break;
                        default:
                            throw new ProcessException("不支持的类型");
                    }
                } else {
                    //默认为会签
                    countersign(taskNode,node);
                }
                this.task.add(taskNode);
            }
        }
    }


    /**
     * 任务基本信息
     * @param node
     * @return
     */
    private UserTask baseTask(ProcessCreateTaskParam node){
        CustomUserTask taskNode = new CustomUserTask();
        //拼接字母是因为activiti不允许ID有数字开头
        taskNode.setId("t-"+node.getOrderNum());
        taskNode.setName(node.getName());
        taskNode.setDueDate(node.getDueDate());
        taskNode.setPriority(node.getPriority());
        taskNode.setCategory(node.getCategory());
        //审批人集合
        List<CandidateParam> candidatePosts = node.getCandidateParams();

        if(CollectionUtil.isNotEmpty(candidatePosts)){
            //用户集合
            List<String> candidateUsers = candidatePosts.stream().filter(item->item.getType().equals(CandidateTypeEnum.USER.getCode())).map(CandidateParam::getId).collect(Collectors.toList());
            List<String> candidateGroup = candidatePosts.stream().filter(item->item.getType().equals(CandidateTypeEnum.GROUP.getCode())).map(CandidateParam::getId).collect(Collectors.toList());
            List<String> candidateNotify = candidatePosts.stream().filter(item->item.getType().equals(CandidateTypeEnum.NOTIFY.getCode())).map(CandidateParam::getId).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(candidateUsers)){
                taskNode.setCandidateUsers(candidateUsers);
            }
            if(CollectionUtil.isNotEmpty(candidateGroup)){
                taskNode.setCandidateGroups(candidateGroup);
            }
            if(CollectionUtil.isNotEmpty(candidateNotify)){
                taskNode.setCandidateNotifyUsers(candidateNotify);
            }
        }

        String assignees = node.getAssignee();
        if(StringUtil.isNotEmpty(assignees)){
            taskNode.setAssignee(assignees);
        }
        return taskNode;
    }

    /**
     * 创建连线
     * @param processDefine
     */
    private void createSequenceFlowElement(ProcessCreateDefineParam processDefine) {
        List<ProcessCreateTaskParam> processNode = processDefine.getProcessNode();
        String sourceRef = "startEvent";
        String endRef = "endEvent";
        if(CollectionUtil.isNotEmpty(processNode)){
            List<Integer> collect = processNode.stream().map(ProcessCreateTaskParam::getOrderNum).sorted().collect(Collectors.toList());
            for(Integer num : collect){
                String tartgetRef = "t-"+num;
                SequenceFlow sequenceFlow = new SequenceFlow(sourceRef,tartgetRef);
                sequenceFlow.setId("t"+ UUID.randomUUID().toString().replace("-",""));
                sourceRef = tartgetRef;
                this.sequenceFlow.add(sequenceFlow);
            }
            this.sequenceFlow.add(new SequenceFlow(sourceRef,endRef));
        }else{
            this.sequenceFlow.add(new SequenceFlow(sourceRef,endRef));
        }
    }


    /**
     * 或签，不设置监听器
     * @param taskNode
     */
    private void orSign(UserTask taskNode,ProcessCreateTaskParam node) {
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setLoopCardinality("1");
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfActiveInstances == 1}");
        multiInstanceLoopCharacteristics.setSequential(false);
        List<ActivitiListener> taskListeners = taskListener(node);
        if(CollectionUtil.isNotEmpty(taskListeners)){
            taskNode.setExecutionListeners(taskListeners);
        }
        taskNode.setLoopCharacteristics(multiInstanceLoopCharacteristics);
    }
    /**
     * 会签
     * @param taskNode
     */
    private void countersign(UserTask taskNode,ProcessCreateTaskParam node) {
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setInputDataItem(ASSIGNEE_LIST);
        multiInstanceLoopCharacteristics.setElementVariable(ASSIGNEE);
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfActiveInstances == nrOfInstances}");
        multiInstanceLoopCharacteristics.setSequential(false);
        taskNode.setAssignee(ASSIGNEE_EXP);
        taskNode.setLoopCharacteristics(multiInstanceLoopCharacteristics);
        List<ActivitiListener> taskListeners = taskListener(node);
        if(CollectionUtil.isNotEmpty(taskListeners)){
            taskNode.setExecutionListeners(taskListeners);
        }
    }

    /**
     * 审批监听器
     * @return
     */
    private List<ActivitiListener> taskListener(ProcessCreateTaskParam node){
        List<ExecutionListenerParam> executionListenerParams = node.getExecutionListenerParams();
        if(CollectionUtil.isNotEmpty(executionListenerParams)){
            ArrayList<ActivitiListener> listener = new ArrayList<>();
            for (ExecutionListenerParam executionListenerParam : executionListenerParams) {
                ActivitiListener activitiListener = taskBaseListener(
                        executionListenerParam.getEvent(),executionListenerParam.getBean(),executionListenerParam.getImplType());
                listener.add(activitiListener);
            }
            return listener;
        }
        return null;
    }


    /**
     * 基础任务监听
     * @param event 事件类型
     * @param bean   springbean 名称
     * @param implType 实现枚举
     * @return list监听
     */
    private ActivitiListener taskBaseListener(String event, String bean, String implType) {
        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent(event);
        activitiListener.setImplementationType(implType);
        if(!bean.startsWith("${")){
            bean = "${" + bean + "}";
        }
        activitiListener.setImplementation(bean);
        return activitiListener;
    }

    /**
     * 给流程添加基础元素信息
     * @param element
     * @param process
     */
    private void builderProcess(List<? extends FlowElement> element, Process process) {
        if (CollectionUtil.isNotEmpty(element)) {
            for (FlowElement el : element) {
                process.addFlowElement(el);
            }
        }
    }

}
