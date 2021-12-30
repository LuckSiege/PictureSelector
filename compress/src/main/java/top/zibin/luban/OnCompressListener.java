package top.zibin.luban;

import java.io.File;

public interface OnCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     *
     * @param index compression index
     */
    void onSuccess(int index, File compressFile);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     *
     * @param index compression error index
     */
    void onError(int index, Throwable e);
}
