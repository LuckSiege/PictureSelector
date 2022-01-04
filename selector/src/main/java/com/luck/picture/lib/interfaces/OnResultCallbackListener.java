package com.luck.picture.lib.interfaces;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：onResult Callback Listener
 */
public interface OnResultCallbackListener<T> {
    /**
     * return LocalMedia result
     *
     * @param result
     */
    void onResult(ArrayList<T> result);

    /**
     * Cancel
     */
    void onCancel();
}
