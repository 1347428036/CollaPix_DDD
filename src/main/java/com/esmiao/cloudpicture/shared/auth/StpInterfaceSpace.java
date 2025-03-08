package com.esmiao.cloudpicture.shared.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esmiao.cloudpicture.application.picture.service.PictureService;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.domain.space.constant.SpaceConstant;
import com.esmiao.cloudpicture.domain.space.constant.SpaceUserPermissionConstant;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.cloudpicture.domain.picture.entity.Picture;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.entity.SpaceUser;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceRoleEnum;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.cloudpicture.application.space.service.SpaceService;
import com.esmiao.cloudpicture.application.space.service.SpaceUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * Custom permission loading interface implementation class
 * Ensure this class is scanned by SpringBoot to complete the custom permission verification extension of Sa-Token
 *
 * @author Steven Chen
 * @createDate 2025-02-23
 */
@RequiredArgsConstructor
@Component
public class StpInterfaceSpace implements StpInterface {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final SpaceUserAuthConfigLoader spaceUserAuthManager;

    private final SpaceUserService spaceUserService;

    private final PictureService pictureService;

    private final SpaceService spaceService;

    /**
     * Return a list of permission codes for an account
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // Determine loginType, perform permission check only for type "space"
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return Collections.emptyList();
        }
        // Get context object
        SpaceUserRequestContext authContext = getRequestContext();
        // Admin permissions, indicating that permission verification is passed
        List<String> adminPermissions = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // If all fields are null, it means querying the public gallery, which can pass
        if (isAllFieldsNull(authContext)) {
            return adminPermissions;
        }

        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(UserConstant.SESSION_KEY_USER_INFO);
        ThrowErrorUtil.throwIf(loginUser == null, ErrorCodeEnum.NOT_LOGIN_ERROR);

        // Prioritize obtaining SpaceUser object from the context
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }

        // If there is spaceUserId, it must be a team space, query SpaceUser object from the database
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            return getPermissionBySpaceUser(spaceUserId, loginUser);
        }
        // If there is no spaceUserId, try to get Space object or handle through spaceId or pictureId
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // If there is no spaceId, get Picture object and Space object through pictureId
            Long pictureId = authContext.getPictureId();
            return getPermissionByPicture(pictureId, loginUser, adminPermissions);
        }

        return getPermissionBySpace(spaceId, loginUser, adminPermissions);
    }

    /**
     * Return a list of role identifiers for an account (permissions and roles can be verified separately)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return SpaceRoleEnum.getAllValues();
    }

    /**
     * Get context object from request
     */
    private SpaceUserRequestContext getRequestContext() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserRequestContext authRequest;
        // Compatible with get and post operations
        if (ContentType.JSON.getValue().equals(contentType)) {
            String body = JakartaServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserRequestContext.class);
        } else {
            Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserRequestContext.class);
        }
        // Distinguish the meaning of id field based on request path
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            String requestUri = request.getRequestURI();
            String partUri = requestUri.replace(contextPath + "/", "");
            String moduleName = StrUtil.subBefore(partUri, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
            }
        }

        return authRequest;
    }

    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true;
        }
        // Get all fields and check if all fields are null
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
            // Get field value
            .map(field -> ReflectUtil.getFieldValue(object, field))
            // Check if all fields are null
            .allMatch(ObjectUtil::isEmpty);
    }

    private List<String> getPermissionBySpaceUser(Long spaceUserId, User loginUser) {
        SpaceUser spaceUser = spaceUserService.getSpaceUserById(spaceUserId);
        ThrowErrorUtil.throwIf(spaceUser == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Cannot find the space user");
        // Get the corresponding spaceUser of the current login user
        SpaceUser loginSpaceUser = spaceUserService.getSpaceUser(spaceUser.getSpaceId(), loginUser.getId());
        if (loginSpaceUser == null) {
            return Collections.emptyList();
        }

        // This will cause the admin to have no permissions in the private space, can query the database again to handle
        return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getSpaceRole());
    }

    private List<String> getPermissionByPicture(Long pictureId, User loginUser, List<String> adminPermissions) {
        // If picture id is also null, default to passing permission verification
        if (pictureId == null) {
            return adminPermissions;
        }

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceId", "userId");
        queryWrapper.eq("id", pictureId);

        Picture picture = pictureService.getPicture(queryWrapper);
        ThrowErrorUtil.throwIf(picture == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Cannot find the picture info");
        Long spaceId = picture.getSpaceId();
        // Public gallery, only the owner or admin can operate
        if (spaceId == null && (picture.getUserId().equals(loginUser.getId()) || loginUser.isAdmin())) {
            return adminPermissions;
        }
        // Private space, only the owner or admin can operate
        if (picture.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
            return adminPermissions;
        }
        // Not your picture, only viewable
        return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
    }

    private List<String> getPermissionBySpace(Long spaceId, User loginUser, List<String> adminPermissions) {
        if (SpaceConstant.PUBLIC_SPACE_ID == spaceId && loginUser.isAdmin()) {
            return adminPermissions;
        }
        // Get Space object
        Space space = spaceService.getSpaceById(spaceId);
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Cannot find the space info");
        // Determine permissions based on Space type
        if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // Private space, only the owner or admin has permissions
            if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                return adminPermissions;
            }

            return Collections.emptyList();
        }

        // Team space, query SpaceUser and get role and permissions
        SpaceUser spaceUser = spaceUserService.getSpaceUser(spaceId, loginUser.getId());
        if (spaceUser == null) {
            return Collections.emptyList();
        }

        return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
    }
}
