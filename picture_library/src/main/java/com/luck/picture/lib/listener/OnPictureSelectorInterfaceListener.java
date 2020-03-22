package com.luck.picture.lib.listener;

/**
 * @author：luck
 * @date：2020-03-22 15:15
 * @describe：PictureSelector额外提供给开发者的接口回调
 */
public interface OnPictureSelectorInterfaceListener {
    /**
     * Camera Menu
     *
     * @param type
     */
    void onCameraClick(int type);
}
