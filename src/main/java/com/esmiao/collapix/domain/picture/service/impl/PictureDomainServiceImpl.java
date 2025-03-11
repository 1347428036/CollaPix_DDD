package com.esmiao.collapix.domain.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.picture.constant.PaginationConstant;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.picture.repository.PictureRepository;
import com.esmiao.collapix.domain.picture.service.PictureDomainService;
import com.esmiao.collapix.domain.picture.valueObject.PictureReviewStatusEnum;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.AliYunAiApi;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskRequest;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.collapix.infrastructure.api.oss.ObjectStorage;
import com.esmiao.collapix.infrastructure.utils.ColorSimilarUtil;
import com.esmiao.collapix.interfaces.assembler.PictureEntityFactory;
import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.infrastructure.manager.storage.FilePictureStorageManager;
import com.esmiao.collapix.infrastructure.manager.storage.UrlPictureStorageManager;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.picture.*;
import com.esmiao.collapix.interfaces.vo.picture.PictureVo;
import com.esmiao.collapix.domain.space.constant.SpaceConstant;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.application.space.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

/**
 * Service implementation for database operations on the table [picture]
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PictureDomainServiceImpl implements PictureDomainService {

    private final static String PUBLIC_SPACE_UPLOAD_PREFIX = "public/%s";

    private final static String PRIVATE_SPACE_UPLOAD_PREFIX = "private/%s";

    private final FilePictureStorageManager fileStorageManager;

    private final UrlPictureStorageManager urlPictureStorageManager;

    private final ObjectStorage<?> objectStorage;

    private final SpaceService spaceService;

    private final AliYunAiApi aliYunAiApi;

    private final PictureRepository pictureRepository;

    @Override
    public UploadPictureResult uploadPicture(Object inputSource, User loginUser, Long spaceId) {
        String uploadPathPrefix = spaceId != null && SpaceConstant.PUBLIC_SPACE_ID == spaceId ?
            // Assign directory according to spaceId
            String.format(PRIVATE_SPACE_UPLOAD_PREFIX, spaceId) :
            // Assign directory according to user id
            String.format(PUBLIC_SPACE_UPLOAD_PREFIX, loginUser.getId());
        return inputSource instanceof String ?
            urlPictureStorageManager.putObject(inputSource, uploadPathPrefix) :
            fileStorageManager.putObject(inputSource, uploadPathPrefix);
    }

    @Override
    public Picture getPicture(QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.getOne(queryWrapper);
    }

    @Override
    public Picture getPictureById(Long pictureId) {
        return pictureRepository.getById(pictureId);
    }

    @Override
    public Page<Picture> listPictureByPage(Page<Picture> page, QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.page(page, queryWrapper);
    }

    @Override
    public List<PictureVo> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        // 1. Validate parameters
        ThrowErrorUtil.throwIf(spaceId == null || StrUtil.isBlank(picColor), ErrorCodeEnum.PARAMS_ERROR);
        ThrowErrorUtil.throwIf(loginUser == null, ErrorCodeEnum.NOT_LOGIN_ERROR);
        // 2. Validate space permission
        Space space = spaceService.getSpaceById(spaceId);
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Space does not exist");
        ThrowErrorUtil.throwIf(!loginUser.getId().equals(space.getUserId()), ErrorCodeEnum.NO_PERMISSION_ERROR, "No space access permission");

        // 3. Query all pictures in the space (must have a dominant color)
        List<Picture> pictureList = pictureRepository.lambdaQuery()
            .eq(Picture::getSpaceId, spaceId)
            .isNotNull(Picture::getPicColor)
            .list();
        // If no pictures, return an empty list directly
        if (CollUtil.isEmpty(pictureList)) {
            return Collections.emptyList();
        }
        // Convert target color to Color object
        Color targetColor = Color.decode(picColor);
        // 4. Calculate similarity and sort
        return pictureList.stream()
            .sorted(Comparator.comparingDouble(picture -> {
                // Extract picture dominant color
                String hexColor = picture.getPicColor();
                // Pictures without dominant color are placed at the end
                if (StrUtil.isBlank(hexColor)) {
                    return Double.MAX_VALUE;
                }
                Color pictureColor = Color.decode(hexColor);
                // The larger the value, the more similar
                return -ColorSimilarUtil.calculateSimilarity(targetColor, pictureColor);
            }))
            // Take the top 12
            .limit(12)
            .map(PictureVo::of)
            .toList();
    }

    @Override
    public OutPaintingTaskStatusResponse getOutPaintingTaskStatus(String taskId) {
        return aliYunAiApi.getOutPaintingTask(taskId);
    }

    @Override
    public List<String> crawlPictureUrlList(String searchText, int count) {
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("Failed to fetch the page", e);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Failed to fetch the page");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Failed to get the element");
        }

        Elements imgElementList = div.select("img.mimg");
        return imgElementList.stream()
            .map(el -> el.attr("src"))
            .filter(StrUtil::isNotBlank)
            .map(url -> {
                // Process the image upload URL to prevent escape issues
                int questionMarkIndex = url.indexOf("?");
                if (questionMarkIndex > -1) {
                    url = url.substring(0, questionMarkIndex);
                }

                return url;
            })
            .limit(count)
            .toList();
    }

    @Override
    public List<Long> getPictureSize(QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.listObjs(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getPictureInfoMaps(QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.listMaps(queryWrapper);
    }

    @Override
    public List<String> getPictureTagList(QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.listObjs(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getPictureTimeDimensionList(QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.listMaps(queryWrapper);
    }

    @Override
    public CreateOutPaintingTaskResponse createOutPaintingTask(
        PictureOutPaintingRequest pictureOutPaintingRequest,
        User loginUser) {

        ThrowErrorUtil.throwIf(pictureOutPaintingRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        // Load picture info
        Long pictureId = NumUtil.parseLong(pictureOutPaintingRequest.getPictureId());
        Picture picture = Optional.ofNullable(pictureRepository.getById(pictureId))
            .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR));

        String objectKey = objectStorage.convertUrlToKey(picture.getUrl());
        String presignedUrl = objectStorage.generatePresignedUrl(objectKey);

        CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
        input.setImageUrl(presignedUrl);
        CreateOutPaintingTaskRequest taskRequest = CreateOutPaintingTaskRequest.of(input, pictureOutPaintingRequest);

        return aliYunAiApi.createOutPaintingTask(taskRequest);
    }

    @Override
    public boolean saveOrUpdate(Picture picture) {
        if (picture == null) {
            return false;
        }
        return pictureRepository.saveOrUpdate(picture);
    }

    @Override
    public void updatePicture(Picture picture) {
        boolean result = pictureRepository.updateById(picture);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        ThrowErrorUtil.throwIf(pictureReviewRequest.getId() == null, ErrorCodeEnum.PARAMS_ERROR);
        Long id = Long.parseLong(pictureReviewRequest.getId());
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        ThrowErrorUtil.throwIf(
            reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum),
            ErrorCodeEnum.PARAMS_ERROR);
        // Check if the picture exists
        Picture oldPicture = pictureRepository.getById(id);
        ThrowErrorUtil.throwIf(oldPicture == null, ErrorCodeEnum.NOT_FOUND_ERROR);
        // Already in this status
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "Don't repeat the review");
        }
        // Update review status
        Picture updatePicture = PictureEntityFactory.buildPicture(pictureReviewRequest, loginUser);
        boolean result = pictureRepository.updateById(updatePicture);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public boolean editPictureByBatch(
        Long spaceId, List<Long> pictureIds, String category, List<String> tags, String nameRule) {

        // 1. Validate space params
        ThrowErrorUtil.throwIf(spaceId == null || CollUtil.isEmpty(pictureIds), ErrorCodeEnum.PARAMS_ERROR);
        // 2. Query specified images, select only the required fields
        List<Picture> pictureList = pictureRepository.lambdaQuery()
            .select(Picture::getId, Picture::getSpaceId)
            .eq(Picture::getSpaceId, spaceId)
            .in(Picture::getId, pictureIds)
            .list();
        if (pictureList.isEmpty()) {
            return false;
        }

        // 3. Update category, tags, names
        pictureList.forEach(picture -> {
            if (StrUtil.isNotBlank(category)) {
                picture.setCategory(category);
            }
            if (CollUtil.isNotEmpty(tags)) {
                picture.setTags(JSONUtil.toJsonStr(tags));
            }
        });
        fillPictureNameWithRule(pictureList, nameRule);

        // 4. Batch updating
        boolean result = pictureRepository.updateBatchById(pictureList);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);

        return true;
    }

    @Override
    public void editPicture(Picture picture, User loginUser) {
        // Data validation
        Picture.validatePicture(picture);

        // Fill in the review parameters
        this.fillReviewParams(picture, loginUser);
        // Operate on the database
        boolean result = pictureRepository.updateById(picture);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public boolean deletePicture(Long pictureId) {
        if (pictureId == null) {
            return false;
        }
        return pictureRepository.removeById(pictureId);
    }

    @Override
    public QueryWrapper<Picture> generateQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // Get values from the object
        Long id = NumUtil.parseLong(pictureQueryRequest.getId());
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = StrUtil.isNotBlank(pictureQueryRequest.getUserId()) ? Long.parseLong(pictureQueryRequest.getUserId()) : null;
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        Long reviewerId = StrUtil.isNotBlank(pictureQueryRequest.getReviewerId()) ? Long.parseLong(pictureQueryRequest.getReviewerId()) : null;
        Long spaceId = NumUtil.parseLong(pictureQueryRequest.getSpaceId());
        if (spaceId == null || pictureQueryRequest.isPublicSpace()) {
            spaceId = SpaceConstant.PUBLIC_SPACE_ID;
        }
        LocalDateTime startEditTime = pictureQueryRequest.getStartEditTime();
        LocalDateTime endEditTime = pictureQueryRequest.getEndEditTime();

        // Search from multiple fields
        if (StrUtil.isNotBlank(searchText)) {
            // Need to concatenate query conditions
            queryWrapper.and(qw -> qw.like("name", searchText)
                .or()
                .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.ge(ObjectUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        queryWrapper.le(ObjectUtil.isNotEmpty(endEditTime), "editTime", endEditTime);

        // JSON array query
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // Sorting
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), PaginationConstant.ORDER_ASC.equals(sortOrder), sortField);

        return queryWrapper;
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (loginUser.isAdmin()) {
            // Admin automatically approves
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("Admin automatically approved");
            picture.setReviewTime(LocalDateTime.now());
        } else {
            // Non-admin, change to pending review when creating or editing
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    @Async
    @Override
    public void deletePictureFile(Picture oldPicture) {
        if (oldPicture == null) {
            return;
        }

        objectStorage.deleteObject(oldPicture.getUrl());
        if (oldPicture.getThumbnailUrl() != null) {
            objectStorage.deleteObject(oldPicture.getThumbnailUrl());
        }
    }

    /**
     * nameRule format: picture{num}
     *
     * @param pictureList Picture list for updating
     * @param nameRule    The rule for generating the name
     */
    private void fillPictureNameWithRule(List<Picture> pictureList, String nameRule) {
        if (CollUtil.isEmpty(pictureList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                String pictureName = nameRule.replaceAll("\\{num}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("Resolve rule failed.", e);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Name resolution error");
        }
    }
}
