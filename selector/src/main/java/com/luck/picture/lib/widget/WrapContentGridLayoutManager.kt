package com.luck.picture.lib.widget

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:WrapContentGridLayoutManager
 */
open class WrapContentGridLayoutManager(context: Context, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    private var scrollType = 0
    private var speedTime: Float = DEFAULT_MILLISECONDS_PER_INCH

    open fun setMillisecondsPerInch(speedTime: Float) {
        this.speedTime = speedTime
    }

    open fun setScrollType(@SpeedLinearSmoothScroller.ScrollType scrollType: Int) {
        this.scrollType = scrollType
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val scroller = SpeedLinearSmoothScroller(recyclerView.context, scrollType, speedTime)
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}