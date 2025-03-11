package com.esmiao.collapix.infrastructure.api.oss;

import cn.hutool.core.io.FileUtil;
import com.esmiao.collapix.infrastructure.config.properties.OSSProperties;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Object storage service implementation for Tencent Cloud 'COS'
 * @author Steven Chen
 */
@Service
public class CosStorage implements ObjectStorage<PutObjectResult> {

    private final OSSProperties ossProperties;

    private final COSClient cosClient;

    public CosStorage(OSSProperties ossProperties, COSClient cosClient) {
        this.ossProperties = ossProperties;
        this.cosClient = cosClient;
    }

    /**
     * @param key The file path with file name in the bucket
     * @param file The file to be uploaded
     * */
    @Override
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucket(), key, file);
        // Processing of images (obtaining basic information is also considered as a kind of processing)
        PicOperations picOperations = new PicOperations();
        // 1: Indicate return the basic information of the image
        picOperations.setIsPicInfo(1);
        List<PicOperations.Rule> rules = new ArrayList<>(1);
        String fileMainName = FileUtil.mainName(key);
        /*
        * Compress the picture rule
        * */
        // The webp file path with file name
        String webpKey = fileMainName + ".webp";
        PicOperations.Rule webpCompressRule = new PicOperations.Rule();
        // The file path with file name to save after rule processed
        webpCompressRule.setFileId(webpKey);
        webpCompressRule.setBucket(ossProperties.getBucket());
        webpCompressRule.setRule("imageMogr2/format/webp");
        rules.add(webpCompressRule);
        /*
        * Generate thumbnail rule
        * If picture is too small, thumbnail will be larger than the original
        * */
        if (file.length() > 2 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            // The file path with file name to save after rule processed
            thumbnailRule.setFileId(fileMainName + "_thumbnail." + FileUtil.getSuffix(key));
            thumbnailRule.setBucket(ossProperties.getBucket());
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 240, 240));
            rules.add(thumbnailRule);
        }

        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);

        return cosClient.putObject(putObjectRequest);
    }

    @Override
    public void deleteObject(String key) throws CosClientException {
        cosClient.deleteObject(ossProperties.getBucket(), key);
    }

    @Override
    public String generatePresignedUrl(String key) {
        // Expiration time in 30min
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        URL url = cosClient.generatePresignedUrl(ossProperties.getBucket(), key, expirationDate, HttpMethodName.GET);

        return url.toString();
    }

    @Override
    public String convertUrlToKey(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getPath();
        } catch (java.net.MalformedURLException e) {
            ThrowErrorUtil.throwEx(ErrorCodeEnum.PARAMS_ERROR, "url is invalid");
        }

        return null;
    }
}
