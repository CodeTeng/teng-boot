package com.lt.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * Redis key 前缀
     */
    String key() default "";

    /**
     * 限制次数
     */
    int limit() default 10;

    /**
     * 时间窗口（秒）
     */
    int seconds() default 60;
}
