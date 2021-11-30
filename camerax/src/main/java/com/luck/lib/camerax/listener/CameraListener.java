package com.luck.lib.camerax.listener;

import androidx.annotation.NonNull;

/**
 * @author：luck
 * @date：2020-01-04 13:38
 * @describe：相机回调监听
 */
public interface CameraListener {
    /**
     * 拍照成功返回
     *
     * @param url
     */
    void onPictureSuccess(@NonNull String url);

    /**
     * 录像成功返回
     *
     * @param url
     */
    void onRecordSuccess(@NonNull String url);

    /**
     * 使用相机出错
     *
     * @param file
     */
    void onError(int videoCaptureError, String message, Throwable cause);
}
