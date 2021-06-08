package com.luck.picture.lib.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.tools.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private final InputStreamProvider srcImg;
    private final File tagImg;
    private int srcWidth;
    private int srcHeight;
    @Deprecated
    private final boolean focusAlpha;
    private static final int DEFAULT_QUALITY = 80;
    private int compressQuality;
    private final boolean isAutoRotating;
    private final Context context;

    Engine(Context context, InputStreamProvider srcImg, File tagImg, boolean focusAlpha, int compressQuality, boolean isAutoRotating) throws IOException {
        this.tagImg = tagImg;
        this.srcImg = srcImg;
        this.context = context;
        this.focusAlpha = focusAlpha;
        this.isAutoRotating = isAutoRotating;
        this.compressQuality = compressQuality <= 0 ? DEFAULT_QUALITY : compressQuality;

        if (srcImg.getMedia().getWidth() > 0 && srcImg.getMedia().getHeight() > 0) {
            this.srcWidth = srcImg.getMedia().getWidth();
            this.srcHeight = srcImg.getMedia().getHeight();
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeStream(srcImg.open(), null, options);
            this.srcWidth = options.outWidth;
            this.srcHeight = options.outHeight;
        }
    }

    private int computeSize() {
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

    File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();
        Bitmap tagBitmap = BitmapFactory.decodeStream(srcImg.open(), null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (isAutoRotating) {
            if (Checker.SINGLE.isJPG(srcImg.getMedia().getMimeType())) {
                boolean isCut = srcImg.getMedia().isCut() && !TextUtils.isEmpty(srcImg.getMedia().getCutPath());
                String url = isCut ? srcImg.getMedia().getCutPath() : srcImg.getMedia().getPath();
                int degree = PictureMimeType.isContent(url) ? BitmapUtils.readPictureDegree(srcImg.open()) : BitmapUtils.readPictureDegree(context, url);
                if (degree > 0) {
                    tagBitmap = BitmapUtils.rotatingImage(tagBitmap, degree);
                }
            }
        }
        if (tagBitmap != null) {
            compressQuality = compressQuality <= 0 || compressQuality > 100 ? DEFAULT_QUALITY : compressQuality;
            tagBitmap.compress(focusAlpha || tagBitmap.hasAlpha() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, compressQuality, stream);
            tagBitmap.recycle();
            FileOutputStream fos = new FileOutputStream(tagImg);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();
            return tagImg;
        }
        return null;
    }
}