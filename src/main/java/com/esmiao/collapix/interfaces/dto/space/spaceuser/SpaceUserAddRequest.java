package com.esmiao.collapix.interfaces.dto.space.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space user add request body
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
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
