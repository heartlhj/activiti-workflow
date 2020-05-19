
package com.gogoing.workflow.bpmn.model;

/**
 * CustomUserTask软连接
 */
public class CustomAlfrescoUserTask extends CustomUserTask {
    public static final String ALFRESCO_SCRIPT_TASK_LISTENER = "org.alfresco.repo.workflow.activiti.tasklistener.ScriptTaskListener";
    protected String runAs;
    protected String scriptProcessor;

    public CustomAlfrescoUserTask() {
    }

    public String getRunAs() {
        return this.runAs;
    }

    public void setRunAs(String runAs) {
        this.runAs = runAs;
    }

    public String getScriptProcessor() {
        return this.scriptProcessor;
    }

    public void setScriptProcessor(String scriptProcessor) {
        this.scriptProcessor = scriptProcessor;
    }

    public CustomAlfrescoUserTask clone() {
        CustomAlfrescoUserTask clone = new CustomAlfrescoUserTask();
        clone.setValues(this);
        return clone;
    }

    public void setValues(CustomAlfrescoUserTask otherElement) {
        super.setValues(otherElement);
        this.setRunAs(otherElement.getRunAs());
        this.setScriptProcessor(otherElement.getScriptProcessor());
    }
}
