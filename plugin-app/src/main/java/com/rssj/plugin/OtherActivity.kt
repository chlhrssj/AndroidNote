package com.rssj.plugin

import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OtherActivity : AppCompatActivity() {

    lateinit var apkPath: String

    private var pluginAssetManager: AssetManager? = null
    private var pluginResources: Resources? = null
    private var pluginTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        apkPath = intent.getStringExtra("APK_PATH").toString()
        Log.i("RssjPlugin --- ", " ---- $apkPath")
        handleResources()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)

        findViewById<TextView>(R.id.tv).let {
            it.setText(R.string.plugin_hello)
        }

    }

    override fun getResources(): Resources? {
        return pluginResources ?: super.getResources()
    }

    override fun getAssets(): AssetManager {
        return pluginAssetManager ?: super.getAssets()
    }

    private fun handleResources() {
        Log.i("RssjPlugin --- ", "begin handle")
        try {
            pluginAssetManager = AssetManager::class.java.newInstance()
            val addAssetPathMethod = pluginAssetManager?.javaClass?.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod?.invoke(pluginAssetManager, apkPath)
        } catch (e: Exception) {
            Log.i("RssjPlugin --- ", e.printStackTrace().toString())
//            e.printStackTrace()
        }
        pluginResources = Resources(pluginAssetManager, super.getResources().displayMetrics, super.getResources().configuration)
        pluginTheme = pluginResources?.newTheme()
        pluginTheme?.setTo(super.getTheme())
        Log.i("RssjPlugin --- ", "end handle")
    }

}