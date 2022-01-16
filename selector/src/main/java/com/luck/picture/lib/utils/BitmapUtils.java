package com.luck.picture.lib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.luck.picture.lib.basic.PictureContentResolver;
import com.luck.picture.lib.config.PictureConfig;
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
     * @param path    资源路径
     */
    public static void rotateImage(Context context, String path) {
        try {
            int degree = readPictureDegree(context, path);
            if (degree > 0) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                if (bitmap != null) {
                    bitmap = rotatingImage(bitmap, degree);
                }
                if (bitmap != null) {
                    saveBitmapFile(bitmap, file);
                    bitmap.recycle();
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
     * 获取图片的缩放比例
     *
     * @param width        图片原始宽度
     * @param height       图片原始高度
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @return
     */
    public static int[] getMaxImageSize(Context context,
                                        int imageWidth, int imageHeight,
                                        int screenWidth, int screenHeight) {
        if (imageWidth == 0 && imageHeight == 0) {
            imageWidth = screenWidth;
            imageHeight = screenHeight;
        }
        if (MediaUtils.isLongImage(imageWidth, imageHeight)) {
            return new int[]{PictureConfig.UNSET, PictureConfig.UNSET};
        }
        int maxBitmapSize = BitmapUtils.calculateMaxBitmapSize(context);
        int inSampleSize = BitmapUtils.calculateInSampleSize(imageWidth, imageHeight, maxBitmapSize, maxBitmapSize);
        int newWidth = (imageWidth) / inSampleSize;
        int newHeight = (imageHeight) / inSampleSize;
        return new int[]{newWidth, newHeight};
    }

    /**
     * calculateInSampleSize
     *
     * @param width
     * @param height
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * This method calculates maximum size of both width and height of bitmap.
     * It is twice the device screen diagonal for default implementation (extra quality to zoom image).
     * Size cannot exceed max texture size.
     *
     * @return - max bitmap size in pixels.
     */
    private static int calculateMaxBitmapSize(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display;
        int width, height;
        Point size = new Point();

        if (wm != null) {
            display = wm.getDefaultDisplay();
            display.getSize(size);
        }

        width = size.x;
        height = size.y;

        // Twice the device screen diagonal as default
        int maxBitmapSize = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

        // Check for max texture size via Canvas
        Canvas canvas = new Canvas();
        final int maxCanvasSize = Math.min(canvas.getMaximumBitmapWidth(), canvas.getMaximumBitmapHeight());
        if (maxCanvasSize > 0) {
            maxBitmapSize = Math.min(maxBitmapSize, maxCanvasSize);
        }

        // Check for max texture size via GL
        final int maxTextureSize = PSEglUtils.getMaxTextureSize();
        if (maxTextureSize > 0) {
            maxBitmapSize = Math.min(maxBitmapSize, maxTextureSize);
        }

        return maxBitmapSize;
    }
}
