package com.luck.picture.lib.utils

import android.content.Context
import android.graphics.ColorFilter
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author：luck
 * @date：2021/11/20 3:27 下午
 * @describe：StyleUtils
 */
object StyleUtils {

    /**
     * 验证文本是否有2个动态匹配符
     *
     * @param text
     */
    fun getFormatCount(text: String?): Int {
        val pattern = "%[^%]*\\d"
        val compile: Pattern = Pattern.compile(pattern)
        val matcher: Matcher? = text?.let { compile.matcher(it) }
        var count = 0
        while (matcher?.find() == true) {
            count++
        }
        return count
    }

    /**
     * getColorFilter
     *
     * @param context
     * @param color
     */
    fun getColorFilter(context: Context, color: Int): ColorFilter? {
        return BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(
                context,
                color
            ), BlendModeCompat.SRC_ATOP
        )
    }
}