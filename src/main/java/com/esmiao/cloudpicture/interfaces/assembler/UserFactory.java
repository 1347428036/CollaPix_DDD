package com.esmiao.cloudpicture.interfaces.assembler;


import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.interfaces.dto.user.UserAddRequest;
import com.esmiao.cloudpicture.interfaces.dto.user.UserUpdateRequest;

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
