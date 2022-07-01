package com.luck.picture.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2022/3/18 10:39 下午
 * @describe：RoundCornerRelativeLayout
 */
public class RoundCornerRelativeLayout extends RelativeLayout {
    private final Path path;
    private final float cornerSize;
    private final boolean isTopNormal;
    private final boolean isBottomNormal;
    private final RectF mRect = new RectF();

    public RoundCornerRelativeLayout(Context context) {
        this(context, null);
    }

    public RoundCornerRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PictureRoundCornerRelativeLayout, defStyleAttr, 0);
        cornerSize = a.getDimension(R.styleable.PictureRoundCornerRelativeLayout_psCorners, 0);
        isTopNormal = a.getBoolean(R.styleable.PictureRoundCornerRelativeLayout_psTopNormal, false);
        isBottomNormal = a.getBoolean(R.styleable.PictureRoundCornerRelativeLayout_psBottomNormal, false);
        a.recycle();
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        path.reset();
        mRect.right = w;
        mRect.bottom = h;
        float[] cornerRadii;
        if (!isTopNormal && !isBottomNormal) {
            path.addRoundRect(mRect, cornerSize, cornerSize, Path.Direction.CW);
        } else {
            if (isTopNormal) {
                cornerRadii = new float[]{0, 0, 0, 0, cornerSize, cornerSize, cornerSize, cornerSize};
                path.addRoundRect(mRect, cornerRadii, Path.Direction.CW);
            }
            if (isBottomNormal) {
                cornerRadii = new float[]{cornerSize, cornerSize, cornerSize, cornerSize, 0, 0, 0, 0};
                path.addRoundRect(mRect, cornerRadii, Path.Direction.CW);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
