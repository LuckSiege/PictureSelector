package com.luck.picture.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * @author：luck
 * @date：2016-12-31 22:02
 * @describe：SquareRelativeLayout
 */
class SquareRelativeLayout : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Set a square layout.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}