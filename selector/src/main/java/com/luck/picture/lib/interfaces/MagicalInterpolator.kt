package com.luck.picture.lib.interfaces

import android.view.animation.Interpolator

/**
 * @author：luck
 * @date：2023/1/13 11:40 上午
 * @describe：OnMagicalInterpolator
 */
interface MagicalInterpolator {
    /**
     * An interpolator defines the rate of change of an animation.
     * This allows the basic animation effects (alpha, scale, translate, rotate)
     * to be accelerated, decelerated, repeated, etc.
     */
    fun newInterpolator(): Interpolator?
}