package com.luck.picture.lib.animators;

import android.view.View;

import androidx.core.view.ViewCompat;

/**
 * @author：luck
 * @date：2020-04-18 14:13
 * @describe：ViewHelper
 */
public final class ViewHelper {
    public static void clear(View v) {
        v.setAlpha(1);
        v.setScaleY(1);
        v.setScaleX(1);
        v.setTranslationY(0);
        v.setTranslationX(0);
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationX(0);
        v.setPivotY(v.getMeasuredHeight() / 2);
        v.setPivotX(v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
    }
}
