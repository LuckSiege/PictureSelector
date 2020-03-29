package com.luck.picture.lib.listener;

import android.content.Context;

import com.luck.picture.lib.config.PictureSelectionConfig;

/**
 * @author：luck
 * @date：2020-03-22 15:15
 * @describe：PictureSelector额外提供给开发者的接口回调
 */
public interface OnPictureSelectorInterfaceListener {
    /**
     * Camera Menu
     *
     * @param context
     * @param config
     * @param type
     */
    void onCameraClick(Context context, PictureSelectionConfig config, int type);
}
