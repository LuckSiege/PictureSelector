package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @author：luck
 * @date：2020/8/25 10:32 AM
 * @describe：MarqueeTextView
 */
public class MarqueeTextView extends MediumBoldTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused){
            super.onFocusChanged(true, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            super.onWindowFocusChanged(true);
        }
    }
}
