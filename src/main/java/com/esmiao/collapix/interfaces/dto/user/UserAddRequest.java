package com.esmiao.collapix.interfaces.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Add new user request
 * @author Steven Chen
 */
@Data
public class UserAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Account
     */
    private String userAccount;

    /**
     * User nickname
     */
    private String userName;

    /**
     * User avatar address
     */
    private String userAvatar;

    /**
     * User profile
     */
    private String userProfile;

    /**
     * User role
     */
    private String userRole;
}
