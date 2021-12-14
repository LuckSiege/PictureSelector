package com.yalantis.ucrop.util;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class RotationGestureDetector {

    private static final int INVALID_POINTER_INDEX = -1;

    private float fX, fY, sX, sY;

    private int mPointerIndex1, mPointerIndex2;
    private float mAngle;
    private boolean mIsFirstTouch;

    private OnRotationGestureListener mListener;

    public RotationGestureDetector(OnRotationGestureListener listener) {
        mListener = listener;
        mPointerIndex1 = INVALID_POINTER_INDEX;
        mPointerIndex2 = INVALID_POINTER_INDEX;
    }

    public float getAngle() {
        return mAngle;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                sX = event.getX();
                sY = event.getY();
                mPointerIndex1 = event.findPointerIndex(event.getPointerId(0));
                mAngle = 0;
                mIsFirstTouch = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                fX = event.getX();
                fY = event.getY();
                mPointerIndex2 = event.findPointerIndex(event.getPointerId(event.getActionIndex()));
                mAngle = 0;
                mIsFirstTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPointerIndex1 != INVALID_POINTER_INDEX && mPointerIndex2 != INVALID_POINTER_INDEX && event.getPointerCount() > mPointerIndex2) {
                    float nfX, nfY, nsX, nsY;

                    nsX = event.getX(mPointerIndex1);
                    nsY = event.getY(mPointerIndex1);
                    nfX = event.getX(mPointerIndex2);
                    nfY = event.getY(mPointerIndex2);

                    if (mIsFirstTouch) {
                        mAngle = 0;
                        mIsFirstTouch = false;
                    } else {
                        calculateAngleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);
                    }

                    if (mListener != null) {
                        mListener.onRotation(this);
                    }
                    fX = nfX;
                    fY = nfY;
                    sX = nsX;
                    sY = nsY;
                }
                break;
            case MotionEvent.ACTION_UP:
                mPointerIndex1 = INVALID_POINTER_INDEX;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPointerIndex2 = INVALID_POINTER_INDEX;
                break;
        }
        return true;
    }

    private float calculateAngleBetweenLines(float fx1, float fy1, float fx2, float fy2,
                                             float sx1, float sy1, float sx2, float sy2) {
        return calculateAngleDelta(
                (float) Math.toDegrees((float) Math.atan2((fy1 - fy2), (fx1 - fx2))),
                (float) Math.toDegrees((float) Math.atan2((sy1 - sy2), (sx1 - sx2))));
    }

    private float calculateAngleDelta(float angleFrom, float angleTo) {
        mAngle = angleTo % 360.0f - angleFrom % 360.0f;

        if (mAngle < -180.0f) {
            mAngle += 360.0f;
        } else if (mAngle > 180.0f) {
            mAngle -= 360.0f;
        }

        return mAngle;
    }

    public static class SimpleOnRotationGestureListener implements OnRotationGestureListener {

        @Override
        public boolean onRotation(RotationGestureDetector rotationDetector) {
            return false;
        }
    }

    public interface OnRotationGestureListener {

        boolean onRotation(RotationGestureDetector rotationDetector);
    }

}