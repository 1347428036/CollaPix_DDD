package com.esmiao.cloudpicture.infrastructure.annotation;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.esmiao.cloudpicture.shared.auth.StpKit;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Space permission check: must have the specified permission to enter this method
 * <p> Can be annotated on a method or class (equivalent to annotating all methods in this class)
 *
 * @author Steven Chen
 * @createDate 2025-02-24
 */
@SaCheckPermission(type = StpKit.SPACE_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaSpaceCheckPermission {

    /**
     * Permission codes to be checked
     *
     * @return Permission codes to be checked
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] value() default {};

    /**
     * Verification mode: AND | OR, default is AND
     *
     * @return Verification mode
     */
    @AliasFor(annotation = SaCheckPermission.class)
    SaMode mode() default SaMode.AND;

    /**
     * Secondary option when permission check fails, either of the two can pass the check
     *
     * <p>
     * Example 1: @SaCheckPermission(value="user-add", orRole="admin"),
     * means that the request only needs to have user-add permission or admin role to pass the check.
     * </p>
     *
     * <p>
     * Example 2: orRole = {"admin", "manager", "staff"}, having any one of the three roles is enough. <br>
     * Example 3: orRole = {"admin, manager, staff"}, all three roles must be present.
     * </p>
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] orRole() default {};

}