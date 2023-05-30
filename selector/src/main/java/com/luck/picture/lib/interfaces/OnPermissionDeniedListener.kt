package com.luck.picture.lib.interfaces

import androidx.fragment.app.Fragment

/**
 * @author：luck
 * @date：2022/3/15 8:33 下午
 * @describe：OnPermissionDeniedListener
 */
interface OnPermissionDeniedListener {
    /**
     * Permission denied
     *
     * @param permissionArray Permission
     * @param requestCode     Jump to the  [# requestCode][.startActivityForResult] used in system settings
     * @param call            if call.onCall(true);Can follow internal logic，Otherwise, press the user's own
     */
    fun onDenied(fragment: Fragment, permissionArray: Array<String>, requestCode: Int, call: OnCallbackListener<Boolean>)
}