package com.esmiao.collapix.shared.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Space user role
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Role key
     */
    private String key;

    /**
     * Role name
     */
    private String name;

    /**
     * Permission key list
     */
    private List<String> permissions;

    /**
     * Role description
     */
    private String description;
}
