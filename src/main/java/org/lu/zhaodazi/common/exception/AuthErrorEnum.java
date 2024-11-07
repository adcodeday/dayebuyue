package org.lu.zhaodazi.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthErrorEnum implements ErrorEnum{
    EMAIL_LOGIN_ERROR(-1,"邮箱验证码错误"),
    ;
    private final Integer code;
    private final String msg;
}
