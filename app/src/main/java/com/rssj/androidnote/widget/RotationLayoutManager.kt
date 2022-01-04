package com.rssj.androidnote.widget

import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView

/**
 * Create by rssj on 2022/1/4
 */
class RotationLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}