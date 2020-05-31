package com.luck.picture.lib.listener;

import android.content.Context;

import java.util.List;

/**
 * @author：luck
 * @date：2020/5/31 6:42 PM
 * @describe：OnImagePreviewCallback
 */
public interface OnCustomImagePreviewCallback<T> {
    /**
     * Custom Preview Callback
     *
     * @param context
     * @param previewData
     * @param currentPosition
     */
    void onCustomPreviewCallback(Context context, List<T> previewData, int currentPosition);
}
