package com.luck.lib.camerax.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * @author：luck
 * @date：2022/2/16 9:41 上午
 * @describe：CameraXPreviewViewTouchListener
 */
public class CameraXPreviewViewTouchListener implements View.OnTouchListener {

    private final GestureDetector mGestureDetector;

    private final ScaleGestureDetector mScaleGestureDetector;

    public CameraXPreviewViewTouchListener(Context context) {
        mGestureDetector = new GestureDetector(context, onGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (!mScaleGestureDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 缩放监听
     */
    ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float delta = detector.getScaleFactor();
            if (mCustomTouchListener != null) {
                mCustomTouchListener.zoom(delta);
            }
            return true;
        }
    };


    GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mCustomTouchListener != null) {
                mCustomTouchListener.click(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mCustomTouchListener != null) {
                mCustomTouchListener.doubleClick(e.getX(), e.getY());
            }
            return true;
        }
    };


    private CustomTouchListener mCustomTouchListener;

    public interface CustomTouchListener {
        /**
         * 放大
         */
        void zoom(float delta);

        /**
         * 点击
         */
        void click(float x, float y);

        /**
         * 双击
         */
        void doubleClick(float x, float y);
    }

    public void setCustomTouchListener(CustomTouchListener customTouchListener) {
        mCustomTouchListener = customTouchListener;
    }
}
