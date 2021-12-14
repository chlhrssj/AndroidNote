package com.rssj.androidnote.plugin

import android.content.ComponentName
import android.content.Intent
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.rssj.androidnote.R
import com.rssj.pluggable.DynamicUtil
import com.rssj.pluggable.LoadCallback
import java.io.File

class PluginActivity : AppCompatActivity() {

    lateinit var btnLoad: Button
    lateinit var btnOpen: Button
    lateinit var swLoadState: SwitchMaterial

    private var isLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val a: TypedArray = obtainStyledAttributes(R.styleable.AppCompatTheme)
        a.recycle()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin)

        btnLoad = findViewById<Button>(R.id.btn_load).apply {
            setOnClickListener {
                if (!isLoaded) {
                    DynamicUtil.loadApk(
                        this@PluginActivity,
                        DynamicUtil.APK_NAME,
                        object : LoadCallback {
                            override fun onSuccess() {
                                isLoaded = true
                                swLoadState.isChecked = true
                                Toast.makeText(this@PluginActivity, "插件加载成功！", Toast.LENGTH_LONG).show()
                            }

                            override fun onFail(errMsg: String?) {
                                Toast.makeText(this@PluginActivity, errMsg, Toast.LENGTH_LONG).show()
                            }

                        }
                    )
                }
            }
        }

        swLoadState = findViewById<SwitchMaterial>(R.id.sw_loadstate).apply {
            isClickable = false
        }

        btnOpen = findViewById<Button>(R.id.btn_open).apply {
            setOnClickListener {
                if (isLoaded) {
                    openPlugin()
                } else {
                    Toast.makeText(this@PluginActivity, "请先加载apk!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    /**
     * 打开插件Activity
     */
    private fun openPlugin() {
        DynamicUtil.startActivity(this, "com.rssj.androidnote", "com.rssj.plugin.OtherActivity")
//        DynamicUtil.startActivity(this, "com.rssj.androidnote", "com.rssj.androidnote.plugin.UnregisteredActivity")
//        DynamicUtil.startActivity(this, "com.rssj.androidnote", "com.rssj.androidnote.plugin.StubActivity")
    }
}