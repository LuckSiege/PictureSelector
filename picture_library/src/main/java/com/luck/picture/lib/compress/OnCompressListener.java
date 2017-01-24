package com.luck.picture.lib.compress;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：2017/1/24
 */
public interface OnCompressListener {
    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(File file);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(Throwable e);
}
