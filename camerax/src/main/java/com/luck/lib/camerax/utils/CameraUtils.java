package com.luck.lib.camerax.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * @author：luck
 * @date：2021/11/8 4:27 下午
 * @describe：CameraFileUtils
 */
public class CameraUtils {
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    public final static String CAMERA = "Camera";
    public final static String MIME_TYPE_PREFIX_IMAGE = "image";
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";
    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String DCIM_CAMERA = "DCIM/Camera";
    public final static String JPEG = ".jpeg";
    public final static String MP4 = ".mp4";
    /**
     * 拍摄图片地址
     *
     * @param context
     * @param outPutCameraPath
     * @param cameraFileName
     * @param cameraImageFormatForQ
     * @param cameraImageFormat
     * @return
     */
    public static Uri createCameraOutImageUri(Context context,
                                              String outPutCameraPath, String cameraFileName,
                                              String cameraImageFormatForQ, String cameraImageFormat) {
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(outPutCameraPath)) {
            imageUri = createImageUri(context, cameraFileName, cameraImageFormatForQ);
        } else {
            File cameraFile = FileUtils.createCameraFile(context, TYPE_IMAGE,
                    cameraFileName, cameraImageFormat, outPutCameraPath);
            imageUri = FileUtils.parUri(context, cameraFile);
        }
        return imageUri;
    }


    /**
     * 拍摄视频地址
     *
     * @param context
     * @param outPutCameraPath
     * @param cameraFileName
     * @param cameraVideoFormatForQ
     * @param cameraVideoFormat
     * @return
     */
    public static Uri createCameraOutVideoUri(Context context,
                                              String outPutCameraPath, String cameraFileName,
                                              String cameraVideoFormatForQ, String cameraVideoFormat) {
        Uri videoUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(outPutCameraPath)) {
            videoUri = createVideoUri(context, cameraFileName, cameraVideoFormatForQ);
        } else {
            File cameraFile = FileUtils.createCameraFile(context, TYPE_VIDEO, cameraFileName, cameraVideoFormat, outPutCameraPath);
            videoUri = FileUtils.parUri(context, cameraFile);
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
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static ContentValues buildImageContentValues(String cameraFileName, String mimeType) {
        String time = String.valueOf(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
            } else {
                String suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."));
                String fileName = cameraFileName.replaceAll(suffix, "");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            }
        }
        values.put(MediaStore.Images.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO) ? MIME_TYPE_IMAGE : mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, DCIM_CAMERA);
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
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    public static ContentValues buildVideoContentValues(String cameraFileName, String mimeType) {
        String time = String.valueOf(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
            } else {
                String suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."));
                String fileName = cameraFileName.replaceAll(suffix, "");
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            }
        }
        values.put(MediaStore.Video.Media.MIME_TYPE, TextUtils.isEmpty(mimeType)
                || mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE) ? MIME_TYPE_VIDEO : mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time);
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
        }
        return values;
    }

}
