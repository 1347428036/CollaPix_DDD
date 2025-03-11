package com.esmiao.collapix.infrastructure.exception;

import lombok.Getter;

/**
 * Maintain all error code
 * @author Steven Chen
 */

@Getter
public enum ErrorCodeEnum {

    SUCCESS(20000, "ok"),
    PARAMS_ERROR(40000, "Params are wrong"),
    NOT_LOGIN_ERROR(40100, "Not login"),
    NO_PERMISSION_ERROR(40101, "No permission"),
    NOT_FOUND_ERROR(40400, "The data is not exist"),
    FORBIDDEN_ERROR(40300, "No access"),
    SYSTEM_ERROR(50000, "System internal error"),
    OPERATION_ERROR(50001, "Operation failed")
    ;

    /**
     * Status code
     * */
    private final int code;

    /**
     * Status info
     * */
    private final String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
