package com.esmiao.cloudpicture.interfaces.vo.user;

import com.esmiao.cloudpicture.domain.user.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User info view object
 * @author Steven Chen
 */
@Data
public class UserVo implements Serializable {

    /**
     * User unique id
     * */
    private String id;

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

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    public static UserVo of(User user) {
        if (user == null) {
            return null;
        }

        UserVo userVo = new UserVo();
        userVo.setId(String.valueOf(user.getId()));
        userVo.setUserAccount(user.getUserAccount());
        userVo.setUserName(user.getUserName());
        userVo.setUserAvatar(user.getUserAvatar());
        userVo.setUserProfile(user.getUserProfile());
        userVo.setUserRole(user.getUserRole());
        userVo.setCreateTime(user.getCreateTime());

        return userVo;
    }
}
