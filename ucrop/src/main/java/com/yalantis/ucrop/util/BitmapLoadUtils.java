package com.yalantis.ucrop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.task.BitmapLoadTask;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class BitmapLoadUtils {
    private static final int MAX_BITMAP_SIZE = 100 * 1024 * 1024;   // 100 MB
    private static final String CONTENT_SCHEME = "content";

    private static final String TAG = "BitmapLoadUtils";

    public static void decodeBitmapInBackground(@NonNull Context context,
                                                @NonNull Uri uri, @Nullable Uri outputUri,
                                                int requiredWidth, int requiredHeight,
                                                BitmapLoadCallback loadCallback) {

        new BitmapLoadTask(context, uri, outputUri, requiredWidth, requiredHeight, loadCallback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Bitmap transformBitmap(@NonNull Bitmap bitmap, @NonNull Matrix transformMatrix) {
        try {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformMatrix, true);
            if (!bitmap.sameAs(converted)) {
                bitmap = converted;
            }
        } catch (OutOfMemoryError error) {
            Log.e(TAG, "transformBitmap: ", error);
        }
        return bitmap;
    }

    @Deprecated
    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
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
     * 计算图片合适压缩比较
     *
     * @param srcWidth  src width
     * @param srcHeight src height
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

    /**
     * Gets the zoom of the image
     *
     * @param context
     * @param mInputUri
     * @return
     */
    public static int[] getMaxImageSize(Context context, Uri mInputUri) {
        if (FileUtils.isHasHttp(mInputUri.toString())) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream stream = context.getContentResolver().openInputStream(mInputUri);
            BitmapFactory.decodeStream(stream, null, options);
            options.inSampleSize = BitmapLoadUtils.computeSize(options.outWidth,options.outHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        options.inJustDecodeBounds = false;

        Bitmap decodeSampledBitmap = null;

        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            try {
                InputStream stream = context.getContentResolver().openInputStream(mInputUri);
                try {
                    decodeSampledBitmap = BitmapFactory.decodeStream(stream, null, options);
                } finally {
                    BitmapLoadUtils.close(stream);
                }
                if (BitmapLoadUtils.checkSize(decodeSampledBitmap, options)) continue;
                decodeAttemptSuccess = true;
            } catch (OutOfMemoryError error) {
                Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                options.inSampleSize *= 2;
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: ImageDecoder.createSource: ", e);
            }
        }
        if (decodeSampledBitmap == null) {
            return new int[]{0, 0};
        }
        return new int[]{decodeSampledBitmap.getWidth(), decodeSampledBitmap.getHeight()};
    }

    public static boolean checkSize(Bitmap bitmap, BitmapFactory.Options options) {
        int bitmapSize = bitmap != null ? bitmap.getByteCount() : 0;
        if (bitmapSize > getTotalMemory()) {
            options.inSampleSize *= 2;
            return true;
        }
        return false;
    }

    /**
     * Get total memory of current application
     *
     * @return
     */
    public static long getTotalMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        return totalMemory > MAX_BITMAP_SIZE ? MAX_BITMAP_SIZE : totalMemory;
    }

    public static int getExifOrientation(@NonNull Context context, @NonNull Uri imageUri) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        try {
            InputStream stream = context.getContentResolver().openInputStream(imageUri);
            if (stream == null) {
                return orientation;
            }
            orientation = new ImageHeaderParser(stream).getOrientation();
            close(stream);
        } catch (IOException e) {
            Log.e(TAG, "getExifOrientation: " + imageUri.toString(), e);
        }
        return orientation;
    }

    public static int exifToDegrees(int exifOrientation) {
        int rotation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    public static int exifToTranslation(int exifOrientation) {
        int translation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                translation = -1;
                break;
            default:
                translation = 1;
        }
        return translation;
    }

    /**
     * This method calculates maximum size of both width and height of bitmap.
     * It is twice the device screen diagonal for default implementation (extra quality to zoom image).
     * Size cannot exceed max texture size.
     *
     * @return - max bitmap size in pixels.
     */
    @SuppressWarnings({"SuspiciousNameCombination", "deprecation"})
    public static int calculateMaxBitmapSize(@NonNull Context context) {
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
        final int maxTextureSize = EglUtils.getMaxTextureSize();
        if (maxTextureSize > 0) {
            maxBitmapSize = Math.min(maxBitmapSize, maxTextureSize);
        }

        Log.d(TAG, "maxBitmapSize: " + maxBitmapSize);
        return maxBitmapSize;
    }

    @SuppressWarnings("ConstantConditions")
    public static void close(@Nullable Closeable c) {
        if (c != null && c instanceof Closeable) { // java.lang.IncompatibleClassChangeError: interface not implemented
            try {
                c.close();
            } catch (IOException e) {
                // silence
            }
        }
    }

    public static boolean hasContentScheme(Uri uri) {
        return uri != null && CONTENT_SCHEME.equals(uri.getScheme());
    }

}