package com.luck.picture.lib;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author：luck
 * @date：2019-11-15 21:41
 * @describe：单独拍照承载空Activity
 */
public class PictureSelectorCameraEmptyActivity extends PictureBaseActivity {
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            onTakePhoto();
                        } else {
                            ToastUtils.s(mContext, getString(R.string.picture_camera));
                            closeActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        setTheme(R.style.MyTheme_Translucent);
        setContentView(R.layout.picture_empty);
    }

    /**
     * 启动相机
     */
    private void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    startCamera();
                } else {
                    ToastUtils.s(mContext, getString(R.string.picture_camera));
                    closeActivity();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
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
        String mimeType;
        final File file = new File(cameraPath);
        if (file == null) {
            return;
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        String toType;
        long size;
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        if (isAndroidQ) {
            String path = PictureFileUtils.getPath(getApplicationContext(), Uri.parse(cameraPath));
            File f = new File(path);
            size = f.length();
            toType = PictureMimeType.fileToType(f);
        } else {
            toType = PictureMimeType.fileToType(file);
            size = new File(cameraPath).length();
        }
        if (config.chooseMode != PictureMimeType.ofAudio()) {
            int degree = PictureFileUtils.readPictureDegree(file.getAbsolutePath());
            rotateImage(degree, file);
        }
        // 生成新拍照片或视频对象
        LocalMedia media = new LocalMedia();
        media.setPath(cameraPath);
        boolean eqVideo = toType.startsWith(PictureConfig.VIDEO);
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
        cameraHandleResult(medias, media, toType);
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
     * @param toType
     */
    private void cameraHandleResult(List<LocalMedia> medias, LocalMedia media, String toType) {
        // 如果是单选 拍照后直接返回
        boolean eqImg = toType.startsWith(PictureConfig.IMAGE);
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
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }
}
