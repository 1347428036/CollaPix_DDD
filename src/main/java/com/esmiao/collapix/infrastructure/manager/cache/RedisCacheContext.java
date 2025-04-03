package com.esmiao.collapix.infrastructure.manager.cache;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Cache context based on Redis
 *
 * @author Steven Chen
 */
@RequiredArgsConstructor
@Component
public class RedisCacheContext<K, R> implements CacheContext<K, R> {

    @Resource(name = "redisTemplate")
    private final RedisTemplate<K, R> redisTemplate;

    @Override
    public R get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(K key, R value, TimeUnit timeUnit, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
}
