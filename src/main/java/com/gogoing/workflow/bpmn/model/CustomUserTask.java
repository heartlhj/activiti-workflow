

package com.gogoing.workflow.bpmn.model;

import org.activiti.bpmn.model.UserTask;

import java.util.ArrayList;
import java.util.List;

public class CustomUserTask extends UserTask {

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
