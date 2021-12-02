package com.luck.lib.camerax;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author：luck
 * @date：2021/12/1 9:53 下午
 * @describe：CameraImageEngine
 */
public interface CameraImageEngine {
    /**
     * load image source
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(Context context, String url, ImageView imageView);
}
