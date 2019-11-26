package com.luck.picture.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-15 21:41
 * @describe：单独拍照承载空Activity
 */
public class PictureSelectorCameraEmptyActivity extends PictureBaseActivity {
    @Override
    public boolean isImmersive() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            onTakePhoto();
        } else {
            ToastUtils.s(mContext, getString(R.string.picture_camera));
            closeActivity();
            return;
        }
        setTheme(R.style.Picture_Theme_Translucent);
        setContentView(R.layout.picture_empty);
    }

    /**
     * 启动相机
     */
    private void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    /**
     * 根据类型启动相应相机
     */
    private void startCamera() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
            case PictureConfig.TYPE_IMAGE:
                // 拍照
                startOpenCamera();
                break;
            case PictureConfig.TYPE_VIDEO:
                // 录视频
                startOpenCameraVideo();
                break;
            case PictureConfig.TYPE_AUDIO:
                // 录音
                startOpenCameraAudio();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    singleCropHandleResult(data);
                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    multiCropHandleResult(data);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    requestCamera(data);
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            closeActivity();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastUtils.s(mContext, throwable.getMessage());
        }
    }

    /**
     * 单张图片裁剪
     *
     * @param data
     */
    private void singleCropHandleResult(Intent data) {
        List<LocalMedia> medias = new ArrayList<>();
        Uri resultUri = UCrop.getOutput(data);
        String cutPath = resultUri.getPath();
        // 单独拍照
        LocalMedia media = new LocalMedia(cameraPath, 0, false,
                config.isCamera ? 1 : 0, 0, config.chooseMode);
        media.setCut(true);
        media.setCutPath(cutPath);
        String mimeType = PictureMimeType.getImageMimeType(cutPath);
        media.setMimeType(mimeType);
        medias.add(media);
        handlerResult(medias);
    }


    /**
     * 拍照后处理结果
     *
     * @param data
     */
    private void requestCamera(Intent data) {
        List<LocalMedia> medias = new ArrayList<>();
        if (config.chooseMode == PictureMimeType.ofAudio()) {
            cameraPath = getAudioPath(data);
        }
        // on take photo success
        final File file = new File(cameraPath);
        if (file == null) {
            return;
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        String mimeType;
        long size;
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        if (isAndroidQ) {
            String path = PictureFileUtils.getPath(getApplicationContext(), Uri.parse(cameraPath));
            File f = new File(path);
            size = f.length();
            mimeType = PictureMimeType.fileToType(f);
        } else {
            mimeType = PictureMimeType.fileToType(file);
            size = new File(cameraPath).length();
        }
        if (config.chooseMode != PictureMimeType.ofAudio()) {
            int degree = PictureFileUtils.readPictureDegree(this, file.getAbsolutePath());
            rotateImage(degree, file);
        }
        // 生成新拍照片或视频对象
        LocalMedia media = new LocalMedia();
        media.setPath(cameraPath);
        boolean eqVideo = PictureMimeType.eqVideo(mimeType);
        if (config.chooseMode == PictureMimeType.ofAudio()) {
            mimeType = PictureMimeType.MIME_TYPE_AUDIO;
        } else {
            if (eqVideo) {
                mimeType = isAndroidQ ? PictureMimeType.getMimeType(mContext, Uri.parse(cameraPath))
                        : PictureMimeType.getVideoMimeType(cameraPath);
            } else {
                mimeType = isAndroidQ ? PictureMimeType.getMimeType(mContext, Uri.parse(cameraPath))
                        : PictureMimeType.getImageMimeType(cameraPath);
            }
        }
        long duration = MediaUtils.extractDuration(mContext, isAndroidQ, cameraPath);
        media.setMimeType(mimeType);
        media.setDuration(duration);
        media.setSize(size);
        media.setChooseModel(config.chooseMode);
        cameraHandleResult(medias, media, mimeType);
        if (config.chooseMode != PictureMimeType.ofAudio()) {
            int lastImageId = getLastImageId(eqVideo);
            if (lastImageId != -1) {
                removeImage(lastImageId, eqVideo);
            }
        }
    }

    /**
     * 摄像头后处理方式
     *
     * @param medias
     * @param media
     * @param mimeType
     */
    private void cameraHandleResult(List<LocalMedia> medias, LocalMedia media, String mimeType) {
        // 如果是单选 拍照后直接返回
        boolean eqImg = PictureMimeType.eqImage(mimeType);
        if (config.enableCrop && eqImg) {
            // 去裁剪
            originalPath = cameraPath;
            startCrop(cameraPath);
        } else if (config.isCompress && eqImg) {
            // 去压缩
            medias.add(media);
            compressImage(medias);
        } else {
            // 不裁剪 不压缩 直接返回结果
            medias.add(media);
            onResult(medias);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // 存储权限
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        onTakePhoto();
                    } else {
                        closeActivity();
                        ToastUtils.s(mContext, getString(R.string.picture_camera));
                    }
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // 相机权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    closeActivity();
                    ToastUtils.s(mContext, getString(R.string.picture_camera));
                }
                break;
        }
    }
}
