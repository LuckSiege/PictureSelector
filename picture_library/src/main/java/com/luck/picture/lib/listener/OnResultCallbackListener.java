package com.luck.picture.lib.listener;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：onResult Callback Listener
 */
public interface OnResultCallbackListener {
    /**
     * return LocalMedia result
     *
     * @param result
     */
    void onResult(List<LocalMedia> result);

    /**
     * Cancel
     */
    void onCancel();
}
