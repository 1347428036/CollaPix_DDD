package com.esmiao.collapix.domain.space.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.esmiao.collapix.domain.space.valueObject.SpaceLevelEnum;
import com.esmiao.collapix.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.collapix.infrastructure.exception.ThrowErrorUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Space entity
 *
 * @author Steven Chen
 * @TableName space
 */
@TableName(value = "space")
@Data
public class Space implements Serializable {

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level: 0-Standard 1-Pro 2-Ultimate
     */
    private Integer spaceLevel;

    /**
     * Maximum total size of pictures in the space
     */
    private Long maxSize;

    /**
     * Maximum number of pictures in the space
     */
    private Long maxCount;

    /**
     * Total size of pictures in the current space
     */
    private Long totalSize;

    /**
     * Total number of pictures in the current space
     */
    private Long totalCount;

    /**
     * User id who created the space
     */
    private Long userId;

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    /**
     * Edit time
     */
    private LocalDateTime editTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;

    /**
     * Space type：0-private 1-public
     */
    private Integer spaceType;

    /**
     * Whether the space is deleted
     */
    @TableLogic
    private Integer isDelete;

    public void validateSpace(boolean add) {
        /*
         * Validate when create a new space
         * */
        if (add) {
            ThrowErrorUtil.throwIf(StrUtil.isBlank(this.spaceName), ErrorCodeEnum.PARAMS_ERROR, "Space name cannot be empty");
            ThrowErrorUtil.throwIf(this.spaceLevel == null, ErrorCodeEnum.PARAMS_ERROR, "Space level cannot be empty");
            SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
            ThrowErrorUtil.throwIf(spaceTypeEnum == null, ErrorCodeEnum.PARAMS_ERROR, "Space type is invalid");
        }
        /*
         * Validate when adding or updating space
         * */
        ThrowErrorUtil.throwIf(
            StrUtil.isNotBlank(spaceName) && spaceName.length() > 30,
            ErrorCodeEnum.PARAMS_ERROR,
            "Space name is too long");

        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        ThrowErrorUtil.throwIf(spaceLevelEnum == null, ErrorCodeEnum.PARAMS_ERROR, "Space level is invalid");
    }

    public void validateSpaceQuota() {
        ThrowErrorUtil.throwIf(
            totalCount >= maxCount,
            ErrorCodeEnum.NO_PERMISSION_ERROR,
            "Space count is not enough");
        ThrowErrorUtil.throwIf(
            totalSize >= maxSize,
            ErrorCodeEnum.NO_PERMISSION_ERROR,
            "Space size is not enough");
    }
}