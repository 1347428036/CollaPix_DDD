package com.esmiao.cloudpicture.interfaces.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.user.constant.UserConstant;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.annotation.RoleValidation;
import com.esmiao.cloudpicture.infrastructure.common.CommonResponse;
import com.esmiao.cloudpicture.infrastructure.common.DeleteRequest;
import com.esmiao.cloudpicture.infrastructure.utils.ResponseUtil;
import com.esmiao.cloudpicture.interfaces.dto.user.*;
import com.esmiao.cloudpicture.interfaces.assembler.UserFactory;
import com.esmiao.cloudpicture.interfaces.vo.user.LoginUserVo;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author Steven Chen
 */
@RestController("User Controller")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public CommonResponse<LoginUserVo> getLoginUser(HttpServletRequest httpRequest) {
        return ResponseUtil.success(LoginUserVo.of(userService.getLoginUser(httpRequest)));
    }

    @GetMapping("/full-info")
    @RoleValidation(roles = {UserConstant.ROLE_ADMIN})
    public CommonResponse<User> getUserById(@RequestParam("id") Long id) {
        return ResponseUtil.success(userService.getUserById(id));
    }

    @GetMapping("/vo")
    public CommonResponse<UserVo> getUserVoById(@RequestParam("id") Long id) {
        return ResponseUtil.success(UserVo.of(userService.getUserById(id)));
    }

    @PostMapping("/register")
    public CommonResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ResponseUtil.success(userService.userRegister(userRegisterRequest));
    }

    @PostMapping("/login")
    public CommonResponse<LoginUserVo> userLogin(
        @RequestBody UserLoginRequest userLoginRequest,
        HttpServletRequest httpRequest) {

        return ResponseUtil.success(LoginUserVo.of(userService.userLogin(userLoginRequest, httpRequest)));
    }

    @PostMapping("/logout")
    public CommonResponse<Boolean> userLogout(HttpServletRequest httpRequest) {
        userService.logout(httpRequest);
        return ResponseUtil.success(true);
    }

    @PostMapping("/add")
    @RoleValidation(roles = {UserConstant.ROLE_ADMIN})
    public CommonResponse<Long> userAdd(@RequestBody UserAddRequest userAddRequest) {
        User user = UserFactory.build(userAddRequest);
        return ResponseUtil.success(userService.addUser(user));
    }

    @PostMapping("/list")
    @RoleValidation(roles = {UserConstant.ROLE_ADMIN})
    public CommonResponse<Page<UserVo>> listUser(@RequestBody UserQueryRequest queryRequest) {
        return ResponseUtil.success(userService.listUserByPage(queryRequest));
    }

    @PutMapping
    @RoleValidation(roles = {UserConstant.ROLE_ADMIN})
    public CommonResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateRequest) {
        User user = UserFactory.build(updateRequest);
        userService.updateUser(user);
        return ResponseUtil.success(true);
    }

    @DeleteMapping
    @RoleValidation(roles = {UserConstant.ROLE_ADMIN})
    public CommonResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        userService.deleteUser(deleteRequest);
        return ResponseUtil.success(true);
    }
}
