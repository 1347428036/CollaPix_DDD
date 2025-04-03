package com.esmiao.collapix.common;

import com.esmiao.collapix.infrastructure.lock.RedissonDistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DistributedLockMockTest {

    private RedissonDistributedLock distributedLock;

    @Mock
    private Redisson redisson;

    @Mock
    private RLock rLock;

    @BeforeEach
    void setUp() {
        // Mock Redisson.getLock() to return the mocked RLock
        Mockito.when(redisson.getLock(anyString())).thenReturn(rLock);

        // Initialize the RedissonDistributedLock with the mocked Redisson instance
        distributedLock = new RedissonDistributedLock(redisson);
    }

    @Test
    void testTryLock_Success() throws InterruptedException {
        // Mock RLock.tryLock() to return true
        Mockito.when(rLock.tryLock(1000L, 5000L, TimeUnit.MILLISECONDS)).thenReturn(true);

        boolean result = distributedLock.tryLock("testLock", 5000L, 1000L);

        assertTrue(result);
        Mockito.verify(rLock, Mockito.times(1)).tryLock(1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    @Test
    void testTryLock_InterruptedException() throws InterruptedException {
        // Mock RLock.tryLock() to throw InterruptedException
        Mockito.doThrow(new InterruptedException("Thread interrupted")).when(rLock).tryLock(1000L, 5000L, TimeUnit.MILLISECONDS);

        boolean result = distributedLock.tryLock("testLock", 5000L, 1000L);

        assertFalse(result);
        Mockito.verify(rLock, Mockito.times(1)).tryLock(1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    @Test
    void testLock_Success() {
        // Mock RLock.lock() to do nothing
        Mockito.doNothing().when(rLock).lock(5000L, TimeUnit.MILLISECONDS);

        boolean result = distributedLock.lock("testLock", 5000L);

        assertTrue(result);
        Mockito.verify(rLock, Mockito.times(1)).lock(5000L, TimeUnit.MILLISECONDS);
    }

    @Test
    void testLock_Exception() {
        // Mock RLock.lock() to throw Exception
        Mockito.doThrow(new RuntimeException("Mock locking failed")).when(rLock).lock(5000L, TimeUnit.MILLISECONDS);

        boolean result = distributedLock.lock("testLock", 5000L);

        assertFalse(result);
        Mockito.verify(rLock, Mockito.times(1)).lock(5000L, TimeUnit.MILLISECONDS);
    }

    @Test
    void testUnlock() {
        // Mock RLock.unlock() to do nothing
        Mockito.doNothing().when(rLock).unlock();

        distributedLock.unlock("testLock");

        Mockito.verify(rLock, Mockito.times(1)).unlock();
    }

    @Test
    void testTryLock_Timeout() throws InterruptedException {
        // Mock RLock.tryLock() to simulate a timeout by delaying the response
        Mockito.doAnswer(invocation -> {
            // Simulate a delay longer than the acquireTimeout (1000L)
            Thread.sleep(2000L);
            // Return false after the delay
            return false;
        })
        .when(rLock)
        .tryLock(1000L, 1000L, TimeUnit.MILLISECONDS);

        boolean result = distributedLock.tryLock("testLock", 1000L, 1000L);

        assertFalse(result, "Expected tryLock to return false when lock acquisition Mockito.times out");
        Mockito.verify(rLock, Mockito.times(1)).tryLock(1000L, 1000L, TimeUnit.MILLISECONDS);
    }
}
