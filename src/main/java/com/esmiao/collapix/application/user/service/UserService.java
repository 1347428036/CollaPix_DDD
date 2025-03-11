package com.esmiao.collapix.application.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.common.DeleteRequest;
import com.esmiao.collapix.interfaces.dto.user.UserLoginRequest;
import com.esmiao.collapix.interfaces.dto.user.UserQueryRequest;
import com.esmiao.collapix.interfaces.dto.user.UserRegisterRequest;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * Service for handling database operations related to the [user] table.
 * This service provides methods to perform CRUD (Create, Read, Update, Delete)
 * operations on user records, as well as any additional business logic
 * required for managing user data.
 *
 * @author Steven Chen
 * @createDate 2025-01-26
 */
public interface UserService {

    /**
     * Register user.
     *
     * @param registerRequest User account.
     * @return user unique id if register success, else return 0.
     */
    long userRegister(UserRegisterRequest registerRequest);

    /**
     * Login user.
     *
     * @param loginRequest@return login user if login success, else return null.
     */
    User userLogin(UserLoginRequest loginRequest, HttpServletRequest httpRequest);

    void logout(HttpServletRequest request);

    User getUserById(Long userId);

    Page<UserVo> listUserByPage(UserQueryRequest userQueryRequest);

    /**
     * Retrieve current login user information.
     */
    User getLoginUser(HttpServletRequest request);

    Long addUser(User user);

    List<User> listUserByIds(Set<Long> idSet);

    void updateUser(User user);

    void deleteUser(DeleteRequest deleteRequest);
}