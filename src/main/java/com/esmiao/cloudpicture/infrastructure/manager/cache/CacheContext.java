package com.esmiao.cloudpicture.infrastructure.manager.cache;

import java.util.concurrent.TimeUnit;

/**
 * Defined operations for cache context
 * @author Steven Chen
 */
public interface CacheContext<K, R> {

    R get(K key);

    void set(K key, R value, TimeUnit timeUnit, long timeout);
}
