package com.luck.picture.lib.interfaces;

import androidx.fragment.app.Fragment;

/**
 * @author：luck
 * @date：2022/3/15 8:33 下午
 * @describe：OnPermissionDeniedListener
 */
public interface OnPermissionDeniedListener {
    /**
     * Permission denied
     *
     * @param permissionArray Permission
     * @param requestCode     Jump to the  {@link .startActivityForResult # requestCode} used in system settings
     * @param call            if call.onCall(true);Can follow internal logic，Otherwise, press the user's own
     */
    void onDenied(Fragment fragment, String[] permissionArray, int requestCode, OnCallbackListener<Boolean> call);
}
