package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnRequestPermissionListener
 */
interface OnRequestPermissionListener {
    /**
     * Permission request result
     *
     * @param permission
     * @param isResult
     */
    fun onCall(permission: Array<String>, isResult: Boolean)
}