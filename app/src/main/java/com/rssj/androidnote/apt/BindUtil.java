package com.rssj.androidnote.apt;

import android.app.Activity;

import java.lang.reflect.Method;

/**
 * Create by rssj on 2021/9/28
 */
public class BindUtil {

    static final String ClassName = "com.rssj.binder.RssjBindUtil";
    static final String BindMethod = "bind";

    public static <T extends Activity> void bind( T target) {

        try {
            Class<?> clazz = Class.forName(ClassName);
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod(BindMethod, target.getClass());
            method.invoke(obj, target);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
