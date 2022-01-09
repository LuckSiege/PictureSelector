package com.luck.lib.camerax;

import static androidx.camera.core.VideoCapture.ERROR_RECORDING_TOO_SHORT;
import static androidx.camera.view.video.OnVideoSavedCallback.ERROR_MUXER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.luck.lib.camerax.listener.CameraListener;
import com.luck.lib.camerax.listener.CaptureListener;
import com.luck.lib.camerax.listener.ClickListener;
import com.luck.lib.camerax.listener.ImageCallbackListener;
import com.luck.lib.camerax.listener.TypeListener;
import com.luck.lib.camerax.permissions.PermissionChecker;
import com.luck.lib.camerax.permissions.PermissionResultCallback;
import com.luck.lib.camerax.permissions.PermissionUtil;
import com.luck.lib.camerax.utils.CameraUtils;
import com.luck.lib.camerax.utils.DensityUtil;
import com.luck.lib.camerax.utils.FileUtils;
import com.luck.lib.camerax.widget.CaptureLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * @author：luck
 * @date：2020-01-04 13:41
 * @describe：自定义相机View
 */
public class CustomCameraView extends RelativeLayout {

    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

    /**
     * 闪关灯状态
     */
    private static final int TYPE_FLASH_AUTO = 0x021;
    private static final int TYPE_FLASH_ON = 0x022;
    private static final int TYPE_FLASH_OFF = 0x023;
    private int typeFlash = TYPE_FLASH_OFF;
    private PreviewView mCameraPreviewView;
    private ProcessCameraProvider mCameraProvider;
    private ImageCapture mImageCapture;
    private ImageAnalysis mImageAnalyzer;
    private VideoCapture mVideoCapture;

    private int displayId = -1;
    /**
     * 相机模式
     */
    private int buttonFeatures;
    /**
     * 自定义拍照输出路径
     */
    private String outPutCameraDir;
    /**
     * 自定义拍照文件名
     */
    private String outPutCameraFileName;

    /**
     * 设置每秒的录制帧数
     */
    private int videoFrameRate;

    /**
     * 设置编码比特率。
     */
    private int videoBitRate;

    /**
     * 视频录制最小时长
     */
    private int recordVideoMinSecond;

    /**
     * 是否显示录制时间
     */
    private boolean isDisplayRecordTime;

    /**
     * 图片文件类型
     */
    private String imageFormat, imageFormatForQ;

    /**
     * 视频文件类型
     */
    private String videoFormat, videoFormatForQ;
    /**
     * 相机模式
     */
    private int useCameraCases = LifecycleCameraController.IMAGE_CAPTURE;
    /**
     * 摄像头方向
     */
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    /**
     * 回调监听
     */
    private CameraListener mCameraListener;
    private ClickListener mOnClickListener;
    private ImageCallbackListener mImageCallbackListener;
    private ImageView mImagePreview;
    private ImageView mSwitchCamera;
    private ImageView mFlashLamp;
    private TextView tvCurrentTime;
    private CaptureLayout mCaptureLayout;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private DisplayManager displayManager;
    private DisplayListener displayListener;
    private long recordTime = 0;

    private boolean isImageCaptureEnabled() {
        return useCameraCases == LifecycleCameraController.IMAGE_CAPTURE;
    }

    public CustomCameraView(Context context) {
        super(context);
        initView();
    }

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.picture_camera_view, this);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_black));
        mCameraPreviewView = findViewById(R.id.cameraPreviewView);
        mTextureView = findViewById(R.id.video_play_preview);
        mImagePreview = findViewById(R.id.cover_preview);
        mSwitchCamera = findViewById(R.id.image_switch);
        mFlashLamp = findViewById(R.id.image_flash);
        mCaptureLayout = findViewById(R.id.capture_layout);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera);
        displayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
        displayListener = new DisplayListener();
        displayManager.registerDisplayListener(displayListener, null);
        mCameraPreviewView.post(new Runnable() {
            @Override
            public void run() {
                displayId = mCameraPreviewView.getDisplay().getDisplayId();
            }
        });

        mFlashLamp.setOnClickListener(v -> {
            typeFlash++;
            if (typeFlash > 0x023) {
                typeFlash = TYPE_FLASH_AUTO;
            }
            setFlashMode();
        });

        mSwitchCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();
            }
        });

        mCaptureLayout.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {
                if (!mCameraProvider.isBound(mImageCapture)) {
                    bindCameraImageUseCases();
                }
                useCameraCases = LifecycleCameraController.IMAGE_CAPTURE;
                mCaptureLayout.setButtonCaptureEnabled(false);
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                tvCurrentTime.setVisibility(GONE);
                boolean isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT;
                ImageCapture.Metadata metadata = new ImageCapture.Metadata();
                metadata.setReversedHorizontal(isReversedHorizontal);
                ImageCapture.OutputFileOptions fileOptions;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(outPutCameraDir)) {
                    ContentValues contentValues = CameraUtils.buildImageContentValues(outPutCameraFileName, imageFormatForQ);
                    fileOptions = new ImageCapture.OutputFileOptions.Builder(getContext().getContentResolver()
                            , MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            .setMetadata(metadata).build();
                } else {
                    File cameraFile = FileUtils.createCameraFile(getContext(), CameraUtils.TYPE_IMAGE,
                            outPutCameraFileName, imageFormat, outPutCameraDir);
                    fileOptions = new ImageCapture.OutputFileOptions.Builder(cameraFile)
                            .setMetadata(metadata).build();
                }
                mImageCapture.takePicture(fileOptions, ContextCompat.getMainExecutor(getContext()),
                        new MyImageResultCallback(mImagePreview, mCaptureLayout, mImageCallbackListener, mCameraListener));
            }

            @Override
            public void recordStart() {
                if (!mCameraProvider.isBound(mVideoCapture)) {
                    bindCameraVideoUseCases();
                }
                useCameraCases = LifecycleCameraController.VIDEO_CAPTURE;
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                tvCurrentTime.setVisibility(isDisplayRecordTime ? VISIBLE : GONE);
                VideoCapture.OutputFileOptions fileOptions;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(outPutCameraDir)) {
                    ContentValues contentValues = CameraUtils.buildVideoContentValues(outPutCameraFileName, videoFormatForQ);
                    fileOptions = new VideoCapture.OutputFileOptions.Builder(getContext().getContentResolver(),
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues).build();
                } else {
                    File cameraFile = FileUtils.createCameraFile(getContext(), CameraUtils.TYPE_VIDEO,
                            outPutCameraFileName, videoFormat, outPutCameraDir);
                    fileOptions = new VideoCapture.OutputFileOptions.Builder(cameraFile).build();
                }
                mVideoCapture.startRecording(fileOptions, ContextCompat.getMainExecutor(getContext()),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull @NotNull VideoCapture.OutputFileResults outputFileResults) {
                                long minSecond = recordVideoMinSecond <= 0 ? CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO : recordVideoMinSecond;
                                if (recordTime < minSecond || outputFileResults.getSavedUri() == null) {
                                    return;
                                }
                                Uri savedUri = outputFileResults.getSavedUri();
                                Activity activity = (Activity) getContext();
                                SimpleCameraX.putOutputUri(activity.getIntent(), savedUri);
                                String outPutPath = FileUtils.isContent(savedUri.toString()) ? savedUri.toString() : savedUri.getPath();
                                mTextureView.setVisibility(View.VISIBLE);
                                mCameraPreviewView.setVisibility(View.INVISIBLE);
                                tvCurrentTime.setVisibility(GONE);
                                if (mTextureView.isAvailable()) {
                                    startVideoPlay(outPutPath);
                                } else {
                                    mTextureView.setSurfaceTextureListener(surfaceTextureListener);
                                }
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull @NotNull String message,
                                                @Nullable @org.jetbrains.annotations.Nullable Throwable cause) {
                                if (mCameraListener != null) {
                                    if (videoCaptureError == ERROR_RECORDING_TOO_SHORT || videoCaptureError == ERROR_MUXER) {
                                        recordShort(0);
                                    } else {
                                        mCameraListener.onError(videoCaptureError, message, cause);
                                    }
                                }
                            }
                        });
            }

            @Override
            public void changeTime(long duration) {
                if (isDisplayRecordTime && tvCurrentTime.getVisibility() == VISIBLE) {
                    String format = String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                    if (!TextUtils.equals(format, tvCurrentTime.getText())) {
                        tvCurrentTime.setText(format);
                    }
                    if (TextUtils.equals("00:00", tvCurrentTime.getText())) {
                        tvCurrentTime.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void recordShort(final long time) {
                recordTime = time;
                mSwitchCamera.setVisibility(VISIBLE);
                mFlashLamp.setVisibility(VISIBLE);
                tvCurrentTime.setVisibility(GONE);
                mCaptureLayout.resetCaptureLayout();
                mCaptureLayout.setTextWithAnimation(getContext().getString(R.string.picture_recording_time_is_short));
                mVideoCapture.stopRecording();
            }

            @Override
            public void recordEnd(long time) {
                recordTime = time;
                try {
                    mVideoCapture.stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void recordZoom(float zoom) {

            }

            @Override
            public void recordError() {
                if (mCameraListener != null) {
                    mCameraListener.onError(0, "An unknown error", null);
                }
            }
        });

        mCaptureLayout.setTypeListener(new TypeListener() {
            @Override
            public void cancel() {
                onCancelMedia();
                String outputPath = SimpleCameraX.getOutputPath(((Activity) getContext()).getIntent());
                FileUtils.deleteFile(getContext(), outputPath);
            }

            @Override
            public void confirm() {
                Activity activity = (Activity) getContext();
                String outputPath = SimpleCameraX.getOutputPath(activity.getIntent());
                if (isImageCaptureEnabled()) {
                    mImagePreview.setVisibility(INVISIBLE);
                    if (mCameraListener != null) {
                        mCameraListener.onPictureSuccess(outputPath);
                    }
                } else {
                    stopVideoPlay();
                    if (mCameraListener != null) {
                        mCameraListener.onRecordSuccess(outputPath);
                    }
                }
            }
        });
        mCaptureLayout.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick();
                }
            }
        });
    }

    /**
     * 用户针对相机的一些参数配制
     *
     * @param intent
     */
    public void setCameraConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        boolean isCameraAroundState = extras.getBoolean(SimpleCameraX.EXTRA_CAMERA_AROUND_STATE, false);
        buttonFeatures = extras.getInt(SimpleCameraX.EXTRA_CAMERA_MODE, CustomCameraConfig.BUTTON_STATE_BOTH);
        lensFacing = isCameraAroundState ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        outPutCameraDir = extras.getString(SimpleCameraX.EXTRA_OUTPUT_PATH_DIR);
        outPutCameraFileName = extras.getString(SimpleCameraX.EXTRA_CAMERA_FILE_NAME);
        videoFrameRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_FRAME_RATE);
        videoBitRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_BIT_RATE);

        int recordVideoMaxSecond = extras.getInt(SimpleCameraX.EXTRA_RECORD_VIDEO_MAX_SECOND, CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO);
        recordVideoMinSecond = extras.getInt(SimpleCameraX.EXTRA_RECORD_VIDEO_MIN_SECOND, CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO);
        imageFormat = extras.getString(SimpleCameraX.EXTRA_CAMERA_IMAGE_FORMAT, CameraUtils.JPEG);
        imageFormatForQ = extras.getString(SimpleCameraX.EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q, CameraUtils.MIME_TYPE_IMAGE);
        videoFormat = extras.getString(SimpleCameraX.EXTRA_CAMERA_VIDEO_FORMAT, CameraUtils.MP4);
        videoFormatForQ = extras.getString(SimpleCameraX.EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q, CameraUtils.MIME_TYPE_VIDEO);
        int captureLoadingColor = extras.getInt(SimpleCameraX.EXTRA_CAPTURE_LOADING_COLOR, 0xFF7D7DFF);
        isDisplayRecordTime = extras.getBoolean(SimpleCameraX.EXTRA_DISPLAY_RECORD_CHANGE_TIME, false);
        mCaptureLayout.setButtonFeatures(buttonFeatures);
        if (recordVideoMaxSecond > 0) {
            setRecordVideoMaxTime(recordVideoMaxSecond);
        }
        if (recordVideoMinSecond > 0) {
            setRecordVideoMinTime(recordVideoMinSecond);
        }
        setCaptureLoadingColor(captureLoadingColor);
        setProgressColor(captureLoadingColor);
        PermissionChecker.getInstance().requestPermissions((Activity) getContext(),
                new String[]{Manifest.permission.CAMERA},
                new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        buildUseCameraCases();
                    }

                    @Override
                    public void onDenied() {
                        PermissionUtil.goIntentSetting((Activity) getContext(), PermissionChecker.PERMISSION_SETTING_CODE);
                    }
                });
    }

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private class DisplayListener implements DisplayManager.DisplayListener {

        @Override
        public void onDisplayAdded(int displayId) {
        }

        @Override
        public void onDisplayRemoved(int displayId) {
        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (displayId == CustomCameraView.this.displayId) {
                if (mImageCapture != null) {
                    mImageCapture.setTargetRotation(mCameraPreviewView.getDisplay().getRotation());
                }
                if (mImageAnalyzer != null) {
                    mImageAnalyzer.setTargetRotation(mCameraPreviewView.getDisplay().getRotation());
                }
            }
        }
    }

    /**
     * 开始打开相机预览
     */
    public void buildUseCameraCases() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    mCameraProvider = cameraProviderFuture.get();
                    bindCameraUseCases();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    /**
     * 初始相机预览模式
     */
    private void bindCameraUseCases() {
        if (buttonFeatures == CustomCameraConfig.BUTTON_STATE_BOTH ||
                buttonFeatures == CustomCameraConfig.BUTTON_STATE_ONLY_CAPTURE) {
            bindCameraImageUseCases();
        } else {
            bindCameraVideoUseCases();
        }
    }

    /**
     * bindCameraImageUseCases
     */
    private void bindCameraImageUseCases() {
        try {
            int screenAspectRatio = aspectRatio(DensityUtil.getScreenWidth(getContext()), DensityUtil.getScreenHeight(getContext()));
            int rotation = mCameraPreviewView.getDisplay().getRotation();
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            // Preview
            Preview preview = new Preview.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build();

            // ImageCapture
            mImageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build();

            // ImageAnalysis
            mImageAnalyzer = new ImageAnalysis.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build();

            // Must unbind the use-cases before rebinding them
            mCameraProvider.unbindAll();
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            mCameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, preview, mImageCapture, mImageAnalyzer);
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.getSurfaceProvider());
            // setFlashMode
            setFlashMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * bindCameraVideoUseCases
     */
    @SuppressLint("RestrictedApi")
    private void bindCameraVideoUseCases() {
        try {
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            // Preview
            int rotation = mCameraPreviewView.getDisplay().getRotation();
            Preview preview = new Preview.Builder()
                    .setTargetRotation(rotation)
                    .build();
            // VideoCapture
            VideoCapture.Builder builder = new VideoCapture.Builder();
            builder.setTargetRotation(rotation);
            if (videoFrameRate > 0) {
                builder.setVideoFrameRate(videoFrameRate);
            }
            if (videoBitRate > 0) {
                builder.setBitRate(videoBitRate);
            }
            mVideoCapture = builder.build();
            // Must unbind the use-cases before rebinding them
            mCameraProvider.unbindAll();
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            mCameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, preview, mVideoCapture);
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.getSurfaceProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
     * [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     * <p>
     * Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     * of preview ratio to one of the provided values.
     *
     * @param width  - preview width
     * @param height - preview height
     * @return suitable aspect ratio
     */
    private int aspectRatio(int width, int height) {
        double aspect = Math.max(width, height);
        double previewRatio = aspect / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }


    /**
     * 拍照回调
     */
    private static class MyImageResultCallback implements ImageCapture.OnImageSavedCallback {
        private final WeakReference<ImageView> mImagePreviewReference;
        private final WeakReference<CaptureLayout> mCaptureLayoutReference;
        private final WeakReference<ImageCallbackListener> mImageCallbackListenerReference;
        private final WeakReference<CameraListener> mCameraListenerReference;

        public MyImageResultCallback(ImageView imagePreview, CaptureLayout captureLayout,
                                     ImageCallbackListener imageCallbackListener,
                                     CameraListener cameraListener) {
            this.mImagePreviewReference = new WeakReference<>(imagePreview);
            this.mCaptureLayoutReference = new WeakReference<>(captureLayout);
            this.mImageCallbackListenerReference = new WeakReference<>(imageCallbackListener);
            this.mCameraListenerReference = new WeakReference<>(cameraListener);
        }

        @Override
        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
            if (outputFileResults.getSavedUri() == null || mCaptureLayoutReference.get() == null ||
                    mImagePreviewReference.get() == null || mImageCallbackListenerReference.get() == null) {
                return;
            }
            Uri savedUri = outputFileResults.getSavedUri();
            Context context = mImagePreviewReference.get().getContext();
            SimpleCameraX.putOutputUri(((Activity) context).getIntent(), savedUri);
            String outPutCameraPath = FileUtils.isContent(savedUri.toString()) ? savedUri.toString() : savedUri.getPath();
            mCaptureLayoutReference.get().setButtonCaptureEnabled(true);
            mImageCallbackListenerReference.get().onLoadImage(outPutCameraPath, mImagePreviewReference.get());
            mImagePreviewReference.get().setVisibility(View.VISIBLE);
            mCaptureLayoutReference.get().startTypeBtnAnimator();
        }

        @Override
        public void onError(@NonNull ImageCaptureException exception) {
            if (mCaptureLayoutReference.get() != null) {
                mCaptureLayoutReference.get().setButtonCaptureEnabled(true);
            }
            if (mCameraListenerReference.get() != null) {
                mCameraListenerReference.get().onError(exception.getImageCaptureError(),
                        exception.getMessage(), exception.getCause());
            }
        }
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            String outputPath = SimpleCameraX.getOutputPath(((Activity) getContext()).getIntent());
            startVideoPlay(outputPath);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    public void setCameraListener(CameraListener cameraListener) {
        this.mCameraListener = cameraListener;
    }

    /**
     * 设置录制视频最大时长 秒
     */
    public void setRecordVideoMaxTime(int maxDurationTime) {
        mCaptureLayout.setDuration(maxDurationTime);
    }

    /**
     * 设置录制视频最小时长 秒
     */
    public void setRecordVideoMinTime(int minDurationTime) {
        mCaptureLayout.setMinDuration(minDurationTime);
    }

    /**
     * 设置拍照时loading色值
     *
     * @param color
     */
    public void setCaptureLoadingColor(int color) {
        mCaptureLayout.setCaptureLoadingColor(color);
    }

    /**
     * 设置录像时loading色值
     *
     * @param color
     */
    public void setProgressColor(int color) {
        mCaptureLayout.setProgressColor(color);
    }

    /**
     * 切换前后摄像头
     */
    public void toggleCamera() {
        lensFacing = CameraSelector.LENS_FACING_FRONT == lensFacing ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
        bindCameraUseCases();
    }

    /**
     * 闪光灯模式
     */
    private void setFlashMode() {
        if (mImageCapture == null) {
            return;
        }
        switch (typeFlash) {
            case TYPE_FLASH_AUTO:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_auto);
                mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
                break;
            case TYPE_FLASH_ON:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_on);
                mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                break;
            case TYPE_FLASH_OFF:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_off);
                mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                break;
        }
    }

    /**
     * 关闭相机界面按钮
     *
     * @param clickListener
     */
    public void setOnCancelClickListener(ClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    public void setImageCallbackListener(ImageCallbackListener mImageCallbackListener) {
        this.mImageCallbackListener = mImageCallbackListener;
    }

    /**
     * 重置状态
     */
    private void resetState() {
        if (isImageCaptureEnabled()) {
            mImagePreview.setVisibility(INVISIBLE);
        } else {
            try {
                mVideoCapture.stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mSwitchCamera.setVisibility(VISIBLE);
        mFlashLamp.setVisibility(VISIBLE);
        mCameraPreviewView.setVisibility(View.VISIBLE);
        mCaptureLayout.resetCaptureLayout();
    }

    /**
     * 开始循环播放视频
     *
     * @param url
     */
    private void startVideoPlay(String url) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            } else {
                mMediaPlayer.reset();
            }
            if (FileUtils.isContent(url)) {
                mMediaPlayer.setDataSource(getContext(), Uri.parse(url));
            } else {
                mMediaPlayer.setDataSource(url);
            }
            mMediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void
                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * updateVideoViewSize
     *
     * @param videoWidth
     * @param videoHeight
     */
    private void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            RelativeLayout.LayoutParams videoViewParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
            videoViewParam.addRule(CENTER_IN_PARENT, TRUE);
            mTextureView.setLayoutParams(videoViewParam);
        }
    }

    /**
     * 取消拍摄相关
     */
    public void onCancelMedia() {
        stopVideoPlay();
        resetState();
    }

    /**
     * 停止视频播放
     */
    private void stopVideoPlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mTextureView.setVisibility(View.GONE);
    }

    /**
     * onConfigurationChanged
     *
     * @param newConfig
     */
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        buildUseCameraCases();
    }

    /**
     * onDestroy
     */
    public void onDestroy() {
        displayManager.unregisterDisplayListener(displayListener);
    }
}
