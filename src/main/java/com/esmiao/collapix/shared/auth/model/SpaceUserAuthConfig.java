package com.esmiao.collapix.shared.auth.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Space user auth configuration entity
 *
 * @author Steven Chen
 * @createDate 2025-02-22
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Permission list
     */
    private List<SpaceUserPermission> permissions;

    /**
     * Role list
     */
    private List<SpaceUserRole> roles;
}
