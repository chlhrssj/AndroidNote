package com.chlhrssj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by rssj on 2021/9/27
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface BindClick {

    int[] value();

}
