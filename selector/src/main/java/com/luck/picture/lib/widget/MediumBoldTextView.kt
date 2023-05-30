package com.luck.picture.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2020/8/25 10:32 AM
 * @describe：MediumBoldTextView
 */
open class MediumBoldTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var mStrokeWidth = 0.6f
    override fun onDraw(canvas: Canvas) {
        val paint = paint
        if (paint.strokeWidth != mStrokeWidth) {
            paint.strokeWidth = mStrokeWidth
            paint.style = Paint.Style.FILL_AND_STROKE
        }
        super.onDraw(canvas)
    }

    fun setStrokeWidth(mStrokeWidth: Float) {
        this.mStrokeWidth = mStrokeWidth
        invalidate()
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SelectorMediumBoldTextView,
            defStyleAttr,
            0
        )
        mStrokeWidth = a.getFloat(R.styleable.SelectorMediumBoldTextView_stroke_Width, mStrokeWidth)
        a.recycle()
    }
}