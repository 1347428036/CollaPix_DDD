package com.esmiao.collapix.application.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.application.user.service.UserService;
import com.esmiao.collapix.domain.picture.constant.PaginationConstant;
import com.esmiao.collapix.domain.space.service.SpaceDomainService;
import com.esmiao.collapix.domain.space.service.SpaceUserDomainService;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.space.SpaceQueryRequest;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.shared.auth.SpaceUserAuthConfigLoader;
import com.esmiao.collapix.shared.sharding.PictureDynamicShardingManager;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.collapix.domain.space.valueObject.SpaceRoleEnum;
import com.esmiao.collapix.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.collapix.interfaces.vo.space.SpaceVo;
import com.esmiao.collapix.application.space.service.SpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service implementation for database operations on the table [space]
 *
 * @author Steven Chen
 * @createDate 2025-02-01
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SpaceServiceImpl implements SpaceService {

    private static final Map<Long, Object> LOCK_MAP = new ConcurrentHashMap<>();

    private final UserService userService;

    private final TransactionTemplate transactionTemplate;

    private final PictureDynamicShardingManager pictureDynamicShardingManager;

    private final SpaceDomainService spaceDomainService;

    private final SpaceUserDomainService spaceUserDomainService;

    private final SpaceUserAuthConfigLoader spaceUserAuthConfigLoader;

    @Override
    public long addSpace(Space space, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.PARAMS_ERROR);
        // Fill space init info by space level
        spaceDomainService.fillSpaceBySpaceLevel(space);
        space.validateSpace(true);
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (SpaceLevelEnum.COMMON.getValue() != space.getSpaceLevel() && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCodeEnum.NO_PERMISSION_ERROR, "No permission to create a space with this level");
        }
        // Add lock for every user
        //todo: Use distributed lock in the future
        Object lock = LOCK_MAP.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {
            try {
                Long newSpaceId = transactionTemplate.execute(status -> {
                    if (!loginUser.isAdmin()) {
                        boolean exists = spaceDomainService.checkSpaceExistenceForUser(userId, space.getSpaceType());
                        ThrowErrorUtil.throwIf(
                            exists,
                            ErrorCodeEnum.OPERATION_ERROR,
                            "Each user can only have one space of each type");
                    }

                    boolean result = spaceDomainService.saveSpace(space);
                    ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
                    if (SpaceTypeEnum.TEAM.getValue() == space.getSpaceType()) {
                        SpaceUser spaceUser = new SpaceUser();
                        spaceUser.setUserId(userId);
                        spaceUser.setSpaceId(space.getId());
                        spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                        boolean saved = spaceUserDomainService.saveSpaceUser(spaceUser);
                        ThrowErrorUtil.throwIf(!saved, ErrorCodeEnum.OPERATION_ERROR, "Create team member failed");
                    }
                    pictureDynamicShardingManager.createSpacePictureTable(space);
                    return space.getId();
                });
                return Optional.ofNullable(newSpaceId)
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.OPERATION_ERROR));
            } finally {
                LOCK_MAP.remove(userId);
            }
        }
    }

    @Override
    public void updateSpace(Space space) {
        ThrowErrorUtil.throwIf(space == null || space.getId() == null || space.getId() <= 0, ErrorCodeEnum.PARAMS_ERROR);
        // Autofill space info
        spaceDomainService.fillSpaceBySpaceLevel(space);
        space.validateSpace(false);
        boolean exists = spaceDomainService.checkSpaceExistence(space.getId());
        ThrowErrorUtil.throwIf(!exists, ErrorCodeEnum.NOT_FOUND_ERROR);
        spaceDomainService.updateSpaceById(space);
    }

    @Override
    public void updateSpaceQuota(Long spaceId, long picSize, long oldPictureSize, boolean updatingPicture) {
        boolean success = spaceDomainService.updateSpaceQuota(spaceId, picSize, oldPictureSize, updatingPicture);
        ThrowErrorUtil.throwIf(!success, ErrorCodeEnum.OPERATION_ERROR, "Update space quota failed");
    }

    @Override
    public void releaseSpaceQuota(Long spaceId, long picSize) {
        boolean success = spaceDomainService.reduceSpaceQuota(spaceId, picSize);
        ThrowErrorUtil.throwIf(!success, ErrorCodeEnum.OPERATION_ERROR, "Release space quota failed");
    }

    @Override
    public void editSpace(Space space, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.PARAMS_ERROR);
        ThrowErrorUtil.throwIf(space.getId() == null || space.getId() <= 0, ErrorCodeEnum.PARAMS_ERROR);
        spaceDomainService.fillSpaceBySpaceLevel(space);
        // Data validation
        space.validateSpace(false);
        // Check if the space exists
        Space oldSpace = spaceDomainService.getSpaceById(space.getId());
        ThrowErrorUtil.throwIf(oldSpace == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        User loginUser = userService.getLoginUser(request);
        // Only the owner or admin can edit
        spaceDomainService.validateSpacePermission(loginUser, oldSpace);
        // Operate on the database
        spaceDomainService.updateSpaceById(space);
    }

    @Override
    public Space getSpaceById(Long spaceId) {
        ThrowErrorUtil.throwIf(spaceId == null || spaceId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        return spaceDomainService.getSpaceById(spaceId);
    }

    @Override
    public SpaceVo getSpaceVo(Long spaceId, HttpServletRequest request) {
        // Query the database
        Space space = this.getSpaceById(spaceId);
        ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        // Get the encapsulated class
        SpaceVo spaceVo = this.generateSpaceVo(space, loginUser);
        List<String> permissionList = spaceUserAuthConfigLoader.getPermissionList(space, loginUser);
        spaceVo.setPermissions(permissionList);

        return spaceVo;
    }

    @Override
    public List<Space> listSpaces(QueryWrapper<Space> queryWrapper) {
        return spaceDomainService.getSpaceList(queryWrapper);
    }

    @Override
    public Page<SpaceVo> listSpaceByPage(SpaceQueryRequest queryRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(queryRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<Space> spacePage = spaceDomainService.getSpacePage(
            new Page<>(current, size),
            spaceDomainService.generateQueryWrapper(queryRequest));

        return this.generateSpaceVoPage(spacePage, request);
    }

    @Override
    public Page<SpaceVo> listSpaceVoByPage(SpaceQueryRequest queryRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(
            queryRequest.getPageSize() > PaginationConstant.DEFAULT_PAGE_SIZE_LIMIT,
            ErrorCodeEnum.PARAMS_ERROR);

        return listSpaceByPage(queryRequest, request);
    }

    @Override
    public void deleteSpace(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowErrorUtil.throwIf(deleteRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long spaceId = NumUtil.parseLong(deleteRequest.getId());
        ThrowErrorUtil.throwIf(spaceId == null || spaceId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        // Check if the space exists
        Space oldSpace = spaceDomainService.getSpaceById(spaceId);
        ThrowErrorUtil.throwIf(oldSpace == null, ErrorCodeEnum.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        // Only the owner or admin can delete
        spaceDomainService.validateSpacePermission(loginUser, oldSpace);
        // Operate on the database
        boolean result = spaceDomainService.deleteSpace(spaceId);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public SpaceVo generateSpaceVo(Space space, User loginUser) {
        SpaceVo spaceVo = SpaceVo.of(space);
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            if (loginUser.getId().equals(userId)) {
                spaceVo.setUser(UserVo.of(loginUser));

                return spaceVo;
            }
            User user = userService.getUserById(userId);
            spaceVo.setUser(UserVo.of(user));
        }

        return spaceVo;
    }

    /**
     * Paginate to get space encapsulation
     */
    @Override
    public Page<SpaceVo> generateSpaceVoPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVo> spaceVoPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVoPage;
        }
        // 1. Associated query user information
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listUserByIds(userIdSet).stream()
            .collect(Collectors.groupingBy(User::getId));
        // 2. Fill information
        // Object list => encapsulated object list
        List<SpaceVo> spaceVoList = spaceList.stream()
            .map(SpaceVo::of)
            .peek(spaceVo -> {
                Long userId = Long.parseLong(spaceVo.getUserId());
                if (userIdUserListMap.containsKey(userId)) {
                    User user = userIdUserListMap.get(userId).get(0);
                    spaceVo.setUser(UserVo.of(user));
                }
            })
            .toList();
        spaceVoPage.setRecords(spaceVoList);

        return spaceVoPage;
    }

    @Override
    public void validateSpacePermission(User loginUser, Space space) {
        spaceDomainService.validateSpacePermission(loginUser, space);
    }
}
