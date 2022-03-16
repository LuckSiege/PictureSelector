package com.luck.lib.camerax.listener;

import android.content.Context;
import android.view.ViewGroup;

/**
 * @author：luck
 * @date：2021/12/1 8:48 下午
 * @describe：OnSimpleXPermissionDescriptionListener
 */
public interface OnSimpleXPermissionDescriptionListener {
    /**
     * Permission description
     *
     * @param context
     * @param permission
     */
    void onPermissionDescription(Context context, ViewGroup viewGroup, String permission);

    /**
     * onDismiss
     */
    void onDismiss(ViewGroup viewGroup);
}
