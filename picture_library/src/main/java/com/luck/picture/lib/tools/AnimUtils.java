package com.luck.picture.lib.tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：动画相关
 */
public class AnimUtils {
    private final static int DURATION = 450;

    public static void zoom(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.12f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.12f)
        );
        set.setDuration(DURATION);
        set.start();
    }

    public static void disZoom(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.12f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.12f, 1f)
        );
        set.setDuration(DURATION);
        set.start();
    }
}
