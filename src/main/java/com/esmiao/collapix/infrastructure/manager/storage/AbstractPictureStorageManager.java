package com.esmiao.collapix.infrastructure.manager.storage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.esmiao.collapix.infrastructure.api.oss.ObjectStorage;
import com.esmiao.collapix.infrastructure.config.properties.OSSProperties;
import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.interfaces.dto.picture.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Picture storage operation template class
 * @author Steven Chen
 */
@Slf4j
public abstract class AbstractPictureStorageManager implements StorageManager<UploadPictureResult> {

    protected final Set<String> ALLOW_FORMAT_LIST = Set.of("jpeg", "jpg", "png", "webp");

    protected final long PIC_SIZE_LIMIT = 2 * 1024 * 1024L;

    private final ObjectStorage<PutObjectResult> objectStorage;

    private final OSSProperties ossProperties;

    protected AbstractPictureStorageManager(ObjectStorage<PutObjectResult> objectStorage, OSSProperties ossProperties) {
        this.objectStorage = objectStorage;
        this.ossProperties = ossProperties;
    }

    @Override
    public final UploadPictureResult putObject(Object inputSource, String pathPrefix) {
        validatePicture(inputSource);
        String uuid = RandomUtil.randomString(16);
        String originalFileName = getOriginalFileName(inputSource);
        String uploadFilename = String.format(
            "%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFileName));
        String uploadPath = generateFilePath(pathPrefix, uploadFilename);
        File tempFile = null;
        try {
            // Create temp file
            tempFile = File.createTempFile(uploadPath, null);
            processFile(inputSource, tempFile);
            // Upload file to storage
            PutObjectResult putObjectResult = objectStorage.putObject(uploadPath, tempFile);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            List<CIObject> ciObjects = putObjectResult.getCiUploadResult().getProcessResults().getObjectList();
            if (CollUtil.isNotEmpty(ciObjects)) {
                CIObject webpCompressObject = ciObjects.get(0);
                CIObject thumbnailObject = webpCompressObject;
                if (ciObjects.size() > 1) {
                    thumbnailObject = ciObjects.get(1);
                }

                return generateUploadResult(originalFileName, webpCompressObject, thumbnailObject, imageInfo);
            }

            return generateUploadResult(imageInfo, originalFileName, FileUtil.size(tempFile), uploadPath);
        } catch (Exception e) {
            log.error("Upload to object storage failed", e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "Upload failed");
        } finally {
            this.deleteTempFile(tempFile);
        }
    }

    protected abstract void validatePicture(Object inputSource);

    protected abstract String getOriginalFileName(Object inputSource);

    protected String generateFilePath(String pathPrefix, String fileName) {
        return String.format("/%s/%s/%s", ossProperties.getBaseFolder(), pathPrefix, fileName);
    }

    protected abstract void processFile(Object inputSource, File destFile) throws IOException;

    private UploadPictureResult generateUploadResult(
        String originalFileName,
        CIObject webpCompressObject,
        CIObject thumbnailObject,
        ImageInfo imageInfo) {

        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = webpCompressObject.getWidth();
        int picHeight = webpCompressObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFileName));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(webpCompressObject.getFormat());
        uploadPictureResult.setPicSize(webpCompressObject.getSize().longValue());
        uploadPictureResult.setUrl(ossProperties.getHost() + "/" + webpCompressObject.getKey());
        uploadPictureResult.setThumbnailUrl(ossProperties.getHost() + "/" + thumbnailObject.getKey());
        uploadPictureResult.setPicColor(imageInfo.getAve());

        return uploadPictureResult;
    }

    private UploadPictureResult generateUploadResult(
        ImageInfo imageInfo,
        String originFilename,
        long fileSize,
        String uploadPath) {

        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(fileSize);
        uploadPictureResult.setUrl(ossProperties.getHost() + uploadPath);
        uploadPictureResult.setPicColor(imageInfo.getAve());

        return uploadPictureResult;
    }

    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("Picture delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
