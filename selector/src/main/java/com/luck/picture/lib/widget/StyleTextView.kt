package com.luck.picture.lib.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.StyleUtils

/**
 * @author：luck
 * @date：2022/12/20 10:37 上午
 * @describe：StyleTextView
 */
open class StyleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MediumBoldTextView(context, attrs, defStyleAttr) {
    var textNormalSize: Int
    var textSelectedSize: Int
    var textNormalColor: Int
    var textSelectedColor: Int
    var textNormalString: String? = null
    var textSelectedString: String? = null
    var normalBackground: Drawable? = null
    var selectedBackground: Drawable? = null

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.SelectorStyleTextView,
            defStyleAttr,
            0
        )
        textNormalSize =
            a.getDimensionPixelSize(R.styleable.SelectorStyleTextView_psNormalTextSize, 0)
        textSelectedSize =
            a.getDimensionPixelSize(R.styleable.SelectorStyleTextView_psSelectedTextSize, 0)

        textNormalColor = a.getColor(R.styleable.SelectorStyleTextView_psNormalTextColor, 0)
        textSelectedColor = a.getColor(R.styleable.SelectorStyleTextView_psSelectedTextColor, 0)

        textNormalString = a.getString(R.styleable.SelectorStyleTextView_psNormalText)
        textSelectedString = a.getString(R.styleable.SelectorStyleTextView_psSelectedText)

        normalBackground = a.getDrawable(R.styleable.SelectorStyleTextView_psNormalBackground)
        selectedBackground = a.getDrawable(R.styleable.SelectorStyleTextView_psSelectedBackground)

        a.recycle()
    }

    open fun setDataStyle(config: SelectorConfig, result: MutableList<LocalMedia>) {
        isEnabled = if (config.isEmptyResultBack) {
            true
        } else {
            result.isNotEmpty()
        }
        if (result.isEmpty()) {
            if (!TextUtils.isEmpty(textNormalString)) {
                text = textNormalString
            }
            if (textNormalSize != 0) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textNormalSize.toFloat())
            }
            if (textNormalColor != 0) {
                setTextColor(textNormalColor)
            }
            normalBackground?.let {
                background = it
            }
        } else {
            if (!TextUtils.isEmpty(textSelectedString)) {
                textSelectedString?.let {
                    text =
                        if (config.selectionMode == SelectionMode.MULTIPLE) when (StyleUtils.getFormatCount(
                            it
                        )) {
                            1 -> {
                                String.format(it, result.size)
                            }
                            2 -> {
                                String.format(it, result.size, config.getSelectCount())
                            }
                            else -> {
                                it
                            }
                        } else textNormalString
                }
            }
            if (textSelectedSize != 0) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectedSize.toFloat())
            }
            if (textSelectedColor != 0) {
                setTextColor(textSelectedColor)
            }
            selectedBackground?.let {
                background = it
            }
        }
    }
}