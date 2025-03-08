package com.esmiao.cloudpicture.domain.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.domain.user.reposiitory.UserRepository;
import com.esmiao.cloudpicture.domain.user.service.UserDomainService;
import com.esmiao.cloudpicture.domain.user.valueObject.UserRoleEnum;
import com.esmiao.cloudpicture.interfaces.dto.user.UserQueryRequest;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.domain.picture.constant.PaginationConstant;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 针对表【user(用户表)】的数据库操作Service实现
 *
 * @author Steven Chen
 * @createDate 2025-01-26
 */
@RequiredArgsConstructor
@Service
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // Check if the user account already exists
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.userRepository.getBaseMapper().selectCount(queryWrapper);
        ThrowErrorUtil.throwIf(
            count > 0,
            ErrorCodeEnum.PARAMS_ERROR,
            "Account already exists");
        // Encrypt password
        String encryptPassword = encryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("Unknown name");
        user.setUserRole(UserRoleEnum.USER.getValue());
        // Save to database
        boolean saved = userRepository.save(user);
        ThrowErrorUtil.throwIf(
            !saved,
            ErrorCodeEnum.SYSTEM_ERROR,
            "Registration failed");

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword) {
        String encryptPassword = encryptPassword(userPassword);
        User user = userRepository.query()
            .eq("userAccount", userAccount)
            .eq("userPassword", encryptPassword)
            .one();
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.PARAMS_ERROR, "User account or password is wrong");

        return user;
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getById(userId);
    }

    @Override
    public String encryptPassword(String userPassword) {
        // Encrypt the user password
        return DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
    }

    @Override
    public QueryWrapper<User> generateQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowErrorUtil.throwIf(userQueryRequest == null, ErrorCodeEnum.PARAMS_ERROR, "请求参数为空");
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), PaginationConstant.ORDER_ASC.equals(sortOrder), sortField);

        return queryWrapper;
    }

    @Override
    public boolean addUser(User user) {
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.PARAMS_ERROR);
        user.setUserPassword((UserConstant.DEFAULT_PASSWORD));
        return userRepository.save(user);
    }

    @Override
    public Page<User> listByPage(Page<User> page, QueryWrapper<User> queryWrapper) {
        return userRepository.page(page, queryWrapper);
    }

    @Override
    public List<User> listByIds(Set<Long> idSet) {
        return userRepository.listByIds(idSet);
    }

    @Override
    public boolean updateById(User user) {
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.PARAMS_ERROR);
        ThrowErrorUtil.throwIf(user.getId() == null || user.getId() <= 0, ErrorCodeEnum.PARAMS_ERROR);
        user.setUserPassword(this.encryptPassword(user.getUserPassword()));

        return userRepository.updateById(user);
    }

    @Override
    public boolean removeById(Long id) {
        ThrowErrorUtil.throwIf(id == null || id <= 0, ErrorCodeEnum.PARAMS_ERROR);
        return userRepository.removeById(id);
    }

    @Override
    public List<UserVo> convertToUserVo(List<User> users) {
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }

        return users.stream()
            .map(UserVo::of)
            .collect(Collectors.toList());
    }
}




