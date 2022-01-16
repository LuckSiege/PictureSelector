package com.luck.picture.lib.interfaces;

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnRequestPermissionListener
 */
public interface OnRequestPermissionListener {
    /**
     * Permission request result
     *
     * @param permissionArray
     * @param isResult
     */
    void onCall(String[] permissionArray, boolean isResult);
}
