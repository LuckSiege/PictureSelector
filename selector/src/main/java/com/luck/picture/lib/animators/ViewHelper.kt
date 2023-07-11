package com.luck.picture.lib.animators

import android.view.View
import androidx.core.view.ViewCompat

/**
 * @author：luck
 * @date：2020-04-18 14:13
 * @describe：ViewHelper
 */
object ViewHelper {
    fun clear(v: View) {
        v.alpha = 1f
        v.scaleY = 1f
        v.scaleX = 1f
        v.translationY = 0f
        v.translationX = 0f
        v.rotation = 0f
        v.rotationY = 0f
        v.rotationX = 0f
        v.pivotY = (v.measuredHeight shr 1).toFloat()
        v.pivotX = (v.measuredWidth shr 1).toFloat()
        ViewCompat.animate(v).setInterpolator(null).startDelay = 0
    }
}