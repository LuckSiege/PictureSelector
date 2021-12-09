package com.luck.picture.lib.engine;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackIndexListener;

/**
 * @author：luck
 * @date：2021/12/9 7:22 下午
 * @describe：OriginalFileEngine
 */
public interface OriginalFileEngine {
    /**
     * Custom Original File engine
     * <p>
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the original path into the {@link LocalMedia} object;
     *
     * </p>
     *
     * <p>
     * 1、LocalMedia media = new LocalMedia();
     * media.setOriginalPath("Your original path");
     * media.setOriginal(true);
     * </p>
     * <p>
     * 2、listener.onCall( "you result" );
     * </p>
     *
     * @param context  context
     * @param index    The location of the resource in the result queue
     * @param media    LocalMedia
     * @param listener
     */
    void onStartOriginalFileTransform(Context context, int index, LocalMedia media,
                                     OnCallbackIndexListener<LocalMedia> listener);
}
