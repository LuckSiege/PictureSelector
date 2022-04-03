package com.luck.picture.lib.interfaces;

import android.net.Uri;

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnCompressCallbackListener
 */
public interface OnCompressCallbackListener {
    /**
     * compress success
     *
     * @param srcUri
     * @param compressPath
     */
    void onSuccess(Uri srcUri, String compressPath);
}
