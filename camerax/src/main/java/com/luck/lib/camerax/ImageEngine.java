package com.luck.lib.camerax;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author：luck
 * @date：2021/12/1 9:53 下午
 * @describe：ImageEngine
 */
public interface ImageEngine {
    /**
     * load image source
     *
     * @param context
     * @param imageView
     * @param url
     */
    void loadImage(Context context, ImageView imageView, String url);
}
