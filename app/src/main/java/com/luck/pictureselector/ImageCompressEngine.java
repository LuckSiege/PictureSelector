package com.luck.pictureselector;

import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnCallbackListener;

import java.util.List;

/**
 * @author：luck
 * @date：2021/5/19 9:41 AM
 * @describe：图片压缩引擎
 */
public class ImageCompressEngine implements CompressEngine {

    @Override
    public void onCompress(List<LocalMedia> compressData, OnCallbackListener<List<LocalMedia>> listener) {

        listener.onCall(compressData);
    }

    private ImageCompressEngine() {
    }

    private static ImageCompressEngine instance;

    public static ImageCompressEngine createCompressEngine() {
        if (null == instance) {
            synchronized (ImageCompressEngine.class) {
                if (null == instance) {
                    instance = new ImageCompressEngine();
                }
            }
        }
        return instance;
    }
}
