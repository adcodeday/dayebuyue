package org.lu.zhaodazi.common.domain.vo.res;

import lombok.Data;
import org.lu.zhaodazi.common.exception.ErrorEnum;

@Data
public class ApiResult<T> {
    private Boolean success;
    private Integer errCode;
    private String errMsg;
    private T data;

    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(null);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(data);
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(code);
        result.setErrMsg(msg);
        return result;
    }

    public static <T> ApiResult<T> fail(ErrorEnum errorEnum) {
        ApiResult<T> result = new ApiResult<T>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(errorEnum.getCode());
        result.setErrMsg(errorEnum.getMsg());
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }
}