package com.esmiao.collapix.application.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.interfaces.dto.space.SpaceQueryRequest;
import com.esmiao.collapix.interfaces.vo.space.SpaceVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Service for database operations on table [space]
 *
 * @author Steven Chen
 * @createDate 2025-02-08
 */
public interface SpaceService {

    /**
     * Create a new space
     *
     * @param space   Space info for creating
     * @param request Space info for creating
     */
    long addSpace(Space space, HttpServletRequest request);

    void updateSpace(Space space);

    void updateSpaceQuota(Long spaceId, long picSize, long oldPictureSize, boolean updatingPicture);

    void releaseSpaceQuota(Long spaceId, long picSize);

    void editSpace(Space space, HttpServletRequest request);

    Space getSpaceById(Long spaceId);

    SpaceVo getSpaceVo(Long spaceId, HttpServletRequest request);

    List<Space> listSpaces(QueryWrapper<Space> queryWrapper);

    Page<SpaceVo> listSpaceByPage(SpaceQueryRequest queryRequest, HttpServletRequest request);

    Page<SpaceVo> listSpaceVoByPage(SpaceQueryRequest queryRequest, HttpServletRequest request);

    void deleteSpace(DeleteRequest deleteRequest, HttpServletRequest request);

    SpaceVo generateSpaceVo(Space space, User loginUser);

    Page<SpaceVo> generateSpaceVoPage(Page<Space> picturePage, HttpServletRequest request);

    void validateSpacePermission(User loginUser, Space space);
}