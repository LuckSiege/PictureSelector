package com.luck.picture.lib.interfaces;

import android.content.Context;

/**
 * @author：luck
 * @date：2022/4/3 5:37 下午
 * @describe：OnVideoThumbnailEventListener
 */
public interface OnVideoThumbnailEventListener {
    /**
     * video thumbnail
     *
     * @param context
     * @param videoPath
     */
    void onVideoThumbnail(Context context, String videoPath, OnKeyValueResultCallbackListener call);
}
