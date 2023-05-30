package com.luck.picture.lib.interfaces

import androidx.fragment.app.Fragment

/**
 * @author：luck
 * @date：2022/3/18 2:55 下午
 * @describe：OnRecordAudioListener
 */
interface OnRecordAudioListener {
    /**
     * Intercept record audio click events, and users can implement their own record audio framework
     *
     * @param fragment    fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    fun onRecordAudio(fragment: Fragment, requestCode: Int)
}