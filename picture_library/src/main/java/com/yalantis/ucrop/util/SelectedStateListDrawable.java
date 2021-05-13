package com.yalantis.ucrop.util;

import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

/**
 * Hack class to properly support state drawable back to Android 1.6
 */
public class SelectedStateListDrawable extends StateListDrawable {

    private final int mSelectionColor;

    public SelectedStateListDrawable(Drawable drawable, int selectionColor) {
        super();
        this.mSelectionColor = selectionColor;
        addState(new int[]{android.R.attr.state_selected}, drawable);
        addState(new int[]{}, drawable);
    }

    @Override
    protected boolean onStateChange(int[] states) {
        boolean isStatePressedInArray = false;
        for (int state : states) {
            if (state == android.R.attr.state_selected) {
                isStatePressedInArray = true;
            }
        }
        if (isStatePressedInArray) {
            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mSelectionColor, BlendModeCompat.SRC_ATOP);
            super.setColorFilter(colorFilter);
        } else {
            super.clearColorFilter();
        }
        return super.onStateChange(states);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

}
