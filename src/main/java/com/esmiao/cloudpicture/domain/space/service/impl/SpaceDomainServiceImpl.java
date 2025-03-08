package com.esmiao.cloudpicture.domain.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.domain.picture.constant.PaginationConstant;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.repository.SpaceRepository;
import com.esmiao.cloudpicture.domain.space.service.SpaceDomainService;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.interfaces.dto.space.SpaceQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Service implementation for database operations on the table [space]
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SpaceDomainServiceImpl implements SpaceDomainService {

    private final SpaceRepository spaceRepository;

    @Override
    public boolean saveSpace(Space space) {
        return spaceRepository.save(space);
    }

    @Override
    public void updateSpaceById(Space space) {
        boolean result = spaceRepository.updateById(space);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public boolean updateSpaceQuota(Long spaceId, long picSize, long oldPictureSize, boolean updatingPicture) {
        ThrowErrorUtil.throwIf(spaceId == null || spaceId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        return spaceRepository.lambdaUpdate()
            .eq(Space::getId, spaceId)
            .setSql(updatingPicture, "totalSize = totalSize - " + oldPictureSize + " + " + picSize)
            .setSql(!updatingPicture, "totalSize = totalSize + " + picSize)
            .setSql(!updatingPicture, "totalCount = totalCount + 1")
            .update();
    }

    @Override
    public boolean reduceSpaceQuota(Long spaceId, long picSize) {
        ThrowErrorUtil.throwIf(spaceId == null || spaceId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        return spaceRepository.lambdaUpdate()
            .eq(Space::getId, spaceId)
            .setSql("totalSize = totalSize - " + picSize)
            .setSql("totalCount = totalCount - 1")
            .update();
    }

    @Override
    public boolean checkSpaceExistenceForUser(long userId, int spaceType) {
        return spaceRepository.lambdaQuery()
            .eq(Space::getUserId, userId)
            .eq(Space::getSpaceType, spaceType)
            .exists();
    }

    @Override
    public boolean checkSpaceExistence(long spaceId) {
        return spaceRepository.lambdaQuery()
            .select(Space::getId)
            .eq(Space::getId, spaceId)
            .exists();
    }

    @Override
    public Space getSpaceById(Long spaceId) {
        return spaceRepository.getById(spaceId);
    }

    @Override
    public Page<Space> getSpacePage(Page<Space> page, QueryWrapper<Space> queryWrapper) {
        return spaceRepository.page(page, queryWrapper);
    }

    @Override
    public List<Space> getSpaceList(QueryWrapper<Space> queryWrapper) {
        return spaceRepository.list(queryWrapper);
    }

    @Override
    public List<Space> getSpaceListByIds(Set<Long> spaceIdSet) {
        if (CollUtil.isEmpty(spaceIdSet)) {
            return Collections.emptyList();
        }

        return spaceRepository.listByIds(spaceIdSet);
    }

    @Override
    public boolean deleteSpace(Long spaceId) {
        return spaceRepository.removeById(spaceId);
    }

    @Override
    public QueryWrapper<Space> generateQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // Get values from the object
        Long id = NumUtil.parseLong(spaceQueryRequest.getId());
        String spaceName = spaceQueryRequest.getSpaceName();
        Long userId = NumUtil.parseLong(spaceQueryRequest.getUserId());
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        // Sorting
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), PaginationConstant.ORDER_ASC.equals(sortOrder), sortField);

        return queryWrapper;
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            if (space.getMaxCount() == null) {
                space.setMaxCount(spaceLevelEnum.getMaxCount());
            }

            if (space.getMaxSize() == null) {
                space.setMaxSize(spaceLevelEnum.getMaxSize());
            }
        }
    }

    @Override
    public void validateSpacePermission(User loginUser, Space space) {
        ThrowErrorUtil.throwIf(
            !space.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin(),
            ErrorCodeEnum.NO_PERMISSION_ERROR);
    }
}
