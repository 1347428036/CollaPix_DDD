package com.esmiao.collapix.infrastructure.utils;


import com.esmiao.collapix.infrastructure.common.CommonResponse;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;

/**
 * A global response tool.
 * @author Steven Chen
 */
public class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> CommonResponse<T> success(T data) {
        return buildResponse(ErrorCodeEnum.SUCCESS, data);
    }

    public static <T> CommonResponse<T> error(int code, String message) {
        return buildResponse(code, null, message);
    }

    public static <T> CommonResponse<T> error(ErrorCodeEnum code) {
        return buildResponse(code);
    }

    public static <T> CommonResponse<T> buildResponse(int code, T data, String message) {
        return new CommonResponse<>(code, data, message);
    }

    public static <T> CommonResponse<T> buildResponse(ErrorCodeEnum code, T data, String message) {
        return new CommonResponse<>(code.getCode(), data, message);
    }

    public static <T> CommonResponse<T> buildResponse(ErrorCodeEnum code, T data) {
        return new CommonResponse<>(code.getCode(), data, code.getMessage());
    }

    public static <T> CommonResponse<T> buildResponse(ErrorCodeEnum code) {
        return new CommonResponse<>(code.getCode(), null, code.getMessage());
    }
}
