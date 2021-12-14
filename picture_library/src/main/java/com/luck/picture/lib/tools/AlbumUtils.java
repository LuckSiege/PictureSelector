package com.luck.picture.lib.tools;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;

import java.io.File;

/**
 * @author：luck
 * @date：2021/11/11 8:29 下午
 * @describe：AlbumUtils
 */
public class AlbumUtils {

    /**
     * 生成BucketId
     *
     * @param context          上下文
     * @param cameraFile       拍照资源文件
     * @param outPutCameraPath 自定义拍照输出目录
     * @return
     */
    public static long generateCameraBucketId(Context context, File cameraFile, String outPutCameraPath) {
        long bucketId;
        if (TextUtils.isEmpty(outPutCameraPath)) {
            bucketId = MediaUtils.getCameraFirstBucketId(context);
        } else {
            if (cameraFile.getParentFile() != null) {
                bucketId = cameraFile.getParentFile().getName().hashCode();
            } else {
                bucketId = MediaUtils.getCameraFirstBucketId(context);
            }
        }
        return bucketId;
    }

    /**
     * 创建目录名
     *
     * @param path             资源路径
     * @param mimeType         资源类型
     * @param outPutCameraPath 自定义拍照输出路径
     * @return
     */
    public static String generateCameraFolderName(String path, String mimeType, String outPutCameraPath) {
        String folderName;
        if (TextUtils.isEmpty(outPutCameraPath)) {
            if (SdkVersionUtils.isQ() && PictureMimeType.isHasVideo(mimeType)) {
                folderName = Environment.DIRECTORY_MOVIES;
            } else {
                folderName = PictureMimeType.CAMERA;
            }
        } else {
            File cameraFile = new File(path);
            if (cameraFile.getParentFile() != null) {
                folderName = cameraFile.getParentFile().getName();
            } else {
                folderName = PictureMimeType.CAMERA;
            }
        }
        return folderName;
    }
}
