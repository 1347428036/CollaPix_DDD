package com.esmiao.cloudpicture.application.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.domain.user.service.UserDomainService;
import com.esmiao.cloudpicture.infrastructure.common.DeleteRequest;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.interfaces.dto.user.UserLoginRequest;
import com.esmiao.cloudpicture.interfaces.dto.user.UserQueryRequest;
import com.esmiao.cloudpicture.interfaces.dto.user.UserRegisterRequest;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.shared.auth.StpKit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * User application service
 *
 * @author Steven Chen
 * @createDate 2025-01-26
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDomainService userDomainService;

    @Override
    public long userRegister(UserRegisterRequest registerRequest) {
        ThrowErrorUtil.throwIf(registerRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        User.validateRegisterInfo(userAccount, userPassword, checkPassword);

        return userDomainService.userRegister(userAccount, userPassword, checkPassword);
    }

    @Override
    public User userLogin(UserLoginRequest loginRequest, HttpServletRequest httpRequest) {
        ThrowErrorUtil.throwIf(loginRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        User.validateLoginInfo(userAccount, userPassword);
        User user = userDomainService.userLogin(userAccount, userPassword);
        /*
         * Save to session
         * */
        httpRequest.getSession().setAttribute(UserConstant.SESSION_KEY_USER_INFO, user);
        httpRequest.getSession().setAttribute(UserConstant.SESSION_KEY_USER_INFO_STATUS, UserConstant.SESSION_VALUE_USER_INFO_STATUS_NORMAL);
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.SESSION_KEY_USER_INFO, user);

        return user;
    }

    @Override
    public void logout(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY_USER_INFO);
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.NOT_LOGIN_ERROR);

        request.getSession().invalidate();
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ThrowErrorUtil.throwIf(session == null, ErrorCodeEnum.NOT_LOGIN_ERROR);
        Integer status = (Integer) session.getAttribute(UserConstant.SESSION_KEY_USER_INFO_STATUS);
        User user = (User) session.getAttribute(UserConstant.SESSION_KEY_USER_INFO);

        ThrowErrorUtil.throwIf(status == null || user == null, ErrorCodeEnum.NOT_LOGIN_ERROR);
        if (UserConstant.SESSION_VALUE_USER_INFO_STATUS_CHANGED.equals(status)) {
            user = userDomainService.getUserById(user.getId());
        }

        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.NOT_LOGIN_ERROR);

        return user;
    }

    @Override
    public User getUserById(Long userId) {
        ThrowErrorUtil.throwIf(userId == null || userId <= 0, ErrorCodeEnum.PARAMS_ERROR);
        User user = userDomainService.getUserById(userId);
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.NOT_FOUND_ERROR);

        return user;
    }

    @Override
    public Long addUser(User user) {
        boolean saved = userDomainService.addUser(user);
        ThrowErrorUtil.throwIf(!saved, ErrorCodeEnum.OPERATION_ERROR);

        return user.getId();
    }

    @Override
    public Page<UserVo> listUserByPage(UserQueryRequest userQueryRequest) {
        ThrowErrorUtil.throwIf(userQueryRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Page<User> userPage = userDomainService.listByPage(
            new Page<>(userQueryRequest.getCurrent(), userQueryRequest.getPageSize()),
            userDomainService.generateQueryWrapper(userQueryRequest));
        Page<UserVo> userVoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        userVoPage.setRecords(userDomainService.convertToUserVo(userPage.getRecords()));

        return userVoPage;
    }

    @Override
    public List<User> listUserByIds(Set<Long> idSet) {
        ThrowErrorUtil.throwIf(idSet == null || idSet.isEmpty(), ErrorCodeEnum.PARAMS_ERROR);
        List<User> users = userDomainService.listByIds(idSet);
        ThrowErrorUtil.throwIf(users == null || users.isEmpty(), ErrorCodeEnum.NOT_FOUND_ERROR);

        return users;
    }

    @Override
    public void updateUser(User user) {
        boolean updated = userDomainService.updateById(user);
        ThrowErrorUtil.throwIf(!updated, ErrorCodeEnum.OPERATION_ERROR);
    }

    @Override
    public void deleteUser(DeleteRequest deleteRequest) {
        ThrowErrorUtil.throwIf(deleteRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long userId = NumUtil.parseLong(deleteRequest.getId());
        boolean deleted = userDomainService.removeById(userId);
        ThrowErrorUtil.throwIf(!deleted, ErrorCodeEnum.OPERATION_ERROR);
    }
}




