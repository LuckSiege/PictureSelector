package com.luck.picture.lib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectMimeType;

import java.io.File;

/**
 * @author：luck
 * @date：2021/11/8 4:27 下午
 * @describe：CameraFileUtils
 */
public class MediaStoreUtils {

    /**
     * 拍摄图片地址
     *
     * @param context 上下文
     * @param config  PictureSelector配制类
     * @return
     */
    public static Uri createCameraOutImageUri(Context context, SelectorConfig config) {
        Uri imageUri;
        String cameraFileName;
        if (TextUtils.isEmpty(config.outPutCameraImageFileName)) {
            cameraFileName = "";
        } else {
            cameraFileName = config.isOnlyCamera
                    ? config.outPutCameraImageFileName : System.currentTimeMillis() + "_" + config.outPutCameraImageFileName;
        }
        if (SdkVersionUtils.isQ() && TextUtils.isEmpty(config.outPutCameraDir)) {
            imageUri = createImageUri(context, cameraFileName, config.cameraImageFormatForQ);
            config.cameraPath = imageUri != null ? imageUri.toString() : "";
        } else {
            File cameraFile = PictureFileUtils.createCameraFile(context, SelectMimeType.TYPE_IMAGE,
                    cameraFileName, config.cameraImageFormat, config.outPutCameraDir);
            config.cameraPath = cameraFile.getAbsolutePath();
            imageUri = PictureFileUtils.parUri(context, cameraFile);
        }
        return imageUri;
    }


    /**
     * 拍摄视频地址
     *
     * @param context 上下文
     * @param config  PictureSelector配制类
     * @return
     */
    public static Uri createCameraOutVideoUri(Context context, SelectorConfig config) {
        Uri videoUri;
        String cameraFileName;
        if (TextUtils.isEmpty(config.outPutCameraVideoFileName)) {
            cameraFileName = "";
        } else {
            cameraFileName = config.isOnlyCamera
                    ? config.outPutCameraVideoFileName : System.currentTimeMillis() + "_" + config.outPutCameraVideoFileName;
        }
        if (SdkVersionUtils.isQ() && TextUtils.isEmpty(config.outPutCameraDir)) {
            videoUri = createVideoUri(context, cameraFileName, config.cameraVideoFormatForQ);
            config.cameraPath = videoUri != null ? videoUri.toString() : "";
        } else {
            File cameraFile = PictureFileUtils.createCameraFile(context, SelectMimeType.TYPE_VIDEO,
                    cameraFileName, config.cameraVideoFormat, config.outPutCameraDir);
            config.cameraPath = cameraFile.getAbsolutePath();
            videoUri = PictureFileUtils.parUri(context, cameraFile);
        }
        return videoUri;
    }


    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param ctx            上下文
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static Uri createImageUri(final Context ctx, String cameraFileName, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        ContentValues contentValues = buildImageContentValues(cameraFileName, mimeType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValues);
        }
        return imageFilePath[0];
    }


    /**
     * 构建图片的ContentValues,用于保存拍照后的照片
     *
     * @param customFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static ContentValues buildImageContentValues(String customFileName, String mimeType) {
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(customFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        } else {
            if (customFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
            } else {
                String suffix = customFileName.substring(customFileName.lastIndexOf("."));
                String fileName = customFileName.replaceAll(suffix, "");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            }
        }
        values.put(MediaStore.Images.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) ? PictureMimeType.MIME_TYPE_IMAGE : mimeType);
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);
        }
        return values;
    }


    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param ctx            上下文
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static Uri createVideoUri(final Context ctx, String cameraFileName, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues contentValues = buildVideoContentValues(cameraFileName, mimeType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, contentValues);
        }
        return imageFilePath[0];
    }

    /**
     * 构建视频的ContentValues,用于保存拍照后的照片
     *
     * @param customFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static ContentValues buildVideoContentValues(String customFileName, String mimeType) {
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(customFileName)) {
            values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
        } else {
            if (customFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
            } else {
                String suffix = customFileName.substring(customFileName.lastIndexOf("."));
                String fileName = customFileName.replaceAll(suffix, "");
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            }
        }
        values.put(MediaStore.Video.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) ? PictureMimeType.MIME_TYPE_VIDEO : mimeType);
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time);
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
        }
        return values;
    }
}
