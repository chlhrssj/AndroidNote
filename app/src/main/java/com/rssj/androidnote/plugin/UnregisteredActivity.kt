package com.rssj.androidnote.plugin

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rssj.androidnote.R
import java.lang.IllegalStateException

/**
 * Create by rssj on 2021/12/14
 *
 * Hook AppCompatActivity大失败，在AppCompatDelegateImpl.setContentView()的时候会判定AppCompatTheme相关属性，
 * 但是hook了AssetManager之后还是拿不到相关属性，有兴趣再看看这方面的问题
 *
 */
class UnregisteredActivity : Activity() {

    lateinit var apkPath: String

    private var pluginAssetManager: AssetManager? = null
    private var pluginResources: Resources? = null
    private var pluginTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        apkPath = intent.getStringExtra("APK_PATH").toString()
        handleResources()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aop)

    }

    override fun getResources(): Resources? {
        return pluginResources ?: super.getResources()
    }

    override fun getAssets(): AssetManager {
        return pluginAssetManager ?: super.getAssets()
    }
//
//    override fun getTheme(): Resources.Theme {
//        return pluginTheme ?: super.getTheme()
//    }

    private fun handleResources() {
        try {
            pluginAssetManager = AssetManager::class.java.newInstance()
            val addAssetPathMethod = pluginAssetManager!!.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod.invoke(pluginAssetManager, apkPath)
        } catch (e: Exception) {
//            e.printStackTrace()
        }
        pluginResources = Resources(pluginAssetManager, super.getResources().displayMetrics, super.getResources().configuration)
        pluginTheme = pluginResources!!.newTheme()
        pluginTheme!!.setTo(super.getTheme())
    }

}