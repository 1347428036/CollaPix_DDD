package com.esmiao.collapix.infrastructure.common;

import cn.hutool.core.util.StrUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.space.SpaceAddRequest;
import com.esmiao.collapix.domain.space.entity.Space;
import com.esmiao.collapix.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.collapix.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.collapix.interfaces.dto.space.SpaceEditRequest;
import com.esmiao.collapix.interfaces.dto.space.SpaceUpdateRequest;

import java.time.LocalDateTime;

/**
 * Space entity convert factory
 * @author Steven Chen
 */
public class SpaceEntityFactory {
    
    private SpaceEntityFactory(){}

    /**
     * Build space entity with default value if needed.
     * */
    public static Space buildSpace(SpaceAddRequest spaceAddRequest) {
        if (spaceAddRequest == null) {
            return null;
        }

        Space space = new Space();
        space.setSpaceName(StrUtil.isBlank(spaceAddRequest.getSpaceName()) ? "Default space" : spaceAddRequest.getSpaceName());
        space.setSpaceLevel(spaceAddRequest.getSpaceLevel() == null ? SpaceLevelEnum.COMMON.getValue() : spaceAddRequest.getSpaceLevel());
        space.setSpaceType(spaceAddRequest.getSpaceType() == null ? SpaceTypeEnum.PRIVATE.getValue() : spaceAddRequest.getSpaceType());

        return space;
    }

    /**
     * Build space entity for updating.
     * */
    public static Space buildSpace(SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null) {
            return null;
        }

        Space space = new Space();
        space.setId(NumUtil.parseLong(spaceUpdateRequest.getId()));
        space.setSpaceName(spaceUpdateRequest.getSpaceName());
        space.setSpaceLevel(spaceUpdateRequest.getSpaceLevel());
        space.setMaxSize(spaceUpdateRequest.getMaxSize());
        space.setMaxCount(spaceUpdateRequest.getMaxCount());

        return space;
    }

    /**
     * Build space entity for editing.
     * */
    public static Space buildSpace(SpaceEditRequest editRequest) {
        if (editRequest == null) {
            return null;
        }

        Space space = new Space();
        space.setId(NumUtil.parseLong(editRequest.getId()));
        space.setSpaceName(editRequest.getSpaceName());
        // Set edit time
        space.setEditTime(LocalDateTime.now());

        return space;
    }
}
