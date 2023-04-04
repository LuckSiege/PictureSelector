package com.luck.picture.lib.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectorConfig;
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
public abstract class IBridgeMediaLoader {
    protected static final String TAG = IBridgeMediaLoader.class.getSimpleName();
    protected static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    protected static final String ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
    protected static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif')";
    protected static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    protected static final String DISTINCT_BUCKET_Id = "DISTINCT bucket_id";
    protected static final String COLUMN_COUNT = "count";
    protected static final String COLUMN_BUCKET_ID = "bucket_id";
    protected static final String COLUMN_DURATION = "duration";
    protected static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    protected static final String COLUMN_ORIENTATION = "orientation";
    protected static final int MAX_SORT_SIZE = 60;
    private final Context mContext;
    protected final SelectorConfig mConfig;

    public IBridgeMediaLoader(Context context, SelectorConfig config) {
        super();
        this.mContext = context;
        this.mConfig = config;
    }

    protected Context getContext() {
        return mContext;
    }

    protected SelectorConfig getConfig() {
        return mConfig;
    }

    /**
     * A list of which columns to return. Passing null will return all columns, which is inefficient.
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
     * A list of which columns to return. Passing null will return all columns, which is inefficient.
     */
    protected static final String[] ALL_PROJECTION = {
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
            COLUMN_ORIENTATION,
            "COUNT(*) AS " + COLUMN_COUNT};

    /**
     * query album cover
     *
     * @param bucketId
     */
    public abstract String getAlbumFirstCover(long bucketId);

    /**
     * query album list
     */
    public abstract void loadAllAlbum(OnQueryAllAlbumListener<LocalMediaFolder> query);

    /**
     * page query specified contents
     *
     * @param bucketId
     * @param page
     * @param pageSize
     */
    public abstract void loadPageMediaData(long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> query);


    /**
     * query specified contents
     */
    public abstract void loadOnlyInAppDirAllMedia(OnQueryAlbumListener<LocalMediaFolder> query);


    /**
     * A filter declaring which rows to return,
     * formatted as an SQL WHERE clause (excluding the WHERE itself).
     * Passing null will return all rows for the given URI.
     */
    protected abstract String getSelection();

    /**
     * You may include ?s in selection, which will be replaced by the values from selectionArgs,
     * in the order that they appear in the selection. The values will be bound as Strings.
     */
    protected abstract String[] getSelectionArgs();

    /**
     * How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     * Passing null will use the default sort order, which may be unordered.
     */
    protected abstract String getSortOrder();

    /**
     * parse LocalMedia
     *
     * @param data      Cursor
     * @param isUsePool object pool
     */
    protected abstract LocalMedia parseLocalMedia(Cursor data, boolean isUsePool);

    /**
     * Get video (maximum or minimum time)
     *
     * @return
     */
    protected String getDurationCondition() {
        long maxS = getConfig().filterVideoMaxSecond == 0 ? Long.MAX_VALUE : getConfig().filterVideoMaxSecond;
        return String.format(Locale.CHINA, "%d <%s " + COLUMN_DURATION + " and " + COLUMN_DURATION + " <= %d",
                Math.max((long) 0, getConfig().filterVideoMinSecond), "=", maxS);
    }

    /**
     * Get media size (maxFileSize or miniFileSize)
     *
     * @return
     */
    protected String getFileSizeCondition() {
        long maxS = getConfig().filterMaxFileSize == 0 ? Long.MAX_VALUE : getConfig().filterMaxFileSize;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
                Math.max(0, getConfig().filterMinFileSize), "=", maxS);
    }

    /**
     * getQueryMimeCondition
     *
     * @return
     */
    protected String getQueryMimeCondition() {
        List<String> filters = getConfig().queryOnlyList;
        HashSet<String> filterSet = new HashSet<>(filters);
        Iterator<String> iterator = filterSet.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        int index = -1;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            if (getConfig().chooseMode == SelectMimeType.ofVideo()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)) {
                    continue;
                }
            } else if (getConfig().chooseMode == SelectMimeType.ofImage()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)) {
                    continue;
                }
            } else if (getConfig().chooseMode == SelectMimeType.ofAudio()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)) {
                    continue;
                }
            }
            index++;
            stringBuilder.append(index == 0 ? " AND " : " OR ").append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value).append("'");
        }
        if (getConfig().chooseMode != SelectMimeType.ofVideo()) {
            if (!getConfig().isGif && !filterSet.contains(PictureMimeType.ofGIF())) {
                stringBuilder.append(NOT_GIF);
            }
        }
        return stringBuilder.toString();
    }
}
