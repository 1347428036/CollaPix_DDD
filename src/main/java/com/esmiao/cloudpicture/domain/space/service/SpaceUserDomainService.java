package com.esmiao.cloudpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esmiao.cloudpicture.domain.space.entity.SpaceUser;
import com.esmiao.cloudpicture.interfaces.dto.space.spaceuser.SpaceUserQueryRequest;

import java.util.List;

/**
 * Perform the database operations on the table [space_user] (Space-User Association).
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
public interface SpaceUserDomainService {


    boolean saveSpaceUser(SpaceUser spaceUser);

    boolean checkExistence(Long spaceUserId);

    boolean checkExistence(Long userId, Long spaceId);

    void deleteSpaceUser(Long spaceUserId);

    QueryWrapper<SpaceUser> generateQueryWrapper(SpaceUserQueryRequest request);

    SpaceUser getSpaceUser(QueryWrapper<SpaceUser> queryWrapper);

    SpaceUser getSpaceUser(Long spaceId, Long userId);

    SpaceUser getSpaceUserById(Long spaceUserId);

    List<SpaceUser> getSpaceUserList(QueryWrapper<SpaceUser> queryWrapper);

    String getSpaceUserRoleById(Long spaceUserId);

    void updateSpaceUserById(SpaceUser spaceUser);

}
