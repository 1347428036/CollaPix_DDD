package com.esmiao.collapix.interfaces.controller.picture;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.application.picture.service.PictureService;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.user.constant.UserConstant;
import com.esmiao.collapix.infrastructure.annotation.RoleValidation;
import com.esmiao.collapix.infrastructure.annotation.SaSpaceCheckPermission;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskResponse;
import com.esmiao.collapix.infrastructure.api.ai.aliyun.model.OutPaintingTaskStatusResponse;
import com.esmiao.collapix.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.esmiao.collapix.infrastructure.common.CommonResponse;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.infrastructure.utils.ResponseUtil;
import com.esmiao.collapix.interfaces.assembler.PictureEntityFactory;
import com.esmiao.collapix.interfaces.dto.picture.*;
import com.esmiao.collapix.interfaces.vo.picture.PictureTagCategory;
import com.esmiao.collapix.interfaces.vo.picture.PictureVo;
import com.esmiao.collapix.domain.space.constant.SpaceUserPermissionConstant;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Picture Controller for handling picture-related operations.
 *
 * @author Steven Chen
 */
@RestController("Picture Controller")
@RequestMapping("/picture")
public class PictureController {

    private final PictureService pictureService;

    /**
     * Constructor for PictureController.
     *
     * @param pictureService Picture service for picture operations.
     */
    public PictureController(PictureService pictureService) {

        this.pictureService = pictureService;
    }

    /**
     * Gets a picture by id (only admin available).
     *
     * @param id The ID of the picture to retrieve.
     * @return A common response containing the picture details.
     */
    @GetMapping("/full")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Picture> getPictureById(@RequestParam(value = "id") String id) {
        // Get the encapsulated class
        return ResponseUtil.success(pictureService.getPictureById(NumUtil.parseLong(id)));
    }

    /**
     * Gets a picture by id (encapsulated class).
     *
     * @param id      The ID of the picture to retrieve.
     * @param request The HTTP request.
     * @return A common response containing the encapsulated picture details.
     */
    @GetMapping
    public CommonResponse<PictureVo> getPictureVoById(
        @RequestParam(value = "id") String id, HttpServletRequest request) {

        Long pictureId = NumUtil.parseLong(id);
        return ResponseUtil.success(pictureService.getPictureVoById(pictureId, request));
    }

    /**
     * Lists picture tag categories.
     *
     * @return A common response containing the picture tag categories.
     */
    @GetMapping("/tag-category")
    public CommonResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("hot", "funny", "life", "hd", "art", "campus", "background", "resume", "creative");
        List<String> categoryList = Arrays.asList("template", "ecommerce", "emoji", "material", "poster");
        pictureTagCategory.setTags(tagList);
        pictureTagCategory.setCategories(categoryList);

        return ResponseUtil.success(pictureTagCategory);
    }

    /**
     * Query AI out-painting task status
     */
    @GetMapping("/out-painting/status")
    public CommonResponse<OutPaintingTaskStatusResponse> getPictureOutPaintingTaskStatus(String taskId) {
        return ResponseUtil.success(pictureService.getOutPaintingTaskStatus(taskId));
    }

    /**
     * Uploads a picture (can be re-uploaded).
     *
     * @param multipartFile The picture file to upload.
     * @param id            The picture unique id.
     * @param request       The HTTP request.
     * @return A common response containing the uploaded picture details.
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<PictureVo> uploadPicture(
        @RequestPart("file") MultipartFile multipartFile,
        @RequestParam(value = "id", required = false) String id,
        @RequestParam(value = "spaceId", required = false) String spaceId,
        HttpServletRequest request) {

        PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
        pictureUploadRequest.setId(id);
        pictureUploadRequest.setSpaceId(spaceId);
        Picture picture = pictureService.uploadPicture(multipartFile, pictureUploadRequest, request);

        return ResponseUtil.success(PictureVo.of(picture));
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
    @PostMapping(value = "/upload/url")
    public CommonResponse<PictureVo> uploadPictureByUrl(
        @RequestBody PictureUploadRequest pictureUploadRequest,
        HttpServletRequest request) {

        Picture picture = pictureService.uploadPicture(pictureUploadRequest.getUrl(), pictureUploadRequest, request);

        return ResponseUtil.success(PictureVo.of(picture));
    }

    /**
     * Updates a picture (only admin available).
     *
     * @param pictureUpdateRequest The request containing the picture details to update.
     * @return A common response indicating the success of the update.
     */
    @PostMapping("/update")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Boolean> updatePicture(
        @RequestBody PictureUpdateRequest pictureUpdateRequest,
        HttpServletRequest request) {

        // Convert entity and DTO
        Picture picture = PictureEntityFactory.buildPicture(pictureUpdateRequest);
        pictureService.updatePicture(picture, request);

        return ResponseUtil.success(true);
    }

    /**
     * Lists pictures by page (only admin available).
     *
     * @param pictureQueryRequest The request containing the pagination and query parameters.
     * @return A common response containing the paginated list of picture details.
     */
    @PostMapping("/full/page")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Page<PictureVo>> listPictureByPage(
        @RequestBody PictureQueryRequest pictureQueryRequest,
        HttpServletRequest request) {

        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // Query the database
        Page<Picture> picturePage = pictureService.listPictureByPage(
            new Page<>(current, size),
            pictureService.generateQueryWrapper(pictureQueryRequest));

        return ResponseUtil.success(pictureService.generatePictureVoPage(picturePage, request));
    }

    /**
     * Lists pictures by page (encapsulated class).
     *
     * @param pictureQueryRequest The request containing the pagination and query parameters.
     * @param request             The HTTP request.
     * @return A common response containing the paginated list of encapsulated picture details.
     */
    @PostMapping("/page")
    public CommonResponse<Page<PictureVo>> listPictureVoByPage(
        @RequestBody PictureQueryRequest pictureQueryRequest,
        HttpServletRequest request) {

        return ResponseUtil.success(pictureService.listPictureVoByPage(pictureQueryRequest, request));
    }

    @PostMapping("/review")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Boolean> doPictureReview(
        @RequestBody PictureReviewRequest pictureReviewRequest,
        HttpServletRequest request) {

        pictureService.doPictureReview(pictureReviewRequest, request);

        return ResponseUtil.success(true);
    }

    @PostMapping("/upload/batch")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Integer> uploadPictureByBatch(
        @RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
        HttpServletRequest request) {

        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, request);
        return ResponseUtil.success(uploadCount);
    }

    /**
     * Search picture by picture
     */
    @PostMapping("/search/picture")
    @Operation(summary = "Search picture by picture")
    public CommonResponse<List<ImageSearchResult>> searchPictureByPicture(
        @RequestBody SearchPictureByPictureRequest searchRequest) {

        return ResponseUtil.success(pictureService.searchPictureByPicture(searchRequest));
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_VIEW)
    @PostMapping("/search/color")
    public CommonResponse<List<PictureVo>> searchPictureByColor(
        @RequestBody SearchPictureByColorRequest searchResult,
        HttpServletRequest request) {

        return ResponseUtil.success(pictureService.searchPictureByColor(searchResult, request));
    }

    /**
     * Create AI out-painting task
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    @PostMapping("/out-painting")
    public CommonResponse<CreateOutPaintingTaskResponse> createPictureOutPaintingTask(
        @RequestBody PictureOutPaintingRequest outPaintingRequest,
        HttpServletRequest request) {

        return ResponseUtil.success(pictureService.createOutPaintingTask(outPaintingRequest, request));
    }

    /**
     * Edits a picture (for users).
     *
     * @param pictureEditRequest The request containing the picture details to edit.
     * @param request            The HTTP request.
     * @return A common response indicating the success of the edit.
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    @PutMapping
    public CommonResponse<Boolean> editPicture(
        @RequestBody PictureEditRequest pictureEditRequest,
        HttpServletRequest request) {

        // Convert entity and DTO here
        Picture picture = PictureEntityFactory.buildPicture(pictureEditRequest);
        pictureService.editPicture(picture, request);

        return ResponseUtil.success(true);
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    @PutMapping("/batch")
    public CommonResponse<Boolean> editPictureByBatch(
        @RequestBody PictureEditByBatchRequest pictureEditByBatchRequest,
        HttpServletRequest request) {

        pictureService.editPictureByBatch(pictureEditByBatchRequest);

        return ResponseUtil.success(true);
    }

    /**
     * Deletes a picture.
     *
     * @param deleteRequest The request containing the picture ID to delete.
     * @param request       The HTTP request.
     * @return A common response indicating the success of the deletion.
     */
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_DELETE)
    @DeleteMapping
    public CommonResponse<Boolean> deletePicture(
        @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        pictureService.deletePicture(deleteRequest, request);

        return ResponseUtil.success(true);
    }
}