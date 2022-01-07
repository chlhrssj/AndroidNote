package com.rssj.androidnote.widget

import android.graphics.PointF
import android.util.Log
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Create by rssj on 2022/1/4
 */
class RotationLayoutManager : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider {

    companion object {

        private const val TAG = "RotationLayoutManager"

        private const val FILL_START = -1
        private const val FILL_END = 1

        private const val DEFAULT_SCALE = 0.7f

    }

    private var mPendingPosition = RecyclerView.NO_POSITION

    //将要滚到的position
    private var mPendingScrollToPosition: Int = RecyclerView.NO_POSITION

    //将要填充的view的position
    private var mPendingFillPosition: Int = RecyclerView.NO_POSITION

    //直接搞个SnapHelper来findCenterView
    private val mSnapHelper = LinearSnapHelper()

    //Recyclerview内置的帮助类
    private val mOrientationHelper: OrientationHelper =
        OrientationHelper.createHorizontalHelper(this)

    //整个布局的中心
    private val centerLayoutX by lazy { width / 2 }
    private val centerLayoutY by lazy { height / 2 }

    //每个item边界的间隔
    private var interval = 0

    //每个item中点的距离
    private var radius = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        var currentPosition = 0
        //当childCount != 0时，证明是已经填充过View的，因为有回收
        //所以直接赋值为第一个child的position就可以
        if (childCount != 0) {
            currentPosition = getPosition(getChildAt(0)!!)
        }

        if (mPendingPosition != RecyclerView.NO_POSITION) {
            currentPosition = mPendingPosition
        }

        //轻量级的将view移除屏幕
        detachAndScrapAttachedViews(recycler)
        //开始填充view

        fillLayout(recycler, state, currentPosition)

        //分发Item Fill事件
        dispatchOnItemFillListener()
//        logChildCount("onLayoutChildren", recycler)
    }

    private fun logChildCount(tag: String, recycler: RecyclerView.Recycler) {
        Log.d(tag, "childCount = $childCount -- scrapSize = ${recycler.scrapList.size}")
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        //填充View，consumed就是修复后的移动值
        val consumed = fillScroll(dx, recycler, state)
        //移动View
        mOrientationHelper.offsetChildren(-consumed)
        //回收View
        recycleChildren(consumed, recycler)
        //变换children
        transformChildren()
        //分发事件
        dispatchOnItemFillListener()
        //输出children
//        logChildCount("scrollHorizontallyBy", recycler)
        return consumed
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position >= itemCount) return
        mPendingPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller =
            LinearSmoothScroller(recyclerView.context)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0)!!)
        val direction = if (targetPosition < firstChildPos) -1 else 1
        return PointF(direction.toFloat(), 0f)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (childCount == 0) return
//        logDebug("onScrollStateChanged -- $state")

        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            val centerView = getSelectedView() ?: return
            val centerPosition = getPosition(centerView)
            scrollToCenter(centerView, centerPosition)
        }
    }

    /**
     * 初始化填充view
     */
    private fun fillLayout(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        currentPosition: Int
    ) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        var centerX = 0
        var centerY = 0

        if (currentPosition != 0 && state.itemCount > currentPosition + 1) {
            //左边有view
            centerX = 0
            centerY = (height - paddingTop - paddingBottom) / 2

            val view = recycler.getViewForPosition(currentPosition - 1)
            addView(view)
            measureChild(view, 0, 0)

            val measureWidth = getDecoratedMeasuredWidth(view)
            val measureHeight = getDecoratedMeasuredHeight(view)

            left = centerX - measureWidth / 2
            right = centerX + measureWidth / 2
            top = 0
            bottom = top + measureHeight
            layoutDecorated(view, left, top, right, bottom)
        }

        //居中的view
        centerX = width / 2
        centerY = height / 2

        val view = recycler.getViewForPosition(currentPosition)
        addView(view)
        measureChild(view, 0, 0)

        val measureWidth = getDecoratedMeasuredWidth(view)
        val measureHeight = getDecoratedMeasuredHeight(view)

        left = centerX - measureWidth / 2
        right = centerX + measureWidth / 2
        top = centerY - measureHeight / 2
        bottom = centerY + measureHeight / 2
        layoutDecorated(view, left, top, right, bottom)

        interval = max(width / 2 - measureWidth, 0)
        radius = width / 2

        if (currentPosition < state.itemCount - 1) {
            //右边有view
            centerX = width
            centerY = (height - paddingTop - paddingBottom) / 2

            val view = recycler.getViewForPosition(currentPosition + 1)
            addView(view)
            measureChild(view, 0, 0)

            val measureWidth = getDecoratedMeasuredWidth(view)
            val measureHeight = getDecoratedMeasuredHeight(view)

            left = centerX - measureWidth / 2
            right = centerX + measureWidth / 2
            top = 0
            bottom = top + measureHeight
            layoutDecorated(view, left, top, right, bottom)
        }

        transformChildren()
    }

    private fun recycleChildren(
        dx: Int,
        recycler: RecyclerView.Recycler
    ) {
        //要回收View的集合，暂存
        val recycleViews = hashSetOf<View>()

        //dx>0就是手指从右滑向左，所以要回收前面的children
        if (dx > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)!!
                val right = getDecoratedRight(child)
                //itemView的right<0就是要超出屏幕要回收View
                if (right > 0) break
                recycleViews.add(child)
            }
        }

        //dx<0就是手指从左滑向右，所以要回收后面的children
        if (dx < 0) {
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)!!
                val left = getDecoratedLeft(child)

                //itemView的left>recyclerView.width就是要超出屏幕要回收View
                if (left < width) break
                recycleViews.add(child)
            }
        }

        //真正把View移除掉
        for (view in recycleViews) {
            removeAndRecycleView(view, recycler)
        }
        recycleViews.clear()
    }

    private fun fillScroll(
        delta: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val absDelta = kotlin.math.abs(delta)
        var remainSpace = kotlin.math.abs(delta)

        val fillDirection = if (delta > 0) FILL_END else FILL_START

        //检查是否滚动到了顶部或者底部
        if (checkScrollToEdge(fillDirection, state)) {
           return 0
        }

        //检查滚动距离是否可以填充下一个view
        if (canNotFillScroll(fillDirection, absDelta)) {
            return delta
        }

        //获取将要填充的view
        mPendingFillPosition = getPendingFillPosition(fillDirection)
        Log.i(TAG, "$mPendingFillPosition")

        //
        if (remainSpace > 0 && hasMore(state)) {
            val anchor = getAnchor(fillDirection)
            val child = nextView(recycler, fillDirection)
            if (fillDirection == FILL_START) {
                addView(child, 0)
            } else {
                addView(child)
            }
            measureChildWithMargins(child, 0, 0)

            val measureWidth = mOrientationHelper.getDecoratedMeasurement(child)

            layoutDecorated(
                child,
                anchor - measureWidth / 2,
                0,
                anchor + measureWidth / 2,
                height
            )
        }

        return delta

    }

    private fun transformChildren() {

        if (childCount == 0) return
        for (i in 0 until childCount) {
            val child = getChildAt(i)!!
            val scaleX = getScaleByDistance(child)
            val scaleY = getScaleByDistance(child)
            child.scaleX = scaleX
            child.scaleY = scaleY
        }
    }

    /**
     * 如果anchorView的(start或end)+delta还是没出现在屏幕内，
     * 就继续滚动，不填充view
     */
    private fun canNotFillScroll(fillDirection: Int, delta: Int): Boolean {
        val anchorView = getAnchorView(fillDirection)
        return if (fillDirection == FILL_START) {
            val start = mOrientationHelper.getDecoratedStart(anchorView)
//            Log.i(TAG, "${start + delta} --- $interval")
            start + delta < interval
        } else {
            val end = mOrientationHelper.getDecoratedEnd(anchorView)
//            Log.i(TAG, "${width - (end + delta)} --- $interval")
            width - (end + delta) < interval
        }
    }

    /**
     * 检查是否滚动到了底部或者顶部
     */
    private fun checkScrollToEdge(
        fillDirection: Int,
        state: RecyclerView.State
    ): Boolean {
        val anchorView = getAnchorView(fillDirection)
        val anchorPosition = getPosition(anchorView)
//        Log.i(TAG, "$anchorPosition $childCount")
        if (fillDirection == FILL_START
            && anchorPosition == 0
            && getViewCenter(anchorView) >= centerLayoutX
        ) {
            return true
        }
        if (fillDirection == FILL_END
            && anchorPosition == state.itemCount - 1
            && getViewCenter(anchorView) <= centerLayoutX
        ) {
            return true
        }
        return false
    }

    private fun getFixLastScroll(fillDirection: Int): Int {
        val anchorView = getAnchorView(fillDirection)
        return if (fillDirection == FILL_START) {
            mOrientationHelper.getDecoratedStart(anchorView) - mOrientationHelper.startAfterPadding - getOffsetSpace()
        } else {
            mOrientationHelper.getDecoratedEnd(anchorView) - mOrientationHelper.endAfterPadding + getOffsetSpace()
        }
    }

    /**
     * 获取view的中点
     */
    private fun getViewCenter(child: View): Int {
        return (child.right - child.left) / 2 + child.left
    }

    /**
     * 判断view是否在正中央
     */
    private fun isCenter(child: View): Boolean {
        return getViewCenter(child) == centerLayoutX
    }

    /**
     * 获取居中被选中的view
     */
    private fun getSelectedView(): View? {
        return mSnapHelper.findSnapView(this)
    }

    /**
     * 获取锚点view，fill_end是最后一个，fill_start是第一个
     */
    private fun getAnchorView(fillDirection: Int): View {
        return if (fillDirection == FILL_START) {
            getChildAt(0)!!
        } else {
            getChildAt(childCount - 1)!!
        }
    }

    /**
     * 根据view距离中心的位置计算缩放比例
     */
    private fun getScaleByDistance(
        child: View
    ): Float {
        //获取view的中点和父布局的中点比对得出缩放比例
        val absDis = kotlin.math.abs(centerLayoutX - getViewCenter(child))
        if (absDis == 0)
            return 1f

        return 1 - ((absDis * (1 - DEFAULT_SCALE)) / centerLayoutX)

    }

    /**
     * 获取开始item距离开始位置的偏移量
     * 或者结束item距离尾端的偏移量
     */
    private fun getOffsetSpace(): Int {
        return 0
    }

    /**
     * 获取锚点view的position
     */
    private fun getAnchorPosition(fillDirection: Int): Int {
        return getPosition(getAnchorView(fillDirection))
    }

    /**
     * 获取将要填充的view的position
     */
    private fun getPendingFillPosition(fillDirection: Int): Int {
        return getAnchorPosition(fillDirection) + fillDirection
    }

    /**
     * 如果不是循环模式，将要填充的view的position不在合理范围内
     * 就返回false
     */
    private fun hasMore(state: RecyclerView.State): Boolean {

        return mPendingFillPosition >= 0 && mPendingFillPosition < state.itemCount
    }

    /**
     * 获取要开始填充的锚点位置
     */
    private fun getAnchor(
        fillDirection: Int
    ): Int {
        val anchorView = getAnchorView(fillDirection)
        return if (fillDirection == FILL_START) {
            getViewCenter(anchorView) - radius
        } else {
            getViewCenter(anchorView) + radius
        }
    }

    /**
     * 获取下一个view，fill_start就-1，fill_end就是+1
     */
    private fun nextView(
        recycler: RecyclerView.Recycler,
        fillDirection: Int
    ): View {
        val child = recycler.getViewForPosition(mPendingFillPosition)
        mPendingFillPosition += fillDirection
        return child
    }

    /**
     * 分发OnItemFillListener事件
     */
    private fun dispatchOnItemFillListener() {
//        if (childCount == 0 || mOnItemFillListener.isEmpty()) return
//
//        val centerView = getSelectedView() ?: return
//        val centerPosition = getPosition(centerView)
//
//        for (i in 0 until childCount) {
//            val child = getChildAt(i) ?: continue
//            val position = getPosition(child)
//
//            if (position == centerPosition) {
//                onItemSelected(child, position)
//            } else {
//                onItemUnSelected(child, position)
//            }
//        }
    }

    /**
     * 滚动到中间的item
     */
    private fun scrollToCenter(centerView: View, centerPosition: Int) {
        val destination =
            mOrientationHelper.totalSpace / 2 - mOrientationHelper.getDecoratedMeasurement(
                centerView
            ) / 2
        val distance = destination - mOrientationHelper.getDecoratedStart(centerView)

        //平滑动画的滚动到中心
//        smoothOffsetChildren(distance, centerPosition)
        //直接滚动到中心

//        dispatchOnItemSelectedListener(centerPosition)
    }
}