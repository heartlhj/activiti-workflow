package com.gogoing.workflow.constant;

/**
 * 流程常量
 * @author lhj
 * @since 2020/5/6 18:41
 */
public final class ProcessConstants {
    private ProcessConstants() {
    }
    public static final String MODEL = "model";
    public static final String MODEL_ID = "modelId";
    public static final String MODEL_NAME = "name";
    public static final String MODEL_REVISION = "revision";
    public static final String MODEL_DESCRIPTION = "description";
    public static final String MODEL_KEY = "key";
    public static final String NOTIFY = "notify";

    /**
     * 部署流程的bpmn文件的后缀
     */
    public static final String RESOURCE_NAME_SUFFIX = ".bpmn";

    /**
     * UTF-8的字符验证码
     */
    public static final String COMMON_CHARACTER_ENCODING_UTF_8 = "UTF-8";
}
