package com.luck.picture.lib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import com.luck.picture.lib.basic.PictureContentResolver;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author：luck
 * @date：2020-01-15 18:22
 * @describe：BitmapUtils
 */
public class BitmapUtils {
    private static final int ARGB_8888_MEMORY_BYTE = 4;
    private static final int MAX_BITMAP_SIZE = 100 * 1024 * 1024;   // 100 MB

    /**
     * 判断拍照 图片是否旋转
     *
     * @param context
     * @param path    资源路径
     */
    public static void rotateImage(Context context, String path) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Bitmap bitmap = null;
        try {
            int degree = readPictureDegree(context, path);
            if (degree > 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                if (PictureMimeType.isContent(path)) {
                    inputStream = PictureContentResolver.openInputStream(context, Uri.parse(path));
                    BitmapFactory.decodeStream(inputStream, null, options);
                } else {
                    BitmapFactory.decodeFile(path, options);
                }
                options.inSampleSize = computeSize(options.outWidth, options.outHeight);
                options.inJustDecodeBounds = false;
                if (PictureMimeType.isContent(path)) {
                    inputStream = PictureContentResolver.openInputStream(context, Uri.parse(path));
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                } else {
                    bitmap = BitmapFactory.decodeFile(path, options);
                }
                if (bitmap != null) {
                    bitmap = rotatingImage(bitmap, degree);
                    if (PictureMimeType.isContent(path)) {
                        outputStream = (FileOutputStream) PictureContentResolver.openOutputStream(context, Uri.parse(path));
                    } else {
                        outputStream = new FileOutputStream(path);
                    }
                    saveBitmapFile(bitmap, outputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(inputStream);
            PictureFileUtils.close(outputStream);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
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
     * @param fos
     */
    private static void saveBitmapFile(Bitmap bitmap, FileOutputStream fos) {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(fos);
            PictureFileUtils.close(stream);
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
                inputStream = PictureContentResolver.openInputStream(context, Uri.parse(filePath));
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
     * 获取图片的缩放比例
     *
     * @param imageWidth  图片原始宽度
     * @param imageHeight 图片原始高度
     * @return
     */
    public static int[] getMaxImageSize(int imageWidth, int imageHeight) {
        int maxWidth = PictureConfig.UNSET, maxHeight = PictureConfig.UNSET;
        if (imageWidth == 0 && imageHeight == 0) {
            return new int[]{maxWidth, maxHeight};
        }
        int inSampleSize = BitmapUtils.computeSize(imageWidth, imageHeight);
        long totalMemory = getTotalMemory();
        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            maxWidth = imageWidth / inSampleSize;
            maxHeight = imageHeight / inSampleSize;
            int bitmapSize = maxWidth * maxHeight * ARGB_8888_MEMORY_BYTE;
            if (bitmapSize > totalMemory) {
                inSampleSize *= 2;
                continue;
            }
            decodeAttemptSuccess = true;
        }
        return new int[]{maxWidth, maxHeight};
    }

    /**
     * 获取当前应用可用内存
     *
     * @return
     */
    public static long getTotalMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        return totalMemory > MAX_BITMAP_SIZE ? MAX_BITMAP_SIZE : totalMemory;
    }

    /**
     * 计算图片合适压缩比较
     *
     * @param srcWidth  资源宽度
     * @param srcHeight 资源高度
     * @return
     */
    public static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }
}
