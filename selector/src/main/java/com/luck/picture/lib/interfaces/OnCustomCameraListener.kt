package com.luck.picture.lib.interfaces

import android.net.Uri
import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.MediaType

/**
 * @author：luck
 * @date：2022/3/18 2:55 下午
 * @describe：OnCustomCameraListener
 */
interface OnCustomCameraListener {
    /**
     * Intercept record camera click events, and users can implement their own record camera framework
     *
     * @param fragment    fragment    Fragment to receive result
     * @param outputUri   Camera output uri
     * @param requestCode requestCode for result
     */
    fun onCamera(fragment: Fragment, type: MediaType, outputUri: Uri, requestCode: Int)
}