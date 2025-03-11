package com.esmiao.collapix.infrastructure.manager.storage;

import cn.hutool.core.io.FileUtil;
import com.esmiao.collapix.infrastructure.api.oss.ObjectStorage;
import com.esmiao.collapix.infrastructure.config.properties.OSSProperties;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * File type picture operations in object storage
 * @author Steven Chen
 */
@Slf4j
@Component
public class FilePictureStorageManager extends AbstractPictureStorageManager {

    public FilePictureStorageManager(ObjectStorage<PutObjectResult> objectStorage, OSSProperties ossProperties) {
        super(objectStorage, ossProperties);
    }

    @Override
    protected void validatePicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        ThrowErrorUtil.throwIf(multipartFile == null, ErrorCodeEnum.PARAMS_ERROR, "Picture cannot be empty");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        ThrowErrorUtil.throwIf(fileSize > PIC_SIZE_LIMIT, ErrorCodeEnum.PARAMS_ERROR, "Picture size cannot be larger than 2M");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowErrorUtil.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCodeEnum.PARAMS_ERROR, "Unknown picture type ");
    }

    @Override
    protected String getOriginalFileName(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File destFile) throws IOException {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(destFile);
    }
}
