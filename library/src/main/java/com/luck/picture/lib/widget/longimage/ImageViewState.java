package com.luck.picture.lib.widget.longimage;

import android.graphics.PointF;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Wraps the scale, center and orientation of a displayed image for easy restoration on screen rotate.
 */
@SuppressWarnings("WeakerAccess")
public class ImageViewState implements Serializable {

    private final float scale;

    private final float centerX;

    private final float centerY;

    private final int orientation;

    public ImageViewState(float scale, @NonNull PointF center, int orientation) {
        this.scale = scale;
        this.centerX = center.x;
        this.centerY = center.y;
        this.orientation = orientation;
    }

    public float getScale() {
        return scale;
    }

    @NonNull
    public PointF getCenter() {
        return new PointF(centerX, centerY);
    }

    public int getOrientation() {
        return orientation;
    }

}
