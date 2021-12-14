package com.luck.picture.lib.model;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.listener.OnQueryDataResultListener;

/**
 * @author：luck
 * @date：2021/11/11 12:53 下午
 * @describe：IBridgeMediaLoader
 */
public class IBridgeMediaLoader {
    /**
     * 查询所有资源
     *
     * @param listener 回调监听
     * @return
     */
    public void loadAllMedia(OnQueryDataResultListener<LocalMediaFolder> listener) {

    }

    /**
     * 查询指定目录下资源
     *
     * @param listener 回调监听
     * @return
     */
    public void loadOnlyInAppDirectoryAllMedia(OnQueryDataResultListener<LocalMediaFolder> listener) {

    }


    /**
     * Queries for data in the specified directory
     *
     * @param bucketId
     * @param page
     * @param limit
     * @param listener
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, int limit, OnQueryDataResultListener<LocalMedia> listener) {
    }

    /**
     * Queries for data in the specified directory
     *
     * @param bucketId
     * @param listener
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, OnQueryDataResultListener<LocalMedia> listener) {

    }

    /**
     * Queries for data in the specified directory (page)
     *
     * @param bucketId
     * @param page
     * @param limit
     * @param pageSize
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener<LocalMedia> listener) {

    }

    /**
     * Get the latest cover of an album catalog
     *
     * @param bucketId
     * @return
     */
    public String getFirstCover(long bucketId) {
        return null;
    }
}
