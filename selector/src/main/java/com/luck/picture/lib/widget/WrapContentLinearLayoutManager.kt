package com.luck.picture.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:WrapContentLinearLayoutManager
 */
open class WrapContentLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

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

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}