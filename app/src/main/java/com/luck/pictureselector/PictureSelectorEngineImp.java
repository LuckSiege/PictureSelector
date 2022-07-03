package com.luck.pictureselector;

import android.content.Context;
import android.util.Log;

import com.luck.picture.lib.basic.IBridgeLoaderFactory;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ExtendLoaderEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.MediaPlayerEngine;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.engine.SandboxFileEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.loader.IBridgeMediaLoader;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2020/4/22 12:15 PM
 * @describe：PictureSelectorEngineImp
 */
public class PictureSelectorEngineImp implements PictureSelectorEngine {
    private static final String TAG = PictureSelectorEngineImp.class.getSimpleName();

    /**
     * 重新创建{@link ImageEngine}引擎
     *
     * @return
     */
    @Override
    public ImageEngine createImageLoaderEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致ImageEngine被回收
        return GlideEngine.createGlideEngine();
    }

    /**
     * 重新创建{@link CompressEngine}引擎
     *
     * @return
     */
    @Override
    public CompressEngine createCompressEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致CompressEngine被回收
        return null;
    }

    /**
     * 重新创建{@link CompressEngine}引擎
     *
     * @return
     */
    @Override
    public CompressFileEngine createCompressFileEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致CompressFileEngine被回收
        return null;
    }

    /**
     * 重新创建{@link ExtendLoaderEngine}引擎
     *
     * @return
     */
    @Override
    public ExtendLoaderEngine createLoaderDataEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致ExtendLoaderEngine被回收
        return null;
    }

    @Override
    public VideoPlayerEngine createVideoPlayerEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致VideoPlayerEngine被回收
        return null;
    }

    /**
     *  重新创建{@link IBridgeMediaLoader}引擎
     * @return
     */
    @Override
    public IBridgeLoaderFactory onCreateLoader() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致IBridgeLoaderFactory被回收
        return null;
    }

    /**
     * 重新创建{@link SandboxFileEngine}引擎
     *
     * @return
     */
    @Override
    public SandboxFileEngine createSandboxFileEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致SandboxFileEngine被回收
        return null;
    }

    /**
     * 重新创建{@link UriToFileTransformEngine}引擎
     *
     * @return
     */
    @Override
    public UriToFileTransformEngine createUriToFileTransformEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致UriToFileTransformEngine被回收
        return null;
    }

    /**
     * 如果出现内存不足导致OnInjectLayoutResourceListener被回收，需要重新引入自定义布局
     *
     * @return
     */
    @Override
    public OnInjectLayoutResourceListener createLayoutResourceListener() {
        return new OnInjectLayoutResourceListener() {
            @Override
            public int getLayoutResourceId(Context context, int resourceSource) {
                switch (resourceSource) {
                    case InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_fragment_selector;
                    case InjectResourceSource.PREVIEW_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_fragment_preview;
                    case InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_item_grid_image;
                    case InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_item_grid_video;
                    case InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_item_grid_audio;
                    case InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_album_folder_item;
                    case InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_preview_image;
                    case InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_preview_video;
                    case InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE:
                        return R.layout.ps_custom_preview_gallery_item;
                    default:
                        return 0;
                }
            }
        };
    }

    @Override
    public OnResultCallbackListener<LocalMedia> getResultCallbackListener() {
        return new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> result) {
                // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致OnResultCallbackListener被回收
                // 可以在这里进行一些补救措施，通过广播或其他方式将结果推送到相应页面，防止结果丢失的情况
                Log.i(TAG, "onResult:" + result.size());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "PictureSelector onCancel");
            }
        };
    }
}
