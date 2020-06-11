

package com.gogoing.workflow.bpmn.model;

import org.activiti.bpmn.model.UserTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 自定义用户节点
 * @author lhj
 * @param
 * @return
 * @date 2020-6-11 10:50
 */
public class CustomUserTask extends UserTask {

    //抄送用户
    protected List<String> candidateNotifyUsers = new ArrayList();

    public List<String> getCandidateNotifyUsers() {
        return candidateNotifyUsers;
    }

    public void setCandidateNotifyUsers(List<String> candidateNotifyUsers) {
        this.candidateNotifyUsers = candidateNotifyUsers;
    }

    public CustomUserTask clone() {
        CustomUserTask clone = new CustomUserTask();
        clone.setValues(this);
        return clone;
    }
    public void setValues(CustomUserTask otherElement) {
        super.setValues(otherElement);
        this.setCandidateNotifyUsers(otherElement.getCandidateNotifyUsers());
    }
}
