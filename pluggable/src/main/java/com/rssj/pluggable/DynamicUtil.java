package com.rssj.pluggable;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.internal.operators.single.SingleJust;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Create by rssj on 2021/12/2
 * 插件化工具类
 */
public class DynamicUtil {

    /**
     * 加载插件APK
     *
     * @param apkName      apk的名字（在主APP的assets里面）
     * @param loadCallback 加载结果回调
     */
    public static void loadApk(Context context, String apkName, LoadCallback loadCallback) {
        new SingleJust(apkName)
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String o) throws Throwable {

                        if (apkName == null || apkName.equals("")) {
                            throw new FileNotFoundException("插件APP路径为空！");
                        }

                        File cacheFile = new File(context.getCacheDir(), apkName);
                        if (cacheFile.exists()) {

                        }

                        return o;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (loadCallback != null) {
                        loadCallback.onSuccess();
                    }
                }, (Consumer<Throwable>) throwable -> {
                    if (loadCallback != null) {
                        loadCallback.onFail(throwable.toString());
                    }
                });
    }

}
