package com.esmiao.cloudpicture.infrastructure.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.annotation.RoleValidation;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

/**
 * The aop implementation to validate user role in every endpoint which annotated with {@code RoleValidation}
 *
 * @author Steven Chen
 */

@Aspect
@Component
public class RoleValidationInterceptor {

    private final UserService userService;

    public RoleValidationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Around("@annotation(roleValidation)")
    public Object validateRole(ProceedingJoinPoint joinPoint, RoleValidation roleValidation) throws Throwable {
        String[] roles = roleValidation.roles();
        if (ArrayUtil.isEmpty(roles)) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        User user = userService.getLoginUser(requestAttributes.getRequest());
        ThrowErrorUtil.throwIf(user == null, ErrorCodeEnum.NOT_LOGIN_ERROR);
        /*
         * Validate user role
         * */
        Set<String> roleSet = CollUtil.newHashSet(roles);
        boolean useAnyRole = roleValidation.anyRole();
        ThrowErrorUtil.throwIf(useAnyRole && !roleSet.contains(user.getUserRole()), ErrorCodeEnum.NO_PERMISSION_ERROR);

        return joinPoint.proceed();
    }
}
