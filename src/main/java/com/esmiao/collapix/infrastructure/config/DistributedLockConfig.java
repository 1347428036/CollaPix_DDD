package com.esmiao.collapix.infrastructure.config;

import com.esmiao.collapix.infrastructure.lock.DistributedLock;
import com.esmiao.collapix.infrastructure.lock.RedissonDistributedLock;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Distributed Lock bean configuration
 *
 * @author Steven Chen
 * @createDate 2025-04-02
 */
@RequiredArgsConstructor
@Configuration
public class DistributedLockConfig {

    @Bean
    public DistributedLock redissonDistributedLock(RedissonClient redisson) {
        return new RedissonDistributedLock((Redisson) redisson);
    }
}
