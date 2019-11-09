package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q相关处理类
 */
public class AndroidQTransformUtils {

    /**
     * 解析Android Q版本下视频
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @return
     */
    public static String parseVideoPathToAndroidQ(Context ctx, String path, String fileName, String mineType) {
        try {
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            File filesDir = ctx.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (filesDir != null) {
                String newPath = new StringBuffer()
                        .append(filesDir)
                        .append(File.separator)
                        .append(TextUtils.isEmpty(fileName) ? System.currentTimeMillis() : fileName)
                        .append(suffix).toString();
                ParcelFileDescriptor parcelFileDescriptor =
                        ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                FileInputStream inputStream = new FileInputStream(fileDescriptor);
                boolean copyFileSuccess = FileUtils.copyFile(inputStream, newPath);
                if (copyFileSuccess) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @return
     */
    public static String parseImagePathToAndroidQ(Context ctx, String path, String fileName, String mineType) {
        try {
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            String filesDir = PictureFileUtils.getDiskCacheDir(ctx.getApplicationContext());
            if (filesDir != null) {
                String newPath = new StringBuffer()
                        .append(filesDir)
                        .append(File.separator)
                        .append(TextUtils.isEmpty(fileName) ? System.currentTimeMillis() : fileName)
                        .append(suffix).toString();
                ParcelFileDescriptor parcelFileDescriptor =
                        ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                FileInputStream inputStream = new FileInputStream(fileDescriptor);
                boolean copyFileSuccess = FileUtils.copyFile(inputStream, newPath);
                if (copyFileSuccess) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析Android Q版本下音频
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @return
     */
    public static String parseAudioPathToAndroidQ(Context ctx, String path, String fileName, String mineType) {
        try {
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            File filesDir = ctx.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (filesDir != null) {
                String newPath = new StringBuffer()
                        .append(filesDir)
                        .append(File.separator)
                        .append(TextUtils.isEmpty(fileName) ? System.currentTimeMillis() : fileName)
                        .append(suffix).toString();

                ParcelFileDescriptor parcelFileDescriptor =
                        ctx.getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                FileInputStream inputStream = new FileInputStream(fileDescriptor);
                boolean copyFileSuccess = FileUtils.copyFile(inputStream, newPath);
                if (copyFileSuccess) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
