package com.luck.picture.lib.model;

import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.listener.OnQueryDataResultListener;

/**
 * @author：luck
 * @date：2021/11/11 12:53 下午
 * @describe：IBridgeMediaLoader
 */
public interface IBridgeMediaLoader {
    /**
     * 查询所有资源
     *
     * @param listener 回调监听
     * @return
     */
    void loadAllMedia(OnQueryDataResultListener<LocalMediaFolder> listener);
}
