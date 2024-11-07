package org.lu.zhaodazi.common.exception;

import lombok.Data;

@Data
public class CommonException extends RuntimeException{
    private Integer code;
    public CommonException() {
        super();
    }

    public CommonException(String errorMsg) {
        super(errorMsg);
    }

    public CommonException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.code = errorCode;
    }

    public CommonException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.code = errorCode;
    }
    public CommonException(ErrorEnum error) {
        super(error.getMsg());
        this.code = error.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
