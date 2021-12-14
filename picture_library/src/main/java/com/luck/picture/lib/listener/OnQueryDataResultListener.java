package com.luck.picture.lib.listener;

import java.util.List;

/**
 * @author：luck
 * @date：2020-04-16 12:42
 * @describe：OnQueryMediaResultListener
 */
public class OnQueryDataResultListener<T> {
    /**
     * Query to complete The callback listener
     *
     * @param result        The data source
     * @param currentPage The page number
     * @param isHasMore   Is there more
     */
    public void onComplete(List<T> result, int currentPage, boolean isHasMore) {

    }

    /**
     * Query all to complete The callback listener
     *
     * @param data The data source
     */
    public void onComplete(List<T> data) {

    }

    /**
     * Query all to complete The callback listener
     *
     * @param data The data source
     */
    public void onComplete(T data) {

    }
}
