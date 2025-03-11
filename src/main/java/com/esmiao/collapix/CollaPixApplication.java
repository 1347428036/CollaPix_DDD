package com.esmiao.collapix;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

/**
 * Application start endpoint
 * @author Steven Chen
 */
@EnableRedisIndexedHttpSession(redisNamespace = "collapix:session", maxInactiveIntervalInSeconds = 8 * 60 * 60)
@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
@MapperScan("com.esmiao.collapix.infrastructure.mapper")
public class CollaPixApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollaPixApplication.class, args);
    }

}
