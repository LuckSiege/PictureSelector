package com.luck.picture.lib.basic;

import android.view.animation.Interpolator;

/**
 * @author：luck
 * @date：2022/6/20 6:01 下午
 * @describe：InterpolatorFactory
 */
public interface InterpolatorFactory {
    /**
     * An interpolator defines the rate of change of an animation.
     * This allows the basic animation effects (alpha, scale, translate, rotate)
     * to be accelerated, decelerated, repeated, etc.
     */
    Interpolator newInterpolator();
}
