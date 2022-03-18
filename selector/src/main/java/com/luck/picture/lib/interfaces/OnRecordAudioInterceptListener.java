package com.luck.picture.lib.interfaces;

import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.SelectMimeType;

/**
 * @author：luck
 * @date：2022/3/18 2:55 下午
 * @describe：OnRecordAudioInterceptListener
 */
public interface OnRecordAudioInterceptListener {
    /**
     * Intercept record audio click events, and users can implement their own record audio framework
     *
     * @param fragment    fragment    Fragment to receive result
     * @param requestCode requestCode for result
     *                    <p>
     *                    {@link SelectMimeType.ofAudio()}
     *                    <p>
     *                    If you use your own camera, you need to put the result URL
     *                    Intent.putExtra(MediaStore.EXTRA_OUTPUT, URI) after taking photos
     *                    </p>
     */
    void onRecordAudio(Fragment fragment, int requestCode);
}
