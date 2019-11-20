package com.luck.picture.lib.engine;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：图片加载引擎接口
 */
public interface ImageEngine {
    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载图片
     *
     * @param context       上下文
     * @param url           图片路径
     * @param imageView     承载图片ImageView
     * @param placeholderId 占位图
     */
    void loadFolderAsBitmapImage(@NonNull Context context, @NonNull String url,
                                 @NonNull ImageView imageView, @DrawableRes int placeholderId);

    /**
     * 加载gif图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    void loadAsGifImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * 加载图片
     *
     * @param context       上下文
     * @param url           图片路径
     * @param imageView     承载图片ImageView
     * @param placeholderId 占位图
     */
    void loadAsBitmapGridImage(@NonNull Context context, @NonNull String url,
                               @NonNull ImageView imageView, @DrawableRes int placeholderId);
}
