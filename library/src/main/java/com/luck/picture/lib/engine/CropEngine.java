package com.luck.picture.lib.engine;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * @describe：CropEngine
 */
public interface CropEngine {

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
     *   media.setCut(true);
     *   media.setCutPath("Your crop path");
     * </p>
     * <p>
     * 2、listener.onCall( "you result" );
     * </p>
     *
     * @param context
     * @param list
     * @param listener
     */
    void onStartCrop(Context context, List<LocalMedia> list, OnCallbackListener<List<LocalMedia>> listener);
}
