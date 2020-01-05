package com.luck.picture.lib;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.view.CameraView;

import com.luck.picture.lib.camera.CustomCameraView;
import com.luck.picture.lib.camera.listener.CameraListener;
import com.luck.picture.lib.camera.view.CaptureLayout;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.tools.ToastUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @author：luck
 * @date：2020-01-04 14:05
 * @describe：自定义拍照和录音
 */
public class PictureCustomCameraActivity extends PictureSelectorCameraEmptyActivity {


    private CustomCameraView mCameraView;

    @Override
    public boolean isImmersive() {
        return false;
    }

    @Override
    public int getResourceId() {
        return R.layout.picture_custom_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mCameraView = findViewById(R.id.camera_view);
        mCameraView.setPictureSelectionConfig(config);
        // 绑定生命周期
        mCameraView.setBindToLifecycle(new WeakReference<>(this).get());
        // 视频最大拍摄时长
        if (config.recordVideoSecond > 0) {
            mCameraView.setRecordVideoMaxTime(config.recordVideoSecond);
        }
        // 视频最小拍摄时长
        if (config.recordVideoMinSecond > 0) {
            mCameraView.setRecordVideoMinTime(config.recordVideoMinSecond);
        }
        // 获取CameraView
        CameraView cameraView = mCameraView.getCameraView();
        if (cameraView != null && config.isCameraAroundState) {
            cameraView.toggleCamera();
        }
        // 获取录制按钮
        CaptureLayout captureLayout = mCameraView.getCaptureLayout();
        if (captureLayout != null) {
            captureLayout.setButtonFeatures(config.buttonFeatures);
        }
        // 拍照预览
        mCameraView.setImageCallbackListener((file, imageView) -> {
            if (config != null && config.imageEngine != null && file != null) {
                config.imageEngine.loadImage(getContext(), file.getAbsolutePath(), imageView);
            }
        });
        // 设置拍照或拍视频回调监听
        mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureSuccess(@NonNull File file) {
                if (config.camera) {
                    Intent intent = new Intent();
                    intent.putExtra(PictureConfig.EXTRA_MEDIA_PATH, file.getAbsolutePath());
                    requestCamera(intent);
                } else {
                    setResult(RESULT_OK);
                    onBackPressed();
                }
            }

            @Override
            public void onRecordSuccess(@NonNull File file) {
                if (config.camera) {
                    Intent intent = new Intent();
                    intent.putExtra(PictureConfig.EXTRA_MEDIA_PATH, file.getAbsolutePath());
                    requestCamera(intent);
                } else {
                    setResult(RESULT_OK);
                    onBackPressed();
                }
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                ToastUtils.s(getContext(), message);
                onBackPressed();
            }
        });

        //左边按钮点击事件
        mCameraView.setOnClickListener(() -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onDestroy() {
        CameraX.unbindAll();
        super.onDestroy();
    }
}
