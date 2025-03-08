package com.esmiao.cloudpicture.interfaces.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Steven Chen
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    private String userAccount;

    private String userPassword;
}
