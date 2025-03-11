package com.esmiao.collapix.application.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.collapix.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.interfaces.dto.picture.*;
import com.esmiao.collapix.interfaces.vo.picture.PictureVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * Service for application level
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
public interface PictureService {

    /**
     * Upload a picture
     *
     * @param inputSource          The uploaded file
     * @param pictureUploadRequest The request containing picture details
     * @param request              The user who is uploading the picture
     * @return The uploaded picture details in VO format
     */
    Picture uploadPicture(
        Object inputSource,
        PictureUploadRequest pictureUploadRequest,
        HttpServletRequest request);

    /**
     * Picture review
     *
     * @param pictureReviewRequest The request containing picture review details
     * @param request              The user who is performing the review
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, HttpServletRequest request);

    /**
     * Batch fetch and create pictures
     *
     * @param pictureUploadByBatchRequest Request containing details of pictures to be uploaded in batch
     * @param request                     The user who is uploading the pictures
     * @return The number of successfully created pictures
     */
    Integer uploadPictureByBatch(
        PictureUploadByBatchRequest pictureUploadByBatchRequest,
        HttpServletRequest request
    );

    void editPicture(Picture picture, HttpServletRequest request);

    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest);

    void deletePicture(DeleteRequest deleteRequest, HttpServletRequest request);

    List<PictureVo> searchPictureByColor(SearchPictureByColorRequest searchRequest, HttpServletRequest request);

    CreateOutPaintingTaskResponse createOutPaintingTask(
        PictureOutPaintingRequest pictureOutPaintingRequest,
        HttpServletRequest request
    );

    QueryWrapper<Picture> generateQueryWrapper(PictureQueryRequest request);

    Page<PictureVo> generatePictureVoPage(Page<Picture> picturePage, HttpServletRequest request);

    void updatePicture(Picture picture, HttpServletRequest request);

    Picture getPicture(QueryWrapper<Picture> queryWrapper);

    Picture getPictureById(Long pictureId);

    PictureVo getPictureVoById(Long pictureId, HttpServletRequest request);

    Page<Picture> listPictureByPage(Page<Picture> page, QueryWrapper<Picture> queryWrapper);

    Page<PictureVo> listPictureVoByPage(PictureQueryRequest queryRequest, HttpServletRequest request);

    List<ImageSearchResult> searchPictureByPicture(SearchPictureByPictureRequest searchPictureByPictureRequest);

    OutPaintingTaskStatusResponse getOutPaintingTaskStatus(String taskId);

    List<Long> getPictureSizeList(QueryWrapper<Picture> queryWrapper);

    List<Map<String, Object>> getPictureInfoMaps(QueryWrapper<Picture> queryWrapper);

    List<String> getPictureJsonTagList(QueryWrapper<Picture> queryWrapper);

    List<Map<String, Object>> getPictureTimeDimensionList(QueryWrapper<Picture> queryWrapper);
}