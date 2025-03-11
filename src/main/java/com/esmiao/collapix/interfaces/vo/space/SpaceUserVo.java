package com.esmiao.collapix.interfaces.vo.space;

import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import com.esmiao.collapix.domain.space.entity.SpaceUser;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Space user view object
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * Space id
     */
    private String spaceId;

    /**
     * User id
     */
    private String userId;

    /**
     * Space user role：viewer/editor/admin
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

    /**
     * User info
     */
    private UserVo user;

    /**
     * Space info
     */
    private SpaceVo space;

    public static SpaceUserVo of(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }

        SpaceUserVo spaceUserVO = new SpaceUserVo();
        spaceUserVO.setId(NumUtil.parseString(spaceUser.getId()));
        spaceUserVO.setSpaceId(NumUtil.parseString(spaceUser.getSpaceId()));
        spaceUserVO.setUserId(NumUtil.parseString(spaceUser.getUserId()));
        spaceUserVO.setSpaceRole(spaceUser.getSpaceRole());
        spaceUserVO.setCreateTime(spaceUser.getCreateTime());
        spaceUserVO.setUpdateTime(spaceUser.getUpdateTime());

        return spaceUserVO;
    }
}
