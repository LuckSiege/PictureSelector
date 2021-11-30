package com.luck.picture.lib.interfaces;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2021/11/27 5:44 下午
 * @describe：OnMediaEditEventInterceptListener
 */
public interface OnMediaEditEventInterceptListener {
    /**
     * Custom crop image engine
     * <p>
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the {@link LocalMedia} object;
     *
     * </p>
     *
     * <p>
     * 1、LocalMedia media = new LocalMedia();
     * media.setEditorImage(true);
     * media.setCut(true);
     * media.setCutPath("Your edit path"); or media.setCustomData("Your edit path");
     * </p>
     * <p>
     * 2、listener.onCall( "you result" );
     * </p>
     *
     * @param context
     * @param media
     * @param listener
     */
    void onStartMediaEdit(Context context, LocalMedia media, OnCallbackListener<LocalMedia> listener);
}
