package com.gogoing.workflow.mapper;

import com.gogoing.workflow.domain.ProcessTaskResult;
import com.gogoing.workflow.domain.TaskFinishQuery;
import com.gogoing.workflow.domain.TaskQuery;
import com.gogoing.workflow.domain.TaskUnFinishQuery;
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

    /**
     *  查询待审批任务
     * @param taskFinishQuery 查询条件
     * @return
     */
    List<ProcessTaskResult> selectFinishTask(TaskFinishQuery taskFinishQuery);

    /**
     *  查询待审批任务数量
     * @param taskFinishQuery 查询条件
     * @return
     */
    Long selectFinishTaskCount(TaskFinishQuery taskFinishQuery);

    /**
     *  查询抄送任务
     * @param taskQuery 查询条件
     * @return
     */
    List<ProcessTaskResult> selectNotifyTask(TaskQuery taskQuery);

    /**
     *  查询抄送任务数量
     * @param taskQuery 查询条件
     * @return
     */
    Long selectNotifyTaskCount(TaskQuery taskQuery);
}
