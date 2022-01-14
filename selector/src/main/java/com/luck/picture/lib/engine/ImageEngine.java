package com.luck.picture.lib.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.interfaces.OnCallbackListener;

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：ImageEngine
 */
public interface ImageEngine {
    /**
     * load image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * load image bitmap
     *
     * @param context
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @param call
     */
    void loadImageBitmap(@NonNull Context context, @NonNull String url, int maxWidth, int maxHeight,
                         OnCallbackListener<Bitmap> call);

    /**
     * load album cover
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadAlbumCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * load picture list picture
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * When the recyclerview slides quickly, the callback can be used to pause the loading of resources
     *
     * @param context
     */
    void pauseRequests(Context context);

    /**
     * When the recyclerview is slow or stops sliding, the callback can do some operations to restore resource loading
     *
     * @param context
     */
    void resumeRequests(Context context);
}
