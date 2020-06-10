package com.gogoing.workflow.domain;

import lombok.Data;
import org.activiti.bpmn.model.ImplementationType;

/***
 * @description 节点监听类 会签，或签将审批人集合在实现类放入{assigneeList} 变量
 * @author LHJ
 * @date 2020/3/16 10:03
 */
@Data
public class ExecutionListenerParam extends AbstractParam {
    /**
     * {@link ExecutionListenerEventEnum.EVENT_NAME_START} 节点开始创建
     * {@link ExecutionListenerEventEnum.EVENT_NAME_TASK}  监听连线，当连线到达节点
     * {@link ExecutionListenerEventEnum.EVENT_NAME_END}  节点结束
     */
    private String event;
    /**
     * 方法或bean名称
     */
    private String bean;
    /**
     * {@link ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION } java类的全路径,类需实现 {@link TaskListener}，会执行notify(）方法， {@link bean}为${com.gogogoing.workflow.Test}.这种实现类不能注入Spring bean
     * {@link ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION } 执行特定类的特定方法 配置 {@link bean}为${test.test()}
     * {@link ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION } 配置spring的bean 实现 {@link ExecutionListener} {@link bean}为${test}。实现类交由spring管理。
     */
    private String implType;


}
