package com.esmiao.collapix.infrastructure.config;

import com.esmiao.collapix.infrastructure.config.properties.OSSProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS bean configuration
 * @author Steven Chen
 */
@Configuration
public class OSSConfig {

    /**
     * Tencent COS storage client
     * */
    @Bean
    public COSClient cosClient(OSSProperties ossProperties) {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(ossProperties.getSecretId(), ossProperties.getSecretKey());
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(ossProperties.getRegion()));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }


}
