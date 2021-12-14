package com.rssj.plugin

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OtherActivity : Activity() {

    lateinit var apkPath: String

    private var pluginAssetManager: AssetManager? = null
    private var pluginResources: Resources? = null
    private var pluginTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        apkPath = intent.getStringExtra("APK_PATH")?:"plugin.apk"
        handleResources()
        super.onCreate(savedInstanceState)
        val root = layoutInflater.inflate(R.layout.activity_other, null)
        Log.i("RSSJ_PLUGIN", root.toString())
        setContentView(root)
        val textView = root.findViewById<TextView>(R.id.tv)
        Log.i("RSSJ_PLUGIN", textView.toString())
        val str = getString(R.string.plugin_hello)
        Log.i("RSSJ_PLUGIN", str)
        textView.text = str
        root.setBackgroundResource(R.color.white)

    }

    override fun getResources(): Resources? {
        return pluginResources ?: super.getResources()
    }

    override fun getAssets(): AssetManager {
        return pluginAssetManager ?: super.getAssets()
    }

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
//        pluginTheme = pluginResources!!.newTheme()
//        pluginTheme!!.setTo(super.getTheme())
    }

}