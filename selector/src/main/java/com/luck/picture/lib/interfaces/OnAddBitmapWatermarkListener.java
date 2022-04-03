package com.luck.picture.lib.interfaces;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2022/4/2 4:37 下午
 * @describe：OnAddImageWatermarkListener
 */
public interface OnAddBitmapWatermarkListener {
    /**
     * Add bitmap watermark
     *
     * @param context
     * @param media   <p>
     *                This method callback is already operated in the child thread
     *                </p>
     */
    void onAddBitmapWatermark(Context context, LocalMedia media);
}
