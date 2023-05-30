package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnCallbackListener
 */
interface OnCallbackListener<T> {
    /**
     * @param data
     */
    fun onCall(data: T)
}