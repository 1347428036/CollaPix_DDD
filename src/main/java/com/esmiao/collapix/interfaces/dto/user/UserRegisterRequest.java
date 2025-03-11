package com.esmiao.collapix.interfaces.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
