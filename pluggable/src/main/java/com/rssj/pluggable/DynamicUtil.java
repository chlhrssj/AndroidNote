package com.rssj.pluggable;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
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

    private static boolean isLoaded = false;

    /**
     * 加载插件APK
     *
     * @param apkName      apk的名字（在主APP的assets里面）
     * @param loadCallback 加载结果回调
     */
    public static void loadApk(Context context, String apkName, LoadCallback loadCallback) {
        if (isLoaded) {
            if (loadCallback != null) loadCallback.onSuccess();
            return;
        }
        new SingleJust(apkName)
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String o) throws Throwable {

                        if (apkName == null || apkName.equals("")) {
                            throw new FileNotFoundException("插件APP路径为空！");
                        }

                        //将assets的APK复制到项目私有目录下
                        InputStream inputStream = context.getAssets().open("plugin.apk");
                        File cacheFile = new File(context.getCacheDir(), apkName);
                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                        cacheFile.createNewFile();
                        FileOutputStream fileOutDate = new FileOutputStream(cacheFile);
                        byte[] buffer = new byte[7168];
                        int count = 0;
                        while ((count = inputStream.read(buffer)) > 0) {
                            fileOutDate.write(buffer, 0, count);
                        }
                        fileOutDate.close();
                        inputStream.close();

                        return cacheFile.getAbsolutePath();
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String absolutePath) throws Throwable {

//                        loadClass(context, absolutePath);

                        return absolutePath;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
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

    /**
     * 加载APK里面的类
     * @param context 上下文
     * @param apkPath 插件apk的路径
     * @throws Exception
     */
    private static void loadClass(Context context, String apkPath) throws Exception {
        // 1.获取 pathList 的字段
        Class baseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
        Field pathListField = baseDexClassLoader.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        /**
         * 获取插件的 dexElements[]
         */
        // 2.获取 DexClassLoader 类中的属性 pathList 的值
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
        Object pluginPathList = pathListField.get(dexClassLoader);
        // 3.获取 pathList 中的属性 dexElements[] 的值--- 插件的 dexElements[]
        Class pluginPathListClass = pluginPathList.getClass();
        Field pluginDexElementsField = pluginPathListClass.getDeclaredField("dexElements");
        pluginDexElementsField.setAccessible(true);
        Object[] pluginDexElements = (Object[]) pluginDexElementsField.get(pluginPathList);
        /**
         * 获取宿主的 dexElements[]
         */
        // 4.获取 PathClassLoader 类中的属性 pathList 的值
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object hostPathList = pathListField.get(pathClassLoader);
        // 5.获取 pathList 中的属性 dexElements[] 的值--- 宿主的 dexElements[]
        Class hostPathListClass = hostPathList.getClass();
        Field hostDexElementsField = hostPathListClass.getDeclaredField("dexElements");
        hostDexElementsField.setAccessible(true);
        Object[] hostDexElements = (Object[]) hostDexElementsField.get(hostPathList);
        /**
         * 将插件的 dexElements[] 和宿主的 dexElements[] 合并为一个新的 dexElements[]
         */
        // 6.创建一个新的空数组，第一个参数是数组的类型，第二个参数是数组的长度Object[]
        Object[] dexElements = (Object[]) Array.newInstance(
                hostDexElements.getClass().getComponentType(), pluginDexElements.length + hostDexElements.length);
        // 7.将插件和宿主的 dexElements[] 的值放入新的数组中
        System.arraycopy(pluginDexElements, 0, dexElements, 0, pluginDexElements.length);
        System.arraycopy(hostDexElements, 0, dexElements, pluginDexElements.length,
                hostDexElements.length);
        /**
         * 将生成的新值赋给 "dexElements" 属性
         */
        hostDexElementsField.set(hostPathList, dexElements);
    }

    /**
     * 加载APK里面的资源
     * @param context 上下文
     * @param apkPath 插件apk的路径
     */
    public static void loadResources(Context context, String apkPath) throws Exception {

    }

}
