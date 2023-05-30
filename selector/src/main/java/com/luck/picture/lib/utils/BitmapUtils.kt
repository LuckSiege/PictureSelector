package com.luck.picture.lib.utils

import com.luck.picture.lib.constant.SelectorConstant
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * @author：luck
 * @date：2022/11/30 3:33 下午
 * @describe：BitmapUtils
 */
object BitmapUtils {
    private const val ARGB_8888_MEMORY_BYTE = 4
    private const val MAX_BITMAP_SIZE = 100 * 1024 * 1024

    fun getComputeImageSize(imageWidth: Int, imageHeight: Int): IntArray {
        var maxWidth: Int = SelectorConstant.UNSET
        var maxHeight: Int = SelectorConstant.UNSET
        if (imageWidth == 0 && imageHeight == 0) {
            return intArrayOf(maxWidth, maxHeight)
        }
        var inSampleSize: Int = computeSize(imageWidth, imageHeight)
        val totalMemory: Long = getTotalMemory()
        var decodeAttemptSuccess = false
        while (!decodeAttemptSuccess) {
            maxWidth = imageWidth / inSampleSize
            maxHeight = imageHeight / inSampleSize
            val bitmapSize: Int = maxWidth * maxHeight * ARGB_8888_MEMORY_BYTE
            if (bitmapSize > totalMemory) {
                inSampleSize *= 2
                continue
            }
            decodeAttemptSuccess = true
        }
        return intArrayOf(maxWidth, maxHeight)
    }

    private fun computeSize(width: Int, height: Int): Int {
        var srcWidth = width
        var srcHeight = height
        srcWidth = if (srcWidth % 2 == 1) srcWidth + 1 else srcWidth
        srcHeight = if (srcHeight % 2 == 1) srcHeight + 1 else srcHeight
        val longSide = max(srcWidth, srcHeight)
        val shortSide = min(srcWidth, srcHeight)
        val scale = shortSide.toFloat() / longSide
        return if (scale <= 1 && scale > 0.5625) {
            when {
                longSide < 1664 -> {
                    1
                }
                longSide < 4990 -> {
                    2
                }
                longSide in 4991..10239 -> {
                    4
                }
                else -> {
                    longSide / 1280
                }
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (longSide / 1280 == 0) 1 else longSide / 1280
        } else {
            ceil(longSide / (1280.0 / scale)).toInt()
        }
    }

    private fun getTotalMemory(): Long {
        val totalMemory = Runtime.getRuntime().totalMemory()
        return if (totalMemory > MAX_BITMAP_SIZE) MAX_BITMAP_SIZE.toLong() else totalMemory
    }
}