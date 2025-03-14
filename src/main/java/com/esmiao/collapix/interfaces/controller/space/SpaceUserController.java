package com.esmiao.collapix.interfaces.controller.space;

import com.esmiao.collapix.application.space.service.SpaceUserService;
import com.esmiao.collapix.domain.space.constant.SpaceUserPermissionConstant;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.infrastructure.annotation.SaSpaceCheckPermission;
import com.esmiao.collapix.infrastructure.common.CommonResponse;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.infrastructure.common.SpaceUserEntityFactory;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.infrastructure.utils.ResponseUtil;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserAddRequest;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserEditRequest;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserQueryRequest;
import com.esmiao.collapix.interfaces.vo.space.SpaceUserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Space user controller
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@RequiredArgsConstructor
@RestController("Space User Controller")
@RequestMapping("/space-user")
@Slf4j
public class SpaceUserController {

    private final SpaceUserService spaceUserService;

    @PostMapping
    public CommonResponse<String> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest) {
        SpaceUser spaceUser = SpaceUserEntityFactory.buildSpaceUser(spaceUserAddRequest);
        long id = spaceUserService.addSpaceUser(spaceUser);

        return ResponseUtil.success(NumUtil.parseString(id));
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    @DeleteMapping
    public CommonResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowErrorUtil.throwIf(deleteRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long id = NumUtil.parseLong(deleteRequest.getId());
        spaceUserService.deleteSpaceUser(id);

        return ResponseUtil.success(true);
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    @PostMapping("/single")
    public CommonResponse<SpaceUserVo> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        SpaceUser spaceUser = spaceUserService.getSpaceUser(spaceUserQueryRequest);
        return ResponseUtil.success(SpaceUserVo.of(spaceUser));
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    @PostMapping("/list")
    public CommonResponse<List<SpaceUserVo>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        return ResponseUtil.success(spaceUserService.listSpaceUser(spaceUserQueryRequest));
    }

    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    @PutMapping
    public CommonResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest) {
        SpaceUser spaceUser = SpaceUserEntityFactory.buildSpaceUser(spaceUserEditRequest);
        spaceUserService.editSpaceUser(spaceUser);

        return ResponseUtil.success(true);
    }

    @PostMapping("/list/my")
    public CommonResponse<List<SpaceUserVo>> listMyTeamSpace(HttpServletRequest request) {
        return ResponseUtil.success(spaceUserService.listMyTeamSpaceUsers(request));
    }
}
