package com.luck.picture.lib.listener;

import java.util.List;

/**
 * @author：luck
 * @date：2020-03-17 10:07
 * @describe：Listener
 */
@Deprecated
public interface OnMediaLoadListener<T> {
    /**
     * 加载完成
     *
     * @param data
     */
    void loadComplete(List<T> data);

    /**
     * 异常
     */
    void loadMediaDataError();
}
