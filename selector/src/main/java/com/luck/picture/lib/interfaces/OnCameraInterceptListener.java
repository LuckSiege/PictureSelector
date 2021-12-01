package com.luck.picture.lib.interfaces;

import androidx.fragment.app.Fragment;

/**
 * @author：luck
 * @date：2021/11/23 10:41 上午
 * @describe：OnCameraInterceptListener
 */
public interface OnCameraInterceptListener {

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param fragment    fragment    Fragment to receive result
     * @param cameraMode  Camera mode
     *                    {@link com.luck.picture.lib.config.SelectMimeType.ofImage(),ofVideo(),ofAudio()}
     *                    <p>
     *                    If you use your own camera, you need to put the result URL
     *                    Intent.putExtra(MediaStore.EXTRA_OUTPUT, URI) after taking photos
     *                    </p>
     * @param requestCode requestCode for result
     */
    void openCamera(Fragment fragment, int cameraMode, int requestCode);
}
