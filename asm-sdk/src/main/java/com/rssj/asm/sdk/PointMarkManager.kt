package com.rssj.asm.sdk

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import java.util.*

/**
 * Create by rssj on 2021/11/11
 */
@Keep
class PointMarkManager private constructor(app: Application) {

    companion object {
        @JvmStatic
        lateinit var instance: PointMarkManager

        @JvmStatic
        fun init(application: Application) {
            instance = PointMarkManager(application)
        }
    }

    var application: Application = app
    var trackInfoList: MutableList<String> = LinkedList()


    /**
     * 点击记录
     */
    fun trackClick(className: String) {
        Log.i("PointMarkManager", "-----  $className")
        trackInfoList.add("$className 被点击了 ${System.currentTimeMillis()}")
        if (trackInfoList.size > 100) {
            trackInfoList.removeFirst()
        }
    }

    /**
     * 生命周期记录
     */
    fun trackLifecycle(className: String, lifecycleEvent: String) {
        trackInfoList.add("$className --- $lifecycleEvent --- ${System.currentTimeMillis()}")
        if (trackInfoList.size > 100) {
            trackInfoList.removeFirst()
        }
    }

    /**
     * 打印记录
     */
    fun printEvent() {
        for (str in trackInfoList) {
            Log.i("PointMark --- ", str)
        }
    }

}