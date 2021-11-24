package com.luck.picture.lib.tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：动画相关
 */
public class AnimUtils {
    private final static int DURATION = 350;


    public static void zoomItemAnimation(View view, boolean isSelected) {
        float v1 = 0, v2 = 0;
        if (isSelected) {
            if (view.getScaleX() == 1.0F && view.getScaleX() == 1.0F) {
                v1 = 1F;
                v2 = 1.12F;
            }
        } else {
            if (view.getScaleX() == 1.12F && view.getScaleX() == 1.12F) {
                v1 = 1.12F;
                v2 = 1F;
            }
        }
        if (v1 > 0) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", v1, v2),
                    ObjectAnimator.ofFloat(view, "scaleY", v1, v2));
            set.setDuration(DURATION);
            set.start();
        }
    }

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
            targetValue = 0F;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrow, "rotation", srcValue, targetValue);
        objectAnimator.setDuration(DURATION);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }
}
