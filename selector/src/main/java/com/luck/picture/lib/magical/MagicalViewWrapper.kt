package com.luck.picture.lib.magical

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.math.roundToInt

/**
 * @author：luck
 * @date：2021/12/15 11:10 上午
 * @describe：MagicalViewWrapper
 */
class MagicalViewWrapper(private val viewWrapper: View) {
    private val params: ViewGroup.MarginLayoutParams =
        viewWrapper.layoutParams as ViewGroup.MarginLayoutParams
    val width: Int
        get() = params.width
    val height: Int
        get() = params.height

    fun setWidth(width: Float) {
        params.width = width.roundToInt()
        viewWrapper.layoutParams = params
    }

    fun setHeight(height: Float) {
        params.height = height.roundToInt()
        viewWrapper.layoutParams = params
    }

    var marginTop: Int
        get() = params.topMargin
        set(m) {
            params.topMargin = m
            viewWrapper.layoutParams = params
        }
    var marginRight: Int
        get() = params.rightMargin
        set(mr) {
            params.rightMargin = mr
            viewWrapper.layoutParams = params
        }
    var marginLeft: Int
        get() = params.leftMargin
        set(mr) {
            params.leftMargin = mr
            viewWrapper.layoutParams = params
        }
    var marginBottom: Int
        get() = params.bottomMargin
        set(m) {
            params.bottomMargin = m
            viewWrapper.layoutParams = params
        }

    init {
        if (params is LinearLayout.LayoutParams) {
            params.gravity = Gravity.START
        }
    }
}