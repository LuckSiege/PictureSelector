package com.luck.picture.lib.interfaces;

import java.util.List;

/**
 * @author：luck
 * @date：2020-04-16 12:42
 * @describe：OnQueryDataSourceListener
 */
public interface OnQueryDataSourceListener<T> {
    /**
     * Query data source
     *
     * @param result The data source
     */
    void onComplete(List<T> result);
}
