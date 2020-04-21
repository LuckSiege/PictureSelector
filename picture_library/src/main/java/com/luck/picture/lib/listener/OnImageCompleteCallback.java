package com.luck.picture.lib.listener;

/**
 * @author：luck
 * @date：2020-01-03 16:43
 * @describe：Image load complete callback
 */
public interface OnImageCompleteCallback {
    /**
     * Start loading
     */
    void onShowLoading();

    /**
     * Stop loading
     */
    void onHideLoading();
}
