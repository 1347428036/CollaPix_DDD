package com.esmiao.cloudpicture.infrastructure.manager.cache;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Multi layer cache manager
 * Layer 1 -- Redis cache
 * Layer 2 -- Caffeine cache
 *
 * @author Steven Chen
 */
@ConditionalOnProperty(name = "app.cache.manager", havingValue = "multilayer", matchIfMissing = true)
@RequiredArgsConstructor
@Component
@Slf4j
public class MultiLayerCacheManager<K, R> implements CacheManager<K, R> {

    private static final long DEFAULT_EXPIRATION_SECONDS = 5 * 60;

    public static final long SKIP_TIMEOUT = 0;

    private final CaffeineCacheContext<K, R> caffeCacheHandler;

    private final RedisCacheContext<K, R> redisCacheHandler;

    @PostConstruct
    public void initialize() {
        log.info("Current app cache manager: MultiLayerCacheManager");
    }

    @Override
    public R get(K key, Supplier<R> valuSupplier) {
        return valuSupplier.get();
        //return get(key, valuSupplier, TimeUnit.SECONDS, DEFAULT_EXPIRATION_SECONDS);
    }

    @Override
    public R get(K key, Supplier<R> valuSupplier, TimeUnit timeUnit, long expiration) {
        Assert.notNull(timeUnit, "Time unit cannot be null");
        R cacheValue;
        cacheValue = caffeCacheHandler.get(key);
        if (cacheValue != null) {
            log.debug("from caffeine");
            return cacheValue;
        }

        cacheValue = redisCacheHandler.get(key);
        if (cacheValue != null) {
            log.debug("from redis");
            caffeCacheHandler.set(key, cacheValue, timeUnit, expiration);

            return cacheValue;
        }

        cacheValue = valuSupplier.get();
        log.debug("from db");
        if (expiration > SKIP_TIMEOUT) {
            redisCacheHandler.set(key, cacheValue, timeUnit, expiration);
        } else {
            // Set random expiration time to avoid cache avalanche
            redisCacheHandler.set(key, cacheValue, TimeUnit.SECONDS, DEFAULT_EXPIRATION_SECONDS + RandomUtil.randomLong(0, 30));
        }
        // Caffeine cache doesn't use expiration
        caffeCacheHandler.set(key, cacheValue, timeUnit, expiration);

        return cacheValue;
    }
}
