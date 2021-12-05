package com.luck.picture.lib.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;

/**
 * @author：luck
 * @date：2021/11/11 12:53 下午
 * @describe：IBridgeMediaLoader
 */
public class IBridgeMediaLoader {
    protected static final String TAG = IBridgeMediaLoader.class.getSimpleName();
    protected static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    protected static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    protected static final String NOT_GIF_UNKNOWN = "!='image/*'";
    protected static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN + ")";
    protected static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    protected static final String COLUMN_COUNT = "count";
    protected static final String COLUMN_BUCKET_ID = "bucket_id";
    protected static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    protected static final int MAX_SORT_SIZE = 60;
    protected Context mContext;
    protected PictureSelectionConfig config;

    /**
     * 查询所有资源
     *
     * @param listener 回调监听
     * @return
     */
    public void loadAllMedia(OnQueryAllAlbumListener<LocalMediaFolder> query) {

    }

    /**
     * 查询指定目录下资源
     *
     * @param listener 回调监听
     * @return
     */
    public void loadOnlyInAppDirAllMedia(OnQueryAlbumListener<LocalMediaFolder> query) {

    }

    /**
     * Query the first item data of album list
     *
     * @param bucketId
     * @param listener
     * @return
     */
    public void loadFirstPageMedia(long bucketId, int pageSize, OnQueryDataResultListener<LocalMedia> query) {
    }

    /**
     * Query the data in a bucket ID directory
     *
     * @param bucketId
     * @param page
     * @param pageSize
     * @param listener
     */
    public void loadPageMediaData(long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

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
    public void loadPageMediaData(long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

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
