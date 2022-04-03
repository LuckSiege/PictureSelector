package com.luck.picture.lib.engine;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2021/11/23 8:23 下午
 * @describe：UriToFileTransformEngine
 */
public interface UriToFileTransformEngine {
    /**
     * Custom Sandbox File engine
     * <p>
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the sandbox path into the {@link LocalMedia} object;
     * </p>
     *
     * <p>
     * ### This method callback is already operated in the child thread
     * </p>
     *
     * @param context  context
     * @param path
     * @param mineType
     */
    String onSandboxFileTransform(Context context, String path, String mineType);
}
