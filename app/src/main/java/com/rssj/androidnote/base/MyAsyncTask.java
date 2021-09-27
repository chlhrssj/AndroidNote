package com.rssj.androidnote.base;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 步骤1：创建AsyncTask子类
 * 注：
 *   a. 继承AsyncTask类
 *   b. 为3个泛型参数指定类型；若不使用，可用java.lang.Void类型代替
 *   c. 根据需求，在AsyncTask子类内实现核心方法
 */

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    final String TAG = "AsyncTask";


    // 方法1：onPreExecute（）
    // 作用：执行 线程任务前的操作
    // 注：根据需求复写
    @Override
    protected void onPreExecute() {

        Log.i(TAG, "开始前" + " ----- " + Thread.currentThread().getName());

    }

    // 方法2：doInBackground（）
    // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
    // 注：必须复写，从而自定义线程任务
    @Override
    protected String doInBackground(String... params) {

        Log.i(TAG, "后台线程" + " ----- " + Thread.currentThread().getName());
        // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
        publishProgress(0);
        publishProgress(10);
        publishProgress(50);
        publishProgress(100);

        return "已经执行完毕";
    }

    // 方法3：onProgressUpdate（）
    // 作用：在主线程 显示线程任务执行的进度
    // 注：根据需求复写
    @Override
    protected void onProgressUpdate(Integer... progresses) {
        Log.i(TAG, progresses[0].toString() + "%" + " ----- " + Thread.currentThread().getName());
    }

    // 方法4：onPostExecute（）
    // 作用：接收线程任务执行结果、将执行结果显示到UI组件
    // 注：必须复写，从而自定义UI操作
    @Override
    protected void onPostExecute(String result) {

        Log.i(TAG, result + " ----- " + Thread.currentThread().getName());

    }

    // 方法5：onCancelled()
    // 作用：将异步任务设置为：取消状态
    @Override
    protected void onCancelled() {
        Log.i(TAG, "取消了");
    }
}