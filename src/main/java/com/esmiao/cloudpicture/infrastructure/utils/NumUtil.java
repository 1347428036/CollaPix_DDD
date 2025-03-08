package com.esmiao.cloudpicture.infrastructure.utils;

import cn.hutool.core.util.StrUtil;

/**
 * Number tool
 * @author Steven Chen
 */
public class NumUtil {

    public static Long parseLong(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }

        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String parseString(Long num) {
        return num != null ? String.valueOf(num) : null;
    }
}
