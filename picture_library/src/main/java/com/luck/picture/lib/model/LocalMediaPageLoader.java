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
public class LocalMediaPageLoader {
    private static final String TAG = LocalMediaPageLoader.class.getSimpleName();

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF = "!='image/gif'";
    private static final String GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";

    /**
     * 过滤掉小于500毫秒的录音
     */
    private static final int AUDIO_DURATION = 500;
    private Context mContext;
    private PictureSelectionConfig config;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    /**
     * 图片
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
     * 查询指定后缀名的图片
     */
    private static final String SELECTION_SPECIFIED_FORMAT = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * 查询指定后缀名的图片
     */
    private static final String SELECTION_SPECIFIED_FORMAT_29 = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * 查询条件(音视频)
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
     * 全部模式下条件
     *
     * @param timeCondition
     * @param isGif
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, boolean isGif) {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                    + " OR "
                    + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + timeCondition)
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        }
        return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + timeCondition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
    }

    /**
     * 获取图片or视频
     */
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    /**
     * 获取指定类型的文件
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    /**
     * 获取指定类型的文件
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
     * 媒体文件数据库字段
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
     * 获取某个相册目录最新一张封面
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
     * 查询指定目录下的数据
     *
     * @param bucketId
     * @param listener
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, OnQueryDataResultListener listener) {
        loadPageMediaData(bucketId, page, config.pageSize, listener);
    }

    /**
     * 查询指定目录下的数据(分页)
     *
     * @param bucketId
     * @return
     */
    public void loadPageMediaData(long bucketId, int page, int pageSize, OnQueryDataResultListener listener) {
        PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<MediaData>() {

            @Override
            public MediaData doInBackground() {
                Cursor data = null;
                try {
                    String orderBy = page == -1 ? MediaStore.Files.FileColumns._ID + " DESC" : MediaStore.Files.FileColumns._ID + " DESC limit " + pageSize + " offset " + (page - 1) * pageSize;
                    data = mContext.getContentResolver().query(QUERY_URI, PROJECTION_PAGE, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
                    if (data != null) {
                        List<LocalMedia> result = new ArrayList<>();
                        if (data.getCount() > 0) {
                            data.moveToFirst();
                            do {
                                long id = data.getLong
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[0]));

                                String url = SdkVersionUtils.checkedAndroid_Q() ? getRealPathAndroid_Q(id) : data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[1]));

                                if (config.isFilterInvalidFile) {
                                    boolean isFileExists = PictureFileUtils.isFileExists(mContext, url);
                                    if (!isFileExists) {
                                        continue;
                                    }
                                }

                                String mimeType = data.getString
                                        (data.getColumnIndexOrThrow(PROJECTION_PAGE[2]));
                                // 这里解决部分机型获取mimeType返回 image/* 格式导致无法判别其具体类型 例如小米8，9，10等机型
                                if (mimeType.endsWith("image/*")) {
                                    if (PictureMimeType.isContent(url)) {
                                        String absolutePath = PictureFileUtils.getPath(mContext, Uri.parse(url));
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
                                        // 如果设置了最小显示多少秒的视频
                                        continue;
                                    }
                                    if (config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                                        // 如果设置了最大显示多少秒的视频
                                        continue;
                                    }
                                    if (duration == 0) {
                                        // 时长如果为0，就当做损坏的视频处理过滤掉
                                        continue;
                                    }
                                    if (size <= 0) {
                                        // 视频大小为0过滤掉
                                        continue;
                                    }
                                }

                                LocalMedia image = new LocalMedia
                                        (id, url, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucket_id);

                                result.add(image);

                            } while (data.moveToNext());

                            return new MediaData(data.getCount() > 0, result);
                        }
                        return new MediaData(false, result);
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
                    listener.onComplete(result.data, result.isHasNextMore);
                }
            }
        });
    }

    /**
     * 查询本地图库数据
     *
     * @param listener
     */
    public void loadAllMedia(OnQueryDataResultListener listener) {
        PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {
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
                if (listener != null) {
                    listener.onComplete(result, false);
                }
            }
        });
    }

    /**
     * 获取封面uri
     *
     * @param cursor
     * @return
     */
    private static String getFirstUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        return getRealPathAndroid_Q(id);
    }

    /**
     * 获取封面url
     *
     * @param cursor
     * @return
     */
    private static String getFirstUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
    }


    private String getPageSelection(long bucketId) {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                if (bucketId == -1) {
                    // 获取全部
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                // 获取指定相册目录
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                        + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";

            case PictureConfig.TYPE_IMAGE:
                if (bucketId == -1) {
                    // 获取全部
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                            + ") AND " + MediaStore.MediaColumns.SIZE + ">0";

                }
                // 获取指定相册目录
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + (config.isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                        + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
            case PictureConfig.TYPE_VIDEO:
            case PictureConfig.TYPE_AUDIO:
                if (bucketId == -1) {
                    // 获取全部
                    return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                            + ") AND " + MediaStore.MediaColumns.SIZE + ">0";
                }
                // 获取指定相册目录
                return "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0";
        }
        return null;
    }

    private String[] getPageSelectionArgs(long bucketId) {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                if (bucketId == -1) {
                    // 获取全部(相机胶卷)
                    return new String[]{
                            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                    };
                }
                // 获取指定目录资源
                return new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                        ValueOf.toString(bucketId)
                };
            case PictureConfig.TYPE_IMAGE:
                // 只获取图片
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, bucketId);
            case PictureConfig.TYPE_VIDEO:
                // 只获取视频
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, bucketId);
            case PictureConfig.TYPE_AUDIO:
                // 只获取音频
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO, bucketId);
        }
        return null;
    }


    private String getSelection() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // 获取全部，不包括音频
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
                    // 获取指定类型的图片
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        return SELECTION_SPECIFIED_FORMAT_29 + "='" + config.specifiedFormat + "' AND " + MediaStore.MediaColumns.SIZE + ">0";
                    }
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "') AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id;
                }
                return getSelectionArgsForSingleMediaCondition(getDurationCondition(0, 0));
            case PictureConfig.TYPE_AUDIO:
                // 获取音频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定类型的图片
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
                // 只获取图片
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // 只获取视频
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
    }

    /**
     * 文件夹数量进行排序
     *
     * @param imageFolders
     */
    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        // 文件夹按图片数量排序
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
     * 适配Android Q
     *
     * @param id
     * @return
     */
    private static String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(ValueOf.toString(id)).build().toString();
    }

    /**
     * 获取视频(最长或最小时间)
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
     * 置空
     */
    public static void setInstanceNull() {
        instance = null;
    }
}
