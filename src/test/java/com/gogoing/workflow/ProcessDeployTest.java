package com.gogoing.workflow;

import com.gogoing.workflow.bpmn.model.CustomUserTask;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lhj
 * @version 1.0
 * @description: 流程部署测试
 * @date 2020-5-20 22:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ProcessDeployTest {

	private static String PATH = "leave.bpmn20.xml";

	@Resource
	private RepositoryService repositoryService;

	//流程模型对象
	private BpmnModel model;

	/**
	 * 通过模型部署
	 */
	@Test
	public void deployByObject() {
		//部署流程
		Deployment deploy = repositoryService.createDeployment().
				addBpmnModel("test20120.bpmn", model).key("test20120").name("测试").deploy();
		log.info("通过模型部署成功，部署ID为："+deploy.getId());

	}

	/**
	 * 通过文件部署流程
	 */
	@Test
	public void deployByFile() {
		Deployment deploy1 = repositoryService.createDeployment().addClasspathResource(PATH).key("leave").name("请假流程")
				.deploy();
		log.info("通过BPMN文件部署成功，部署ID为："+deploy1.getId());
	}
	/**
	 * 创建模型
	 * @return
	 */
	@Before
	public void createProcess() {
		model = new BpmnModel();
		//流程对象
		Process process = new Process();
		//流程定义KEY,流程发起时可根据key启动一条流程
		process.setId("test");
		//流程名称
		process.setName("测试");
		//流程元素，包括节点、连线
		process.addFlowElement(createStartEventElement());
		process.addFlowElement(createEndEventElement());
		process.addFlowElement(createUserTaskElement());
		List<SequenceFlow> sequenceFlowElement = createSequenceFlowElement();
		for (SequenceFlow sequenceFlow : sequenceFlowElement) {
			process.addFlowElement(sequenceFlow);
		}
		model.addProcess(process);
		//自动生成坐标
		new BpmnAutoLayout(model).execute();
	}

	/**
	 * 建造开始事件节点
	 */
	private StartEvent createStartEventElement() {
		StartEvent startEvent = new StartEvent();
		startEvent.setId("startEvent");
		startEvent.setName("开始");
		//设置流程发起人，也就是权限所属人
		startEvent.setInitiator("initiator");
		startEvent.setFormKey("start");
		return startEvent;
	}

	/**
	 * 建造结束事件节点
	 */
	private EndEvent createEndEventElement() {
		EndEvent endEvent = new EndEvent();
		endEvent.setId("endEvent");
		endEvent.setName("结束");
		return endEvent;
	}

	/**
	 * 建造用户任务节点，该节点为审批节点
	 */
	private UserTask createUserTaskElement() {
		CustomUserTask taskNode =  new CustomUserTask();
		taskNode.setId("task");
		taskNode.setName("用户节点");
		//设置审批人
		taskNode.setAssignee("lisi");

		//设置抄送人
		List<String> candidateUsers = new ArrayList();
		candidateUsers.add("liusi");
		candidateUsers.add("li");
		taskNode.setCandidateUsers(candidateUsers);

		//设置抄送人
		List<String> candidateNotifyUsers = new ArrayList();
		candidateNotifyUsers.add("wangwu");
		candidateNotifyUsers.add("zhangsan");
		taskNode.setCandidateNotifyUsers(candidateNotifyUsers);
		return taskNode;
	}

	/**
	 * 创建连线
	 */
	private List<SequenceFlow> createSequenceFlowElement() {
		List<SequenceFlow> processNode = new ArrayList<>();
		SequenceFlow sequenceFlow1 = new SequenceFlow("startEvent"/*开始的元素ID*/,"task"/*结束的元素ID*/);
		SequenceFlow sequenceFlow2 = new SequenceFlow("task","endEvent");
		processNode.add(sequenceFlow1);
		processNode.add(sequenceFlow2);
		return processNode;
	}
}
