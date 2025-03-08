package com.esmiao.cloudpicture.interfaces.vo.user;

import com.esmiao.cloudpicture.domain.user.entity.User;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Login user info without sensitive data.
 * @author Steven Chen
 */
@Data
public class LoginUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    private String id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像地址
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    public static LoginUserVo of(User user) {
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(user.getId().toString());
        loginUserVo.setUserAccount(user.getUserAccount());
        loginUserVo.setUserName(user.getUserName());
        loginUserVo.setUserAvatar(user.getUserAvatar());
        loginUserVo.setUserProfile(user.getUserProfile());
        loginUserVo.setUserRole(user.getUserRole());

        return loginUserVo;
    }
}
