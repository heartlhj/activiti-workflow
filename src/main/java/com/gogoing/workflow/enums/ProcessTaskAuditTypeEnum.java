package com.gogoing.workflow.enums;

/**
 * 节点类型枚举
 * 类型说明：
 * 1.审批 没有驳回，只有通过
 * 2.审核 可驳回、可通过
 * 3.知会 不影响任何流程，仅被知会人确定已经查看
 * @author chenling
 * @date 2019/11/27 21:32
 * @since V1.0.0
 */
public enum ProcessTaskAuditTypeEnum {

    COUNTERSIGN("1","会签审批"),
    OR_SIGN("2","或签审批"),
    NOTIFY("3","知会");


    private String type;

    private String desc;

    ProcessTaskAuditTypeEnum(String code, String desc) {
        this.type = code;
        this.desc = desc;
    }

    public String getCode() {
        return type;
    }

    public String getDesc() {
        return desc;
    }


    public static ProcessTaskAuditTypeEnum gain(String code) {
        if (code == null) {
            return null;
        }
        for (ProcessTaskAuditTypeEnum type : ProcessTaskAuditTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }



}
