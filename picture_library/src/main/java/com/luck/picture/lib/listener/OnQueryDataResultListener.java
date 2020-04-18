package com.luck.picture.lib.listener;

import java.util.List;

/**
 * @author：luck
 * @date：2020-04-16 12:42
 * @describe：OnQueryMediaResultListener
 */
public interface OnQueryDataResultListener<T> {
    /**
     * 查询完成
     *
     * @param data
     * @param isHasMore
     */
    void onComplete(List<T> data, boolean isHasMore);
}
