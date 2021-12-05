package com.luck.picture.lib.basic;

import com.luck.picture.lib.entity.LocalMediaFolder;

/**
 * @author：luck
 * @date：2021/11/18 8:35 下午
 * @describe：IPictureSelectorEvent
 */
public interface IPictureSelectorEvent {
    /**
     * 获取相册目录
     */
    void loadAllAlbum();

    /**
     * 获取首页资源
     */
    void loadFirstPageMedia(long firstBucketId);

    /**
     * 加载应用沙盒内的资源
     */
    void loadOnlyInAppDirectoryAllMedia();

    /**
     * 加载更多
     */
    void loadMoreData();
}
