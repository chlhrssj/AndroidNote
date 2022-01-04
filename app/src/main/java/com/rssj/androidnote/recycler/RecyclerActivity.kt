package com.rssj.androidnote.recycler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rssj.androidnote.R
import com.rssj.androidnote.widget.RotationLayoutManager

class RecyclerActivity : AppCompatActivity() {


    lateinit var rv1: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

        rv1 = findViewById<RecyclerView>(R.id.rv_1).apply {
            val adapter1 = Adapter1()
            adapter1.addData(R.color.purple_200)
            adapter1.addData(R.color.purple_500)
            adapter1.addData(R.color.purple_700)
            adapter1.addData(R.color.purple_200)
            adapter1.addData(R.color.purple_500)
            adapter1.addData(R.color.purple_700)
            adapter1.addData(R.color.purple_200)
            adapter1.addData(R.color.purple_500)
            adapter1.addData(R.color.purple_700)
            this.adapter = adapter1
            this.layoutManager = RotationLayoutManager()
        }

    }

    private class Adapter1 : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_rv1) {

        override fun convert(helper: BaseViewHolder, item: Int) {

            val imageView = helper.getView<ImageView>(R.id.imageView)
            imageView.setImageResource(item)

        }

    }

}