package com.luck.picture.lib.utils;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：动画相关
 */
public class AnimUtils {
    public final static int DURATION = 250;
    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param isFlag
     */
    public static void rotateArrow(ImageView arrow, boolean isFlag) {
        float srcValue, targetValue;
        if (isFlag) {
            srcValue = 0F;
            targetValue = 180F;
        } else {
            srcValue = 180F;
            targetValue = 360F;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrow, "rotation", srcValue, targetValue);
        objectAnimator.setDuration(DURATION);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }
}
