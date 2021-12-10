package com.rssj.pluggable;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

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

    public static final String PKG = "PLUGIN_PKG";
    public static final String CLS = "PLUGIN_CLS";

    public static final String STUBCLS = "com.rssj.androidnote.plugin.StubActivity";

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

                        loadClass(context, absolutePath);
                        hook(context);
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
     * 打开插件APK
     */
    public static void startActivity(Context context, String pkg, String cls) {
        Intent intent = new Intent(context, StubActivity.class);
        intent.putExtra(PKG, pkg);
        intent.putExtra(CLS, cls);
        context.startActivity(intent);
    }

    /**
     * 针对插件化进行hook
     */
    public static void hook(Context context) {
        hook_mH_sdk29();
        if (Build.VERSION.SDK_INT >= 29) {

        } else if (Build.VERSION.SDK_INT >= 28) {

        } else if (Build.VERSION.SDK_INT >= 26) {

        } else {

        }

    }

    /**
     * Hook mH拦截ASM回来的信息
     */
    private static void hook_mH_sdk29() {
        try {
            //确定hook点，ActivityThread类的mh
            // 先拿到ActivityThread
            Class<?> ActivityThreadClz = Class.forName("android.app.ActivityThread");
            Field field = ActivityThreadClz.getDeclaredField("sCurrentActivityThread");
            field.setAccessible(true);
            Object ActivityThreadObj = field.get(null);//OK，拿到主线程实例

            //现在拿mH
            Field mHField = ActivityThreadClz.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mHObj = (Handler) mHField.get(ActivityThreadObj);//ok，当前的mH拿到了
            //再拿它的mCallback成员
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            //2.现在，造一个代理mH，
            // 他就是一个简单的Handler子类
            ProxyHandlerCallback proxyMHCallback = new ProxyHandlerCallback();//错，不需要重写全部mH，只需要对mH的callback进行重新定义

            //3.替换
            //将Handler的mCallback成员，替换成创建出来的代理HandlerCallback
            mCallbackField.set(mHObj, proxyMHCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ProxyHandlerCallback implements Handler.Callback {

        private int EXECUTE_TRANSACTION = 159;//这个值，是android.app.ActivityThread的内部类H 中定义的常量EXECUTE_TRANSACTION

        @Override
        public boolean handleMessage(Message msg) {
            boolean result = false;//返回值，请看Handler的源码，dispatchMessage就会懂了
            //Handler的dispatchMessage有3个callback优先级，首先是msg自带的callback，其次是Handler的成员mCallback,最后才是Handler类自身的handlerMessage方法,
            //它成员mCallback.handleMessage的返回值为true，则不会继续往下执行 Handler.handlerMessage
            //我们这里只是要hook，插入逻辑，所以必须返回false，让Handler原本的handlerMessage能够执行.
            if (msg.what == EXECUTE_TRANSACTION) {//这是跳转的时候,要对intent进行还原
                try {
                    //先把相关@hide的类都建好
                    Class<?> ClientTransactionClz = Class.forName("android.app.servertransaction.ClientTransaction");
                    Class<?> LaunchActivityItemClz = Class.forName("android.app.servertransaction.LaunchActivityItem");

                    Field mActivityCallbacksField = ClientTransactionClz.getDeclaredField("mActivityCallbacks");//ClientTransaction的成员
                    mActivityCallbacksField.setAccessible(true);
                    //类型判定，好习惯
                    if (!ClientTransactionClz.isInstance(msg.obj)) return false;
                    Object mActivityCallbacksObj = mActivityCallbacksField.get(msg.obj);//根据源码，在这个分支里面,msg.obj就是 ClientTransaction类型,所以，直接用
                    //拿到了ClientTransaction的List<ClientTransactionItem> mActivityCallbacks;
                    List list = (List) mActivityCallbacksObj;

                    if (list.size() == 0) return false;
                    Object LaunchActivityItemObj = list.get(0);//所以这里直接就拿到第一个就好了

                    if (!LaunchActivityItemClz.isInstance(LaunchActivityItemObj)) return false;
                    //这里必须判定 LaunchActivityItemClz，
                    // 因为 最初的ActivityResultItem传进去之后都被转化成了这LaunchActivityItemClz的实例

                    Field mIntentField = LaunchActivityItemClz.getDeclaredField("mIntent");
                    mIntentField.setAccessible(true);
                    Intent mIntent = (Intent) mIntentField.get(LaunchActivityItemObj);
                    if (mIntent != null && mIntent.getComponent() != null) {
                        String className = mIntent.getComponent().getClassName();
                        if (className != null && className.equals(STUBCLS)) {
                            //如果是占位Activity则用真实Activity替代
                            String readClassName = mIntent.getStringExtra(CLS);
                            if (readClassName != null && !readClassName.equals("")) {
//                                mIntent.getComponent()
                            }
                        }
                    }
//                    Intent oriIntent = (Intent) mIntent.getExtras().get(ORI_INTENT_TAG);
                    //那么现在有了最原始的intent，应该怎么处理呢？
                    Log.d("HOOK_RSSJ", "1111111");
                    mIntentField.set(LaunchActivityItemObj, mIntent);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

}
