package com.rssj.androidnote.aop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.rssj.androidnote.R

class AopActivity : AppCompatActivity() {

    lateinit var btnAdd: View
    lateinit var btnSub: View
    lateinit var btnClear: View
    lateinit var tv: TextView

    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aop)

        btnAdd = findViewById(R.id.btn_add)
        btnSub = findViewById(R.id.btn_sub)
        btnClear = findViewById(R.id.btn_clear)
        tv = findViewById(R.id.tv_1)

        btnAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tv.text = "${++num}"
            }
        })

        btnAdd.setOnClickListener {
            tv.text = "${++num}"
        }

    }
}