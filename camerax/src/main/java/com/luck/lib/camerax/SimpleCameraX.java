package com.luck.lib.camerax;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener;
import com.luck.lib.camerax.utils.FileUtils;

/**
 * @author：luck
 * @date：2021/11/29 7:52 下午
 * @describe：SimpleCameraX
 */
public class SimpleCameraX {

    private static final String EXTRA_PREFIX = BuildConfig.LIBRARY_PACKAGE_NAME;

    public static final String EXTRA_OUTPUT_PATH_DIR = EXTRA_PREFIX + ".OutputPathDir";

    public static final String EXTRA_CAMERA_FILE_NAME = EXTRA_PREFIX + ".CameraFileName";

    public static final String EXTRA_CAMERA_MODE = EXTRA_PREFIX + ".CameraMode";

    public static final String EXTRA_VIDEO_FRAME_RATE = EXTRA_PREFIX + ".VideoFrameRate";

    public static final String EXTRA_VIDEO_BIT_RATE = EXTRA_PREFIX + ".VideoBitRate";

    public static final String EXTRA_CAMERA_AROUND_STATE = EXTRA_PREFIX + ".CameraAroundState";

    public static final String EXTRA_RECORD_VIDEO_MAX_SECOND = EXTRA_PREFIX + ".RecordVideoMaxSecond";

    public static final String EXTRA_RECORD_VIDEO_MIN_SECOND = EXTRA_PREFIX + ".RecordVideoMinSecond";

    public static final String EXTRA_CAMERA_IMAGE_FORMAT = EXTRA_PREFIX + ".CameraImageFormat";

    public static final String EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q = EXTRA_PREFIX + ".CameraImageFormatForQ";

    public static final String EXTRA_CAMERA_VIDEO_FORMAT = EXTRA_PREFIX + ".CameraVideoFormat";

    public static final String EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q = EXTRA_PREFIX + ".CameraVideoFormatForQ";

    public static final String EXTRA_CAPTURE_LOADING_COLOR = EXTRA_PREFIX + ".CaptureLoadingColor";

    public static final String EXTRA_DISPLAY_RECORD_CHANGE_TIME = EXTRA_PREFIX + ".DisplayRecordChangeTime";

    public static final String EXTRA_MANUAL_FOCUS = EXTRA_PREFIX + ".isManualFocus";

    public static final String EXTRA_ZOOM_PREVIEW = EXTRA_PREFIX + ".isZoomPreview";

    public static final String EXTRA_AUTO_ROTATION = EXTRA_PREFIX + ".isAutoRotation";


    private final Intent mCameraIntent;

    private final Bundle mCameraBundle;

    public static SimpleCameraX of() {
        return new SimpleCameraX();
    }

    private SimpleCameraX() {
        mCameraIntent = new Intent();
        mCameraBundle = new Bundle();
    }

    /**
     * Send the camera Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        if (CustomCameraConfig.imageEngine == null) {
            throw new NullPointerException("Missing ImageEngine,please implement SimpleCamerax.setImageEngine");
        }
        activity.startActivityForResult(getIntent(activity), requestCode);
    }


    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        if (CustomCameraConfig.imageEngine == null) {
            throw new NullPointerException("Missing ImageEngine,please implement SimpleCamerax.setImageEngine");
        }
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Get Intent to start {@link PictureCameraActivity}
     *
     * @return Intent for {@link PictureCameraActivity}
     */
    public Intent getIntent(@NonNull Context context) {
        mCameraIntent.setClass(context, PictureCameraActivity.class);
        mCameraIntent.putExtras(mCameraBundle);
        return mCameraIntent;
    }

    /**
     * Set Camera Preview Image Engine
     *
     * @param engine
     * @return
     */
    public SimpleCameraX setImageEngine(CameraImageEngine engine) {
        CustomCameraConfig.imageEngine = engine;
        return this;
    }

    /**
     * Permission description
     *
     * @param explainListener
     * @return
     */
    public SimpleCameraX setPermissionDescriptionListener(OnSimpleXPermissionDescriptionListener explainListener) {
        CustomCameraConfig.explainListener = explainListener;
        return this;
    }

    /**
     * Permission denied
     *
     * @param deniedListener
     * @return
     */
    public SimpleCameraX setPermissionDeniedListener(OnSimpleXPermissionDeniedListener deniedListener) {
        CustomCameraConfig.deniedListener = deniedListener;
        return this;
    }

    /**
     * 相机模式
     *
     * @param cameraMode Use {@link CustomCameraConfig}
     * @return
     */
    public SimpleCameraX setCameraMode(int cameraMode) {
        mCameraBundle.putInt(EXTRA_CAMERA_MODE, cameraMode);
        return this;
    }


    /**
     * 视频帧率，越高视频体积越大
     *
     * @param videoFrameRate 0~100
     * @return
     */
    public SimpleCameraX setVideoFrameRate(int videoFrameRate) {
        mCameraBundle.putInt(EXTRA_VIDEO_FRAME_RATE, videoFrameRate);
        return this;
    }

    /**
     * bit率， 越大视频体积越大
     *
     * @param bitRate example 3 * 1024 * 1024
     * @return
     */
    public SimpleCameraX setVideoBitRate(int bitRate) {
        mCameraBundle.putInt(EXTRA_VIDEO_BIT_RATE, bitRate);
        return this;
    }


    /**
     * 相机前置或后置
     *
     * @param isCameraAroundState true 前置,默认false后置
     * @return
     */
    public SimpleCameraX setCameraAroundState(boolean isCameraAroundState) {
        mCameraBundle.putBoolean(EXTRA_CAMERA_AROUND_STATE, isCameraAroundState);
        return this;
    }


    /**
     * 拍照自定义输出路径
     *
     * @param outputPath
     * @return
     */
    public SimpleCameraX setOutputPathDir(String outputPath) {
        mCameraBundle.putString(EXTRA_OUTPUT_PATH_DIR, outputPath);
        return this;
    }

    /**
     * 拍照输出文件名
     *
     * @param fileName
     * @return
     */
    public SimpleCameraX setCameraOutputFileName(String fileName) {
        mCameraBundle.putString(EXTRA_CAMERA_FILE_NAME, fileName);
        return this;
    }

    /**
     * 视频最大录制时长 单位：秒
     *
     * @param maxSecond
     * @return
     */
    public SimpleCameraX setRecordVideoMaxSecond(int maxSecond) {
        mCameraBundle.putInt(EXTRA_RECORD_VIDEO_MAX_SECOND, maxSecond * 1000 + 500);
        return this;
    }

    /**
     * 视频最小录制时长 单位：秒
     *
     * @param minSecond
     * @return
     */
    public SimpleCameraX setRecordVideoMinSecond(int minSecond) {
        mCameraBundle.putInt(EXTRA_RECORD_VIDEO_MIN_SECOND, minSecond * 1000);
        return this;
    }

    /**
     * 图片输出类型
     * <p>
     * 比如 xxx.jpg or xxx.png
     * </p>
     *
     * @param format
     * @return
     */
    public SimpleCameraX setCameraImageFormat(String format) {
        mCameraBundle.putString(EXTRA_CAMERA_IMAGE_FORMAT, format);
        return this;
    }

    /**
     * Android Q 以上 图片输出类型
     * <p>
     * 比如 "image/jpeg"
     * </p>
     *
     * @param format
     * @return
     */
    public SimpleCameraX setCameraImageFormatForQ(String format) {
        mCameraBundle.putString(EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q, format);
        return this;
    }

    /**
     * 视频输出类型
     * <p>
     * 比如 xxx.mp4
     * </p>
     *
     * @param format
     * @return
     */
    public SimpleCameraX setCameraVideoFormat(String format) {
        mCameraBundle.putString(EXTRA_CAMERA_VIDEO_FORMAT, format);
        return this;
    }

    /**
     * Android Q 以上 视频输出类型
     * <p>
     * 比如 "video/mp4"
     * </p>
     *
     * @param format
     * @return
     */
    public SimpleCameraX setCameraVideoFormatForQ(String format) {
        mCameraBundle.putString(EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q, format);
        return this;
    }

    /**
     * 拍照Loading的色值
     *
     * @param color
     * @return
     */
    public SimpleCameraX setCaptureLoadingColor(int color) {
        mCameraBundle.putInt(EXTRA_CAPTURE_LOADING_COLOR, color);
        return this;
    }

    /**
     * 是否显示录制时间
     *
     * @param isDisplayRecordTime
     * @return
     */
    public SimpleCameraX isDisplayRecordChangeTime(boolean isDisplayRecordTime) {
        mCameraBundle.putBoolean(EXTRA_DISPLAY_RECORD_CHANGE_TIME, isDisplayRecordTime);
        return this;
    }

    /**
     * 是否手动点击对焦
     *
     * @param isManualFocus
     * @return
     */
    public SimpleCameraX isManualFocusCameraPreview(boolean isManualFocus) {
        mCameraBundle.putBoolean(EXTRA_MANUAL_FOCUS, isManualFocus);
        return this;
    }

    /**
     * 是否可缩放相机
     *
     * @param isZoom
     * @return
     */
    public SimpleCameraX isZoomCameraPreview(boolean isZoom) {
        mCameraBundle.putBoolean(EXTRA_ZOOM_PREVIEW, isZoom);
        return this;
    }

    /**
     * 是否自动纠偏
     *
     * @param isAutoRotation
     * @return
     */
    public SimpleCameraX isAutoRotation(boolean isAutoRotation) {
        mCameraBundle.putBoolean(EXTRA_AUTO_ROTATION, isAutoRotation);
        return this;
    }

    /**
     * 保存相机输出的路径
     *
     * @param intent
     * @param uri
     */
    public static void putOutputUri(Intent intent, Uri uri) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

    /**
     * 获取保存相机输出的路径
     *
     * @param intent
     * @return
     */
    public static String getOutputPath(Intent intent) {
        Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        if (uri == null) {
            return "";
        }
        return FileUtils.isContent(uri.toString()) ? uri.toString() : uri.getPath();
    }
}
