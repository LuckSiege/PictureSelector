package com.luck.picture.lib;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.camera.CustomCameraView;
import com.luck.picture.lib.camera.listener.CameraListener;
import com.luck.picture.lib.camera.listener.ClickListener;
import com.luck.picture.lib.camera.view.CaptureLayout;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.listener.OnPermissionDialogOptionCallback;
import com.luck.picture.lib.permissions.PermissionChecker;

import java.io.File;

/**
 * @author：luck
 * @date：2020-01-04 14:05
 * @describe：Custom photos and videos
 */
public class PictureCustomCameraActivity extends PictureSelectorCameraEmptyActivity {
    private final static String TAG = PictureCustomCameraActivity.class.getSimpleName();

    private CustomCameraView mCameraView;
    protected boolean isEnterSetting;

    @Override
    public boolean isImmersive() {
        return false;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        // 验证存储权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 验证相机权限和麦克风权限
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                    createCameraView();
                } else {
                    PermissionChecker.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE);
                }
            } else {
                PermissionChecker.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
            }
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    /**
     * 创建CameraView
     */
    private void createCameraView() {
        if (mCameraView == null) {
            mCameraView = new CustomCameraView(getContext());
            setContentView(mCameraView);
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 这里只针对权限被手动拒绝后进入设置页面重新获取权限后的操作
        if (isEnterSetting) {
            boolean isExternalStorage = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (isExternalStorage) {
                boolean isCameraPermissionChecker = PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.CAMERA);
                if (isCameraPermissionChecker) {
                    boolean isRecordAudio = PermissionChecker
                            .checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
                    if (isRecordAudio) {
                        createCameraView();
                    } else {
                        showPermissionsDialog(false, new String[]{Manifest.permission.RECORD_AUDIO}, getString(R.string.picture_audio));
                    }
                } else {
                    showPermissionsDialog(false, new String[]{Manifest.permission.CAMERA}, getString(R.string.picture_camera));
                }
            } else {
                showPermissionsDialog(false, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, getString(R.string.picture_jurisdiction));
            }
            isEnterSetting = false;
        }
    }

    /**
     * 初始化控件
     */
    protected void initView() {
        mCameraView.initCamera(config);
        // 视频最大拍摄时长
        if (config.recordVideoSecond > 0) {
            mCameraView.setRecordVideoMaxTime(config.recordVideoSecond);
        }
        // 视频最小拍摄时长
        if (config.recordVideoMinSecond > 0) {
            mCameraView.setRecordVideoMinTime(config.recordVideoMinSecond);
        }
        // 设置拍照时loading色值
        if (config.captureLoadingColor != 0) {
            mCameraView.setCaptureLoadingColor(config.captureLoadingColor);
        }
        // 获取录制按钮
        CaptureLayout captureLayout = mCameraView.getCaptureLayout();
        if (captureLayout != null) {
            captureLayout.setButtonFeatures(config.buttonFeatures);
        }
        // 拍照预览
        mCameraView.setImageCallbackListener((file, imageView) -> {
            if (config != null && PictureSelectionConfig.imageEngine != null && file != null) {
                PictureSelectionConfig.imageEngine.loadImage(getContext(), file.getAbsolutePath(), imageView);
            }
        });
        // 设置拍照或拍视频回调监听
        mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureSuccess(@NonNull File file) {
                config.cameraMimeType = PictureMimeType.ofImage();
                Intent intent = new Intent();
                intent.putExtra(PictureConfig.EXTRA_MEDIA_PATH, file.getAbsolutePath());
                intent.putExtra(PictureConfig.EXTRA_CONFIG, config);
                if (config.camera) {
                    dispatchHandleCamera(intent);
                } else {
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }
            }

            @Override
            public void onRecordSuccess(@NonNull File file) {
                config.cameraMimeType = PictureMimeType.ofVideo();
                Intent intent = new Intent();
                intent.putExtra(PictureConfig.EXTRA_MEDIA_PATH, file.getAbsolutePath());
                intent.putExtra(PictureConfig.EXTRA_CONFIG, config);
                if (config.camera) {
                    dispatchHandleCamera(intent);
                } else {
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                Log.i(TAG, "onError: " + message);
            }
        });

        //左边按钮点击事件
        mCameraView.setOnClickListener(new ClickListener() {
            @Override
            public void onClick() {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (config != null && config.camera && PictureSelectionConfig.listener != null) {
            PictureSelectionConfig.listener.onCancel();
        }
        exit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // 存储权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionChecker.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
                } else {
                    showPermissionsDialog(true, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, getString(R.string.picture_jurisdiction));
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // 相机权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean isRecordAudio = PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
                    if (isRecordAudio) {
                        createCameraView();
                    } else {
                        PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE);
                    }
                } else {
                    showPermissionsDialog(true, new String[]{Manifest.permission.CAMERA}, getString(R.string.picture_camera));
                }
                break;
            case PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE:
                // 录音权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createCameraView();
                } else {
                    showPermissionsDialog(false, new String[]{Manifest.permission.RECORD_AUDIO}, getString(R.string.picture_audio));
                }
                break;
        }
    }

    @Override
    protected void showPermissionsDialog(boolean isCamera, String[] permissions, String errorMsg) {
        if (isFinishing()) {
            return;
        }
        if (PictureSelectionConfig.onPermissionsObtainCallback != null) {
            PictureSelectionConfig.onPermissionsObtainCallback.onPermissionsIntercept(getContext(),
                    isCamera, permissions, errorMsg, new OnPermissionDialogOptionCallback() {

                        @Override
                        public void onCancel() {
                            if (PictureSelectionConfig.listener != null) {
                                PictureSelectionConfig.listener.onCancel();
                            }
                            exit();
                        }

                        @Override
                        public void onSetting() {
                            isEnterSetting = true;
                        }
                    });
            return;
        }
        PictureCustomDialog dialog = new PictureCustomDialog(getContext(), R.layout.picture_wind_base_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_commit = dialog.findViewById(R.id.btn_commit);
        btn_commit.setText(getString(R.string.picture_go_setting));
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        tvTitle.setText(getString(R.string.picture_prompt));
        tv_content.setText(errorMsg);
        btn_cancel.setOnClickListener(v -> {
            if (!isFinishing()) {
                dialog.dismiss();
            }
            if (PictureSelectionConfig.listener != null) {
                PictureSelectionConfig.listener.onCancel();
            }
            exit();
        });
        btn_commit.setOnClickListener(v -> {
            if (!isFinishing()) {
                dialog.dismiss();
            }
            PermissionChecker.launchAppDetailsSettings(getContext());
            isEnterSetting = true;
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mCameraView != null) {
            mCameraView.unbindCameraController();
        }
        super.onDestroy();
    }
}
