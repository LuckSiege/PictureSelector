package com.luck.picture.lib.engine;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/23 8:23 下午
 * @describe：SandboxFileEngine
 */
public interface SandboxFileEngine {

    /**
     * Custom Sandbox File engine
     * <p>
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the sandbox path into the {@link LocalMedia} object;
     *
     * </p>
     *
     * <p>
     * 1、LocalMedia media = new LocalMedia();
     *   media.setSandboxPath("Your sandbox path");
     * </p>
     * <p>
     * 2、listener.onCall( "you result" );
     * </p>
     *
     * @param list
     * @param listener
     */
    void onStartSandboxFileTransform(List<LocalMedia> list, OnCallbackListener<List<LocalMedia>> listener);

}
