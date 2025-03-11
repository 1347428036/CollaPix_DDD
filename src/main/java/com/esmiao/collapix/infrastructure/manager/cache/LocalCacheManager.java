package com.esmiao.collapix.infrastructure.manager.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Local cache manager to disable cache for testing purpose
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Slf4j
@ConditionalOnProperty(name = "app.cache.manager", havingValue = "local")
@Component
public class LocalCacheManager<K, R> implements CacheManager<K, R> {

    @PostConstruct
    public void initialize() {
        log.info("Current app cache manager: LocalCacheManager");
    }

    @Override
    public R get(K key, Supplier<R> valuSupplier) {
        return valuSupplier.get();
    }

    @Override
    public R get(K key, Supplier<R> valuSupplier, TimeUnit timeUnit, long expiration) {
        return valuSupplier.get();
    }
}
