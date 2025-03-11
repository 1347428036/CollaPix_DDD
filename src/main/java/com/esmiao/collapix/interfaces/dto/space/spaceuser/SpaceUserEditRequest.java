package com.esmiao.collapix.interfaces.dto.space.spaceuser;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Edit space user reuqest body
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Space user unique ID
     */
    private String id;

    /**
     * Space user role：viewer/editor/admin
     */
    private String spaceRole;
}
