package com.yalantis.ucrop.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.yalantis.ucrop.R;

public class SuccessTickView extends View {
    private float mDensity = -1;
    private Paint mPaint;
    private final float CONST_RADIUS = dip2px(1.2f);
    private final float CONST_RECT_WEIGHT = dip2px(3);
    private final float CONST_LEFT_RECT_W = dip2px(15);
    private final float CONST_RIGHT_RECT_W = dip2px(25);
    private final float MIN_LEFT_RECT_W = dip2px(3.3f);
    private final float MAX_RIGHT_RECT_W = CONST_RIGHT_RECT_W + dip2px(6.7f);
    private Context context;
    private float mMaxLeftRectWidth;
    private float mLeftRectWidth;
    private float mRightRectWidth;
    private boolean mLeftRectGrowMode;

    public SuccessTickView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SuccessTickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, Color.parseColor("#A5DC86")));
        mLeftRectWidth = CONST_LEFT_RECT_W;
        mRightRectWidth = CONST_RIGHT_RECT_W;
        mLeftRectGrowMode = false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int totalW = getWidth();
        int totalH = getHeight();
        // rotate canvas first
        canvas.rotate(45, totalW / 2, totalH / 2);

        totalW /= 1.2;
        totalH /= 1.4;
        mMaxLeftRectWidth = (totalW + CONST_LEFT_RECT_W) / 2 + CONST_RECT_WEIGHT - 1;

        RectF leftRect = new RectF();
        if (mLeftRectGrowMode) {
            leftRect.left = 0;
            leftRect.right = leftRect.left + mLeftRectWidth;
            leftRect.top = (totalH + CONST_RIGHT_RECT_W) / 2;
            leftRect.bottom = leftRect.top + CONST_RECT_WEIGHT;
        } else {
            leftRect.right = (totalW + CONST_LEFT_RECT_W) / 2 + CONST_RECT_WEIGHT - 1;
            leftRect.left = leftRect.right - mLeftRectWidth;
            leftRect.top = (totalH + CONST_RIGHT_RECT_W) / 2;
            leftRect.bottom = leftRect.top + CONST_RECT_WEIGHT;
        }

        canvas.drawRoundRect(leftRect, CONST_RADIUS, CONST_RADIUS, mPaint);

        RectF rightRect = new RectF();
        rightRect.bottom = (totalH + CONST_RIGHT_RECT_W) / 2 + CONST_RECT_WEIGHT - 1;
        rightRect.left = (totalW + CONST_LEFT_RECT_W) / 2;
        rightRect.right = rightRect.left + CONST_RECT_WEIGHT;
        rightRect.top = rightRect.bottom - mRightRectWidth;
        canvas.drawRoundRect(rightRect, CONST_RADIUS, CONST_RADIUS, mPaint);
    }

    public float dip2px(float dpValue) {
        if (mDensity == -1) {
            mDensity = getResources().getDisplayMetrics().density;
        }
        return dpValue * mDensity + 0.5f;
    }

    public void startTickAnim() {
        // hide tick
        mLeftRectWidth = 0;
        mRightRectWidth = 0;
        invalidate();
        Animation tickAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (0.54 < interpolatedTime && 0.7 >= interpolatedTime) {  // grow left and right rect to right
                    mLeftRectGrowMode = true;
                    mLeftRectWidth = mMaxLeftRectWidth * ((interpolatedTime - 0.54f) / 0.16f);
                    if (0.65 < interpolatedTime) {
                        mRightRectWidth = MAX_RIGHT_RECT_W * ((interpolatedTime - 0.65f) / 0.19f);
                    }
                    invalidate();
                } else if (0.7 < interpolatedTime && 0.84 >= interpolatedTime) { // shorten left rect from right, still grow right rect
                    mLeftRectGrowMode = false;
                    mLeftRectWidth = mMaxLeftRectWidth * (1 - ((interpolatedTime - 0.7f) / 0.14f));
                    mLeftRectWidth = mLeftRectWidth < MIN_LEFT_RECT_W ? MIN_LEFT_RECT_W : mLeftRectWidth;
                    mRightRectWidth = MAX_RIGHT_RECT_W * ((interpolatedTime - 0.65f) / 0.19f);
                    invalidate();
                } else if (0.84 < interpolatedTime && 1 >= interpolatedTime) { // restore left rect width, shorten right rect to const
                    mLeftRectGrowMode = false;
                    mLeftRectWidth = MIN_LEFT_RECT_W + (CONST_LEFT_RECT_W - MIN_LEFT_RECT_W) * ((interpolatedTime - 0.84f) / 0.16f);
                    mRightRectWidth = CONST_RIGHT_RECT_W + (MAX_RIGHT_RECT_W - CONST_RIGHT_RECT_W) * (1 - ((interpolatedTime - 0.84f) / 0.16f));
                    invalidate();
                }
            }
        };
        tickAnim.setDuration(750);
        tickAnim.setStartOffset(100);
        startAnimation(tickAnim);
    }
}