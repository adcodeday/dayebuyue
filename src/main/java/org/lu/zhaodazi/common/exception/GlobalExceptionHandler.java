package org.lu.zhaodazi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.common.domain.vo.res.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 校验参数异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<?> constraintViolationException(ConstraintViolationException e){
        String message = e.getMessage();
        log.info("参数有效性检查未通过：{}",message);
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getCode(),message);

    }

    @ExceptionHandler(CommonException.class)
    public ApiResult<?> commonException(CommonException e){
        String message =e.getMessage();
        log.info(message);
        return ApiResult.fail(AuthErrorEnum.EMAIL_LOGIN_ERROR.getCode(),message);

    }
}
