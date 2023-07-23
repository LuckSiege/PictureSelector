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
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.luck.lib.camerax.listener.CameraListener;
import com.luck.lib.camerax.listener.CameraXOrientationEventListener;
import com.luck.lib.camerax.listener.CameraXPreviewViewTouchListener;
import com.luck.lib.camerax.listener.CaptureListener;
import com.luck.lib.camerax.listener.ClickListener;
import com.luck.lib.camerax.listener.ImageCallbackListener;
import com.luck.lib.camerax.listener.TypeListener;
import com.luck.lib.camerax.permissions.PermissionChecker;
import com.luck.lib.camerax.permissions.PermissionResultCallback;
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil;
import com.luck.lib.camerax.utils.CameraUtils;
import com.luck.lib.camerax.utils.DensityUtil;
import com.luck.lib.camerax.utils.FileUtils;
import com.luck.lib.camerax.utils.SimpleXSpUtils;
import com.luck.lib.camerax.widget.CaptureLayout;
import com.luck.lib.camerax.widget.FocusImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


/**
 * @author：luck
 * @date：2020-01-04 13:41
 * @describe：自定义相机View
 */
public class CustomCameraView extends RelativeLayout implements CameraXOrientationEventListener.OnOrientationChangedListener {

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
     * 手指点击对焦
     */
    private boolean isManualFocus;

    /**
     * 双击可放大缩小
     */
    private boolean isZoomPreview;

    /**
     * 是否自动纠偏
     */
    private boolean isAutoRotation;

    private long recordTime = 0;

    /**
     * 回调监听
     */
    private CameraListener mCameraListener;
    private ClickListener mOnClickListener;
    private ImageCallbackListener mImageCallbackListener;
    private ImageView mImagePreview;
    private View mImagePreviewBg;
    private ImageView mSwitchCamera;
    private ImageView mFlashLamp;
    private TextView tvCurrentTime;
    private CaptureLayout mCaptureLayout;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private DisplayManager displayManager;
    private DisplayListener displayListener;
    private CameraXOrientationEventListener orientationEventListener;
    private CameraInfo mCameraInfo;
    private CameraControl mCameraControl;
    private FocusImageView focusImageView;
    private Executor mainExecutor;
    private Activity activity;

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
        activity = (Activity) getContext();
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_black));
        mCameraPreviewView = findViewById(R.id.cameraPreviewView);
        mTextureView = findViewById(R.id.video_play_preview);
        focusImageView = findViewById(R.id.focus_view);
        mImagePreview = findViewById(R.id.cover_preview);
        mImagePreviewBg = findViewById(R.id.cover_preview_bg);
        mSwitchCamera = findViewById(R.id.image_switch);
        mFlashLamp = findViewById(R.id.image_flash);
        mCaptureLayout = findViewById(R.id.capture_layout);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera);
        displayManager = (DisplayManager) getContext().getSystemService(Context.DISPLAY_SERVICE);
        displayListener = new DisplayListener();
        displayManager.registerDisplayListener(displayListener, null);
        mainExecutor = ContextCompat.getMainExecutor(getContext());

        mCameraPreviewView.post(new Runnable() {
            @Override
            public void run() {
                if (mCameraPreviewView != null) {
                    Display display = mCameraPreviewView.getDisplay();
                    if (display != null) {
                        displayId = display.getDisplayId();
                    }
                }
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
                ImageCapture.Metadata metadata = new ImageCapture.Metadata();
                metadata.setReversedHorizontal(isReversedHorizontal());
                ImageCapture.OutputFileOptions fileOptions;
                File cameraFile;
                if (isSaveExternal()) {
                    cameraFile = FileUtils.createTempFile(getContext(), false);
                } else {
                    cameraFile = FileUtils.createCameraFile(getContext(), CameraUtils.TYPE_IMAGE,
                            outPutCameraFileName, imageFormat, outPutCameraDir);
                }
                fileOptions = new ImageCapture.OutputFileOptions.Builder(cameraFile)
                        .setMetadata(metadata).build();
                mImageCapture.takePicture(fileOptions, mainExecutor,
                        new MyImageResultCallback(CustomCameraView.this, mImagePreview, mImagePreviewBg,
                                mCaptureLayout, mImageCallbackListener, mCameraListener));
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
                File cameraFile;
                if (isSaveExternal()) {
                    cameraFile = FileUtils.createTempFile(getContext(), true);
                } else {
                    cameraFile = FileUtils.createCameraFile(getContext(), CameraUtils.TYPE_VIDEO,
                            outPutCameraFileName, videoFormat, outPutCameraDir);
                }
                fileOptions = new VideoCapture.OutputFileOptions.Builder(cameraFile).build();
                mVideoCapture.startRecording(fileOptions, mainExecutor,
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull @NotNull VideoCapture.OutputFileResults outputFileResults) {
                                long minSecond = recordVideoMinSecond <= 0 ? CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO : recordVideoMinSecond;
                                if (recordTime < minSecond || outputFileResults.getSavedUri() == null) {
                                    return;
                                }
                                Uri savedUri = outputFileResults.getSavedUri();
                                SimpleCameraX.putOutputUri(activity.getIntent(), savedUri);
                                String outPutPath = FileUtils.isContent(savedUri.toString()) ? savedUri.toString() : savedUri.getPath();
                                mTextureView.setVisibility(View.VISIBLE);
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
                try {
                    mVideoCapture.stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            }

            @Override
            public void confirm() {
                String outputPath = SimpleCameraX.getOutputPath(activity.getIntent());
                if (isSaveExternal()) {
                    outputPath = isMergeExternalStorageState(activity, outputPath);
                } else {
                    // 对前置镜头导致的镜像进行一个纠正
                    if (isImageCaptureEnabled() && isReversedHorizontal()) {
                        File cameraFile = FileUtils.createCameraFile(getContext(), CameraUtils.TYPE_IMAGE,
                                outPutCameraFileName, imageFormat, outPutCameraDir);
                        if (FileUtils.copyPath(activity, outputPath, cameraFile.getAbsolutePath())) {
                            outputPath = cameraFile.getAbsolutePath();
                            SimpleCameraX.putOutputUri(activity.getIntent(), Uri.fromFile(cameraFile));
                        }
                    }
                }
                if (isImageCaptureEnabled()) {
                    mImagePreview.setVisibility(INVISIBLE);
                    mImagePreviewBg.setAlpha(0F);
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

    private String isMergeExternalStorageState(Activity activity, String outputPath) {
        try {
            // 对前置镜头导致的镜像进行一个纠正
            if (isImageCaptureEnabled() && isReversedHorizontal()) {
                File tempFile = FileUtils.createTempFile(activity, false);
                if (FileUtils.copyPath(activity, outputPath, tempFile.getAbsolutePath())) {
                    outputPath = tempFile.getAbsolutePath();
                }
            }
            // 当用户未设置存储路径时，相片默认是存在外部公共目录下
            Uri externalSavedUri;
            if (isImageCaptureEnabled()) {
                ContentValues contentValues = CameraUtils.buildImageContentValues(outPutCameraFileName, imageFormatForQ);
                externalSavedUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else {
                ContentValues contentValues = CameraUtils.buildVideoContentValues(outPutCameraFileName, videoFormatForQ);
                externalSavedUri = getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            if (externalSavedUri == null) {
                return outputPath;
            }
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(externalSavedUri);
            boolean isWriteFileSuccess = FileUtils.writeFileFromIS(new FileInputStream(outputPath), outputStream);
            if (isWriteFileSuccess) {
                FileUtils.deleteFile(getContext(), outputPath);
                SimpleCameraX.putOutputUri(activity.getIntent(), externalSavedUri);
                return externalSavedUri.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outputPath;
    }


    private boolean isSaveExternal() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(outPutCameraDir);
    }

    private boolean isReversedHorizontal() {
        return lensFacing == CameraSelector.LENS_FACING_FRONT;
    }

    /**
     * 用户针对相机的一些参数配制
     *
     * @param intent
     */
    public void setCameraConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        boolean isCameraAroundState = extras.getBoolean(SimpleCameraX.EXTRA_CAMERA_AROUND_STATE, false);
        buttonFeatures = extras.getInt(SimpleCameraX.EXTRA_CAMERA_MODE, CustomCameraConfig.BUTTON_STATE_BOTH);
        lensFacing = isCameraAroundState ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        outPutCameraDir = extras.getString(SimpleCameraX.EXTRA_OUTPUT_PATH_DIR);
        outPutCameraFileName = extras.getString(SimpleCameraX.EXTRA_CAMERA_FILE_NAME);
        videoFrameRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_FRAME_RATE);
        videoBitRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_BIT_RATE);
        isManualFocus = extras.getBoolean(SimpleCameraX.EXTRA_MANUAL_FOCUS);
        isZoomPreview = extras.getBoolean(SimpleCameraX.EXTRA_ZOOM_PREVIEW);
        isAutoRotation = extras.getBoolean(SimpleCameraX.EXTRA_AUTO_ROTATION);

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
        String format = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(recordVideoMaxSecond),
                TimeUnit.MILLISECONDS.toSeconds(recordVideoMaxSecond)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(recordVideoMaxSecond)));
        tvCurrentTime.setText(format);
        if (isAutoRotation && buttonFeatures != CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER) {
            orientationEventListener = new CameraXOrientationEventListener(getContext(), this);
            startCheckOrientation();
        }
        setCaptureLoadingColor(captureLoadingColor);
        setProgressColor(captureLoadingColor);
        boolean isCheckSelfPermission = PermissionChecker.checkSelfPermission(getContext(), new String[]{Manifest.permission.CAMERA});
        if (isCheckSelfPermission) {
            buildUseCameraCases();
        } else {
            if (CustomCameraConfig.explainListener != null) {
                if (!SimpleXSpUtils.getBoolean(getContext(), Manifest.permission.CAMERA, false)) {
                    CustomCameraConfig.explainListener
                            .onPermissionDescription(getContext(), this, Manifest.permission.CAMERA);
                }
            }
            PermissionChecker.getInstance().requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    new PermissionResultCallback() {
                        @Override
                        public void onGranted() {
                            buildUseCameraCases();
                            if (CustomCameraConfig.explainListener != null) {
                                CustomCameraConfig.explainListener.onDismiss(CustomCameraView.this);
                            }
                        }

                        @Override
                        public void onDenied() {
                            if (CustomCameraConfig.deniedListener != null) {
                                SimpleXSpUtils.putBoolean(getContext(), Manifest.permission.CAMERA, true);
                                CustomCameraConfig.deniedListener.onDenied(getContext(), Manifest.permission.CAMERA, PermissionChecker.PERMISSION_SETTING_CODE);
                                if (CustomCameraConfig.explainListener != null) {
                                    CustomCameraConfig.explainListener.onDismiss(CustomCameraView.this);
                                }
                            } else {
                                SimpleXPermissionUtil.goIntentSetting(activity, PermissionChecker.PERMISSION_SETTING_CODE);
                            }
                        }
                    });
        }
    }

    /**
     * 检测手机方向
     */
    private void startCheckOrientation() {
        if (orientationEventListener != null) {
            orientationEventListener.star();
        }
    }

    /**
     * 停止检测手机方向
     */
    public void stopCheckOrientation(){
        if (orientationEventListener != null) {
            orientationEventListener.stop();
        }
    }

    private int getTargetRotation() {
        return mImageCapture.getTargetRotation();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (mImageCapture != null) {
            mImageCapture.setTargetRotation(orientation);
        }
        if (mImageAnalyzer != null) {
            mImageAnalyzer.setTargetRotation(orientation);
        }
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
        }, mainExecutor);
    }

    /**
     * 初始相机预览模式
     */
    private void bindCameraUseCases() {
        if (null != mCameraProvider && isBackCameraLevel3Device(mCameraProvider)) {
            if (CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER == buttonFeatures) {
                bindCameraVideoUseCases();
            } else {
                bindCameraImageUseCases();
            }
        } else {
            switch (buttonFeatures) {
                case CustomCameraConfig.BUTTON_STATE_ONLY_CAPTURE:
                    bindCameraImageUseCases();
                    break;
                case CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER:
                    bindCameraVideoUseCases();
                    break;
                default:
                    bindCameraWithUserCases();
                    break;
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private boolean isBackCameraLevel3Device(ProcessCameraProvider cameraProvider) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<CameraInfo> cameraInfos = CameraSelector.DEFAULT_BACK_CAMERA
                    .filter(cameraProvider.getAvailableCameraInfos());
            if (!cameraInfos.isEmpty()) {
                return  Objects.equals(Camera2CameraInfo.from(cameraInfos.get(0)).getCameraCharacteristic(
                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL), CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY);
            }
        }
        return false;
    }

    /**
     * bindCameraWithUserCases
     */
    private void bindCameraWithUserCases() {
        try {
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            // Preview
            Preview preview = new Preview.Builder()
                    .setTargetRotation(mCameraPreviewView.getDisplay().getRotation())
                    .build();
            // ImageCapture
            buildImageCapture();
            // VideoCapture
            buildVideoCapture();
            UseCaseGroup.Builder useCase = new UseCaseGroup.Builder();
            useCase.addUseCase(preview);
            useCase.addUseCase(mImageCapture);
            useCase.addUseCase(mVideoCapture);
            UseCaseGroup useCaseGroup = useCase.build();
            // Must unbind the use-cases before rebinding them
            mCameraProvider.unbindAll();
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.getSurfaceProvider());
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            Camera camera = mCameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, useCaseGroup);
            // setFlashMode
            setFlashMode();
            mCameraInfo = camera.getCameraInfo();
            mCameraControl = camera.getCameraControl();
            initCameraPreviewListener();
        } catch (Exception e) {
            e.printStackTrace();
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
            buildImageCapture();

            // ImageAnalysis
            mImageAnalyzer = new ImageAnalysis.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build();

            // Must unbind the use-cases before rebinding them
            mCameraProvider.unbindAll();
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.getSurfaceProvider());
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            Camera camera = mCameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, preview, mImageCapture, mImageAnalyzer);
            // setFlashMode
            setFlashMode();
            mCameraInfo = camera.getCameraInfo();
            mCameraControl = camera.getCameraControl();
            initCameraPreviewListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * bindCameraVideoUseCases
     */
    private void bindCameraVideoUseCases() {
        try {
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            // Preview
            Preview preview = new Preview.Builder()
                    .setTargetRotation(mCameraPreviewView.getDisplay().getRotation())
                    .build();
            buildVideoCapture();
            // Must unbind the use-cases before rebinding them
            mCameraProvider.unbindAll();
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.getSurfaceProvider());
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            Camera camera = mCameraProvider.bindToLifecycle((LifecycleOwner) getContext(), cameraSelector, preview, mVideoCapture);
            mCameraInfo = camera.getCameraInfo();
            mCameraControl = camera.getCameraControl();
            initCameraPreviewListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildImageCapture() {
        int screenAspectRatio = aspectRatio(DensityUtil.getScreenWidth(getContext()), DensityUtil.getScreenHeight(getContext()));
        mImageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(mCameraPreviewView.getDisplay().getRotation())
                .build();
    }

    @SuppressLint("RestrictedApi")
    private void buildVideoCapture() {
        VideoCapture.Builder videoBuilder = new VideoCapture.Builder();
        videoBuilder.setTargetRotation(mCameraPreviewView.getDisplay().getRotation());
        if (videoFrameRate > 0) {
            videoBuilder.setVideoFrameRate(videoFrameRate);
        }
        if (videoBitRate > 0) {
            videoBuilder.setBitRate(videoBitRate);
        }
        mVideoCapture = videoBuilder.build();
    }


    private void initCameraPreviewListener() {
        LiveData<ZoomState> zoomState = mCameraInfo.getZoomState();
        CameraXPreviewViewTouchListener cameraXPreviewViewTouchListener = new CameraXPreviewViewTouchListener(getContext());
        cameraXPreviewViewTouchListener.setCustomTouchListener(new CameraXPreviewViewTouchListener.CustomTouchListener() {
            @Override
            public void zoom(float delta) {
                if (isZoomPreview) {
                    if (zoomState.getValue() != null) {
                        float currentZoomRatio = zoomState.getValue().getZoomRatio();
                        mCameraControl.setZoomRatio(currentZoomRatio * delta);
                    }
                }
            }

            @Override
            public void click(float x, float y) {
                if (isManualFocus) {
                    MeteringPointFactory factory = mCameraPreviewView.getMeteringPointFactory();
                    MeteringPoint point = factory.createPoint(x, y);
                    FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build();
                    if (mCameraInfo.isFocusMeteringSupported(action)) {
                        mCameraControl.cancelFocusAndMetering();
                        focusImageView.setDisappear(false);
                        focusImageView.startFocus(new Point((int) x, (int) y));
                        ListenableFuture<FocusMeteringResult> future = mCameraControl.startFocusAndMetering(action);
                        future.addListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FocusMeteringResult result = future.get();
                                    focusImageView.setDisappear(true);
                                    if (result.isFocusSuccessful()) {
                                        focusImageView.onFocusSuccess();
                                    } else {
                                        focusImageView.onFocusFailed();
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }, mainExecutor);
                    }
                }
            }

            @Override
            public void doubleClick(float x, float y) {
                if (isZoomPreview) {
                    if (zoomState.getValue() != null) {
                        float currentZoomRatio = zoomState.getValue().getZoomRatio();
                        float minZoomRatio = zoomState.getValue().getMinZoomRatio();
                        if (currentZoomRatio > minZoomRatio) {
                            mCameraControl.setLinearZoom(0f);
                        } else {
                            mCameraControl.setLinearZoom(0.5f);
                        }
                    }
                }
            }
        });
        mCameraPreviewView.setOnTouchListener(cameraXPreviewViewTouchListener);
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
        private final WeakReference<View> mImagePreviewBgReference;
        private final WeakReference<CaptureLayout> mCaptureLayoutReference;
        private final WeakReference<ImageCallbackListener> mImageCallbackListenerReference;
        private final WeakReference<CameraListener> mCameraListenerReference;
        private final WeakReference<CustomCameraView> mCameraViewLayoutReference;

        public MyImageResultCallback(CustomCameraView cameraView,ImageView imagePreview, View imagePreviewBg, CaptureLayout captureLayout,
                                     ImageCallbackListener imageCallbackListener,
                                     CameraListener cameraListener) {
            this.mCameraViewLayoutReference = new WeakReference<>(cameraView);
            this.mImagePreviewReference = new WeakReference<>(imagePreview);
            this.mImagePreviewBgReference = new WeakReference<>(imagePreviewBg);
            this.mCaptureLayoutReference = new WeakReference<>(captureLayout);
            this.mImageCallbackListenerReference = new WeakReference<>(imageCallbackListener);
            this.mCameraListenerReference = new WeakReference<>(cameraListener);
        }

        @Override
        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
            Uri savedUri = outputFileResults.getSavedUri();
            if (savedUri != null) {
                CustomCameraView customCameraView = mCameraViewLayoutReference.get();
                if (customCameraView != null) {
                    customCameraView.stopCheckOrientation();
                }
                ImageView mImagePreview = mImagePreviewReference.get();
                if (mImagePreview != null) {
                    Context context = mImagePreview.getContext();
                    SimpleCameraX.putOutputUri(((Activity) context).getIntent(), savedUri);
                    mImagePreview.setVisibility(View.VISIBLE);
                    if (customCameraView != null && customCameraView.isAutoRotation) {
                        int targetRotation = customCameraView.getTargetRotation();
                        // 这种角度拍出来的图片宽比高大，所以使用ScaleType.FIT_CENTER缩放模式
                        if (targetRotation == Surface.ROTATION_90 || targetRotation == Surface.ROTATION_270) {
                            mImagePreview.setAdjustViewBounds(true);
                        } else {
                            mImagePreview.setAdjustViewBounds(false);
                            mImagePreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        View mImagePreviewBackground = mImagePreviewBgReference.get();
                        if (mImagePreviewBackground != null) {
                            mImagePreviewBackground.animate().alpha(1F).setDuration(220).start();
                        }
                    }
                    ImageCallbackListener imageCallbackListener = mImageCallbackListenerReference.get();
                    if (imageCallbackListener != null) {
                        String outPutCameraPath = FileUtils.isContent(savedUri.toString()) ? savedUri.toString() : savedUri.getPath();
                        imageCallbackListener.onLoadImage(outPutCameraPath, mImagePreview);
                    }
                }

                CaptureLayout captureLayout = mCaptureLayoutReference.get();
                if (captureLayout != null) {
                    captureLayout.setButtonCaptureEnabled(true);
                    captureLayout.startTypeBtnAnimator();
                }
            }
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
            String outputPath = SimpleCameraX.getOutputPath(activity.getIntent());
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
            mImagePreviewBg.setAlpha(0F);
        } else {
            try {
                mVideoCapture.stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mSwitchCamera.setVisibility(VISIBLE);
        mFlashLamp.setVisibility(VISIBLE);
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
        String outputPath = SimpleCameraX.getOutputPath(activity.getIntent());
        FileUtils.deleteFile(getContext(), outputPath);
        stopVideoPlay();
        resetState();
        startCheckOrientation();
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
        stopCheckOrientation();
        focusImageView.destroy();
    }
}
