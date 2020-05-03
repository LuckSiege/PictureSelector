package com.luck.picture.lib.listener;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2020-01-15 14:38
 * @describe：Custom video playback callback
 */
public interface OnVideoSelectedPlayCallback<T extends LocalMedia> {
    /**
     * Play the video
     *
     * @param data
     */
    void startPlayVideo(T data);
}
