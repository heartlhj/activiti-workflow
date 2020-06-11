package com.gogoing.workflow.bpmn.entity;

import com.gogoing.workflow.constant.ProcessConstants;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityManagerImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.data.IdentityLinkDataManager;

/**
 * @author lhj
 * @version 1.0
 * @description: 自定义身份信息保存
 * @date 2020-6-11 9:51
 */
public class CustomIdentityLinkEntityManagerImpl extends IdentityLinkEntityManagerImpl {

    public CustomIdentityLinkEntityManagerImpl(ProcessEngineConfigurationImpl processEngineConfiguration, IdentityLinkDataManager identityLinkDataManager) {
        super(processEngineConfiguration, identityLinkDataManager);
    }

    @Override
    public IdentityLinkEntity addIdentityLink(TaskEntity taskEntity, String userId, String groupId, String type) {
        IdentityLinkEntity identityLinkEntity = (IdentityLinkEntity)this.identityLinkDataManager.create();
        taskEntity.getIdentityLinks().add(identityLinkEntity);
        identityLinkEntity.setTask(taskEntity);
        identityLinkEntity.setUserId(userId);
        identityLinkEntity.setGroupId(groupId);
        if (ProcessConstants.NOTIFY.equals(type)) {
            identityLinkEntity.setProcessInstanceId(taskEntity.getProcessInstanceId());
        }
        identityLinkEntity.setType(type);
        this.insert(identityLinkEntity);
        if (userId != null && taskEntity.getProcessInstanceId() != null) {
            this.involveUser(taskEntity.getProcessInstance(), userId, "participant");
        }

        return identityLinkEntity;
    }

}
