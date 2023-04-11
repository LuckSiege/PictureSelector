package com.luck.lib.camerax.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author：luck
 * @date：2022/6/19 11:56 上午
 * @describe：BitmapUtils
 */
public class BitmapUtils {
    /**
     * 水平镜像
     *
     * @param bmp
     * @return
     */
    public static Bitmap toHorizontalMirror(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(-1F, 1F);
        matrix.postRotate(w > h ? 90 : 0);
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

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
