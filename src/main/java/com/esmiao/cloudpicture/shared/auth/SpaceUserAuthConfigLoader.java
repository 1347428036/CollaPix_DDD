package com.esmiao.cloudpicture.shared.auth;

import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.shared.auth.model.SpaceUserAuthConfig;
import com.esmiao.cloudpicture.domain.space.entity.Space;

import java.util.List;

/**
 * Space user auth related data context
 *
 * @author Steven Chen
 * @createDate 2025-02-24
 */
public interface SpaceUserAuthConfigLoader {

    SpaceUserAuthConfig loadSpaceUserAuthConfig();

    List<String> getPermissionsByRole(String spaceUserRole);

    List<String> getPermissionList(Space space, User loginUser);
}
