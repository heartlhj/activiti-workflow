# 流程引擎

------

在 **activiti 6.0.0** 提供接口的基础上进行封装和扩展，支持通用审批流。项目可直接投入使用。 

提供的功能有：
> * 流程发起
> * 流程实例查询
> * 流程审批
> * 流程驳回
> * 流程撤回
> * 流程终止

扩展支持节点抄送，通过重写**UserTaskActivityBehavior**的handleAssignments将抄送用户存入数据库。

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


通过swagger进行接口管理，项目启动后，通过访问：http://ip:port/swagger-ui.html

