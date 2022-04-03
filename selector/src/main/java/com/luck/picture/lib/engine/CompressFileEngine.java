package com.luck.picture.lib.engine;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCompressCallbackListener;

/**
 * @author：luck
 * @date：2021/5/19 9:36 AM
 * @describe：CompressFileEngine
 */
public interface CompressFileEngine {
    /**
     * Custom compression engine
     * <p>
     * Users can implement this interface, and then access their own compression framework to plug
     * the compressed path into the {@link LocalMedia} object;
     * </p>
     *
     * <p>
     * ### This method callback is already operated in the child thread
     * </p>
     *
     * @param context
     * @param srcUri
     */
    void onCompress(Context context, Uri srcUri, OnCompressCallbackListener call);
}
