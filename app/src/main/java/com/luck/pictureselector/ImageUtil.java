package com.luck.pictureselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.luck.picture.lib.utils.DensityUtil;

/**
 * @author：luck
 * @date：2022/4/2 5:22 下午
 * @describe：ImageUtil
 */
public class ImageUtil {
    /**
     * 设置水印图片在左上角
     *
     * @param context     上下文
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                DensityUtil.dip2px(context, paddingLeft), DensityUtil.dip2px(context, paddingTop));
    }


    /**
     * 设置水印图片到右上角
     *
     * @param context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskRightTop(Context context, Bitmap src, Bitmap watermark, int paddingRight, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - DensityUtil.dip2px(context, paddingRight),
                DensityUtil.dip2px(context, paddingTop));
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        canvas.save();
        canvas.restore();
        return newBitmap;
    }
}

