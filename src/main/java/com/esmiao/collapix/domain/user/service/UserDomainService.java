package com.esmiao.collapix.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.interfaces.dto.user.UserQueryRequest;
import com.esmiao.collapix.interfaces.vo.user.UserVo;

import java.util.List;
import java.util.Set;

/**
 * Service for handling database operations related to the [user] table.
 * This service provides methods to perform CRUD (Create, Read, Update, Delete)
 * operations on user records, as well as any additional business logic
 * required for managing user data.
* @author Steven Chen
* @createDate 2025-01-26
*/
public interface UserDomainService {

    /**
     * Register user.
     * @param userAccount User account.
     * @param userPassword User password.
     * @param checkPassword The password for double check.
     * @return user unique id if register success, else return 0.
     * */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * Login user.
     *
     * @param userAccount  User account.
     * @param userPassword User password.
     * @return login user if login success, else return null.
     */
    User userLogin(String userAccount, String userPassword);
    /**
     * Retrieve current login user information.
     * */
    User getUserById(long userId);

    boolean addUser(User user);

    Page<User> listByPage(Page<User> page, QueryWrapper<User> queryWrapper);

    List<User> listByIds(Set<Long> idSet);

    boolean updateById(User user);

    boolean removeById(Long id);

    String encryptPassword(String userPassword);

    QueryWrapper<User> generateQueryWrapper(UserQueryRequest userQueryRequest);

    List<UserVo> convertToUserVo(List<User> users);
}