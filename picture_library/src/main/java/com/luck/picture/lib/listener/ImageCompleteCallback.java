package com.luck.picture.lib.listener;

/**
 * @author：luck
 * @date：2020-01-03 16:43
 * @describe：图片加载完成回调
 */
public interface ImageCompleteCallback {
    /**
     * 开始加载
     */
    void onShowLoading();

    /**
     * 隐藏加载
     */
    void onHideLoading();
}
