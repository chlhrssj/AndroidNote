package com.rssj.androidnote.aop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rssj.androidnote.R
import com.rssj.androidnote.widget.CustomDialog
import com.rssj.asm.sdk.PointMarkManager



class AopActivity : AppCompatActivity() {

    lateinit var btnAdd: View
    lateinit var btnSub: View
    lateinit var btnClear: View
    lateinit var btnPrint: View
    lateinit var tv: TextView

    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aop)

        btnAdd = findViewById(R.id.btn_add)
        btnSub = findViewById(R.id.btn_sub)
        btnClear = findViewById(R.id.btn_clear)
        btnPrint = findViewById(R.id.btn_print)
        tv = findViewById(R.id.tv_1)

        btnAdd.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tv.text = "${++num}"
            }
        })

        btnAdd.setOnClickListener {
            tv.text = "${++num}"
        }

        btnClear.setOnClickListener {
            num = 0
            tv.text = "0"
        }

        btnPrint.setOnClickListener {
            showTrackInfo()
        }

    }


    private fun showTrackInfo() {
        val rootView: View =
            LayoutInflater.from(this).inflate(R.layout.dialog_track_print, null)

        val trackAdapter = TrackAdapter()
        trackAdapter.addData(PointMarkManager.instance.trackInfoList)
        val rvData: RecyclerView = rootView.findViewById(R.id.rv_data)
        rvData.run {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(this@AopActivity)
        }
        trackAdapter.notifyDataSetChanged()


        val customDialog = CustomDialog(
            this, rootView, Gravity.CENTER,
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        customDialog.show()

        customDialog.setCancelable(true)
        customDialog.setCanceledOnTouchOutside(true)

    }


}

class TrackAdapter() :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_track) {

    override fun convert(helper: BaseViewHolder, item: String) {

        helper.run {
            setText(R.id.tv_desc, item)
        }

    }

}