package com.rssj.asm.sdk

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.Keep

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
    var trackInfoList: MutableList<String> = ArrayList()

    /**
     * 点击屏蔽间隔
     */
    var clickInterval = 400
    var timer: Long = 0

    /**
     * 判断是否双击
     */
    fun doubleClick(): Boolean {
        val curr = System.currentTimeMillis()
        return if (curr - timer < clickInterval) {
            false
        } else {
            timer = curr
            true
        }
    }

    /**
     * 点击记录
     */
    fun trackClick(className: String) {
        trackInfoList.add("$className 被点击了 ${System.currentTimeMillis()}")
    }

    /**
     * 生命周期记录
     */
    fun trackLifecycle(className: String, lifecycleEvent: String) {
        trackInfoList.add("$className --- $lifecycleEvent --- ${System.currentTimeMillis()}")
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