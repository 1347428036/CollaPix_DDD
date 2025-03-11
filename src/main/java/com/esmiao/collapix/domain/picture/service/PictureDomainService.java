package com.esmiao.collapix.domain.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.collapix.interfaces.dto.picture.*;
import com.esmiao.collapix.interfaces.vo.picture.PictureVo;

import java.util.List;
import java.util.Map;

/**
 * Service for database operations on table [picture]
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
public interface PictureDomainService {

    /**
     * Upload a picture
     *
     * @param inputSource The uploaded file
     * @param loginUser   The user who is uploading the picture
     * @param spaceId     The space ID
     * @return The uploaded picture details in VO format
     */
    UploadPictureResult uploadPicture(
        Object inputSource,
        User loginUser,
        Long spaceId);

    Picture getPictureById(Long pictureId);

    Page<Picture> listPictureByPage(Page<Picture> page, QueryWrapper<Picture> queryWrapper);

    List<PictureVo> searchPictureByColor(Long spaceId, String color, User loginUser);

    OutPaintingTaskStatusResponse getOutPaintingTaskStatus(String taskId);

    List<String> crawlPictureUrlList(String searchText, int count);

    List<Long> getPictureSize(QueryWrapper<Picture> queryWrapper);

    List<Map<String, Object>> getPictureInfoMaps(QueryWrapper<Picture> queryWrapper);

    List<String> getPictureTagList(QueryWrapper<Picture> queryWrapper);

    List<Map<String, Object>> getPictureTimeDimensionList(QueryWrapper<Picture> queryWrapper);

    CreateOutPaintingTaskResponse createOutPaintingTask(
        PictureOutPaintingRequest pictureOutPaintingRequest,
        User loginUser
    );

    boolean saveOrUpdate(Picture picture);

    void updatePicture(Picture picture);

    /**
     * Picture review
     *
     * @param pictureReviewRequest The request containing picture review details
     * @param loginUser            The user who is performing the review
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void editPicture(Picture picture, User loginUser);

    boolean editPictureByBatch(
        Long spaceId,
        List<Long> pictureIds,
        String category,
        List<String> tags,
        String nameRule);

    boolean deletePicture(Long pictureId);

    QueryWrapper<Picture> generateQueryWrapper(PictureQueryRequest request);

    void fillReviewParams(Picture picture, User loginUser);

    void deletePictureFile(Picture oldPicture);

    Picture getPicture(QueryWrapper<Picture> queryWrapper);
}