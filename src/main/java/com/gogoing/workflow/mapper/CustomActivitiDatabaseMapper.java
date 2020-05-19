package com.gogoing.workflow.mapper;

import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskUnFinishQuery;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 自定义数据查询接口
 * @author lhj
 * @since 2020/3/25 16:08
 */
@Mapper
public interface CustomActivitiDatabaseMapper {

    /**
     * 新增
     * @param identityInfoEntity
     * @return
     */
    int insertIdentityInfoByNotify(IdentityLinkEntityImpl identityInfoEntity);

    /**
     *  查询待审批任务
     * @param taskUnFinishQuery 查询条件
     * @return
     */
    List<ProcessTaskResult> selectUnFinishTask(TaskUnFinishQuery taskUnFinishQuery);

    /**
     *  查询待审批任务数量
     * @param taskUnFinishQuery 查询条件
     * @return
     */
    Long selectUnFinishTaskCount(TaskUnFinishQuery taskUnFinishQuery);
}
