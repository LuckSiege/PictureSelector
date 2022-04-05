package com.luck.picture.lib.interfaces;

import android.content.Context;

/**
 * @author：luck
 * @date：2022/4/2 4:37 下午
 * @describe：OnBitmapWatermarkEventListener
 */
public interface OnBitmapWatermarkEventListener {
    /**
     * Add bitmap watermark
     *
     * @param context
     * @param srcPath
     * @param mimeType
     */
    void onAddBitmapWatermark(Context context, String srcPath, String mimeType, OnKeyValueResultCallbackListener call);
}
