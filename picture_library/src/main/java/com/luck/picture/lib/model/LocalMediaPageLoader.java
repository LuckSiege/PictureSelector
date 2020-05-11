package com.luck.picture.lib.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ValueOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF_UNKNOWN = "!='image/*'";
    private static final String NOT_GIF = "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN;
    private static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";

    /**
     * Filter out recordings that are less than 500 milliseconds long
     */
    private static final int AUDIO_DURATION = 500;
    private Context mContext;
    private PictureSelectionConfig config;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    /**
     * Image
     */
    private static final String SELECTION = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? )"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;

    private static final String SELECTION_29 = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? "
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + ") AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;

    private static final String SELECTION_NOT_GIF_29 = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    /**
     * Queries for images with the specified suffix
     */
    private static final String SELECTION_SPECIFIED_FORMAT = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * Queries for images with the specified suffix targetSdk>=29
     */
    private static final String SELECTION_SPECIFIED_FORMAT_29 = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * Query criteria (audio and video)
     *
     * @param timeCondition
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition(String timeCondition) {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + " AND " + timeCondition;
        }
        return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + ") AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + timeCondition + ")" + GROUP_BY_BUCKET_Id;
    }

    /**
     * All mode conditions
     *
     * @param timeCondition
     * @param isGif
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, boolean isGif) {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                    + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + timeCondition + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
        }

        return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR " + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + timeCondition) + ")" + " AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
    }

    /**
     * Get pictures or videos
     */
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

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


    public LocalMediaPageLoader(Context context, PictureSelectionConfig config) {
        this.mContext = context;
        this.config = config;
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
            COLUMN_BUCKET_ID};

    /**
     * Get the latest cover of an album catalog
     *
     * @param bucketId
     * @return
     */
    public String getFirstCover(long bucketId) {
        Cursor data = null;
        try {
            String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
            data = mContext.getContentResolver().query(QUERY_URI, new String[]{
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.MediaColumns.DATA}, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
            if (data != null && data.getCount() > 0) {
                if (data.moveToFirst()) {
                    long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                    return SdkVersionUtils.checkedAndroid_Q() ? getRealPathAndroid_Q(id) : data.getString
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
    public void loadPageMediaData(long bucketId, int page, int limit, OnQueryDataResultListener listener) {
        loadPageMediaData(bucketId, page, limit, config.pageSize, listener);
    }

    /**
     * Queries for data in the specified directory
     *
     * @param bucketId
     * @param listener
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, OnQueryDataResultListener listener) {
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
    public void loadPageMediaData(long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaData>() {

            @Override
            public MediaData doInBackground() {
                Cursor data = null;
                try {
                    String orderBy = page == -1 ? MediaStore.Files.FileColumns._ID + " DESC" : MediaStore.Files.FileColumns._ID + " DESC limit " + limit + " offset " + (page - 1) * pageSize;
                    data = mContext.getContentResolver().query(QUERY_URI, PROJECTION_PAGE, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
                    if (data != null) {
                        List<LocalMedia> result = new ArrayList<>();
                        if (data.getCount() > 0) {
                            data.moveToFirst();
                            do {
                                long id = data.getLong
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[0]));

                                String absolutePath = data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[1]));

                                String url = SdkVersionUtils.checkedAndroid_Q() ? getRealPathAndroid_Q(id) : absolutePath;

                                if (config.isFilterInvalidFile) {
                                    if (!PictureFileUtils.isFileExists(absolutePath)) {
                                        continue;
                                    }
                                }
                                String mimeType = data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[2]));

                                mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                                // Here, it is solved that some models obtain mimeType and return the format of image / *,
                                // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                                if (mimeType.endsWith("image/*")) {
                                    if (PictureMimeType.isContent(url)) {
                                        mimeType = PictureMimeType.getImageMimeType(absolutePath);
                                    } else {
                                        mimeType = PictureMimeType.getImageMimeType(url);
                                    }
                                    if (!config.isGif) {
                                        boolean isGif = PictureMimeType.isGif(mimeType);
                                        if (isGif) {
                                            continue;
                                        }
                                    }
                                }
                                int width = data.getInt
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[3]));

                                int height = data.getInt
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[4]));

                                long duration = data.getLong
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[5]));

                                long size = data.getLong
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[6]));

                                String folderName = data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[7]));

                                String fileName = data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[8]));

                                long bucket_id = data.getLong
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[9]));

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
                                        (id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucket_id);

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
    public void loadAllMedia(OnQueryDataResultListener listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {
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
                                        long size = countMap.get(bucketId);
                                        long id = data.getLong(data.getColumnIndex(MediaStore.Files.FileColumns._ID));
                                        mediaFolder.setName(bucketDisplayName);
                                        mediaFolder.setImageNum(ValueOf.toInt(size));
                                        mediaFolder.setFirstImagePath(getRealPathAndroid_Q(id));
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
                                    int size = data.getInt(data.getColumnIndex(COLUMN_COUNT));
                                    mediaFolder.setBucketId(bucketId);
                                    String url = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DATA));
                                    mediaFolder.setFirstImagePath(url);
                                    mediaFolder.setName(bucketDisplayName);
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
                                String firstUrl = SdkVersionUtils.checkedAndroid_Q() ? getFirstUri(data) : getFirstUrl(data);
                                allMediaFolder.setFirstImagePath(firstUrl);
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
        return getRealPathAndroid_Q(id);
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
        String durationCondition = getDurationCondition(0, 0);
        boolean isSpecifiedFormat = !TextUtils.isEmpty(config.specifiedFormat);
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                if (bucketId == -1) {
                    // ofAll
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + durationCondition + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                // Gets the specified album directory
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                        + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + durationCondition + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";

            case PictureConfig.TYPE_IMAGE:
                // Gets the image of the specified type
                if (bucketId == -1) {
                    // ofAll
                    if (isSpecifiedFormat) {
                        return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                                + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + " AND " + MediaStore.MediaColumns.MIME_TYPE + "='" + config.specifiedFormat + "'")
                                + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                            + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                // Gets the specified album directory
                if (isSpecifiedFormat) {
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + " AND " + MediaStore.MediaColumns.MIME_TYPE + "='" + config.specifiedFormat + "'")
                            + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                        + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
            case PictureConfig.TYPE_VIDEO:
            case PictureConfig.TYPE_AUDIO:
                if (bucketId == -1) {
                    // ofAll
                    if (isSpecifiedFormat) {
                        return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.MediaColumns.MIME_TYPE + "='" + config.specifiedFormat + "'" + " AND " + durationCondition + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + durationCondition + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                // Gets the specified album directory
                if (isSpecifiedFormat) {
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.MediaColumns.MIME_TYPE + "='" + config.specifiedFormat + "'" + " AND " + durationCondition + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + durationCondition + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
        }
        return null;
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
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), config.isGif);
            case PictureConfig.TYPE_IMAGE:
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定类型的图片
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        return SELECTION_SPECIFIED_FORMAT_29 + "='" + config.specifiedFormat + "' AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "') AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
                }
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    return config.isGif ? SELECTION_29 : SELECTION_NOT_GIF_29;
                }
                return config.isGif ? SELECTION : SELECTION_NOT_GIF;
            case PictureConfig.TYPE_VIDEO:
                // 获取视频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // Gets the specified album directory
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        return SELECTION_SPECIFIED_FORMAT_29 + "='" + config.specifiedFormat + "' AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "') AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
                }
                return getSelectionArgsForSingleMediaCondition(getDurationCondition(0, 0));
            case PictureConfig.TYPE_AUDIO:
                // Get Audio
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // Gets the specified album directory
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        return SELECTION_SPECIFIED_FORMAT_29 + "='" + config.specifiedFormat + "' AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "') AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
                }
                return getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                return SELECTION_ALL_ARGS;
            case PictureConfig.TYPE_IMAGE:
                // Get photo
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // Get video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
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
     * Android Q
     *
     * @param id
     * @return
     */
    private static String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(ValueOf.toString(id)).build().toString();
    }

    /**
     * Get video (maximum or minimum time)
     *
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = config.videoMaxSecond == 0 ? Long.MAX_VALUE : config.videoMaxSecond;
        if (exMaxLimit != 0) {
            maxS = Math.min(maxS, exMaxLimit);
        }
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.DURATION + " and " + MediaStore.MediaColumns.DURATION + " <= %d",
                Math.max(exMinLimit, config.videoMinSecond),
                Math.max(exMinLimit, config.videoMinSecond) == 0 ? "" : "=",
                maxS);
    }


    private static LocalMediaPageLoader instance;

    public static LocalMediaPageLoader getInstance(Context context, PictureSelectionConfig
            config) {
        if (instance == null) {
            synchronized (LocalMediaPageLoader.class) {
                if (instance == null) {
                    instance = new LocalMediaPageLoader(context.getApplicationContext(), config);
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
