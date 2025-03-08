package com.esmiao.cloudpicture.shared.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space user permission entity
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Permission key
     */
    private String key;

    /**
     * Permission name
     */
    private String name;

    /**
     * Permission description
     */
    private String description;
}
