package com.esmiao.collapix.infrastructure.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Cache context based on Caffeine
 *
 * @author Steven Chen
 */
@Component
public class CaffeineCacheContext<K, R> implements CacheContext<K, R> {

    private final Cache<K, R> cache = Caffeine.newBuilder()
        .initialCapacity(1024)
        .maximumSize(10000L)
        .expireAfterWrite(5L, TimeUnit.MINUTES)
        .build();

    @Override
    public R get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void set(K key, R value, TimeUnit timeUnit, long timeout) {
        cache.put(key, value);
    }
}
