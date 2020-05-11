package com.luck.picture.lib.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.CameraView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.luck.picture.lib.PictureMediaScannerConnection;
import com.luck.picture.lib.R;
import com.luck.picture.lib.camera.listener.CameraListener;
import com.luck.picture.lib.camera.listener.CaptureListener;
import com.luck.picture.lib.camera.listener.ClickListener;
import com.luck.picture.lib.camera.listener.ImageCallbackListener;
import com.luck.picture.lib.camera.listener.TypeListener;
import com.luck.picture.lib.camera.view.CaptureLayout;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author：luck
 * @date：2020-01-04 13:41
 * @describe：自定义相机View
 */
public class CustomCameraView extends RelativeLayout {
    /**
     * 只能拍照
     */
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    /**
     * 只能录像
     */
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    /**
     * 两者都可以
     */
    public static final int BUTTON_STATE_BOTH = 0x103;
    /**
     * 闪关灯状态
     */
    private static final int TYPE_FLASH_AUTO = 0x021;
    private static final int TYPE_FLASH_ON = 0x022;
    private static final int TYPE_FLASH_OFF = 0x023;
    private int type_flash = TYPE_FLASH_OFF;
    private PictureSelectionConfig mConfig;
    /**
     * 回调监听
     */
    private CameraListener mCameraListener;
    private ClickListener mOnClickListener;
    private ImageCallbackListener mImageCallbackListener;
    private androidx.camera.view.CameraView mCameraView;
    private ImageView mImagePreview;
    private ImageView mSwitchCamera;
    private ImageView mFlashLamp;
    private CaptureLayout mCaptureLayout;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private long recordTime = 0;
    private File mVideoFile;
    private File mPhotoFile;

    public CustomCameraView(Context context) {
        this(context, null);
    }

    public CustomCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        setWillNotDraw(false);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_black));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.picture_camera_view, this);
        mCameraView = view.findViewById(R.id.cameraView);
        mCameraView.enableTorch(true);
        mTextureView = view.findViewById(R.id.video_play_preview);
        mImagePreview = view.findViewById(R.id.image_preview);
        mSwitchCamera = view.findViewById(R.id.image_switch);
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera);
        mFlashLamp = view.findViewById(R.id.image_flash);
        setFlashRes();
        mFlashLamp.setOnClickListener(v -> {
            type_flash++;
            if (type_flash > 0x023)
                type_flash = TYPE_FLASH_AUTO;
            setFlashRes();
        });
        mCaptureLayout = view.findViewById(R.id.capture_layout);
        mCaptureLayout.setDuration(15 * 1000);
        //切换摄像头
        mSwitchCamera.setOnClickListener(v -> mCameraView.toggleCamera());
        //拍照 录像
        mCaptureLayout.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                mCameraView.setCaptureMode(androidx.camera.view.CameraView.CaptureMode.IMAGE);
                File imageOutFile = createImageFile();
                if (imageOutFile == null) {
                    return;
                }
                mPhotoFile = imageOutFile;
                mCameraView.takePicture(imageOutFile, ContextCompat.getMainExecutor(getContext()),
                        new MyImageResultCallback(getContext(), mConfig, imageOutFile,
                                mImagePreview, mCaptureLayout, mImageCallbackListener, mCameraListener));
            }

            @Override
            public void recordStart() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                mCameraView.setCaptureMode(androidx.camera.view.CameraView.CaptureMode.VIDEO);
                mCameraView.startRecording(createVideoFile(), ContextCompat.getMainExecutor(getContext()),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull File file) {
                                mVideoFile = file;
                                if (recordTime < 1500 && mVideoFile.exists() && mVideoFile.delete()) {
                                    return;
                                }
                                if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(mConfig.cameraPath)) {
                                    PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<Boolean>() {

                                        @Override
                                        public Boolean doInBackground() {
                                            return AndroidQTransformUtils.copyPathToDCIM(getContext(),
                                                    file, Uri.parse(mConfig.cameraPath));
                                        }

                                        @Override
                                        public void onSuccess(Boolean result) {
                                            PictureThreadUtils.cancel(PictureThreadUtils.getIoPool());
                                        }
                                    });
                                }
                                mTextureView.setVisibility(View.VISIBLE);
                                mCameraView.setVisibility(View.INVISIBLE);
                                if (mTextureView.isAvailable()) {
                                    startVideoPlay(mVideoFile);
                                } else {
                                    mTextureView.setSurfaceTextureListener(surfaceTextureListener);
                                }
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                if (mCameraListener != null) {
                                    mCameraListener.onError(videoCaptureError, message, cause);
                                }
                            }
                        });
            }

            @Override
            public void recordShort(final long time) {
                recordTime = time;
                mSwitchCamera.setVisibility(VISIBLE);
                mFlashLamp.setVisibility(VISIBLE);
                mCaptureLayout.resetCaptureLayout();
                mCaptureLayout.setTextWithAnimation(getContext().getString(R.string.picture_recording_time_is_short));
                mCameraView.stopRecording();
            }

            @Override
            public void recordEnd(long time) {
                recordTime = time;
                mCameraView.stopRecording();
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
        //确认 取消
        mCaptureLayout.setTypeListener(new TypeListener() {
            @Override
            public void cancel() {
                stopVideoPlay();
                resetState();
            }

            @Override
            public void confirm() {
                if (mCameraView.getCaptureMode() == androidx.camera.view.CameraView.CaptureMode.VIDEO) {
                    if (mVideoFile == null) {
                        return;
                    }
                    stopVideoPlay();
                    if (mCameraListener != null || !mVideoFile.exists()) {
                        mCameraListener.onRecordSuccess(mVideoFile);
                    }
                } else {
                    if (mPhotoFile == null || !mPhotoFile.exists()) {
                        return;
                    }
                    mImagePreview.setVisibility(INVISIBLE);
                    if (mCameraListener != null) {
                        mCameraListener.onPictureSuccess(mPhotoFile);
                    }
                }
            }
        });
        mCaptureLayout.setLeftClickListener(() -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick();
            }
        });
    }

    /**
     * 拍照回调
     */
    private static class MyImageResultCallback implements ImageCapture.OnImageSavedCallback {
        private WeakReference<Context> mContextReference;
        private WeakReference<PictureSelectionConfig> mConfigReference;
        private WeakReference<File> mFileReference;
        private WeakReference<ImageView> mImagePreviewReference;
        private WeakReference<CaptureLayout> mCaptureLayoutReference;
        private WeakReference<ImageCallbackListener> mImageCallbackListenerReference;
        private WeakReference<CameraListener> mCameraListenerReference;

        public MyImageResultCallback(Context context, PictureSelectionConfig config,
                                     File imageOutFile, ImageView imagePreview,
                                     CaptureLayout captureLayout, ImageCallbackListener imageCallbackListener,
                                     CameraListener cameraListener) {
            super();
            this.mContextReference = new WeakReference<>(context);
            this.mConfigReference = new WeakReference<>(config);
            this.mFileReference = new WeakReference<>(imageOutFile);
            this.mImagePreviewReference = new WeakReference<>(imagePreview);
            this.mCaptureLayoutReference = new WeakReference<>(captureLayout);
            this.mImageCallbackListenerReference = new WeakReference<>(imageCallbackListener);
            this.mCameraListenerReference = new WeakReference<>(cameraListener);
        }

        @Override
        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
            if (mConfigReference.get() != null) {
                if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(mConfigReference.get().cameraPath)) {
                    PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<Boolean>() {

                        @Override
                        public Boolean doInBackground() {
                            return AndroidQTransformUtils.copyPathToDCIM(mContextReference.get(),
                                    mFileReference.get(), Uri.parse(mConfigReference.get().cameraPath));
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            PictureThreadUtils.cancel(PictureThreadUtils.getIoPool());
                        }
                    });
                }
            }
            if (mImageCallbackListenerReference.get() != null
                    && mFileReference.get() != null
                    && mImagePreviewReference.get() != null) {
                mImageCallbackListenerReference.get().onLoadImage(mFileReference.get(), mImagePreviewReference.get());
            }
            if (mImagePreviewReference.get() != null) {
                mImagePreviewReference.get().setVisibility(View.VISIBLE);
            }
            if (mCaptureLayoutReference.get() != null) {
                mCaptureLayoutReference.get().startTypeBtnAnimator();
            }
        }

        @Override
        public void onError(@NonNull ImageCaptureException exception) {
            if (mCameraListenerReference.get() != null) {
                mCameraListenerReference.get().onError(exception.getImageCaptureError(), exception.getMessage(), exception.getCause());
            }
        }
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            startVideoPlay(mVideoFile);
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

    public File createImageFile() {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            String diskCacheDir = PictureFileUtils.getDiskCacheDir(getContext());
            File rootDir = new File(diskCacheDir);
            if (!rootDir.exists() && rootDir.mkdirs()) {
            }
            boolean isOutFileNameEmpty = TextUtils.isEmpty(mConfig.cameraFileName);
            String suffix = TextUtils.isEmpty(mConfig.suffixType) ? PictureFileUtils.POSTFIX : mConfig.suffixType;
            String newFileImageName = isOutFileNameEmpty ? DateUtils.getCreateFileName("IMG_") + suffix : mConfig.cameraFileName;
            File cameraFile = new File(rootDir, newFileImageName);
            Uri outUri = getOutUri(PictureMimeType.ofImage());
            if (outUri != null) {
                mConfig.cameraPath = outUri.toString();
            }
            return cameraFile;
        } else {
            String cameraFileName = "";
            if (!TextUtils.isEmpty(mConfig.cameraFileName)) {
                boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(mConfig.cameraFileName);
                mConfig.cameraFileName = !isSuffixOfImage ? StringUtils.renameSuffix(mConfig.cameraFileName, PictureMimeType.JPEG) : mConfig.cameraFileName;
                cameraFileName = mConfig.camera ? mConfig.cameraFileName : StringUtils.rename(mConfig.cameraFileName);
            }
            File cameraFile = PictureFileUtils.createCameraFile(getContext(),
                    PictureMimeType.ofImage(), cameraFileName, mConfig.suffixType, mConfig.outPutCameraPath);
            if (cameraFile != null) {
                mConfig.cameraPath = cameraFile.getAbsolutePath();
            }
            return cameraFile;
        }
    }

    public File createVideoFile() {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            String diskCacheDir = PictureFileUtils.getVideoDiskCacheDir(getContext());
            File rootDir = new File(diskCacheDir);
            if (!rootDir.exists() && rootDir.mkdirs()) {
            }
            boolean isOutFileNameEmpty = TextUtils.isEmpty(mConfig.cameraFileName);
            String suffix = TextUtils.isEmpty(mConfig.suffixType) ? PictureMimeType.MP4 : mConfig.suffixType;
            String newFileImageName = isOutFileNameEmpty ? DateUtils.getCreateFileName("VID_") + suffix : mConfig.cameraFileName;
            File cameraFile = new File(rootDir, newFileImageName);
            Uri outUri = getOutUri(PictureMimeType.ofVideo());
            if (outUri != null) {
                mConfig.cameraPath = outUri.toString();
            }
            return cameraFile;
        } else {
            String cameraFileName = "";
            if (!TextUtils.isEmpty(mConfig.cameraFileName)) {
                boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(mConfig.cameraFileName);
                mConfig.cameraFileName = !isSuffixOfImage ? StringUtils
                        .renameSuffix(mConfig.cameraFileName, PictureMimeType.MP4) : mConfig.cameraFileName;
                cameraFileName = mConfig.camera ? mConfig.cameraFileName : StringUtils.rename(mConfig.cameraFileName);
            }
            File cameraFile = PictureFileUtils.createCameraFile(getContext(),
                    PictureMimeType.ofVideo(), cameraFileName, mConfig.suffixType, mConfig.outPutCameraPath);
            mConfig.cameraPath = cameraFile.getAbsolutePath();
            return cameraFile;
        }
    }

    private Uri getOutUri(int type) {
        return type == PictureMimeType.ofVideo()
                ? MediaUtils.createVideoUri(getContext(), mConfig.suffixType) : MediaUtils.createImageUri(getContext(), mConfig.suffixType);
    }

    public void setCameraListener(CameraListener cameraListener) {
        this.mCameraListener = cameraListener;
    }

    public void setPictureSelectionConfig(PictureSelectionConfig config) {
        this.mConfig = config;
    }

    public void setBindToLifecycle(LifecycleOwner lifecycleOwner) {
        mCameraView.bindToLifecycle(lifecycleOwner);
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {

        });
    }

    /**
     * 设置录制视频最大时长 秒
     */
    public void setRecordVideoMaxTime(int maxDurationTime) {
        mCaptureLayout.setDuration(maxDurationTime * 1000);
    }

    /**
     * 设置录制视频最小时长 秒
     */
    public void setRecordVideoMinTime(int minDurationTime) {
        mCaptureLayout.setMinDuration(minDurationTime * 1000);
    }

    /**
     * 关闭相机界面按钮
     *
     * @param clickListener
     */
    public void setOnClickListener(ClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    public void setImageCallbackListener(ImageCallbackListener mImageCallbackListener) {
        this.mImageCallbackListener = mImageCallbackListener;
    }

    private void setFlashRes() {
        switch (type_flash) {
            case TYPE_FLASH_AUTO:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_auto);
                mCameraView.setFlash(ImageCapture.FLASH_MODE_AUTO);
                break;
            case TYPE_FLASH_ON:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_on);
                mCameraView.setFlash(ImageCapture.FLASH_MODE_ON);
                break;
            case TYPE_FLASH_OFF:
                mFlashLamp.setImageResource(R.drawable.picture_ic_flash_off);
                mCameraView.setFlash(ImageCapture.FLASH_MODE_OFF);
                break;
        }
    }

    public CameraView getCameraView() {
        return mCameraView;
    }

    public CaptureLayout getCaptureLayout() {
        return mCaptureLayout;
    }

    /**
     * 重置状态
     */
    private void resetState() {
        if (mCameraView.getCaptureMode() == androidx.camera.view.CameraView.CaptureMode.VIDEO) {
            if (mCameraView.isRecording()) {
                mCameraView.stopRecording();
            }
            if (mVideoFile != null && mVideoFile.exists()) {
                mVideoFile.delete();
                if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(mConfig.cameraPath)) {
                    getContext().getContentResolver().delete(Uri.parse(mConfig.cameraPath), null, null);
                } else {
                    new PictureMediaScannerConnection(getContext(), mVideoFile.getAbsolutePath());
                }
            }
        } else {
            mImagePreview.setVisibility(INVISIBLE);
            if (mPhotoFile != null && mPhotoFile.exists()) {
                mPhotoFile.delete();
                if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(mConfig.cameraPath)) {
                    getContext().getContentResolver().delete(Uri.parse(mConfig.cameraPath), null, null);
                } else {
                    new PictureMediaScannerConnection(getContext(), mPhotoFile.getAbsolutePath());
                }
            }
        }
        mSwitchCamera.setVisibility(VISIBLE);
        mFlashLamp.setVisibility(VISIBLE);
        mCameraView.setVisibility(View.VISIBLE);
        mCaptureLayout.resetCaptureLayout();
    }

    /**
     * 开始循环播放视频
     *
     * @param videoFile
     */
    private void startVideoPlay(File videoFile) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.setDataSource(videoFile.getAbsolutePath());
            mMediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();

                float ratio = mp.getVideoWidth() * 1f / mp.getVideoHeight();
                int width1 = mTextureView.getWidth();
                ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
                layoutParams.height = (int) (width1 / ratio);
                mTextureView.setLayoutParams(layoutParams);
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止视频播放
     */
    private void stopVideoPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mTextureView.setVisibility(View.GONE);
    }
}
