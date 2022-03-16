package com.luck.lib.camerax.listener;

import android.content.Context;

/**
 * @author：luck
 * @date：2022/3/15 8:33 下午
 * @describe：OnPermissionDeniedListener
 */
public interface OnSimpleXPermissionDeniedListener {
    /**
     * Permission denied
     *
     * @param permission  Permission
     * @param requestCode Jump to the  {@link .startActivityForResult # requestCode} used in system settings
     */
    void onDenied(Context context, String permission, int requestCode);
}
