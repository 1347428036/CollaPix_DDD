package com.esmiao.collapix.infrastructure.manager.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Defined operations of cache manager
 * @author Steven Chen
 */
public interface CacheManager<K, R> {

    R get(K key, Supplier<R> valuSupplier);

    R get(K key, Supplier<R> valuSupplier, TimeUnit timeUnit, long expiration);
}
