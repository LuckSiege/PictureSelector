package com.luck.picture.lib.interfaces;

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
     * @param isHasMore   Is there more
     */
    public void onComplete(List<T> result,  boolean isHasMore) {

    }

    /**
     * Query all to complete The callback listener
     *
     * @param result The data source
     */
    public void onComplete(List<T> result) {

    }

    /**
     * Query all to complete The callback listener
     *
     * @param data The data source
     */
    public void onComplete(T data) {

    }
}
