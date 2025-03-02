package com.esmiao.cloudpicture;

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
@EnableRedisIndexedHttpSession(redisNamespace = "cloudpicture:session", maxInactiveIntervalInSeconds = 8 * 60 * 60)
@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
@MapperScan("com.esmiao.cloudpicture.infrastructure.mapper")
public class CloudPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudPictureApplication.class, args);
    }

}
