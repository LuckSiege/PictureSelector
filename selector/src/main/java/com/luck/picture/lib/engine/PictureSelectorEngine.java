package com.luck.picture.lib.engine;

import android.view.View;

import com.luck.picture.lib.basic.IBridgeLoaderFactory;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

/**
 * @author：luck
 * @date：2020/4/22 11:36 AM
 * @describe：PictureSelectorEngine
 */
public interface PictureSelectorEngine {

    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    ImageEngine createImageLoaderEngine();

    /**
     * Create compress Engine
     *
     * @return
     */
    CompressEngine createCompressEngine();

    /**
     * Create compress Engine
     *
     * @return
     */
    CompressFileEngine createCompressFileEngine();

    /**
     * Create loader data Engine
     *
     * @return
     */
    ExtendLoaderEngine createLoaderDataEngine();

    /**
     * Create video player  Engine
     *
     * @return
     */
    VideoPlayerEngine createVideoPlayerEngine();

    /**
     * Create loader data Engine
     *
     * @return
     */
    IBridgeLoaderFactory onCreateLoader();

    /**
     * Create SandboxFileEngine  Engine
     *
     * @return
     */
    SandboxFileEngine createSandboxFileEngine();

    /**
     * Create UriToFileTransformEngine  Engine
     *
     * @return
     */
    UriToFileTransformEngine createUriToFileTransformEngine();

    /**
     * Create LayoutResource  Listener
     *
     * @return
     */
    OnInjectLayoutResourceListener createLayoutResourceListener();

    /**
     * Create Result Listener
     *
     * @return
     */
    OnResultCallbackListener<LocalMedia> getResultCallbackListener();
}
