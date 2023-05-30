package com.luck.picture.lib.widget

import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:SpeedLinearSmoothScroller
 */
const val DEFAULT_MILLISECONDS_PER_INCH = 25f

class SpeedLinearSmoothScroller(context: Context, @ScrollType scrollType: Int, speedTime: Float) :
    LinearSmoothScroller(context) {

    private var scrollType = 0
    private var speedTime = 0F

    init {
        this.scrollType = scrollType
        this.speedTime = speedTime
    }

    @IntDef(SNAP_TO_ANY, SNAP_TO_START, SNAP_TO_END)
    annotation class ScrollType


    fun setSpeedTime(speedTime: Float) {
        this.speedTime = speedTime
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return speedTime / displayMetrics.densityDpi;
    }

    override fun getVerticalSnapPreference(): Int {
        return scrollType
    }
}