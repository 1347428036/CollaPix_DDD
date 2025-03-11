package com.esmiao.collapix.infrastructure.exception;

import java.util.function.Supplier;

/**
 * The tool for trowing exception.
 *
 * @author Steven Chen
 */
public class ThrowErrorUtil {

    public static void throwEx(ErrorCodeEnum errorCodeEnum, String customMessage) {
        throw new BusinessException(errorCodeEnum.getCode(), customMessage);
    }

    public static void throwIf(boolean condition, ErrorCodeEnum errorCodeEnum, String customMessage) {
        throwIf(condition, () -> new BusinessException(errorCodeEnum.getCode(), customMessage));
    }

    public static void throwIf(boolean condition, ErrorCodeEnum errorCodeEnum) {
        throwIf(condition, () -> new BusinessException(errorCodeEnum));
    }

    public static void throwIf(boolean condition, RuntimeException exception) {
        throwIf(condition, () -> exception);
    }

    /**
     * Throw an exception if the condition is true.
     *
     * @param condition         The condition to check.
     * @param exceptionSupplier The supplier of the exception to throw.
     *                          Using supplier to avoid creating exception
     *                          when the condition is false.
     */
    public static void throwIf(boolean condition, Supplier<RuntimeException> exceptionSupplier) {
        if (condition) {
            throw exceptionSupplier.get();
        }
    }
}
