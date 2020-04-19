package com.luck.picture.lib.listener;

/**
 * @author：luck
 * @date：2020-04-19 19:10
 * @describe：OnCallbackListener
 */
public interface OnCallbackListener<T> {

    /**
     * @param t
     */
    void onCall(T t);
}
