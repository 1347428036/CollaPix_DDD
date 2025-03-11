package com.esmiao.collapix.infrastructure.common;

import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserAddRequest;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserEditRequest;

/**
 * Space user entity conversion factory
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
public class SpaceUserEntityFactory {

    private SpaceUserEntityFactory() {
    }

    public static SpaceUser buildSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        if (spaceUserAddRequest == null) {
            return null;
        }

        SpaceUser spaceUser = new SpaceUser();
        spaceUser.setUserId(NumUtil.parseLong(spaceUserAddRequest.getUserId()));
        spaceUser.setSpaceId(NumUtil.parseLong(spaceUserAddRequest.getSpaceId()));
        spaceUser.setSpaceRole(spaceUserAddRequest.getSpaceRole());

        return spaceUser;
    }

    public static SpaceUser buildSpaceUser(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        spaceUser.setId(NumUtil.parseLong(request.getId()));
        spaceUser.setSpaceRole(request.getSpaceRole());

        return spaceUser;
    }
}
