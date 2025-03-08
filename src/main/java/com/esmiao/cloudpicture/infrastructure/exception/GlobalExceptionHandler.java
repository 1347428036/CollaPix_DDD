package com.esmiao.cloudpicture.infrastructure.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.esmiao.cloudpicture.infrastructure.common.CommonResponse;
import com.esmiao.cloudpicture.infrastructure.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Steven Chen
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public CommonResponse<?> notLoginException(NotLoginException e) {
        log.error("NotLoginException", e);
        return ResponseUtil.error(ErrorCodeEnum.NOT_LOGIN_ERROR);
    }

    @ExceptionHandler(NotPermissionException.class)
    public CommonResponse<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return ResponseUtil.error(ErrorCodeEnum.NO_PERMISSION_ERROR);
    }


    @ExceptionHandler(BusinessException.class)
    public CommonResponse<?> handleBusinessException(BusinessException e) {
        log.error("BusinessException", e);
        return ResponseUtil.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<?> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResponseUtil.error(ErrorCodeEnum.SYSTEM_ERROR);
    }
}
