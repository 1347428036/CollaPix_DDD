package com.esmiao.collapix.application.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esmiao.collapix.application.space.service.SpaceUserService;
import com.esmiao.collapix.application.user.service.UserService;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import com.esmiao.collapix.domain.space.service.SpaceDomainService;
import com.esmiao.collapix.domain.space.service.SpaceUserDomainService;
import com.esmiao.collapix.domain.space.valueObject.SpaceRoleEnum;
import com.esmiao.collapix.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.space.spaceuser.SpaceUserQueryRequest;
import com.esmiao.collapix.interfaces.vo.space.SpaceUserVo;
import com.esmiao.collapix.interfaces.vo.space.SpaceVo;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Perform the database operations on the table [space_user] (Space-User Association).
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@RequiredArgsConstructor
@Service
public class SpaceUserServiceImpl implements SpaceUserService {

    private final SpaceDomainService spaceService;

    private final UserService userService;

    private final SpaceUserDomainService spaceUserDomainService;

    @Override
    public long addSpaceUser(SpaceUser spaceUser) {
        ThrowErrorUtil.throwIf(spaceUser == null, ErrorCodeEnum.PARAMS_ERROR);
        validateSpaceUser(spaceUser, true);
        boolean result = spaceUserDomainService.saveSpaceUser(spaceUser);
        ThrowErrorUtil.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR);

        return spaceUser.getId();
    }

    @Override
    public void editSpaceUser(SpaceUser spaceUser) {
        validateSpaceUser(spaceUser, false);

        String spaceUserRole = spaceUserDomainService.getSpaceUserRoleById(spaceUser.getId());
        ThrowErrorUtil.throwIf(spaceUserRole == null, ErrorCodeEnum.NOT_FOUND_ERROR);
        if (spaceUser.getSpaceRole().equals(spaceUserRole)) {
            return;
        }

        spaceUserDomainService.updateSpaceUserById(spaceUser);
    }

    @Override
    public SpaceUser getSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest) {
        ThrowErrorUtil.throwIf(spaceUserQueryRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long spaceId = NumUtil.parseLong(spaceUserQueryRequest.getSpaceId());
        Long userId = NumUtil.parseLong(spaceUserQueryRequest.getUserId());
        ThrowErrorUtil.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCodeEnum.PARAMS_ERROR);
        QueryWrapper<SpaceUser> queryWrapper = spaceUserDomainService.generateQueryWrapper(spaceUserQueryRequest);
        SpaceUser spaceUser = spaceUserDomainService.getSpaceUser(queryWrapper);
        ThrowErrorUtil.throwIf(spaceUser == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        return spaceUser;
    }

    @Override
    public SpaceUser getSpaceUser(Long spaceId, Long userId) {
        return spaceUserDomainService.getSpaceUser(spaceId, userId);
    }

    @Override
    public SpaceUser getSpaceUserById(Long spaceUserId) {
        return spaceUserDomainService.getSpaceUserById(spaceUserId);
    }

    @Override
    public List<SpaceUserVo> listSpaceUser(SpaceUserQueryRequest spaceUserQueryRequest) {
        ThrowErrorUtil.throwIf(spaceUserQueryRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        QueryWrapper<SpaceUser> queryWrapper = spaceUserDomainService.generateQueryWrapper(spaceUserQueryRequest);
        List<SpaceUser> spaceUserList = spaceUserDomainService.getSpaceUserList(queryWrapper);

        return this.generateSpaceUserVoList(spaceUserList);
    }

    @Override
    public List<SpaceUserVo> listMyTeamSpaceUsers(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(NumUtil.parseString(loginUser.getId()));

        return listSpaceUser(spaceUserQueryRequest);
    }

    @Override
    public void deleteSpaceUser(Long spaceUserId) {
        spaceUserDomainService.deleteSpaceUser(spaceUserId);
    }

    @Override
    public void validateSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowErrorUtil.throwIf(spaceUser == null, ErrorCodeEnum.PARAMS_ERROR);
        if (add) {
            Long spaceId = spaceUser.getSpaceId();
            Long userId = spaceUser.getUserId();
            ThrowErrorUtil.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCodeEnum.PARAMS_ERROR);
            User user = userService.getUserById(userId);
            ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.NOT_FOUND_ERROR, "User not exists");
            Space space = spaceService.getSpaceById(spaceId);
            ThrowErrorUtil.throwIf(space == null, ErrorCodeEnum.NOT_FOUND_ERROR, "Space not exists");
            ThrowErrorUtil.throwIf(
                SpaceTypeEnum.TEAM.getValue() != space.getSpaceType(),
                ErrorCodeEnum.OPERATION_ERROR, "The space is not a team space.");
            boolean exists = spaceUserDomainService.checkExistence(userId, spaceId);
            ThrowErrorUtil.throwIf(exists, ErrorCodeEnum.OPERATION_ERROR, "The user is already a member of the current team space.");

            return;
        }
        ThrowErrorUtil.throwIf(spaceUser.getId() == null || spaceUser.getId() <= 0, ErrorCodeEnum.PARAMS_ERROR);
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceUser.getSpaceRole());
        ThrowErrorUtil.throwIf(spaceRoleEnum == null, ErrorCodeEnum.PARAMS_ERROR, "Invalid space role");
    }

    private List<SpaceUserVo> generateSpaceUserVoList(List<SpaceUser> spaceUsers) {
        if (CollUtil.isEmpty(spaceUsers)) {
            return Collections.emptyList();
        }

        /*
         * 1. Collect userId and spaceId
         * */
        Set<Long> userIdSet = spaceUsers.stream()
            .map(SpaceUser::getUserId)
            .collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUsers.stream()
            .map(SpaceUser::getSpaceId)
            .collect(Collectors.toSet());
        /*
         * 2. Batch query user and space info
         * */
        Map<Long, List<User>> userIdUserListMap = userService.listUserByIds(userIdSet)
            .stream()
            .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.getSpaceListByIds(spaceIdSet)
            .stream()
            .collect(Collectors.groupingBy(Space::getId));
        /*
         * 3. Fill user info and space info to view object
         * */
        return spaceUsers.stream()
            .map(spaceUser -> {
                SpaceUserVo spaceUserVo = SpaceUserVo.of(spaceUser);
                Long userId = spaceUser.getUserId();
                // Fill user info
                User user = null;
                if (userIdUserListMap.containsKey(userId)) {
                    user = userIdUserListMap.get(userId).get(0);
                }
                spaceUserVo.setUser(UserVo.of(user));
                // Fill space info
                Long spaceId = spaceUser.getSpaceId();
                Space space = null;
                if (spaceIdSpaceListMap.containsKey(spaceId)) {
                    space = spaceIdSpaceListMap.get(spaceId).get(0);
                }
                spaceUserVo.setSpace(SpaceVo.of(space));

                return spaceUserVo;
            })
            .collect(Collectors.toList());
    }
}




