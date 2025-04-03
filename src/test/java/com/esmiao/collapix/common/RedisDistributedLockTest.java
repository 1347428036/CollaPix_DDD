package com.esmiao.collapix.common;

import com.esmiao.collapix.infrastructure.lock.DistributedLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test RedisDistributedLock with real redis connection
 *
 * @author Steven Chen
 * @createDate 2025-04-03
 */
@SpringBootTest
public class RedisDistributedLockTest {

    @Autowired
    private DistributedLock distributedLock;

    @Test
    public void testLock() {
        String lockKey = "testLock";
        boolean locked = distributedLock.lock(lockKey, 60000);
        Assertions.assertTrue(locked);
        distributedLock.unlock(lockKey);
    }

    @Test
    public void testLockExpired() throws InterruptedException {
        String lockKey = "testLock";
        boolean locked = distributedLock.lock(lockKey, 1000);
        Assertions.assertTrue(locked);
        Thread.sleep(2000);
        Assertions.assertThrows(IllegalMonitorStateException.class, () -> distributedLock.unlock(lockKey));
    }

    /**
     * Test if redisson watchdog will extend expire time
     * */
    @Test
    public void testLockExtendExpireTime() throws InterruptedException {
        String lockKey = "testLock";
        boolean locked = distributedLock.tryLock(lockKey, 3000);
        Assertions.assertTrue(locked);
        Thread.sleep(15000);
        Assertions.assertDoesNotThrow(() -> distributedLock.unlock(lockKey));
    }
}
