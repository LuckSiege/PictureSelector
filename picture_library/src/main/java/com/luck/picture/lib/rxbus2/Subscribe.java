package com.luck.picture.lib.rxbus2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rxbus
 * Created by gorden on 2016/7/23.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    int code() default -1;

    ThreadMode threadMode() default ThreadMode.CURRENT_THREAD;
}
