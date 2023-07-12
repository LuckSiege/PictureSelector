package com.luck.picture.lib.player.widget

import android.view.MotionEvent
import android.view.TextureView
import android.widget.ImageView

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：TextureViewUtils
 */
object TextureViewUtils {

    fun checkZoomLevels(
        minZoom: Float, midZoom: Float,
        maxZoom: Float
    ) {
        require(minZoom < midZoom) { "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value" }
        require(midZoom < maxZoom) { "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value" }
    }

    fun hasBitmap(textureView: TextureView): Boolean {
        return textureView.bitmap != null
    }

    fun isSupportedScaleType(scaleType: ImageView.ScaleType?): Boolean {
        if (scaleType == null) {
            return false
        }
        check(scaleType != ImageView.ScaleType.MATRIX) { "Matrix scale type is not supported" }
        return true
    }

    fun getPointerIndex(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    }
}