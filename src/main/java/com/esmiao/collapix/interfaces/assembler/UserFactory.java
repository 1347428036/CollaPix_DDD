package com.esmiao.collapix.interfaces.assembler;


import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.user.UserAddRequest;
import com.esmiao.collapix.interfaces.dto.user.UserUpdateRequest;

/**
 * A factory class to create specific {@code User} instance.
 * @author Steven Chen
 */

public class UserFactory {

    private UserFactory(){}

    public static User build(UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            return null;
        }

        User user = new User();
        user.setUserName(userAddRequest.getUserName());
        user.setUserAccount(userAddRequest.getUserAccount());
        user.setUserAvatar(userAddRequest.getUserAvatar());
        user.setUserProfile(userAddRequest.getUserProfile());
        user.setUserRole(userAddRequest.getUserRole());

        return user;
    }

    public static User build(UserUpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }

        User user = new User();
        user.setId(NumUtil.parseLong(updateRequest.getId()));
        user.setUserName(updateRequest.getUserName());
        user.setUserAvatar(updateRequest.getUserAvatar());
        user.setUserProfile(updateRequest.getUserProfile());
        user.setUserRole(updateRequest.getUserRole());

        return user;
    }
}
