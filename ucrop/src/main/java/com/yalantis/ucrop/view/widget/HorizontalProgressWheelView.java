package com.yalantis.ucrop.view.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.yalantis.ucrop.R;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class HorizontalProgressWheelView extends View {

    private final Rect mCanvasClipBounds = new Rect();

    private ScrollingListener mScrollingListener;
    private float mLastTouchedPosition;

    private Paint mProgressLinePaint;
    private Paint mProgressMiddleLinePaint;
    private int mProgressLineWidth, mProgressLineHeight;
    private int mProgressLineMargin;

    private boolean mScrollStarted;
    private float mTotalScrollDistance;

    private int mMiddleLineColor;

    public HorizontalProgressWheelView(Context context) {
        this(context, null);
    }

    public HorizontalProgressWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollingListener(ScrollingListener scrollingListener) {
        mScrollingListener = scrollingListener;
    }

    public void setMiddleLineColor(@ColorInt int middleLineColor) {
        mMiddleLineColor = middleLineColor;
        mProgressMiddleLinePaint.setColor(mMiddleLineColor);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchedPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollingListener != null) {
                    mScrollStarted = false;
                    mScrollingListener.onScrollEnd();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - mLastTouchedPosition;
                if (distance != 0) {
                    if (!mScrollStarted) {
                        mScrollStarted = true;
                        if (mScrollingListener != null) {
                            mScrollingListener.onScrollStart();
                        }
                    }
                    onScrollEvent(event, distance);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mCanvasClipBounds);

        int linesCount = mCanvasClipBounds.width() / (mProgressLineWidth + mProgressLineMargin);
        float deltaX = (mTotalScrollDistance) % (float) (mProgressLineMargin + mProgressLineWidth);

        for (int i = 0; i < linesCount; i++) {
            if (i < (linesCount / 4)) {
                mProgressLinePaint.setAlpha((int) (255 * (i / (float) (linesCount / 4))));
            } else if (i > (linesCount * 3 / 4)) {
                mProgressLinePaint.setAlpha((int) (255 * ((linesCount - i) / (float) (linesCount / 4))));
            } else {
                mProgressLinePaint.setAlpha(255);
            }
            canvas.drawLine(
                    -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                    mCanvasClipBounds.centerY() - mProgressLineHeight / 4.0f,
                    -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                    mCanvasClipBounds.centerY() + mProgressLineHeight / 4.0f, mProgressLinePaint);
        }

        canvas.drawLine(mCanvasClipBounds.centerX(), mCanvasClipBounds.centerY() - mProgressLineHeight / 2.0f, mCanvasClipBounds.centerX(), mCanvasClipBounds.centerY() + mProgressLineHeight / 2.0f, mProgressMiddleLinePaint);

    }

    private void onScrollEvent(MotionEvent event, float distance) {
        mTotalScrollDistance -= distance;
        postInvalidate();
        mLastTouchedPosition = event.getX();
        if (mScrollingListener != null) {
            mScrollingListener.onScroll(-distance, mTotalScrollDistance);
        }
    }

    private void init() {
        mMiddleLineColor = ContextCompat.getColor(getContext(), R.color.ucrop_color_widget_rotate_mid_line);

        mProgressLineWidth = getContext().getResources().getDimensionPixelSize(R.dimen.ucrop_width_horizontal_wheel_progress_line);
        mProgressLineHeight = getContext().getResources().getDimensionPixelSize(R.dimen.ucrop_height_horizontal_wheel_progress_line);
        mProgressLineMargin = getContext().getResources().getDimensionPixelSize(R.dimen.ucrop_margin_horizontal_wheel_progress_line);

        mProgressLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressLinePaint.setStyle(Paint.Style.STROKE);
        mProgressLinePaint.setStrokeWidth(mProgressLineWidth);
        mProgressLinePaint.setColor(getResources().getColor(R.color.ucrop_color_progress_wheel_line));

        mProgressMiddleLinePaint = new Paint(mProgressLinePaint);
        mProgressMiddleLinePaint.setColor(mMiddleLineColor);
        mProgressMiddleLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressMiddleLinePaint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.ucrop_width_middle_wheel_progress_line));
    }

    public interface ScrollingListener {

        void onScrollStart();

        void onScroll(float delta, float totalDistance);

        void onScrollEnd();
    }

}
