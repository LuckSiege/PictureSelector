package com.luck.picture.lib.camera.listener;

import android.widget.ImageView;

import java.io.File;

/**
 * @author：luck
 * @date：2020-01-04 15:55
 * @describe：图片加载
 */
public interface ImageCallbackListener {
    /**
     * 加载图片回调
     *
     * @param file
     * @param imageView
     */
    void onLoadImage(File file, ImageView imageView);
}
