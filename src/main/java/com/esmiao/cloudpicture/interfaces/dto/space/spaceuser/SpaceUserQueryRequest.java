package com.esmiao.cloudpicture.interfaces.dto.space.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Query space user request
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * Space ID
     */
    private String spaceId;

    /**
     * User ID
     */
    private String userId;

    /**
     * Space user role：viewer/editor/admin
     */
    private String spaceRole;
}
