package com.luck.lib.camerax.widget;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.luck.lib.camerax.CustomCameraConfig;
import com.luck.lib.camerax.listener.CaptureListener;
import com.luck.lib.camerax.listener.IObtainCameraView;
import com.luck.lib.camerax.permissions.PermissionChecker;
import com.luck.lib.camerax.permissions.PermissionResultCallback;
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil;
import com.luck.lib.camerax.utils.DoubleUtils;
import com.luck.lib.camerax.utils.SimpleXSpUtils;

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：CaptureLayout
 */
public class CaptureButton extends View {

    /**
     * 当前按钮状态
     */
    private int state;
    /**
     * 按钮可执行的功能状态（拍照,录制,两者）
     */
    private int buttonState;

    /**
     * 空闲状态
     */
    public static final int STATE_IDLE = 0x001;
    /**
     * 按下状态
     */
    public static final int STATE_PRESS = 0x002;
    /**
     * 长按状态
     */
    public static final int STATE_LONG_PRESS = 0x003;
    /**
     * 录制状态
     */
    public static final int STATE_RECORDER_ING = 0x004;
    /**
     * 禁止状态
     */
    public static final int STATE_BAN = 0x005;
    /**
     * 录制进度外圈色值
     */
    private int progressColor = 0xEE16AE16;

    private float event_Y;

    private Paint mPaint;

    /**
     * 进度条宽度
     */
    private float strokeWidth;
    /**
     * 长按外圆半径变大的Size
     */
    private int outside_add_size;
    /**
     * 长安内圆缩小的Size
     */
    private int inside_reduce_size;

    private float center_X;
    private float center_Y;

    /**
     * 按钮半径
     */
    private float button_radius;
    /**
     * 外圆半径
     */
    private float button_outside_radius;
    /**
     * 内圆半径
     */
    private float button_inside_radius;
    /**
     * 按钮大小
     */
    private int button_size;

    /**
     * 录制视频的进度
     */
    private float progress;
    /**
     * 录制视频最大时间长度
     */
    private int maxDuration;
    /**
     * 最短录制时间限制
     */
    private int minDuration;
    /**
     * 记录当前录制的时间
     */
    private int currentRecordedTime;

    private RectF rectF;

    private LongPressRunnable longPressRunnable;
    /**
     * 按钮回调接口
     */
    private CaptureListener captureListener;
    /**
     * 计时器
     */
    private RecordCountDownTimer timer;
    private boolean isTakeCamera = true;
    private final Activity activity;

    public CaptureButton(Context context) {
        super(context);
        activity = (Activity) context;
    }

    public CaptureButton(Context context, int size) {
        super(context);
        activity = (Activity) context;
        this.button_size = size;
        button_radius = size / 2.0f;

        button_outside_radius = button_radius;
        button_inside_radius = button_radius * 0.75f;

        strokeWidth = size / 15;
        outside_add_size = size / 8;
        inside_reduce_size = size / 8;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        progress = 0;
        longPressRunnable = new LongPressRunnable();

        state = STATE_IDLE;
        buttonState = CustomCameraConfig.BUTTON_STATE_BOTH;
        maxDuration = CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO;
        minDuration = CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO;

        center_X = (button_size + outside_add_size * 2) / 2;
        center_Y = (button_size + outside_add_size * 2) / 2;

        rectF = new RectF(
                center_X - (button_radius + outside_add_size - strokeWidth / 2),
                center_Y - (button_radius + outside_add_size - strokeWidth / 2),
                center_X + (button_radius + outside_add_size - strokeWidth / 2),
                center_Y + (button_radius + outside_add_size - strokeWidth / 2));

        timer = new RecordCountDownTimer(maxDuration, maxDuration / 360);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(button_size + outside_add_size * 2, button_size + outside_add_size * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);

        int outside_color = 0xEEDCDCDC;
        mPaint.setColor(outside_color);
        canvas.drawCircle(center_X, center_Y, button_outside_radius, mPaint);

        int inside_color = 0xFFFFFFFF;
        mPaint.setColor(inside_color);
        canvas.drawCircle(center_X, center_Y, button_inside_radius, mPaint);

        if (state == STATE_RECORDER_ING) {
            mPaint.setColor(progressColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawArc(rectF, -90, progress, false, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTakeCamera) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getPointerCount() > 1 || state != STATE_IDLE)
                        break;
                    event_Y = event.getY();
                    state = STATE_PRESS;
                    if (buttonState != CustomCameraConfig.BUTTON_STATE_ONLY_CAPTURE) {
                        postDelayed(longPressRunnable, 500);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (captureListener != null
                            && state == STATE_RECORDER_ING
                            && (buttonState == CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER
                            || buttonState == CustomCameraConfig.BUTTON_STATE_BOTH)) {
                        captureListener.recordZoom(event_Y - event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    handlerPressByState();
                    break;
            }
        }
        return true;
    }

    private ViewGroup getCustomCameraView() {
        if (activity instanceof IObtainCameraView) {
            IObtainCameraView cameraView = (IObtainCameraView) activity;
            return cameraView.getCustomCameraView();
        }
        return null;
    }

    private void handlerPressByState() {
        removeCallbacks(longPressRunnable);
        switch (state) {
            case STATE_PRESS:
                if (captureListener != null && (buttonState == CustomCameraConfig.BUTTON_STATE_ONLY_CAPTURE || buttonState ==
                        CustomCameraConfig.BUTTON_STATE_BOTH)) {
                    startCaptureAnimation(button_inside_radius);
                } else {
                    state = STATE_IDLE;
                }
                break;
            case STATE_LONG_PRESS:
            case STATE_RECORDER_ING:
                if (PermissionChecker.checkSelfPermission(getContext(), new String[]{Manifest.permission.RECORD_AUDIO})) {
                    timer.cancel();
                    recordEnd();
                }
                break;
        }
        state = STATE_IDLE;
    }

    public void recordEnd() {
        if (captureListener != null) {
            if (currentRecordedTime < minDuration) {
                captureListener.recordShort(currentRecordedTime);
            } else {
                captureListener.recordEnd(currentRecordedTime);
            }
        }
        resetRecordAnim();
    }

    private void resetRecordAnim() {
        state = STATE_BAN;
        progress = 0;
        invalidate();
        startRecordAnimation(
                button_outside_radius,
                button_radius,
                button_inside_radius,
                button_radius * 0.75f
        );
    }

    private void startCaptureAnimation(float inside_start) {
        ValueAnimator inside_anim = ValueAnimator.ofFloat(inside_start, inside_start * 0.75f, inside_start);
        inside_anim.addUpdateListener(animation -> {
            button_inside_radius = (float) animation.getAnimatedValue();
            invalidate();
        });
        inside_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (captureListener != null) {
                    captureListener.takePictures();
                }
                state = STATE_BAN;
            }
        });
        inside_anim.setDuration(50);
        inside_anim.start();
    }

    private void startRecordAnimation(float outside_start, float outside_end, float inside_start, float inside_end) {
        ValueAnimator outside_anim = ValueAnimator.ofFloat(outside_start, outside_end);
        ValueAnimator inside_anim = ValueAnimator.ofFloat(inside_start, inside_end);
        //外圆动画监听
        outside_anim.addUpdateListener(animation -> {
            button_outside_radius = (float) animation.getAnimatedValue();
            invalidate();
        });
        inside_anim.addUpdateListener(animation -> {
            button_inside_radius = (float) animation.getAnimatedValue();
            invalidate();
        });
        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                //设置为录制状态
                if (state == STATE_LONG_PRESS) {
                    if (captureListener != null)
                        captureListener.recordStart();
                    state = STATE_RECORDER_ING;
                    timer.start();
                } else {
                    state = STATE_IDLE;
                }
            }
        });
        set.playTogether(outside_anim, inside_anim);
        set.setDuration(100);
        set.start();
    }


    private void updateProgress(long millisUntilFinished) {
        currentRecordedTime = (int) (maxDuration - millisUntilFinished);
        progress = 360f - millisUntilFinished / (float) maxDuration * 360f;
        invalidate();
        if (captureListener != null) {
            captureListener.changeTime(millisUntilFinished);
        }
    }

    private class RecordCountDownTimer extends CountDownTimer {
        RecordCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateProgress(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            recordEnd();
        }
    }

    private class LongPressRunnable implements Runnable {
        @Override
        public void run() {
            state = STATE_LONG_PRESS;
            if (PermissionChecker.checkSelfPermission(getContext(), new String[]{Manifest.permission.RECORD_AUDIO})) {
                startRecordAnimation(button_outside_radius, button_outside_radius + outside_add_size,
                        button_inside_radius, button_inside_radius - inside_reduce_size);
            } else {
                onExplainCallback();
                handlerPressByState();
                PermissionChecker.getInstance().requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        postDelayed(longPressRunnable, 500);
                        ViewGroup customCameraView = getCustomCameraView();
                        if (customCameraView != null && CustomCameraConfig.explainListener != null) {
                            CustomCameraConfig.explainListener.onDismiss(customCameraView);
                        }
                    }

                    @Override
                    public void onDenied() {
                        if (CustomCameraConfig.deniedListener != null) {
                            SimpleXSpUtils.putBoolean(getContext(), Manifest.permission.RECORD_AUDIO, true);
                            CustomCameraConfig.deniedListener.onDenied(getContext(), Manifest.permission.RECORD_AUDIO, PermissionChecker.PERMISSION_RECORD_AUDIO_SETTING_CODE);
                            ViewGroup customCameraView = getCustomCameraView();
                            if (customCameraView != null && CustomCameraConfig.explainListener != null) {
                                CustomCameraConfig.explainListener.onDismiss(customCameraView);
                            }
                        } else {
                            SimpleXPermissionUtil.goIntentSetting(activity, PermissionChecker.PERMISSION_RECORD_AUDIO_SETTING_CODE);
                        }
                    }
                });
            }
        }
    }

    private void onExplainCallback() {
        if (CustomCameraConfig.explainListener != null) {
            if (!SimpleXSpUtils.getBoolean(getContext(), Manifest.permission.RECORD_AUDIO, false)) {
                ViewGroup customCameraView = getCustomCameraView();
                if (customCameraView != null) {
                    CustomCameraConfig.explainListener.onPermissionDescription(getContext(), customCameraView,
                                    Manifest.permission.RECORD_AUDIO);
                }
            }
        }
    }

    public void setMaxDuration(int duration) {
        this.maxDuration = duration;
        timer = new RecordCountDownTimer(maxDuration, maxDuration / 360);
    }

    public void setMinDuration(int duration) {
        this.minDuration = duration;
    }

    public void setCaptureListener(CaptureListener captureListener) {
        this.captureListener = captureListener;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setButtonFeatures(int state) {
        this.buttonState = state;
    }

    public int getButtonFeatures() {
        return buttonState;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public void setButtonCaptureEnabled(boolean enabled) {
        this.isTakeCamera = enabled;
    }

    public void resetState() {
        state = STATE_IDLE;
    }
}
