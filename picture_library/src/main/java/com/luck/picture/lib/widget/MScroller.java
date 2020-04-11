package com.luck.picture.lib.widget;

import android.content.Context;
import android.widget.Scroller;
import android.view.animation.Interpolator;

/**
 * @author：luck
 * @date：2020-04-11 14:41
 * @describe：MScroller
 */
public class MScroller extends Scroller {

    public MScroller(Context context) {
        this(context, sInterpolator);
    }


    public MScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    private static final Interpolator sInterpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

    public boolean noDuration;

    public void setNoDuration(boolean noDuration) {
        this.noDuration = noDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        if (noDuration) {
            //界面滑动不需要时间间隔
            super.startScroll(startX, startY, dx, dy, 0);
        } else {
            super.startScroll(startX, startY, dx, dy, duration);
        }
    }
}
