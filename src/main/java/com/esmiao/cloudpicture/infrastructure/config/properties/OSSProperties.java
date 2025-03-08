package com.esmiao.cloudpicture.infrastructure.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * A common OSS settings class
 * @author Steven Chen
 */
@Component
@ConfigurationProperties(prefix = "oss")
@Getter
@Setter
public class OSSProperties {

    /**
     * Domain name
     */
    private String host;

    /**
     * Secret ID
     */
    private String secretId;

    /**
     * Secret key (Note: Do not disclose)
     */
    private String secretKey;

    /**
     * Region
     */
    private String region;

    /**
     * Bucket name
     */
    private String bucket;

    /**
     * The base directory of the bucket
     * */
    private String baseFolder;
}
