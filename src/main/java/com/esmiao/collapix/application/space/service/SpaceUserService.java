package com.esmiao.collapix.application.space.service;

import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserQueryRequest;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.interfaces.vo.space.SpaceUserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Perform the database operations on the table [space_user] (Space-User Association).
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
public interface SpaceUserService {

    long addSpaceUser(SpaceUser spaceUser);

    void editSpaceUser(SpaceUser request);

    void deleteSpaceUser(Long spaceUserId);

    void validateSpaceUser(SpaceUser spaceUser, boolean add);

    SpaceUser getSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUser getSpaceUser(Long spaceId, Long userId);

    SpaceUser getSpaceUserById(Long spaceUserId);

    List<SpaceUserVo> listSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest);

    List<SpaceUserVo> listMyTeamSpaceUsers(HttpServletRequest request);
}
