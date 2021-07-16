package com.luck.picture.lib.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaData;
import com.luck.picture.lib.listener.OnQueryDataResultListener;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ValueOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author：luck
 * @date：2020-04-13 15:06
 * @describe：Local media database query class，Support paging
 */
public final class LocalMediaPageLoader {
    private static final String TAG = LocalMediaPageLoader.class.getSimpleName();
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF_UNKNOWN = "!='image/*'";
    private static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN + ")";
    private static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    private final Context mContext;
    private final PictureSelectionConfig config;

    public LocalMediaPageLoader(Context context) {
        this.mContext = context;
        this.config = PictureSelectionConfig.getInstance();
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, String sizeCondition, String queryMimeTypeOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeTypeOptions).append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? AND ").append(timeCondition).append(") AND ").append(sizeCondition);
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return stringBuilder.toString();
        } else {
            return stringBuilder.append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Query conditions in image modes
     *
     * @param queryMimeTypeOptions
     * @param fileSizeCondition
     * @return
     */
    private static String getSelectionArgsForImageMediaCondition(String queryMimeTypeOptions, String fileSizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return stringBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeTypeOptions).append(" AND ").append(fileSizeCondition).toString();
        } else {
            return stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeTypeOptions).append(") AND ").append(fileSizeCondition).append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Video or Audio mode conditions
     *
     * @param queryMimeTypeOptions
     * @param fileSizeCondition
     * @return
     */
    private static String getSelectionArgsForVideoOrAudioMediaCondition(String queryMimeTypeOptions, String fileSizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return stringBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeTypeOptions).append(" AND ").append(fileSizeCondition).toString();
        } else {
            return stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeTypeOptions).append(") AND ").append(fileSizeCondition).append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private static String[] getSelectionArgsForAllMediaType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    }

    /**
     * Gets a file of the specified type
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    /**
     * Gets a file of the specified type
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForPageSingleMediaType(int mediaType, long bucketId) {
        return bucketId == -1 ? new String[]{String.valueOf(mediaType)} : new String[]{String.valueOf(mediaType), ValueOf.toString(bucketId)};
    }

    private static final String[] PROJECTION_29 = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE};

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS " + COLUMN_COUNT};

    /**
     * Media file database field
     */
    private static final String[] PROJECTION_PAGE = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED};

    /**
     * Get the latest cover of an album catalog
     *
     * @param bucketId
     * @return
     */
    public String getFirstCover(long bucketId) {
        Cursor data = null;
        try {
            if (SdkVersionUtils.checkedAndroid_R()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), 1, 0);
                data = mContext.getContentResolver().query(QUERY_URI, new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.DATA}, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = mContext.getContentResolver().query(QUERY_URI, new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.DATA}, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
            }
            if (data != null && data.getCount() > 0) {
                if (data.moveToFirst()) {
                    long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                    return SdkVersionUtils.checkedAndroid_Q() ? PictureMimeType.getRealPathUri(id, mimeType) : data.getString
                            (data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null && !data.isClosed()) {
                data.close();
            }
        }
        return null;
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
        loadPageMediaData(bucketId, page, limit, config.pageSize, listener);
    }

    /**
     * Queries for data in the specified directory
     *
     * @param bucketId
     * @param listener
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, OnQueryDataResultListener<LocalMedia> listener) {
        loadPageMediaData(bucketId, page, config.pageSize, config.pageSize, listener);
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
    public void loadPageMediaData(long bucketId, int page, int limit, int pageSize,
                                  OnQueryDataResultListener<LocalMedia> listener) {
        PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<MediaData>() {

            @Override
            public MediaData doInBackground() {
                Cursor data = null;
                try {
                    if (SdkVersionUtils.checkedAndroid_R()) {
                        Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), limit, (page - 1) * pageSize);
                        data = mContext.getContentResolver().query(QUERY_URI, PROJECTION_PAGE, queryArgs, null);
                    } else {
                        String orderBy = page == -1 ? MediaStore.Files.FileColumns._ID + " DESC" : MediaStore.Files.FileColumns._ID + " DESC limit " + limit + " offset " + (page - 1) * pageSize;
                        data = mContext.getContentResolver().query(QUERY_URI, PROJECTION_PAGE, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
                    }
                    if (data != null) {
                        List<LocalMedia> result = new ArrayList<>();
                        if (data.getCount() > 0) {
                            int idColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[0]);
                            int dataColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[1]);
                            int mimeTypeColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[2]);
                            int widthColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[3]);
                            int heightColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[4]);
                            int durationColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[5]);
                            int sizeColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[6]);
                            int folderNameColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[7]);
                            int fileNameColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[8]);
                            int bucketIdColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[9]);
                            int dateAddedColumn = data.getColumnIndexOrThrow(PROJECTION_PAGE[10]);
                            data.moveToFirst();
                            do {
                                long id = data.getLong(idColumn);
                                String mimeType = data.getString(mimeTypeColumn);
                                mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                                String absolutePath = data.getString(dataColumn);
                                String url = SdkVersionUtils.checkedAndroid_Q() ? PictureMimeType.getRealPathUri(id, mimeType) : absolutePath;
                                if (config.isFilterInvalidFile) {
                                    if (!PictureFileUtils.isFileExists(absolutePath)) {
                                        continue;
                                    }
                                }
                                // Here, it is solved that some models obtain mimeType and return the format of image / *,
                                // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                                if (mimeType.endsWith("image/*")) {
                                    if (PictureMimeType.isContent(url)) {
                                        mimeType = PictureMimeType.getImageMimeType(absolutePath);
                                    } else {
                                        mimeType = PictureMimeType.getImageMimeType(url);
                                    }
                                    if (!config.isGif) {
                                        if (PictureMimeType.isGif(mimeType)) {
                                            continue;
                                        }
                                    }
                                }
                                if (!config.isWebp) {
                                    if (mimeType.startsWith(PictureMimeType.ofWEBP())) {
                                        continue;
                                    }
                                }
                                if (!config.isBmp) {
                                    if (mimeType.startsWith(PictureMimeType.ofBMP())) {
                                        continue;
                                    }
                                }
                                int width = data.getInt(widthColumn);
                                int height = data.getInt(heightColumn);
                                long duration = data.getLong(durationColumn);
                                long size = data.getLong(sizeColumn);
                                String folderName = data.getString(folderNameColumn);
                                String fileName = data.getString(fileNameColumn);
                                long bucket_id = data.getLong(bucketIdColumn);

                                if (config.filterFileSize > 0) {
                                    if (size > config.filterFileSize * FILE_SIZE_UNIT) {
                                        continue;
                                    }
                                }

                                if (PictureMimeType.isHasVideo(mimeType)) {
                                    if (config.videoMinSecond > 0 && duration < config.videoMinSecond) {
                                        // If you set the minimum number of seconds of video to display
                                        continue;
                                    }
                                    if (config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                                        // If you set the maximum number of seconds of video to display
                                        continue;
                                    }
                                    if (duration == 0) {
                                        //If the length is 0, the corrupted video is processed and filtered out
                                        continue;
                                    }
                                    if (size <= 0) {
                                        // The video size is 0 to filter out
                                        continue;
                                    }
                                }

                                LocalMedia image = new LocalMedia
                                        (id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucket_id, data.getLong(dateAddedColumn));

                                result.add(image);

                            } while (data.moveToNext());
                        }
                        return new MediaData(data.getCount() > 0, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "loadMedia Page Data Error: " + e.getMessage());
                    return null;
                } finally {
                    if (data != null && !data.isClosed()) {
                        data.close();
                    }
                }
                return null;
            }

            @Override
            public void onSuccess(MediaData result) {
                if (listener != null && result != null) {
                    listener.onComplete(result.data, page, result.isHasNextMore);
                }
            }
        });
    }

    /**
     * Query the local gallery data
     *
     * @param listener
     */
    public void loadAllMedia(OnQueryDataResultListener<LocalMediaFolder> listener) {
        PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {
            @Override
            public List<LocalMediaFolder> doInBackground() {
                Cursor data = mContext.getContentResolver().query(QUERY_URI,
                        SdkVersionUtils.checkedAndroid_Q() ? PROJECTION_29 : PROJECTION,
                        getSelection(), getSelectionArgs(), ORDER_BY);
                try {
                    if (data != null) {
                        int count = data.getCount();
                        int totalCount = 0;
                        List<LocalMediaFolder> mediaFolders = new ArrayList<>();
                        if (count > 0) {
                            if (SdkVersionUtils.checkedAndroid_Q()) {
                                Map<Long, Long> countMap = new HashMap<>();
                                while (data.moveToNext()) {
                                    long bucketId = data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID));
                                    Long newCount = countMap.get(bucketId);
                                    if (newCount == null) {
                                        newCount = 1L;
                                    } else {
                                        newCount++;
                                    }
                                    countMap.put(bucketId, newCount);
                                }

                                if (data.moveToFirst()) {
                                    Set<Long> hashSet = new HashSet<>();
                                    do {
                                        long bucketId = data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID));
                                        if (hashSet.contains(bucketId)) {
                                            continue;
                                        }
                                        LocalMediaFolder mediaFolder = new LocalMediaFolder();
                                        mediaFolder.setBucketId(bucketId);
                                        String bucketDisplayName = data.getString(
                                                data.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                                        String mimeType = data.getString(data.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                                        long size = countMap.get(bucketId);
                                        long id = data.getLong(data.getColumnIndex(MediaStore.Files.FileColumns._ID));
                                        mediaFolder.setName(bucketDisplayName);
                                        mediaFolder.setImageNum(ValueOf.toInt(size));
                                        mediaFolder.setFirstImagePath(PictureMimeType.getRealPathUri(id, mimeType));
                                        mediaFolder.setFirstMimeType(mimeType);
                                        mediaFolders.add(mediaFolder);
                                        hashSet.add(bucketId);
                                        totalCount += size;
                                    } while (data.moveToNext());
                                }

                            } else {
                                data.moveToFirst();
                                do {
                                    LocalMediaFolder mediaFolder = new LocalMediaFolder();
                                    long bucketId = data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID));
                                    String bucketDisplayName = data.getString(data.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                                    String mimeType = data.getString(data.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                                    int size = data.getInt(data.getColumnIndex(COLUMN_COUNT));
                                    mediaFolder.setBucketId(bucketId);
                                    String url = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DATA));
                                    mediaFolder.setFirstImagePath(url);
                                    mediaFolder.setName(bucketDisplayName);
                                    mediaFolder.setFirstMimeType(mimeType);
                                    mediaFolder.setImageNum(size);
                                    mediaFolders.add(mediaFolder);
                                    totalCount += size;
                                } while (data.moveToNext());
                            }

                            sortFolder(mediaFolders);

                            // 相机胶卷
                            LocalMediaFolder allMediaFolder = new LocalMediaFolder();
                            allMediaFolder.setImageNum(totalCount);
                            allMediaFolder.setChecked(true);
                            allMediaFolder.setBucketId(-1);
                            if (data.moveToFirst()) {
                                allMediaFolder.setFirstImagePath(SdkVersionUtils.checkedAndroid_Q() ? getFirstUri(data) : getFirstUrl(data));
                                allMediaFolder.setFirstMimeType(getFirstCoverMimeType(data));
                            }
                            String bucketDisplayName = config.chooseMode == PictureMimeType.ofAudio() ?
                                    mContext.getString(R.string.picture_all_audio)
                                    : mContext.getString(R.string.picture_camera_roll);
                            allMediaFolder.setName(bucketDisplayName);
                            allMediaFolder.setOfAllType(config.chooseMode);
                            allMediaFolder.setCameraFolder(true);
                            mediaFolders.add(0, allMediaFolder);

                            return mediaFolders;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "loadAllMedia Data Error: " + e.getMessage());
                    return null;
                } finally {
                    if (data != null && !data.isClosed()) {
                        data.close();
                    }
                }
                return new ArrayList<>();
            }

            @Override
            public void onSuccess(List<LocalMediaFolder> result) {
                if (listener != null && result != null) {
                    listener.onComplete(result, 1, false);
                }
            }
        });
    }

    /**
     * Get cover uri
     *
     * @param cursor
     * @return
     */
    private static String getFirstUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
        return PictureMimeType.getRealPathUri(id, mimeType);
    }

    /**
     * Get cover uri mimeType
     *
     * @param cursor
     * @return
     */
    private static String getFirstCoverMimeType(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
    }

    /**
     * Get cover url
     *
     * @param cursor
     * @return
     */
    private static String getFirstUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
    }

    private String getPageSelection(long bucketId) {
        String durationCondition = getDurationCondition();
        String sizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                //  Gets the all
                return getPageSelectionArgsForAllMediaCondition(bucketId, queryMimeCondition, durationCondition, sizeCondition);
            case PictureConfig.TYPE_IMAGE:
                // Gets the image of the specified type
                return getPageSelectionArgsForImageMediaCondition(bucketId, queryMimeCondition, sizeCondition);
            case PictureConfig.TYPE_VIDEO:
            case PictureConfig.TYPE_AUDIO:
                //  Gets the video or audio
                return getPageSelectionArgsForVideoOrAudioMediaCondition(bucketId, queryMimeCondition, durationCondition, sizeCondition);
        }
        return null;
    }

    private static String getPageSelectionArgsForAllMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=?").append(queryMimeCondition).append(" OR ").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? AND ").append(durationCondition).append(") AND ");
        if (bucketId == -1) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForImageMediaCondition(long bucketId, String queryMimeCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?");
        if (bucketId == -1) {
            return stringBuilder.append(queryMimeCondition).append(") AND ").append(sizeCondition).toString();
        } else {
            return stringBuilder.append(queryMimeCondition).append(") AND ").append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForVideoOrAudioMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).append(") AND ");
        if (bucketId == -1) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private String[] getPageSelectionArgs(long bucketId) {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                if (bucketId == -1) {
                    // ofAll
                    return new String[]{
                            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                    };
                }
                //  Gets the specified album directory
                return new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                        ValueOf.toString(bucketId)
                };
            case PictureConfig.TYPE_IMAGE:
                // Get photo
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, bucketId);
            case PictureConfig.TYPE_VIDEO:
                // Get video
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, bucketId);
            case PictureConfig.TYPE_AUDIO:
                // Get audio
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO, bucketId);
        }
        return null;
    }


    private String getSelection() {
        String fileSizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(getDurationCondition(), fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_IMAGE:
                // Get Images
                return getSelectionArgsForImageMediaCondition(queryMimeCondition, fileSizeCondition);
            case PictureConfig.TYPE_VIDEO:
            case PictureConfig.TYPE_AUDIO:
                // Gets the specified album directory
                return getSelectionArgsForVideoOrAudioMediaCondition(queryMimeCondition, fileSizeCondition);
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all
                return getSelectionArgsForAllMediaType();
            case PictureConfig.TYPE_IMAGE:
                // Get photo
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // Get video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
                // Get audio
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
    }

    /**
     * Sort by number of files
     *
     * @param imageFolders
     */
    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        Collections.sort(imageFolders, (lhs, rhs) -> {
            if (lhs.getData() == null || rhs.getData() == null) {
                return 0;
            }
            int lSize = lhs.getImageNum();
            int rSize = rhs.getImageNum();
            return Integer.compare(rSize, lSize);
        });
    }

    /**
     * Get video (maximum or minimum time)
     *
     * @return
     */
    private String getDurationCondition() {
        long maxS = config.videoMaxSecond == 0 ? Long.MAX_VALUE : config.videoMaxSecond;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.DURATION + " and " + MediaStore.MediaColumns.DURATION + " <= %d",
                Math.max((long) 0, config.videoMinSecond),
                Math.max((long) 0, config.videoMinSecond) == 0 ? "" : "=",
                maxS);
    }

    /**
     * Get media size (maxFileSize or miniFileSize)
     *
     * @return
     */
    private String getFileSizeCondition() {
        long maxS = config.filterMaxFileSize == 0 ? Long.MAX_VALUE : config.filterMaxFileSize;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
                Math.max(0, config.filterMinFileSize),
                Math.max(0, config.filterMinFileSize) == 0 ? "" : "=",
                maxS);
    }

    private String getQueryMimeCondition() {
        HashSet<String> stringHashSet = config.queryMimeTypeHashSet;
        if (stringHashSet == null) {
            stringHashSet = new HashSet<>();
        }
        if (!TextUtils.isEmpty(config.specifiedFormat)) {
            stringHashSet.add(config.specifiedFormat);
        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stringHashSet.iterator();
        int index = -1;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            if (config.chooseMode == PictureMimeType.ofVideo()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)) {
                    continue;
                }
            } else if (config.chooseMode == PictureMimeType.ofImage()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)) {
                    continue;
                }
            } else if (config.chooseMode == PictureMimeType.ofAudio()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)) {
                    continue;
                }
            }
            index++;
            stringBuilder.append(index == 0 ? " AND " : " OR ").append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value).append("'");
        }
        if (config.chooseMode != PictureMimeType.ofVideo()) {
            if (!config.isGif && !stringHashSet.contains(PictureMimeType.ofGIF())) {
                stringBuilder.append(NOT_GIF);
            }
        }
        return stringBuilder.toString();
    }

    @SuppressLint("StaticFieldLeak")
    private static LocalMediaPageLoader instance;

    public static LocalMediaPageLoader getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalMediaPageLoader.class) {
                if (instance == null) {
                    instance = new LocalMediaPageLoader(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * set empty
     */
    public static void setInstanceNull() {
        instance = null;
    }
}
