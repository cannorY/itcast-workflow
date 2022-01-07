package com.itheima.activiti.auth.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略token验证，如：
 * 获取图片、登录接口等
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreToken {
    boolean required() default true;
}
