package com.esmiao.collapix.application.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.application.picture.service.PictureService;
import com.esmiao.collapix.application.user.service.UserService;
import com.esmiao.collapix.domain.picture.constant.PaginationConstant;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.picture.service.PictureDomainService;
import com.esmiao.collapix.domain.picture.valueObject.PictureReviewStatusEnum;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.collapix.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.esmiao.collapix.infrastructure.api.imagesearch.sub.BaiduImageSearchApiFacade;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.infrastructure.manager.cache.CacheManager;
import com.esmiao.collapix.infrastructure.utils.CacheUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.assembler.PictureEntityFactory;
import com.esmiao.collapix.interfaces.dto.picture.*;
import com.esmiao.collapix.interfaces.vo.picture.PictureVo;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import com.esmiao.collapix.shared.auth.SpaceUserAuthConfigLoader;
import com.esmiao.collapix.shared.auth.StpKit;
import com.esmiao.collapix.shared.websocket.model.constant.RedisKeyConstant;
import com.esmiao.collapix.domain.space.constant.SpaceConstant;
import com.esmiao.collapix.domain.space.constant.SpaceUserPermissionConstant;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.application.space.service.SpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for database operations on the table [picture]
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PictureServiceImpl implements PictureService {

    private final UserService userService;

    private final SpaceService spaceService;

    private final SpaceUserAuthConfigLoader spaceUserAuthConfigLoader;

    private final CacheManager<String, String> cacheManager;

    private final PictureDomainService pictureDomainService;

    private final TransactionTemplate transactionTemplate;

    @Override
    public Picture uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        // Validate Space existence and quota
        Long spaceId = NumUtil.parseLong(pictureUploadRequest.getSpaceId());
        if (spaceId != null && SpaceConstant.PUBLIC_SPACE_ID != spaceId) {
            Space space = spaceService.getSpaceById(spaceId);
            space.validateSpaceQuota();
        }
        ThrowErrorUtil.throwIf(ObjectUtil.isEmpty(inputSource), ErrorCodeEnum.PARAMS_ERROR, "Picture cannot be empty");
        Long pictureId = NumUtil.parseLong(pictureUploadRequest.getId());
        boolean isUpdating = pictureId != null;
        // If updating an image, check if the image exists
        Picture oldPicture = null;
        if (isUpdating) {
            oldPicture = pictureDomainService.getPictureById(pictureId);
            ThrowErrorUtil.throwIf(
                spaceId != null && ObjUtil.notEqual(oldPicture.getSpaceId(), spaceId),
                ErrorCodeEnum.NO_PERMISSION_ERROR);
            spaceId = oldPicture.getSpaceId();
        }
        /*
         * Upload the image and get the result
         */
        User loginUser = userService.getLoginUser(request);
        UploadPictureResult uploadPictureResult = pictureDomainService.uploadPicture(inputSource, loginUser, spaceId);
        /*
         * Save the image information to the database
         * */
        Picture picture = PictureEntityFactory.buildPicture(uploadPictureResult, pictureUploadRequest, loginUser, pictureId, spaceId);
        pictureDomainService.fillReviewParams(picture, loginUser);
        Long finalSpaceId = spaceId;
        final long oldPictureSize = oldPicture != null ? oldPicture.getPicSize() : 0L;
        transactionTemplate.execute(status -> {
            boolean result = pictureDomainService.saveOrUpdate(picture);
            ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR, "Upload picture failed");
            if (finalSpaceId != null && SpaceConstant.PUBLIC_SPACE_ID != finalSpaceId) {
                spaceService.updateSpaceQuota(finalSpaceId, picture.getPicSize(), oldPictureSize, isUpdating);
            }

            return picture;
        });
        pictureDomainService.deletePictureFile(oldPicture);

        return picture;
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest batchUploadRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(batchUploadRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Integer count = batchUploadRequest.getCount();
        ThrowErrorUtil.throwIf(count > 30, ErrorCodeEnum.PARAMS_ERROR, "Maximum 30 items");

        String searchText = batchUploadRequest.getSearchText();
        List<String> pictureUrls = pictureDomainService.crawlPictureUrlList(searchText, count);
        int uploadedCount = 0;
        for (String url : pictureUrls) {
            try {
                String namePrefix = batchUploadRequest.getNamePrefix();
                if (StrUtil.isBlank(namePrefix)) {
                    namePrefix = searchText;
                }
                PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
                if (StrUtil.isNotBlank(namePrefix)) {
                    // Set picture name with incremental number
                    pictureUploadRequest.setPicName(namePrefix + (uploadedCount + 1));
                }
                Picture picture = this.uploadPicture(url, pictureUploadRequest, request);
                uploadedCount++;
                log.info("Image uploaded successfully, id = {}", picture.getId());
            } catch (Exception e) {
                log.error("Failed to upload picture: {}", url, e);
            }
        }

        return uploadedCount;
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(pictureReviewRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureDomainService.doPictureReview(pictureReviewRequest, loginUser);
    }

    @Override
    public void editPicture(Picture picture, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(picture == null, ErrorCodeEnum.PARAMS_ERROR);
        // Check if the picture exists
        ThrowErrorUtil.throwIf(picture.getId() == null || picture.getId() <= 0, ErrorCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureDomainService.editPicture(picture, loginUser);
    }

    @Override
    public void editPictureByBatch(PictureEditByBatchRequest batchEditRequest) {
        ThrowErrorUtil.throwIf(batchEditRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        List<Long> pictureIdList = batchEditRequest.getPictureIdList().stream()
            .map(NumUtil::parseLong)
            .filter(Objects::nonNull)
            .toList();
        Long spaceId = NumUtil.parseLong(batchEditRequest.getSpaceId());
        String category = batchEditRequest.getCategory();
        List<String> tags = batchEditRequest.getTags();
        transactionTemplate.execute(status -> {
            boolean result = pictureDomainService.editPictureByBatch(spaceId, pictureIdList, category, tags, batchEditRequest.getNameRule());
            ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR, "Batch edit picture failed");

            return true;
        });

    }

    @Override
    public void deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(deleteRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long pictureId = NumUtil.parseLong(deleteRequest.getId());
        ThrowErrorUtil.throwIf(pictureId == null || pictureId <= 0, ErrorCodeEnum.PARAMS_ERROR);

        Picture oldPicture = pictureDomainService.getPictureById(pictureId);
        ThrowErrorUtil.throwIf(oldPicture == null, ErrorCodeEnum.NOT_FOUND_ERROR);
        transactionTemplate.execute(status -> {
            boolean result = pictureDomainService.deletePicture(pictureId);
            ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
            // Release space quota
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null && spaceId != SpaceConstant.PUBLIC_SPACE_ID) {
                spaceService.releaseSpaceQuota(spaceId, oldPicture.getPicSize());
            }

            return true;
        });

        pictureDomainService.deletePictureFile(oldPicture);
    }

    @Override
    public List<PictureVo> searchPictureByColor(SearchPictureByColorRequest searchRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(searchRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        String picColor = searchRequest.getPicColor();
        Long spaceId = NumUtil.parseLong(searchRequest.getSpaceId());
        User loginUser = userService.getLoginUser(request);

        return pictureDomainService.searchPictureByColor(spaceId, picColor, loginUser);
    }

    @Override
    public CreateOutPaintingTaskResponse createOutPaintingTask(
        PictureOutPaintingRequest pictureOutPaintingRequest,
        HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        return pictureDomainService.createOutPaintingTask(pictureOutPaintingRequest, loginUser);
    }

    @Override
    public QueryWrapper<Picture> generateQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        return pictureDomainService.generateQueryWrapper(pictureQueryRequest);
    }

    /**
     * Paginate to get picture encapsulation
     */
    @Override
    public Page<PictureVo> generatePictureVoPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVo> pictureVoPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVoPage;
        }
        // Object list => encapsulated object list
        List<PictureVo> pictureVoList = pictureList.stream().map(PictureVo::of).collect(Collectors.toList());
        // 1. Associated query user information
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listUserByIds(userIdSet).stream()
            .collect(Collectors.groupingBy(User::getId));
        // 2. Fill information
        pictureVoList.forEach(pictureVO -> {
            Long userId = Long.parseLong(pictureVO.getUserId());
            if (userIdUserListMap.containsKey(userId)) {
                User user = userIdUserListMap.get(userId).get(0);
                pictureVO.setUser(UserVo.of(user));
            }
        });
        pictureVoPage.setRecords(pictureVoList);

        return pictureVoPage;
    }

    @Override
    public void updatePicture(Picture picture, HttpServletRequest request) {
        // Data validation
        Picture.validatePicture(picture);
        // Check if the picture exists
        Picture oldPicture = pictureDomainService.getPictureById(picture.getId());
        ThrowErrorUtil.throwIf(oldPicture == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        User loginUser = userService.getLoginUser(request);
        pictureDomainService.fillReviewParams(picture, loginUser);

        pictureDomainService.updatePicture(picture);
    }

    @Override
    public Picture getPicture(QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.getPicture(queryWrapper);
    }

    @Override
    public Picture getPictureById(Long pictureId) {
        ThrowErrorUtil.throwIf(pictureId == null || pictureId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        // Query the database
        Picture picture = pictureDomainService.getPictureById(pictureId);
        ThrowErrorUtil.throwIf(picture == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        return picture;
    }

    @Override
    public PictureVo getPictureVoById(Long pictureId, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(pictureId == null || pictureId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // Query the database
        Picture picture = pictureDomainService.getPictureById(pictureId);
        // Get the encapsulated class
        PictureVo pictureVo = this.generatePictureVo(picture, loginUser);
        Space space = null;
        if (picture.getSpaceId() != null && picture.getSpaceId() != SpaceConstant.PUBLIC_SPACE_ID) {
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowErrorUtil.throwIf(!hasPermission, ErrorCodeEnum.NO_PERMISSION_ERROR);
            space = spaceService.getSpaceById(picture.getSpaceId());
            ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Cannot find the space");
        }

        List<String> permissions = spaceUserAuthConfigLoader.getPermissionList(space, loginUser);
        pictureVo.setPermissions(permissions);

        return pictureVo;
    }

    @Override
    public Page<Picture> listPictureByPage(Page<Picture> page, QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.listPictureByPage(page, queryWrapper);
    }

    @Override
    public Page<PictureVo> listPictureVoByPage(PictureQueryRequest queryRequest, HttpServletRequest request) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // Restrict crawlers
        ThrowErrorUtil.throwIf(size > PaginationConstant.DEFAULT_PAGE_SIZE_LIMIT, ErrorCodeEnum.PARAMS_ERROR);
        Long spaceId = NumUtil.parseLong(queryRequest.getSpaceId());
        if (spaceId == null) {
            // Normal user only can see passed pictures
            queryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            queryRequest.setPublicSpace(true);
        } else {
            boolean hasPermission = StpKit.SPACE.hasPermission(SpaceUserPermissionConstant.PICTURE_VIEW);
            ThrowErrorUtil.throwIf(!hasPermission, ErrorCodeEnum.NO_PERMISSION_ERROR);
        }

        // Build cache key
        String redisKey = CacheUtil.buildJsonKey(RedisKeyConstant.PREFIX_PICTURE_VO_PAGE, queryRequest);
        // Query data from cache
        String cachedValue = cacheManager.get(redisKey, () -> {
            Page<Picture> picturePage = pictureDomainService.listPictureByPage(new Page<>(current, size),
                this.generateQueryWrapper(queryRequest));
            // Generate the encapsulated class
            Page<PictureVo> pictureVoPage = this.generatePictureVoPage(picturePage, request);

            // Convert data to json string
            return JSONUtil.toJsonStr(pictureVoPage);
        });
        if (cachedValue != null) {
            // Hit cache
            return JSONUtil.toBean(cachedValue, new TypeReference<>() {
            }, true);
        }

        return new Page<>(1, 0);
    }

    @Override
    public List<ImageSearchResult> searchPictureByPicture(SearchPictureByPictureRequest searchPictureByPictureRequest) {
        ThrowErrorUtil.throwIf(searchPictureByPictureRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long pictureId = NumUtil.parseLong(searchPictureByPictureRequest.getPictureId());
        ThrowErrorUtil.throwIf(pictureId == null || pictureId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        Picture oldPicture = pictureDomainService.getPictureById(pictureId);
        ThrowErrorUtil.throwIf(oldPicture == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        return BaiduImageSearchApiFacade.searchImage(oldPicture.getUrl());
    }

    @Override
    public OutPaintingTaskStatusResponse getOutPaintingTaskStatus(String taskId) {
        ThrowErrorUtil.throwIf(StrUtil.isBlank(taskId), ErrorCodeEnum.PARAMS_ERROR);
        return pictureDomainService.getOutPaintingTaskStatus(taskId);
    }

    @Override
    public List<Long> getPictureSizeList(QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.getPictureSize(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getPictureInfoMaps(QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.getPictureInfoMaps(queryWrapper);
    }

    @Override
    public List<String> getPictureJsonTagList(QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.getPictureTagList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getPictureTimeDimensionList(QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.getPictureTimeDimensionList(queryWrapper);
    }

    private PictureVo generatePictureVo(Picture picture, User user) {
        // Convert object to encapsulated class
        PictureVo pictureVO = PictureVo.of(picture);
        // Associated query user information
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            if (user != null && userId.equals(user.getId())) {
                pictureVO.setUser(UserVo.of(user));

                return pictureVO;
            }
            User userFromDb = userService.getUserById(userId);
            pictureVO.setUser(UserVo.of(userFromDb));
        }

        return pictureVO;
    }
}
