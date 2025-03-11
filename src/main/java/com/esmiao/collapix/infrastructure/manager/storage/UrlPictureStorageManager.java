package com.esmiao.collapix.infrastructure.manager.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.esmiao.collapix.infrastructure.api.oss.ObjectStorage;
import com.esmiao.collapix.infrastructure.config.properties.OSSProperties;
import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * URL type picture operations in object storage
 * @author Steven Chen
 */
@Component
@Slf4j
public class UrlPictureStorageManager extends AbstractPictureStorageManager {

    private static final Set<String> ALLOWED_CONTENT_TYPE = Set.of("image/jpeg", "image/png", "image/webp", "image/jpg");

    protected UrlPictureStorageManager(ObjectStorage<PutObjectResult> objectStorage, OSSProperties ossProperties) {
        super(objectStorage, ossProperties);
    }

    @Override
    protected void validatePicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 1. Validate if the file url is not empty
        ThrowErrorUtil.throwIf(StrUtil.isBlank(fileUrl), ErrorCodeEnum.PARAMS_ERROR, "File url cannot be empty");
        // 2. Validate url format
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            log.error("Url format error", e);
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "File url is invalid");
        }

        // 3. Validate url protocol
        ThrowErrorUtil.throwIf(
            !fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
            ErrorCodeEnum.PARAMS_ERROR,
            "Only support http or https");
        /*
        * 4. Send [HEAD] request to validate file info
        * */
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()){
            if (!response.isOk()) {
                return;
            }
            String contentType = response.header("Content-Type");
            ThrowErrorUtil.throwIf(
                !ALLOWED_CONTENT_TYPE.contains(contentType.toLowerCase()),
                ErrorCodeEnum.PARAMS_ERROR,
                "File type is not allowed");
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowErrorUtil.throwIf(
                        contentLength > PIC_SIZE_LIMIT,
                        ErrorCodeEnum.PARAMS_ERROR,
                        "File size is too large");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "File length format invalid");
                }
            }
        }
    }

    @Override
    protected String getOriginalFileName(Object inputSource) {
        String fileUrl = (String) inputSource;
        // Extract file name from the URL
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) {
        String fileUrl = (String) inputSource;
        // Download file to temp file
        HttpUtil.downloadFile(fileUrl, file);
    }
}

