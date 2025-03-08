package com.esmiao.cloudpicture.domain.user.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.esmiao.cloudpicture.domain.user.valueObject.UserRoleEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User table
 * @author Steven Chen
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;
    /**
     * The user unique id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;

    /**
     * Edit time
     */
    private LocalDateTime editTime;

    /**
     * Whether deleted
     */
    @TableLogic
    private Integer isDelete;

    public static void validateRegisterInfo(String userAccount, String userPassword, String checkPassword) {
        ThrowErrorUtil.throwIf(
            StrUtil.hasBlank(userAccount, userPassword, checkPassword),
            ErrorCodeEnum.PARAMS_ERROR,
            "Parameters cannot be empty");
        ThrowErrorUtil.throwIf(
            userAccount.length() < 4,
            ErrorCodeEnum.PARAMS_ERROR,
            "User account is too short");
        ThrowErrorUtil.throwIf(
            userPassword.length() < 8 || checkPassword.length() < 8,
            ErrorCodeEnum.PARAMS_ERROR,
            "User password is too short");
        ThrowErrorUtil.throwIf(
            !userPassword.equals(checkPassword),
            ErrorCodeEnum.PARAMS_ERROR,
            "The two input passwords are inconsistent");
    }

    public static void validateLoginInfo(String userAccount, String userPassword) {
        ThrowErrorUtil.throwIf(
            StrUtil.hasBlank(userAccount, userPassword),
            ErrorCodeEnum.PARAMS_ERROR,
            "Parameters cannot be empty");
        ThrowErrorUtil.throwIf(
            userAccount.length() < 4,
            ErrorCodeEnum.PARAMS_ERROR,
            "User account is too short");
        ThrowErrorUtil.throwIf(
            userPassword.length() < 8,
            ErrorCodeEnum.PARAMS_ERROR,
            "User password is too short");
    }

    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(userRole);
    }
}