package com.luck.picture.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.manager.UCropManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.BitmapUtils;
import com.luck.picture.lib.tools.AlbumUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.tools.CameraFileUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-15 21:41
 * @describe：PictureSelectorCameraEmptyActivity
 */
public class PictureSelectorCameraEmptyActivity extends PictureBaseActivity {

    @Override
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , ContextCompat.getColor(this, R.color.picture_color_transparent)
                , ContextCompat.getColor(this, R.color.picture_color_transparent)
                , openWhiteStatusBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (config == null) {
            exit();
            return;
        }
        if (!config.isUseCustomCamera) {
            setActivitySize();
            if (savedInstanceState == null) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    if (PictureSelectionConfig.onCustomCameraInterfaceListener != null) {
                        if (config.chooseMode == PictureConfig.TYPE_VIDEO) {
                            PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_VIDEO);
                        } else {
                            PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_IMAGE);
                        }
                    } else {
                        onTakePhoto();
                    }
                } else {
                    PermissionChecker.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                }
            }
        }
    }

    /**
     * 设置个1像素的Activity
     */
    private void setActivitySize() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }


    @Override
    public int getResourceId() {
        return R.layout.picture_empty;
    }


    /**
     * open camera
     */
    private void onTakePhoto() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    /**
     * Open the Camera by type
     */
    private void startCamera() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
            case PictureConfig.TYPE_IMAGE:
                startOpenCameraImage();
                break;
            case PictureConfig.TYPE_VIDEO:
                startOpenCameraVideo();
                break;
            case PictureConfig.TYPE_AUDIO:
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
                case PictureConfig.REQUEST_CAMERA:
                    dispatchHandleCamera(data);
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (PictureSelectionConfig.listener != null) {
                PictureSelectionConfig.listener.onCancel();
            }
            // Delete this cameraPath when you cancel the camera
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                MediaUtils.deleteCamera(this, config.cameraPath);
            }
            exit();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            if (data == null) {
                return;
            }
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            if (throwable != null) {
                ToastUtils.s(getContext(), throwable.getMessage());
            }
        }
    }

    /**
     * Single picture clipping callback
     *
     * @param data
     */
    protected void singleCropHandleResult(Intent data) {
        if (data == null) {
            return;
        }
        List<LocalMedia> medias = new ArrayList<>();
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri == null) {
            return;
        }
        String cutPath = resultUri.getPath();
        boolean isCutEmpty = TextUtils.isEmpty(cutPath);
        LocalMedia media = LocalMedia.parseLocalMedia(config.cameraPath, config.isCamera ? 1 : 0, config.chooseMode);
        if (SdkVersionUtils.isQ()) {
            int lastIndexOf = TextUtils.isEmpty(config.cameraPath) ? 0 : config.cameraPath.lastIndexOf("/") + 1;
            media.setId(lastIndexOf > 0 ? ValueOf.toLong(config.cameraPath.substring(lastIndexOf)) : System.currentTimeMillis());
            media.setAndroidQToPath(cutPath);
        } else {
            // Taking a photo generates a temporary id
            media.setId(System.currentTimeMillis());
        }
        media.setCut(!isCutEmpty);
        media.setCutPath(cutPath);
        String mimeType = PictureMimeType.getImageMimeType(cutPath);
        media.setMimeType(mimeType);
        media.setCropImageWidth(data.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH,0));
        media.setCropImageHeight(data.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT,0));
        media.setCropOffsetX(data.getIntExtra(UCrop.EXTRA_OUTPUT_OFFSET_X,0));
        media.setCropOffsetY(data.getIntExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y,0));
        media.setCropResultAspectRatio(data.getFloatExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO,0F));
        media.setEditorImage(data.getBooleanExtra(UCrop.EXTRA_EDITOR_IMAGE,false));
        if (PictureMimeType.isContent(media.getPath())) {
            String path = PictureFileUtils.getPath(getContext(), Uri.parse(media.getPath()));
            media.setRealPath(path);
            if (PictureMimeType.isHasVideo(media.getMimeType())) {
                MediaExtraInfo mediaExtraInfo = MediaUtils.getVideoSize(getContext(), media.getPath());
                media.setWidth(mediaExtraInfo.getWidth());
                media.setHeight(mediaExtraInfo.getHeight());
            } else if (PictureMimeType.isHasImage(media.getMimeType())) {
                MediaExtraInfo mediaExtraInfo = MediaUtils.getImageSize(getContext(), media.getPath());
                media.setWidth(mediaExtraInfo.getWidth());
                media.setHeight(mediaExtraInfo.getHeight());
            }
        } else {
            media.setRealPath(media.getPath());
            if (PictureMimeType.isHasVideo(media.getMimeType())) {
                MediaExtraInfo mediaExtraInfo = MediaUtils.getVideoSize(getContext(), media.getPath());
                media.setWidth(mediaExtraInfo.getWidth());
                media.setHeight(mediaExtraInfo.getHeight());
            } else if (PictureMimeType.isHasImage(media.getMimeType())) {
                MediaExtraInfo mediaExtraInfo = MediaUtils.getImageSize(getContext(), media.getPath());
                media.setWidth(mediaExtraInfo.getWidth());
                media.setHeight(mediaExtraInfo.getHeight());
            }
        }

        File file = new File(media.getRealPath());
        media.setSize(file.length());
        media.setFileName(file.getName());

        medias.add(media);
        handlerResult(medias);
    }

    /**
     * dispatchHandleCamera
     *
     * @param intent
     */
    protected void dispatchHandleCamera(Intent intent) {
        try {
            if (config.chooseMode == PictureMimeType.ofAudio()) {
                config.cameraMimeType = PictureMimeType.ofAudio();
                config.cameraPath = getAudioPath(intent);
                if (TextUtils.isEmpty(config.cameraPath)) {
                    return;
                }
                if (SdkVersionUtils.isR()) {
                    try {
                        Uri audioOutUri = CameraFileUtils.createAudioUri(getContext(), TextUtils.isEmpty(config.cameraAudioFormatForQ) ? config.suffixType : config.cameraAudioFormatForQ);
                        if (audioOutUri != null) {
                            InputStream inputStream = PictureContentResolver.getContentResolverOpenInputStream(this, Uri.parse(config.cameraPath));
                            OutputStream outputStream = PictureContentResolver.getContentResolverOpenOutputStream(this, audioOutUri);
                            PictureFileUtils.writeFileFromIS(inputStream,outputStream);
                            config.cameraPath = audioOutUri.toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (TextUtils.isEmpty(config.cameraPath)) {
                return;
            }
            LocalMedia media = new LocalMedia();
            String mimeType;
            if (PictureMimeType.isContent(config.cameraPath)) {
                // content: Processing rules
                String path = PictureFileUtils.getPath(getContext(), Uri.parse(config.cameraPath));
                File cameraFile = new File(path);
                mimeType = PictureMimeType.getImageMimeType(path,config.cameraMimeType);
                media.setSize(cameraFile.length());
                media.setFileName(cameraFile.getName());
                if (PictureMimeType.isHasImage(mimeType)) {
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getImageSize(getContext(), config.cameraPath);
                    media.setWidth(mediaExtraInfo.getWidth());
                    media.setHeight(mediaExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(mimeType)) {
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getVideoSize(getContext(), config.cameraPath);
                    media.setWidth(mediaExtraInfo.getWidth());
                    media.setHeight(mediaExtraInfo.getHeight());
                    media.setDuration(mediaExtraInfo.getDuration());
                } else if (PictureMimeType.isHasAudio(mimeType)) {
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getAudioSize(getContext(), config.cameraPath);
                    media.setDuration(mediaExtraInfo.getDuration());
                }
                int lastIndexOf = TextUtils.isEmpty(config.cameraPath) ? 0 : config.cameraPath.lastIndexOf("/") + 1;
                media.setId(lastIndexOf > 0 ? ValueOf.toLong(config.cameraPath.substring(lastIndexOf)) : System.currentTimeMillis());
                media.setRealPath(path);
                // Custom photo has been in the application sandbox into the file
                String mediaPath = intent != null ? intent.getStringExtra(PictureConfig.EXTRA_MEDIA_PATH) : null;
                media.setAndroidQToPath(!PictureMimeType.isContent(mediaPath) ? mediaPath : null);

                long bucketId = AlbumUtils.generateCameraBucketId(getContext(), cameraFile, "");
                media.setBucketId(bucketId);
                media.setDateAddedTime(cameraFile.lastModified() / 1000);
            } else {
                File cameraFile = new File(config.cameraPath);
                mimeType = PictureMimeType.getImageMimeType(config.cameraPath,config.cameraMimeType);
                media.setSize(cameraFile.length());
                media.setFileName(cameraFile.getName());
                if (PictureMimeType.isHasImage(mimeType)) {
                    BitmapUtils.rotateImage(getContext(), config.isCameraRotateImage, config.cameraPath);
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getImageSize(getContext(), config.cameraPath);
                    media.setWidth(mediaExtraInfo.getWidth());
                    media.setHeight(mediaExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(mimeType)) {
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getVideoSize(getContext(), config.cameraPath);
                    media.setWidth(mediaExtraInfo.getWidth());
                    media.setHeight(mediaExtraInfo.getHeight());
                    media.setDuration(mediaExtraInfo.getDuration());
                } else if (PictureMimeType.isHasAudio(mimeType)) {
                    MediaExtraInfo mediaExtraInfo = MediaUtils.getAudioSize(getContext(), config.cameraPath);
                    media.setDuration(mediaExtraInfo.getDuration());
                }
                // Taking a photo generates a temporary id
                media.setId(System.currentTimeMillis());
                media.setRealPath(config.cameraPath);
                // Custom photo has been in the application sandbox into the file
                String mediaPath = intent != null ? intent.getStringExtra(PictureConfig.EXTRA_MEDIA_PATH) : null;
                if (SdkVersionUtils.isQ()){
                    if (!TextUtils.isEmpty(mediaPath) && !PictureMimeType.isContent(mediaPath)) {
                        media.setAndroidQToPath(mediaPath);
                    } else {
                        media.setAndroidQToPath(config.cameraPath);
                    }
                }

                long bucketId = AlbumUtils.generateCameraBucketId(getContext(), cameraFile, config.outPutCameraPath);
                media.setBucketId(bucketId);
                media.setDateAddedTime(cameraFile.lastModified() / 1000);
            }
            media.setPath(config.cameraPath);
            media.setMimeType(mimeType);
            String folderName = AlbumUtils.generateCameraFolderName(config.cameraPath, mimeType, config.outPutCameraPath);
            media.setParentFolderName(folderName);

            media.setChooseModel(config.chooseMode);

            dispatchCameraHandleResult(media);

            if (SdkVersionUtils.isQ()) {
                if (PictureMimeType.isHasVideo(media.getMimeType()) && PictureMimeType.isContent(config.cameraPath)) {
                    if (config.isFallbackVersion3) {
                        new PictureMediaScannerConnection(getContext(), media.getRealPath());
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(media.getRealPath()))));
                    }
                }
            } else {
                if (config.isFallbackVersion3) {
                    new PictureMediaScannerConnection(getContext(), config.cameraPath);
                } else {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(config.cameraPath))));
                }
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    int lastImageId = MediaUtils.getDCIMLastImageId(getContext());
                    if (lastImageId != -1) {
                        MediaUtils.removeMedia(getContext(), lastImageId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dispatchCameraHandleResult
     *
     * @param media
     */
    private void dispatchCameraHandleResult(LocalMedia media) {
        boolean isHasImage = PictureMimeType.isHasImage(media.getMimeType());
        if (config.enableCrop && !config.isCheckOriginalImage && isHasImage) {
            config.originalPath = config.cameraPath;
            UCropManager.ofCrop(this, config.cameraPath, media.getMimeType(),media.getWidth(),media.getHeight());
        } else if (config.isCompress && isHasImage) {
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            compressImage(result);
        } else {
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            onResult(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // Store Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionChecker.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
                } else {
                    ToastUtils.s(getContext(), getString(R.string.picture_jurisdiction));
                    exit();
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // Camera Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    exit();
                    ToastUtils.s(getContext(), getString(R.string.picture_camera));
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (SdkVersionUtils.isQ()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
        exit();
    }
}
