package com.luck.picture.lib.interfaces;

import java.util.ArrayList;

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
    public void onComplete(ArrayList<T> result, boolean isHasMore) {

    }
}
