package com.esmiao.collapix.interfaces.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Update user info request
 * @author Steven Chen
 */
@Data
public class UserUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;
    /**
     * The user unique id
     */
    private String id;

    /**
     * Account
     */
    private String userAccount;

    /**
     * Password
     */
    private String userPassword;

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