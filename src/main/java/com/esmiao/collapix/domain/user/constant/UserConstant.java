package com.esmiao.collapix.domain.user.constant;

/**
 * The constants related to the user
 * @author Steven Chen
 */

public interface UserConstant {

    // region Session Key
    String SESSION_KEY_USER_INFO = "user_info";
    String SESSION_KEY_USER_INFO_STATUS = "user_info_status";
    // endregion

    // region Session value
    Integer SESSION_VALUE_USER_INFO_STATUS_NORMAL = 0;
    Integer SESSION_VALUE_USER_INFO_STATUS_CHANGED = 1;
    // endregion

    // region User Role
    String ROLE_ADMIN = "admin";
    String ROLE_USER = "user";
    // endregion

    String DEFAULT_PASSWORD = "12345678";
    String SALT = "adawoihjgerresijqwdqpw";
}
