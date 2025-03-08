package com.esmiao.cloudpicture.domain.picture.entity;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import lombok.Data;

/**
 * Picture entity
 * @author Steven Chen
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Image URL
     */
    private String url;

    /**
     * Image name
     */
    private String name;

    /**
     * Introduction
     */
    private String introduction;

    /**
     * Category
     */
    private String category;

    /**
     * Tags (JSON array)
     */
    private String tags;

    /**
     * Image size
     */
    private Long picSize;

    /**
     * Image width
     */
    private Integer picWidth;

    /**
     * Image height
     */
    private Integer picHeight;

    /**
     * Image aspect ratio
     */
    private Double picScale;

    /**
     * Image format
     */
    private String picFormat;

    /**
     * User ID who created the image
     */
    private Long userId;

    /**
     * Space ID
     * This ID is a sharding key, cannot be updated after it is persisted to the table.
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long spaceId;

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    /**
     * Edit time
     */
    private LocalDateTime editTime;

    /**
     * Status: 0-Pending review; 1-Approved; 2-Rejected
     */
    private Integer reviewStatus;

    /**
     * Review message
     */
    private String reviewMessage;

    /**
     * Reviewer ID
     */
    private Long reviewerId;

    /**
     * Review time
     */
    private LocalDateTime reviewTime;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Original image URL
     */
    private String originalUrl;

    /**
     * Dominant color of the image
     */
    private String picColor;

    /**
     * Is deleted
     */
    @TableLogic
    private Integer isDelete;

    public static void validatePicture(Picture picture) {
        ThrowErrorUtil.throwIf(picture == null, ErrorCodeEnum.PARAMS_ERROR);
        // Get values from the object
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // When modifying data, id cannot be empty, validate if there are parameters
        ThrowErrorUtil.throwIf(ObjUtil.isNull(id), ErrorCodeEnum.PARAMS_ERROR, "id cannot be empty");
        if (StrUtil.isNotBlank(url)) {
            ThrowErrorUtil.throwIf(url.length() > 1024, ErrorCodeEnum.PARAMS_ERROR, "url is too long");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowErrorUtil.throwIf(introduction.length() > 800, ErrorCodeEnum.PARAMS_ERROR, "introduction is too long");
        }
    }
}