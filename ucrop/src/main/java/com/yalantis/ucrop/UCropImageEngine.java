package com.yalantis.ucrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * @author：luck
 * @date：2021/12/1 9:53 下午
 * @describe：UCropImageEngine
 */
public interface UCropImageEngine {
    /**
     * load image source
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(Context context, String url, ImageView imageView);

    /**
     * load image source
     *
     * @param context
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @param call
     */
    void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call);


    interface OnCallbackListener<T> {
        /**
         * @param data
         */
        void onCall(T data);
    }
}
