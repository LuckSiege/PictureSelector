package com.luck.picture.lib.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author：luck
 * @date：2021/11/11 12:53 下午
 * @describe：IBridgeMediaLoader
 */
public class IBridgeMediaLoader {
    protected static final String TAG = IBridgeMediaLoader.class.getSimpleName();
    protected static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    protected static final String ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
    protected static final String NOT_GIF_UNKNOWN = "!='image/*'";
    protected static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN + ")";
    protected static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    protected static final String COLUMN_COUNT = "count";
    protected static final String COLUMN_BUCKET_ID = "bucket_id";
    protected static final String COLUMN_DURATION = "duration";
    protected static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    protected static final String COLUMN_ORIENTATION = "orientation";
    protected static final int MAX_SORT_SIZE = 60;
    protected Context mContext;
    protected PictureSelectionConfig config;

    /**
     * 查询所有资源
     *
     * @param query 回调监听
     * @return
     */
    public void loadAllAlbum(OnQueryAllAlbumListener<LocalMediaFolder> query) {

    }

    /**
     * 查询指定目录下资源
     *
     * @param query 回调监听
     * @return
     */
    public void loadOnlyInAppDirAllMedia(OnQueryAlbumListener<LocalMediaFolder> query) {

    }

    /**
     * Query the first item data of album list
     *
     * @param bucketId
     * @param query
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
     * @param query
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

    /**
     * Media file database field
     */
    protected static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION};

    /**
     * Gets a file of the specified type
     *
     * @param mediaType
     * @return
     */
    protected static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    protected static String[] getSelectionArgsForAllMediaType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    }


    /**
     * Get video (maximum or minimum time)
     *
     * @return
     */
    protected String getDurationCondition() {
        long maxS = config.filterVideoMaxSecond == 0 ? Long.MAX_VALUE : config.filterVideoMaxSecond;
        return String.format(Locale.CHINA, "%d <%s " + COLUMN_DURATION + " and " + COLUMN_DURATION + " <= %d",
                Math.max((long) 0, config.filterVideoMinSecond),
                Math.max((long) 0, config.filterVideoMinSecond) == 0 ? "" : "=",
                maxS);
    }

    /**
     * Get media size (maxFileSize or miniFileSize)
     *
     * @return
     */
    protected String getFileSizeCondition() {
        long maxS = config.filterMaxFileSize == 0 ? Long.MAX_VALUE : config.filterMaxFileSize;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
                Math.max(0, config.filterMinFileSize),
                Math.max(0, config.filterMinFileSize) == 0 ? "" : "=",
                maxS);
    }

    /**
     * getQueryMimeCondition
     *
     * @return
     */
    protected String getQueryMimeCondition() {
        List<String> filters = config.queryOnlyList;
        HashSet<String> filterSet = new HashSet<>(filters);
        Iterator<String> iterator = filterSet.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        int index = -1;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            if (config.chooseMode == SelectMimeType.ofVideo()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)) {
                    continue;
                }
            } else if (config.chooseMode == SelectMimeType.ofImage()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)) {
                    continue;
                }
            } else if (config.chooseMode == SelectMimeType.ofAudio()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)) {
                    continue;
                }
            }
            index++;
            stringBuilder.append(index == 0 ? " AND " : " OR ").append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value).append("'");
        }
        if (config.chooseMode != SelectMimeType.ofVideo()) {
            if (!config.isGif && !filterSet.contains(PictureMimeType.ofGIF())) {
                stringBuilder.append(NOT_GIF);
            }
        }
        return stringBuilder.toString();
    }

    public String getSortOrder() {
        String sortOrder;
        if (TextUtils.isEmpty(config.sortOrder)) {
            sortOrder = ORDER_BY;
        } else {
            sortOrder = config.sortOrder;
        }
        return sortOrder;
    }
}
