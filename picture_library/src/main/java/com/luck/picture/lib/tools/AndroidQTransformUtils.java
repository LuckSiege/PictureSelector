package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.OutputStream;

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
     * @param customFileName
     * @param mineType
     * @return
     */
    public static String parseVideoPathToAndroidQ(Context ctx, String path, String customFileName, String mineType) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            String filesDir = PictureFileUtils.getVideoDiskCacheDir(ctx.getApplicationContext());
            parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            String md5Value = Digest.computeToQMD5(inputStream);
            String fileName;
            if (!TextUtils.isEmpty(md5Value)) {
                fileName = TextUtils.isEmpty(customFileName) ? new StringBuffer().append("VID_").append(md5Value.toUpperCase()).append(suffix).toString() : customFileName;
            } else {
                fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("VID_") + suffix : customFileName;
            }
            if (filesDir != null) {
                String newPath = new StringBuffer().append(filesDir).append(File.separator).append(fileName).toString();
                File outFile = new File(newPath);
                if (outFile.exists()) {
                    return newPath;
                }
                boolean copyFileSuccess = PictureFileUtils.copyFile(inputStream, outFile);
                if (copyFileSuccess) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(parcelFileDescriptor);
        }
        return "";
    }

    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @param customFileName
     * @param mineType
     * @return
     */
    public static String parseImagePathToAndroidQ(Context ctx, String path, String customFileName, String mineType) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            String md5Value = Digest.computeToQMD5(inputStream);
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            String filesDir = PictureFileUtils.getDiskCacheDir(ctx.getApplicationContext());
            String fileName;
            if (!TextUtils.isEmpty(md5Value)) {
                fileName = TextUtils.isEmpty(customFileName) ? new StringBuffer().append("IMG_").append(md5Value.toUpperCase()).append(suffix).toString() : customFileName;
            } else {
                fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("IMG_") + suffix : customFileName;
            }
            if (filesDir != null) {
                String newPath = new StringBuffer().append(filesDir).append(File.separator).append(fileName).toString();
                File outFile = new File(newPath);
                if (outFile.exists()) {
                    return newPath;
                }
                boolean copyFileSuccess = PictureFileUtils.copyFile(inputStream, outFile);
                if (copyFileSuccess) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(parcelFileDescriptor);
        }
        return "";
    }

    /**
     * 解析Android Q版本下音频
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @param customFileName
     * @param mineType
     * @return
     */
    public static String parseAudioPathToAndroidQ(Context ctx, String path, String customFileName, String mineType) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
            String suffix = PictureMimeType.getLastImgSuffix(mineType);
            String filesDir = PictureFileUtils.getAudioDiskCacheDir(ctx.getApplicationContext());
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            String md5Value = Digest.computeToQMD5(inputStream);
            String fileName;
            if (!TextUtils.isEmpty(md5Value)) {
                fileName = TextUtils.isEmpty(customFileName) ? new StringBuffer().append("AUD_").append(md5Value.toUpperCase()).append(suffix).toString() : customFileName;
            } else {
                fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("AUD_") + suffix : customFileName;
            }
            if (filesDir != null) {
                String newPath = new StringBuffer().append(filesDir).append(File.separator).append(fileName).toString();
                File outFile = new File(newPath);
                if (outFile.exists()) {
                    return newPath;
                }
                boolean copyFileSuccess = PictureFileUtils.copyFile(inputStream, outFile);
                if (copyFileSuccess) {
                    return newPath;
                }
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
     * @param media
     * @return
     */
    @Nullable
    public static String getPathToAndroidQ(Context context, String cameraFileName, LocalMedia media) {
        if (PictureMimeType.eqVideo(media.getMimeType())) {
            return AndroidQTransformUtils.parseVideoPathToAndroidQ
                    (context.getApplicationContext(), media.getPath(), cameraFileName, media.getMimeType());
        } else if (PictureMimeType.eqAudio(media.getMimeType())) {
            return AndroidQTransformUtils.parseAudioPathToAndroidQ
                    (context.getApplicationContext(), media.getPath(), cameraFileName, media.getMimeType());
        } else {
            return AndroidQTransformUtils.parseImagePathToAndroidQ
                    (context.getApplicationContext(), media.getPath(), cameraFileName, media.getMimeType());
        }
    }

    /**
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inputUri
     * @param outUri
     */
    public static void copyPathToDCIM(Context context, Uri inputUri, Uri outUri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        OutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(outUri);
            byte[] buffer = new byte[1024 * 8];
            int read;
            parcelFileDescriptor = context.getApplicationContext().getContentResolver().openFileDescriptor(inputUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fileDescriptor);
            while ((read = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PictureFileUtils.close(parcelFileDescriptor);
        }
    }
}
