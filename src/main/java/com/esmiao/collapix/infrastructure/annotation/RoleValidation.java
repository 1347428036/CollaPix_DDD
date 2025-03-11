package com.esmiao.collapix.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates which user role is required to access the endpoint.
 * @author Steven Chen
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleValidation {

    String[] roles() default {};

    boolean anyRole() default true;
}
