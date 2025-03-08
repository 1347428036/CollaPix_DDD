package com.esmiao.cloudpicture.interfaces.vo.space;

import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Space object view
 *
 * @author Steven Chen
 */
@Data
public class SpaceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;
    /**
     * id
     */
    private String id;

    /**
     * space name
     */
    private String spaceName;

    /**
     * space level: 0-Standard 1-Pro 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * maximum total size of space images
     */
    private Long maxSize;

    /**
     * maximum number of space images
     */
    private Long maxCount;

    /**
     * total size of images in the current space
     */
    private Long totalSize;

    /**
     * number of images in the current space
     */
    private Long totalCount;

    /**
     * id of the user who created the space
     */
    private String userId;

    /**
     * creation time
     */
    private LocalDateTime createTime;

    /**
     * edit time
     */
    private LocalDateTime editTime;

    /**
     * update time
     */
    private LocalDateTime updateTime;

    /**
     * Space type：0-private 1-public
     */
    private Integer spaceType;

    /**
     * user information of the creator
     */
    private UserVo user;

    /**
     * Current user permissions in the space
     * */
    private List<String> permissions;

    /**
     * convert object to encapsulation class
     *
     * @param space Space dto object
     * @return SpaceVo
     */
    public static SpaceVo of(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVo spaceVo = new SpaceVo();
        spaceVo.setId(String.valueOf(space.getId()));
        spaceVo.setSpaceName(space.getSpaceName());
        spaceVo.setSpaceLevel(space.getSpaceLevel());
        spaceVo.setMaxSize(space.getMaxSize());
        spaceVo.setMaxCount(space.getMaxCount());
        spaceVo.setTotalSize(space.getTotalSize());
        spaceVo.setTotalCount(space.getTotalCount());
        spaceVo.setUserId(String.valueOf(space.getUserId()));
        spaceVo.setCreateTime(space.getCreateTime());
        spaceVo.setEditTime(space.getEditTime());
        spaceVo.setUpdateTime(space.getUpdateTime());
        spaceVo.setSpaceType(space.getSpaceType());

        return spaceVo;
    }
}