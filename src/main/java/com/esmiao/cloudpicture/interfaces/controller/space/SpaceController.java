package com.esmiao.cloudpicture.interfaces.controller.space;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.infrastructure.annotation.RoleValidation;
import com.esmiao.cloudpicture.infrastructure.common.CommonResponse;
import com.esmiao.cloudpicture.infrastructure.common.DeleteRequest;
import com.esmiao.cloudpicture.infrastructure.common.SpaceEntityFactory;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.infrastructure.utils.ResponseUtil;
import com.esmiao.cloudpicture.interfaces.dto.space.*;
import com.esmiao.cloudpicture.interfaces.vo.space.SpaceVo;
import com.esmiao.cloudpicture.application.space.service.SpaceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Space Controller for handling space-related operations.
 * @author Steven Chen
 */
@RestController("Space Controller")
@RequestMapping("/space")
public class SpaceController {

    private final SpaceService spaceService;

    /**
     * Constructor for SpaceController.
     * @param spaceService Space service for space operations.
     */
    public SpaceController(
        SpaceService spaceService) {

        this.spaceService = spaceService;
    }

    @PostMapping("/add")
    public CommonResponse<Long> addSpace(
        @RequestBody SpaceAddRequest spaceAddRequest,
        HttpServletRequest request) {

        // Convert entity and DTO here
        Space space = SpaceEntityFactory.buildSpace(spaceAddRequest);
        return ResponseUtil.success(spaceService.addSpace(space, request));
    }

    /**
     * Deletes a space.
     * @param deleteRequest The request containing the space ID to delete.
     * @param request The HTTP request.
     * @return A common response indicating the success of the deletion.
     */
    @DeleteMapping
    public CommonResponse<Boolean> deleteSpace(
        @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        spaceService.deleteSpace(deleteRequest, request);

        return ResponseUtil.success(true);
    }

    /**
     * Updates a space (only admin available).
     * @param spaceUpdateRequest The request containing the space details to update.
     * @return A common response indicating the success of the update.
     */
    @PostMapping("/update")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        // Convert entity and DTO
        Space space = SpaceEntityFactory.buildSpace(spaceUpdateRequest);
        spaceService.updateSpace(space);
        return ResponseUtil.success(true);
    }

    /**
     * Gets a space by id (encapsulated class).
     * @param id The ID of the space to retrieve.
     * @param request The HTTP request.
     * @return A common response containing the encapsulated space details.
     */
    @GetMapping
    public CommonResponse<SpaceVo> getSpaceVoById(@RequestParam(value = "id")String id, HttpServletRequest request) {
        return ResponseUtil.success(spaceService.getSpaceVo(NumUtil.parseLong(id), request));
    }

    /**
     * Lists spaces by page (only admin available).
     * @param spaceQueryRequest The request containing the pagination and query parameters.
     * @return A common response containing the paginated list of space details.
     */
    @PostMapping("/full/page")
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    public CommonResponse<Page<SpaceVo>> listSpaceByPage(
        @RequestBody SpaceQueryRequest spaceQueryRequest,
        HttpServletRequest request) {

        return ResponseUtil.success(spaceService.listSpaceByPage(spaceQueryRequest, request));
    }

    /**
     * Lists spaces by page (encapsulated class).
     * @param spaceQueryRequest The request containing the pagination and query parameters.
     * @param request The HTTP request.
     * @return A common response containing the paginated list of encapsulated space details.
     */
    @PostMapping("/page")
    public CommonResponse<Page<SpaceVo>> listSpaceVoByPage(
        @RequestBody SpaceQueryRequest spaceQueryRequest,
        HttpServletRequest request) {

        return ResponseUtil.success(spaceService.listSpaceVoByPage(spaceQueryRequest, request));
    }

    /**
     * Edits a space (for users).
     * @param spaceEditRequest The request containing the space details to edit.
     * @param request The HTTP request.
     * @return A common response indicating the success of the edit.
     */
    @RoleValidation(roles = UserConstant.ROLE_ADMIN)
    @PutMapping
    public CommonResponse<Boolean> editSpace(
        @RequestBody SpaceEditRequest spaceEditRequest,
        HttpServletRequest request) {

        Space space = SpaceEntityFactory.buildSpace(spaceEditRequest);
        spaceService.editSpace(space, request);

        return ResponseUtil.success(true);
    }

    @GetMapping("/level/list")
    public CommonResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
            .map(spaceLevelEnum -> new SpaceLevel(
                spaceLevelEnum.getValue(),
                spaceLevelEnum.getText(),
                spaceLevelEnum.getMaxCount(),
                spaceLevelEnum.getMaxSize()))
            .collect(Collectors.toList());

        return ResponseUtil.success(spaceLevelList);
    }

}