package com.luck.picture.lib.engine;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnCallbackListener;

import java.util.List;

/**
 * @author：luck
 * @date：2021/5/19 9:36 AM
 * @describe：CompressEngine
 */
public interface CompressEngine {
    /**
     * compress
     * ## The isCompressed in the localMedia object needs to be changed to true and setCompressPath is the compression path}
     *
     * @param compressData
     */
    void onCompress(List<LocalMedia> compressData, OnCallbackListener<List<LocalMedia>> listener);
}
