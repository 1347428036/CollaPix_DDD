package com.esmiao.collapix.domain.space.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Space user table related entity
 *
 * @author Steven Chen
 * @TableName space_user
 */
@TableName(value = "space_user")
@Data
public class SpaceUser implements Serializable {

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Space id
     */
    private Long spaceId;

    /**
     * User id
     */
    private Long userId;

    /**
     * Space role：viewer/editor/admin
     */
    private String spaceRole;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;
}