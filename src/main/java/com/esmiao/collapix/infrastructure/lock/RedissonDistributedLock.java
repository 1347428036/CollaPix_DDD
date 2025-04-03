package com.esmiao.collapix.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;

import java.util.concurrent.TimeUnit;

/**
 * Distributed lock based on Redisson
 *
 * @author Steven Chen
 * @createDate 2025-04-02
 */
@RequiredArgsConstructor
@Slf4j
public class RedissonDistributedLock implements DistributedLock {

    private final Redisson redisson;

    @Override
    public boolean tryLock(String lockKey, long acquireTimeout, long expireTime) {
        try {
            return redisson.getLock(lockKey).tryLock(expireTime, acquireTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("Failed to get redis lock", e);
        }

        return false;
    }

    @Override
    public boolean lock(String lockKey, long expire) {
        try {
            redisson.getLock(lockKey).lock(expire, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            log.error("Failed to get redis lock", e);
        }

        return false;
    }

    @Override
    public void unlock(String lockKey) {
        redisson.getLock(lockKey).unlock();
    }
}
