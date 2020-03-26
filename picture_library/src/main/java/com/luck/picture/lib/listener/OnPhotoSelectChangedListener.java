package com.luck.picture.lib.listener;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author：luck
 * @date：2020-03-26 10:34
 * @describe：OnPhotoSelectChangedListener
 */
public interface OnPhotoSelectChangedListener<T extends LocalMedia> {
    /**
     * 拍照回调
     */
    void onTakePhoto();

    /**
     * 已选Media回调
     *
     * @param data
     */
    void onChange(List<T> data);

    /**
     * 图片预览回调
     *
     * @param data
     * @param position
     */
    void onPictureClick(T data, int position);
}
