package com.rssj.androidnote.base

import android.app.Application
import com.rssj.asm.sdk.PointMarkManager

/**
 * Create by rssj on 2021/11/12
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        PointMarkManager.init(this)
    }

}