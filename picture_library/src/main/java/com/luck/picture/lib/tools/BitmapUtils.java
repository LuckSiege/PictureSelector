package com.luck.picture.lib.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import com.luck.picture.lib.PictureContentResolver;
import com.luck.picture.lib.config.PictureMimeType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author：luck
 * @date：2020-01-15 18:22
 * @describe：BitmapUtils
 */
public class BitmapUtils {

    /**
     * 判断拍照 图片是否旋转
     *
     * @param context
     * @param isCameraRotateImage
     * @param path
     */
    public static void rotateImage(Context context, boolean isCameraRotateImage, String path) {
        try {
            if (isCameraRotateImage) {
                int degree = readPictureDegree(context, path);
                if (degree > 0) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    File file = new File(path);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                    bitmap = rotatingImage(bitmap, degree);
                    if (bitmap != null) {
                        saveBitmapFile(bitmap, file);
                        bitmap.recycle();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 旋转Bitmap
     *
     * @param bitmap
     * @param angle
     * @return
     */
    public static Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存Bitmap至本地
     *
     * @param bitmap
     * @param file
     */
    private static void saveBitmapFile(Bitmap bitmap, File file) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(bos);
        }
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param context
     * @param filePath 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(Context context, String filePath) {
        ExifInterface exifInterface;
        InputStream inputStream = null;
        try {
            if (PictureMimeType.isContent(filePath)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(filePath));
                exifInterface = new ExifInterface(inputStream);
            } else {
                exifInterface = new ExifInterface(filePath);
            }
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            PictureFileUtils.close(inputStream);
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param inputStream 数据流
     * @return degree旋转的角度
     */
    public static int readPictureDegree(InputStream inputStream) {
        try {
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
