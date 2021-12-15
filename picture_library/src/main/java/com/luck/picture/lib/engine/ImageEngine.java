package com.luck.picture.lib.engine;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：ImageEngine
 */
public interface ImageEngine {
    /**
     * Loading image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Loading image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView, OnImageCompleteCallback callback);

    /**
     * Load network long graph adaption
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Deprecated
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView);


    /**
     * Load album catalog pictures
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadFolderImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Load GIF image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadAsGifImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Load picture list picture
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);
}
