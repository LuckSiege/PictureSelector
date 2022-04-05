package top.zibin.luban;

import java.io.File;

public interface OnNewCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(String source, File compressFile);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(String source, Throwable e);
}
