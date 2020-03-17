package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q相关处理类
 */
public class AndroidQTransformUtils {
    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String copyPathToAndroidQ(Context ctx, Uri uri, String mineType, String customFileName) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            String newPath = PictureFileUtils.createFilePath(ctx, uri, mineType, customFileName);
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor == null) {
                return "";
            }
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            boolean copyFileSuccess = PictureFileUtils.copyFile(inputStream, outFile);
            if (copyFileSuccess) {
                return newPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(parcelFileDescriptor);
        }
        return "";
    }


    /**
     * 复制一份至自己应用沙盒内
     *
     * @param context
     * @param cameraFileName
     * @param uri
     * @param mimeType
     * @return
     */
    @Nullable
    public static String getPathToAndroidQ(Context context, Uri uri, String mimeType, String cameraFileName) {
        return AndroidQTransformUtils.copyPathToAndroidQ(context, uri, mimeType, cameraFileName);
    }

    /**
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inputUri
     * @param outUri
     */
    public static boolean copyPathToDCIM(Context context, Uri inputUri, Uri outUri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = context.getApplicationContext().getContentResolver().openFileDescriptor(inputUri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                return PictureFileUtils.copyFile(new FileInputStream(fileDescriptor),
                        (FileOutputStream) context.getContentResolver().openOutputStream(outUri));
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(parcelFileDescriptor);
        }
        return false;
    }
}
