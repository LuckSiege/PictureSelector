package com.luck.lib.camerax.utils;

import android.content.ContentValues;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

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
