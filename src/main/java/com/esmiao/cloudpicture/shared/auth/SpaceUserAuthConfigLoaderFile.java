package com.esmiao.cloudpicture.shared.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.shared.auth.model.SpaceUserAuthConfig;
import com.esmiao.cloudpicture.shared.auth.model.SpaceUserRole;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.entity.SpaceUser;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceRoleEnum;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.cloudpicture.application.space.service.SpaceUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Space user auth configurations context - File implementation
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@RequiredArgsConstructor
@Component
public class SpaceUserAuthConfigLoaderFile implements SpaceUserAuthConfigLoader {

    private final SpaceUserService spaceUserService;

    private SpaceUserAuthConfig configCache;

    /**
     * Get permissions by role.
     */
    @Override
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (configCache == null) {
            loadSpaceUserAuthConfig();
        }

        if (StrUtil.isBlank(spaceUserRole)) {
            return Collections.emptyList();
        }

        SpaceUserRole role = configCache.getRoles().stream()
            .filter(r -> spaceUserRole.equals(r.getKey()))
            .findFirst()
            .orElse(null);
        if (role == null) {
            return Collections.emptyList();
        }

        return role.getPermissions();
    }

    /**
     * Load space user permission for the specific space and user.
     */
    @Override
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return Collections.emptyList();
        }

        List<String> adminPermissions = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // Public gallery
        if (space == null) {
            if (loginUser.isAdmin()) {
                return adminPermissions;
            }

            return Collections.emptyList();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return Collections.emptyList();
        }
        // Get permissions by space type
        switch (spaceTypeEnum) {
            case PRIVATE -> {
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return adminPermissions;
                }

                return Collections.emptyList();
            }
            case TEAM -> {
                SpaceUser spaceUser = spaceUserService.getSpaceUser(space.getId(), loginUser.getId());
                if (spaceUser == null) {
                    return Collections.emptyList();
                }

                return getPermissionsByRole(spaceUser.getSpaceRole());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public SpaceUserAuthConfig loadSpaceUserAuthConfig() {
        if (configCache != null) {
            return configCache;
        }

        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        configCache = JSONUtil.toBean(json, SpaceUserAuthConfig.class);

        return configCache;
    }
}
