package com.esmiao.cloudpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.interfaces.dto.space.SpaceQueryRequest;

import java.util.List;
import java.util.Set;

/**
 * Service for database operations on table [space]
 *
 * @author Steven Chen
 * @createDate 2025-02-08
 */
public interface SpaceDomainService {

    boolean saveSpace(Space space);

    void updateSpaceById(Space space);

    boolean updateSpaceQuota(Long spaceId, long picSize, long oldPictureSize, boolean updatingPicture);

    boolean reduceSpaceQuota(Long spaceId, long picSize);

    boolean checkSpaceExistenceForUser(long userId, int spaceType);

    boolean checkSpaceExistence(long spaceId);

    Space getSpaceById(Long spaceId);

    Page<Space> getSpacePage(Page<Space> page, QueryWrapper<Space> queryWrapper);

    List<Space> getSpaceList(QueryWrapper<Space> queryWrapper);

    List<Space> getSpaceListByIds(Set<Long> spaceIdSet);

    boolean deleteSpace(Long spaceId);

    QueryWrapper<Space> generateQueryWrapper(SpaceQueryRequest request);

    void fillSpaceBySpaceLevel(Space space);

    void validateSpacePermission(User loginUser, Space space);
}