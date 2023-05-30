package com.luck.picture.lib.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：AnimUtils
 */
object AnimUtils {
    private const val DURATION = 250

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param isFlag
     */
    fun rotateArrow(arrow: ImageView?, isFlag: Boolean) {
        val srcValue: Float
        val targetValue: Float
        if (isFlag) {
            srcValue = 0f
            targetValue = 180f
        } else {
            srcValue = 180f
            targetValue = 0f
        }
        val objectAnimator = ObjectAnimator.ofFloat(arrow, "rotation", srcValue, targetValue)
        objectAnimator.duration = DURATION.toLong()
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.start()
    }

    /**
     * 缩放动画
     *
     * @param view
     */
    fun selectZoom(view: View?) {
        val animatorSet = AnimatorSet()
        val objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.05f, 1.0f)
        val objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.05f, 1.0f)
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY)
        animatorSet.duration = DURATION.toLong()
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()
    }
}