package com.esmiao.collapix.infrastructure.lock;

/**
 * Distributed Lock defined operations
 *
 * @author Steven Chen
 * @createDate 2025-04-02
 */
public interface DistributedLock {

    boolean tryLock(String lockKey, long acquireTimeout, long expireTime);

    boolean lock(String lockKey, long expire);

    void unlock(String lockKey);
}
