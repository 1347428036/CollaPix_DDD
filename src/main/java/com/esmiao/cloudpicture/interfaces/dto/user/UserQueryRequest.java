package com.esmiao.cloudpicture.interfaces.dto.user;

import com.esmiao.cloudpicture.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Query user info request
 * @author Steven Chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;
    /**
     * The user unique id
     */
    private Long id;

    /**
     * Account
     */
    private String userAccount;

    /**
     * User nickname
     */
    private String userName;

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

}