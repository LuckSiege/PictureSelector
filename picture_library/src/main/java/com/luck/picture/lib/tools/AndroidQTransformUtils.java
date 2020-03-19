package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

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
     * @param uri
     * @param size
     * @param mineType
     * @param customFileName
     * @param isOpenNioCopy
     * @return
     */
    public static String copyPathToAndroidQ(Context ctx, Uri uri, long size, String mineType, String customFileName, boolean isOpenNioCopy) {
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
            boolean copyFileSuccess = isOpenNioCopy ? PictureFileUtils.copyFile(inputStream, outFile)
                    : PictureFileUtils.bufferCopy(inputStream, outFile, size);
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
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inputUri
     * @param size
     * @param outUri
     */
    public static boolean copyPathToDCIM(Context context, Uri inputUri, Uri outUri, long size, boolean isOpenNioCopy) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = context.getApplicationContext().getContentResolver().openFileDescriptor(inputUri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                if (isOpenNioCopy) {
                    return PictureFileUtils.copyFile(new FileInputStream(fileDescriptor),
                            (FileOutputStream) context.getContentResolver().openOutputStream(outUri));
                } else {
                    return PictureFileUtils.bufferCopy(new FileInputStream(fileDescriptor),
                            (FileOutputStream) context.getContentResolver().openOutputStream(outUri), size);
                }
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
