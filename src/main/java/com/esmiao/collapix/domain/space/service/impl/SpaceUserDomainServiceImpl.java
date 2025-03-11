package com.esmiao.collapix.domain.space.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.domain.space.repository.SpaceUserRepository;
import com.esmiao.collapix.domain.space.service.SpaceUserDomainService;
import com.esmiao.collapix.domain.space.valueObject.SpaceRoleEnum;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Perform the database operations on the table [space_user] (Space-User Association).
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@RequiredArgsConstructor
@Service
public class SpaceUserDomainServiceImpl implements SpaceUserDomainService {

    private final SpaceUserRepository spaceUserRepository;

    @Override
    public boolean saveSpaceUser(SpaceUser spaceUser) {
        return spaceUserRepository.save(spaceUser);
    }

    @Override
    public void updateSpaceUserById(SpaceUser spaceUser) {
        boolean result = spaceUserRepository.updateById(spaceUser);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public void deleteSpaceUser(Long spaceUserId) {
        ThrowErrorUtil.throwIf(!checkExistence(spaceUserId), ErrorCodeEnum.NOT_FOUND_ERROR);
        boolean result = spaceUserRepository.removeById(spaceUserId);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public SpaceUser getSpaceUser(QueryWrapper<SpaceUser> queryWrapper) {
        if (queryWrapper != null) {
            return spaceUserRepository.getOne(queryWrapper);
        }

        return null;
    }

    @Override
    public SpaceUser getSpaceUser(Long spaceId, Long userId) {
        ThrowErrorUtil.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCodeEnum.PARAMS_ERROR);
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spaceId", spaceId);
        queryWrapper.eq("userId", userId);

        return getSpaceUser(queryWrapper);
    }


    @Override
    public SpaceUser getSpaceUserById(Long spaceUserId) {
        ThrowErrorUtil.throwIf(spaceUserId == null || spaceUserId < 0, ErrorCodeEnum.PARAMS_ERROR);
        return spaceUserRepository.getById(spaceUserId);
    }

    @Override
    public String getSpaceUserRoleById(Long spaceUserId) {
        return spaceUserRepository.lambdaQuery()
            .select(SpaceUser::getSpaceRole)
            .eq(SpaceUser::getId, spaceUserId)
            .one()
            .getSpaceRole();
    }

    @Override
    public List<SpaceUser> getSpaceUserList(QueryWrapper<SpaceUser> queryWrapper) {
        return spaceUserRepository.list(queryWrapper);
    }

    @Override
    public boolean checkExistence(Long spaceUserId) {
        if (spaceUserId == null || spaceUserId < 0) {
            return false;
        }

        return spaceUserRepository.lambdaQuery()
            .select(SpaceUser::getId)
            .eq(SpaceUser::getId, spaceUserId)
            .exists();
    }

    @Override
    public boolean checkExistence(Long userId, Long spaceId) {
        if (ObjectUtil.hasNull(userId, spaceId)) {
            return false;
        }

        return spaceUserRepository.lambdaQuery()
            .select(SpaceUser::getId)
            .eq(SpaceUser::getSpaceId, spaceId)
            .eq(SpaceUser::getUserId, userId)
            .exists();
    }

    @Override
    public QueryWrapper<SpaceUser> generateQueryWrapper(SpaceUserQueryRequest request) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (request == null) {
            return queryWrapper;
        }

        if (StrUtil.isNotBlank(request.getSpaceRole())) {
            ThrowErrorUtil.throwIf(
                SpaceRoleEnum.getEnumByValue(request.getSpaceRole()) == null,
                ErrorCodeEnum.PARAMS_ERROR,
                "Invalid space role");
            queryWrapper.eq("spaceRole", request.getSpaceRole());
        }

        Long id = NumUtil.parseLong(request.getId());
        Long spaceId = NumUtil.parseLong(request.getSpaceId());
        Long userId = NumUtil.parseLong(request.getUserId());

        queryWrapper.eq(ObjectUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(userId), "userId", userId);

        return queryWrapper;
    }
}




