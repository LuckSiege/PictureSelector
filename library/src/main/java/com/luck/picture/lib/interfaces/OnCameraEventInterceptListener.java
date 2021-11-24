package com.luck.picture.lib.interfaces;

import android.content.Context;

/**
 * @author：luck
 * @date：2021/11/23 10:41 上午
 * @describe：OnCameraEventInterceptListener
 */
public interface OnCameraEventInterceptListener {

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param context    Activity hosted by PictureSelector
     * @param cameraType Camera type
     *                   {@link com.luck.picture.lib.config.SelectMimeType.ofImage(),ofVideo(),ofAudio()}
     * @return
     */
    String openCamera(Context context, int cameraType);
}
