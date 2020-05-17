
package com.gogoing.workflow.exception;

import com.gogoing.workflow.enums.BaseEnumType;

import java.io.Serializable;
import java.text.MessageFormat;

public class ProcessException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -1354043731046864103L;
    private String code;

    public ProcessException() {
    }

    public ProcessException(String msg) {
        super(msg);
        this.code = "500";
    }

    public ProcessException(BaseEnumType baseEnumType) {
        super(baseEnumType.getMsg());
        this.code = baseEnumType.getCode();
    }

    public ProcessException(String msg, Object... arguments) {
        super(MessageFormat.format(msg, arguments));
        this.code = "500";
    }

    public ProcessException(BaseEnumType baseEnumType, Object... arguments) {
        super(MessageFormat.format(baseEnumType.getMsg(), arguments));
        this.code = baseEnumType.getCode();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
