package com.luck.picture.lib.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet

/**
 * @author：luck
 * @date：2020/8/25 10:32 AM
 * @describe：MarqueeTextView
 */
class MarqueeTextView : MediumBoldTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    override fun isFocused(): Boolean {
        return true
    }

    override fun isSelected(): Boolean {
        return true
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused) {
            super.onFocusChanged(true, direction, previouslyFocusedRect)
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) {
            super.onWindowFocusChanged(true)
        }
    }
}