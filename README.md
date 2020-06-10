# 流程引擎

项目还在逐步完善中，感兴趣的伙伴欢迎来纠错。

**项目介绍**

在 **activiti 6.0.0** 提供接口的基础上进行封装和扩展，支持通用审批流。项目可直接投入使用。 

提供的功能有：
> * 流程发起
> * 流程实例查询
> * 流程审批
> * 流程驳回
> * 流程撤回
> * 流程终止
> * 列表查询支持分页

自定义BPMN标签：参考博客[activiti自定义标签][1]


流程驳回：参考博客[activiti流程驳回][2]

驳回过程

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9vc2NpbWcub3NjaGluYS5uZXQvb3NjbmV0L3VwLTEyMjAyZGZiYzA3ODU3ZTRiOWEzM2RlYzI1NmQ5YjI5Yjc0LnBuZw?x-oss-process=image/format,png)

自定义完成的方法，完成时不执行流程事件，使用于流程驳回，撤销操作，参考[CustomTaskCompleteCmd](https://github.com/heartlhj/activiti-workflow/blob/master/src/main/java/com/gogoing/workflow/cmd/CustomTaskCompleteCmd.java)类。

扩展支持节点抄送，通过重写**UserTaskActivityBehavior**的handleAssignments将抄送用户存入数据库。具体修改代码位于[com.gogoing.workflow.bpmn][3]。

```java
protected void handleAssignments(TaskEntityManager taskEntityManager, String assignee, String owner, List<String> candidateUsers,
                                   List<String> candidateGroups,List<String> candidateNotifyUsers, TaskEntity task, ExpressionManager expressionManager, DelegateExecution execution) {

    //省略部门代码
    //保存抄送用户
    if (candidateNotifyUsers != null && !candidateNotifyUsers.isEmpty()) {
      for (String notify : candidateNotifyUsers) {
        Expression userIdExpr = expressionManager.createExpression(notify);
        Object value = userIdExpr.getValue(execution);
        if (value instanceof String) {
          List<String> userIds = extractCandidates((String) value);
          for (String userId : userIds) {
            Context.getCommandContext().getIdentityLinkEntityManager().addUserIdentityLink(task, userId, NOTIFY);
          }
        } else if (value instanceof Collection) {
          Iterator userIdSet = ((Collection) value).iterator();
          while (userIdSet.hasNext()) {
            Context.getCommandContext().getIdentityLinkEntityManager().addUserIdentityLink(task, (String)userIdSet.next(), NOTIFY);
          }
          throw new ActivitiException("Expression did not resolve to a string or collection of strings");
        }
      }
    }

  }
```

查询待审批任务同时返回的抄送的任务，参考ProcessTaskService#queryUnFinishTask


封装列表查询工具类[PageUtil][4]，可直接使用。

通过swagger进行接口管理，项目启动后，通过访问：http://ip:8888/swagger-ui.html

**配置文件说明**

```java

#用到了liquibse来初始化数据库
spring.liquibase.enabled = false 
#true数据库没有表时会自动建表
spring.activiti.database-schema-update = true
#是否校验流程文件，默认校验resources下的processes文件夹里的流程文件
spring.activiti.check-process-definitions = false
#自定义流程文件位置
spring.activiti.process-definition-location-prefix = /processes/
#打印流程引擎数据库日志
logging.level.org.activiti.engine.impl.persistence.entity = trace
#是否开发定时任务
spring.activiti.async-executor-activate = true
```


  [1]: https://blog.csdn.net/qq_34758074/article/details/106356127
  [2]: https://blog.csdn.net/qq_34758074/article/details/106365223
  [3]: https://github.com/heartlhj/activiti-workflow/tree/master/src/main/java/com/gogoing/workflow/bpmn
  [4]: https://github.com/heartlhj/activiti-workflow/blob/master/src/main/java/com/gogoing/workflow/utils/PageUtil.java