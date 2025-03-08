package com.esmiao.cloudpicture.infrastructure.utils;

import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.shared.websocket.model.constant.RedisKeyConstant;
import org.springframework.util.DigestUtils;

/**
 * Maintain cache common operations
 * @author Steven Chen
 */
public class CacheUtil {

    private CacheUtil(){}

    public static String buildKey(String prefix, String key) {
        return prefix + RedisKeyConstant.SPLIT + key;
    }

    public static <T> String buildJsonKey(String prefix, T param) {
        String queryCondition = JSONUtil.toJsonStr(param);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());

        return buildKey(prefix, hashKey);
    }
}
