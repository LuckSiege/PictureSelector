package com.luck.picture.lib.loader;

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
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaData;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.SortUtils;
import com.luck.picture.lib.utils.ValueOf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author：luck
 * @date：2020-04-13 15:06
 * @describe：Local media database query class，Support paging
 */
public final class LocalMediaPageLoader extends IBridgeMediaLoader {

    public LocalMediaPageLoader(Context context, PictureSelectionConfig config) {
        this.mContext = context;
        this.config = config;
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @param queryMimeTypeOptions
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, String sizeCondition, String queryMimeTypeOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?")
                .append(queryMimeTypeOptions).append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? AND ").append(timeCondition).append(") AND ")
                .append(sizeCondition);
        if (SdkVersionUtils.isQ()) {
            return stringBuilder.toString();
        } else {
            return stringBuilder.append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Query conditions in image modes
     *
     * @param fileSizeCondition
     * @param queryMimeTypeOptions
     * @return
     */
    private static String getSelectionArgsForImageMediaCondition(String fileSizeCondition, String queryMimeTypeOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        if (SdkVersionUtils.isQ()) {
            return stringBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?")
                    .append(queryMimeTypeOptions).append(" AND ").append(fileSizeCondition).toString();
        } else {
            return stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?")
                    .append(queryMimeTypeOptions).append(") AND ").append(fileSizeCondition).append(")")
                    .append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Video mode conditions
     *
     * @param durationCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForVideoMediaCondition(String durationCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (SdkVersionUtils.isQ()) {
            return stringBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).toString();
        } else {
            return stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(") AND ").append(durationCondition).append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
    }

    /**
     * Audio mode conditions
     *
     * @param durationCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForAudioMediaCondition(String durationCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (SdkVersionUtils.isQ()) {
            return stringBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).toString();
        } else {
            return stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(") AND ").append(durationCondition).append(")").append(GROUP_BY_BUCKET_Id).toString();
        }
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
            MediaStore.MediaColumns.DATA,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE};

    private static final String[] ALL_PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS " + COLUMN_COUNT};


    /**
     * Get the latest cover of an album catalog
     *
     * @param bucketId
     * @return
     */
    @Override
    public String getFirstCover(long bucketId) {
        Cursor data = null;
        try {
            if (SdkVersionUtils.isR()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), 1, 0, getSortOrder());
                data = mContext.getContentResolver().query(QUERY_URI, new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.MIME_TYPE,
                        MediaStore.MediaColumns.DATA}, queryArgs, null);
            } else {
                String orderBy = getSortOrder() + " limit 1 offset 0";
                data = mContext.getContentResolver().query(QUERY_URI, new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.MIME_TYPE,
                        MediaStore.MediaColumns.DATA}, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
            }
            if (data != null && data.getCount() > 0) {
                if (data.moveToFirst()) {
                    long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                    return SdkVersionUtils.isQ() ? MediaUtils.getRealPathUri(id, mimeType) : data.getString
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
     * Queries First for data in the specified directory
     *
     * @param bucketId
     * @param pageSize
     * @param listener
     * @return
     */
    @Override
    public void loadFirstPageMedia(long bucketId, int pageSize, OnQueryDataResultListener<LocalMedia> listener) {
        loadPageMediaData(bucketId, 1, pageSize, pageSize, listener);
    }

    /**
     * Queries for data in the specified directory (page)
     *
     * @param bucketId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public void loadPageMediaData(long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> listener) {
        loadPageMediaData(bucketId, page, pageSize, pageSize, listener);
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
    @Override
    public void loadPageMediaData(long bucketId, int page, int limit, int pageSize,
                                  OnQueryDataResultListener<LocalMedia> listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaData>() {

            @Override
            public MediaData doInBackground() {
                Cursor data = null;
                try {
                    if (SdkVersionUtils.isR()) {
                        Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), limit, (page - 1) * pageSize, getSortOrder());
                        data = mContext.getContentResolver().query(QUERY_URI, PROJECTION, queryArgs, null);
                    } else {
                        String orderBy = page == -1 ? getSortOrder() : getSortOrder() + " limit " + limit + " offset " + (page - 1) * pageSize;
                        data = mContext.getContentResolver().query(QUERY_URI, PROJECTION, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
                    }
                    if (data != null) {
                        ArrayList<LocalMedia> result = new ArrayList<>();
                        if (data.getCount() > 0) {
                            int idColumn = data.getColumnIndexOrThrow(PROJECTION[0]);
                            int dataColumn = data.getColumnIndexOrThrow(PROJECTION[1]);
                            int mimeTypeColumn = data.getColumnIndexOrThrow(PROJECTION[2]);
                            int widthColumn = data.getColumnIndexOrThrow(PROJECTION[3]);
                            int heightColumn = data.getColumnIndexOrThrow(PROJECTION[4]);
                            int durationColumn = data.getColumnIndexOrThrow(PROJECTION[5]);
                            int sizeColumn = data.getColumnIndexOrThrow(PROJECTION[6]);
                            int folderNameColumn = data.getColumnIndexOrThrow(PROJECTION[7]);
                            int fileNameColumn = data.getColumnIndexOrThrow(PROJECTION[8]);
                            int bucketIdColumn = data.getColumnIndexOrThrow(PROJECTION[9]);
                            int dateAddedColumn = data.getColumnIndexOrThrow(PROJECTION[10]);
                            int orientationColumn = data.getColumnIndexOrThrow(PROJECTION[11]);
                            data.moveToFirst();
                            do {
                                long id = data.getLong(idColumn);
                                String mimeType = data.getString(mimeTypeColumn);
                                mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                                String absolutePath = data.getString(dataColumn);
                                if (PictureSelectionConfig.onQueryFilterListener != null) {
                                    if (PictureSelectionConfig.onQueryFilterListener.onFilter(absolutePath)) {
                                        continue;
                                    }
                                }
                                String url = SdkVersionUtils.isQ() ? MediaUtils.getRealPathUri(id, mimeType) : absolutePath;
                                if (config.isFilterInvalidFile) {
                                    if (!PictureFileUtils.isFileExists(absolutePath)) {
                                        continue;
                                    }
                                }
                                // Here, it is solved that some models obtain mimeType and return the format of image / *,
                                // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                                if (mimeType.endsWith("image/*")) {
                                    mimeType = MediaUtils.getMimeTypeFromMediaUrl(absolutePath);
                                    if (!config.isGif) {
                                        if (PictureMimeType.isHasGif(mimeType)) {
                                            continue;
                                        }
                                    }
                                }

                                if (mimeType.endsWith("image/*")) {
                                    continue;
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
                                int orientation = data.getInt(orientationColumn);
                                if (orientation == 90 || orientation == 270) {
                                    width = data.getInt(heightColumn);
                                    height = data.getInt(widthColumn);
                                }
                                long duration = data.getLong(durationColumn);
                                long size = data.getLong(sizeColumn);
                                String folderName = data.getString(folderNameColumn);
                                String fileName = data.getString(fileNameColumn);
                                long bucket_id = data.getLong(bucketIdColumn);


                                if (PictureMimeType.isHasVideo(mimeType) || PictureMimeType.isHasAudio(mimeType)) {
                                    if (config.filterVideoMinSecond > 0 && duration < config.filterVideoMinSecond) {
                                        // If you set the minimum number of seconds of video to display
                                        continue;
                                    }
                                    if (config.filterVideoMaxSecond > 0 && duration > config.filterVideoMaxSecond) {
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

                                LocalMedia media = LocalMedia.parseLocalMedia(id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucket_id, data.getLong(dateAddedColumn));
                                result.add(media);

                            } while (data.moveToNext());
                        }
                        if (bucketId == PictureConfig.ALL && page == 1) {
                            List<LocalMedia> list = SandboxFileLoader.loadInAppSandboxFile(mContext, config.sandboxDir);
                            if (list != null) {
                                result.addAll(list);
                                SortUtils.sortLocalMediaAddedTime(result);
                            }
                        }
                        return new MediaData(data.getCount() > 0, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "loadMedia Page Data Error: " + e.getMessage());
                    return new MediaData();
                } finally {
                    if (data != null && !data.isClosed()) {
                        data.close();
                    }
                }
                return new MediaData();
            }

            @Override
            public void onSuccess(MediaData result) {
                PictureThreadUtils.cancel(this);
                if (listener != null) {
                    listener.onComplete(result.data != null ? result.data : new ArrayList<>(), result.isHasNextMore);
                }
            }
        });
    }

    @Override
    public void loadOnlyInAppDirAllMedia(OnQueryAlbumListener<LocalMediaFolder> query) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<LocalMediaFolder>() {

            @Override
            public LocalMediaFolder doInBackground() {
                return SandboxFileLoader.loadInAppSandboxFolderFile(mContext, config.sandboxDir);
            }

            @Override
            public void onSuccess(LocalMediaFolder result) {
                PictureThreadUtils.cancel(this);
                if (query != null) {
                    query.onComplete(result);
                }
            }
        });
    }

    /**
     * Query the local gallery data
     *
     * @param query
     */
    @Override
    public void loadAllAlbum(OnQueryAllAlbumListener<LocalMediaFolder> query) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {
            @Override
            public List<LocalMediaFolder> doInBackground() {
                Cursor data = mContext.getContentResolver().query(QUERY_URI,
                        SdkVersionUtils.isQ() ? PROJECTION_29 : ALL_PROJECTION,
                        getSelection(), getSelectionArgs(), getSortOrder());
                try {
                    if (data != null) {
                        int count = data.getCount();
                        int totalCount = 0;
                        List<LocalMediaFolder> mediaFolders = new ArrayList<>();
                        if (count > 0) {
                            if (SdkVersionUtils.isQ()) {
                                Map<Long, Long> countMap = new HashMap<>();
                                while (data.moveToNext()) {
                                    String url = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                                    if (PictureSelectionConfig.onQueryFilterListener != null) {
                                        if (PictureSelectionConfig.onQueryFilterListener.onFilter(url)) {
                                            continue;
                                        }
                                    }
                                    long bucketId = data.getLong(data.getColumnIndexOrThrow(COLUMN_BUCKET_ID));
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
                                        String url = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                                        if (PictureSelectionConfig.onQueryFilterListener != null) {
                                            if (PictureSelectionConfig.onQueryFilterListener.onFilter(url)){
                                                continue;
                                            }
                                        }
                                        long bucketId = data.getLong(data.getColumnIndexOrThrow(COLUMN_BUCKET_ID));
                                        if (hashSet.contains(bucketId)) {
                                            continue;
                                        }
                                        LocalMediaFolder mediaFolder = new LocalMediaFolder();
                                        mediaFolder.setBucketId(bucketId);
                                        String bucketDisplayName = data.getString(
                                                data.getColumnIndexOrThrow(COLUMN_BUCKET_DISPLAY_NAME));
                                        String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                                        if (!countMap.containsKey(bucketId)) {
                                            continue;
                                        }
                                        long size = countMap.get(bucketId);
                                        long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                                        mediaFolder.setFolderName(bucketDisplayName);
                                        mediaFolder.setFolderTotalNum(ValueOf.toInt(size));
                                        mediaFolder.setFirstImagePath(MediaUtils.getRealPathUri(id, mimeType));
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
                                    long bucketId = data.getLong(data.getColumnIndexOrThrow(COLUMN_BUCKET_ID));
                                    String bucketDisplayName = data.getString(data.getColumnIndexOrThrow(COLUMN_BUCKET_DISPLAY_NAME));
                                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                                    int size = data.getInt(data.getColumnIndexOrThrow(COLUMN_COUNT));
                                    mediaFolder.setBucketId(bucketId);
                                    String url = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                                    mediaFolder.setFirstImagePath(url);
                                    mediaFolder.setFolderName(bucketDisplayName);
                                    mediaFolder.setFirstMimeType(mimeType);
                                    mediaFolder.setFolderTotalNum(size);
                                    mediaFolders.add(mediaFolder);
                                    totalCount += size;
                                } while (data.moveToNext());
                            }

                            // 相机胶卷
                            LocalMediaFolder allMediaFolder = new LocalMediaFolder();

                            LocalMediaFolder selfFolder = SandboxFileLoader
                                    .loadInAppSandboxFolderFile(mContext, config.sandboxDir);
                            if (selfFolder != null) {
                                mediaFolders.add(selfFolder);
                                String firstImagePath = selfFolder.getFirstImagePath();
                                File file = new File(firstImagePath);
                                long lastModified = file.lastModified();
                                totalCount += selfFolder.getFolderTotalNum();
                                allMediaFolder.setData(new ArrayList<>());
                                if (data.moveToFirst()) {
                                    allMediaFolder.setFirstImagePath(SdkVersionUtils.isQ() ? getFirstUri(data) : getFirstUrl(data));
                                    allMediaFolder.setFirstMimeType(getFirstCoverMimeType(data));
                                    long lastModified2;
                                    if (PictureMimeType.isContent(allMediaFolder.getFirstImagePath())) {
                                        String path = PictureFileUtils.getPath(mContext, Uri.parse(allMediaFolder.getFirstImagePath()));
                                        lastModified2 = new File(path).lastModified();
                                    } else {
                                        lastModified2 = new File(allMediaFolder.getFirstImagePath()).lastModified();
                                    }
                                    if (lastModified > lastModified2) {
                                        allMediaFolder.setFirstImagePath(selfFolder.getFirstImagePath());
                                        allMediaFolder.setFirstMimeType(selfFolder.getFirstMimeType());
                                    }
                                }
                            } else {
                                if (data.moveToFirst()) {
                                    allMediaFolder.setFirstImagePath(SdkVersionUtils.isQ() ? getFirstUri(data) : getFirstUrl(data));
                                    allMediaFolder.setFirstMimeType(getFirstCoverMimeType(data));
                                }
                            }

                            SortUtils.sortFolder(mediaFolders);
                            allMediaFolder.setFolderTotalNum(totalCount);
                            allMediaFolder.setBucketId(PictureConfig.ALL);
                            String folderName;
                            if (TextUtils.isEmpty(config.defaultAlbumName)) {
                                folderName = config.chooseMode == SelectMimeType.ofAudio()
                                        ? mContext.getString(R.string.ps_all_audio) : mContext.getString(R.string.ps_camera_roll);
                            } else {
                                folderName = config.defaultAlbumName;
                            }
                            allMediaFolder.setFolderName(folderName);
                            mediaFolders.add(0, allMediaFolder);
                            if (config.isSyncCover) {
                                if (config.chooseMode == SelectMimeType.ofAll()) {
                                    synchronousFirstCover(mediaFolders);
                                }
                            }
                            return mediaFolders;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "loadAllMedia Data Error: " + e.getMessage());
                } finally {
                    if (data != null && !data.isClosed()) {
                        data.close();
                    }
                }
                return new ArrayList<>();
            }

            @Override
            public void onSuccess(List<LocalMediaFolder> result) {
                PictureThreadUtils.cancel(this);
                if (query != null) {
                    query.onComplete(result);
                }
            }
        });
    }

    /**
     * Synchronous  First data Cover
     *
     * @param mediaFolders
     */
    private void synchronousFirstCover(List<LocalMediaFolder> mediaFolders) {
        for (int i = 0; i < mediaFolders.size(); i++) {
            LocalMediaFolder mediaFolder = mediaFolders.get(i);
            if (mediaFolder == null) {
                continue;
            }
            String firstCover = getFirstCover(mediaFolder.getBucketId());
            if (TextUtils.isEmpty(firstCover)) {
                continue;
            }
            mediaFolder.setFirstImagePath(firstCover);
        }
    }

    /**
     * Get cover uri
     *
     * @param cursor
     * @return
     */
    private static String getFirstUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
        String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
        return MediaUtils.getRealPathUri(id, mimeType);
    }

    /**
     * Get cover uri mimeType
     *
     * @param cursor
     * @return
     */
    private static String getFirstCoverMimeType(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
    }

    /**
     * Get cover url
     *
     * @param cursor
     * @return
     */
    private static String getFirstUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
    }

    private String getPageSelection(long bucketId) {
        String durationCondition = getDurationCondition();
        String sizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                //  Gets the all
                return getPageSelectionArgsForAllMediaCondition(bucketId, queryMimeCondition, durationCondition, sizeCondition);
            case SelectMimeType.TYPE_IMAGE:
                // Gets the image of the specified type
                return getPageSelectionArgsForImageMediaCondition(bucketId, queryMimeCondition, sizeCondition);
            case SelectMimeType.TYPE_VIDEO:
                //  Gets the video or video
                return getPageSelectionArgsForVideoMediaCondition(bucketId, queryMimeCondition, durationCondition, sizeCondition);
            case SelectMimeType.TYPE_AUDIO:
                //  Gets the video or audio
                return getPageSelectionArgsForAudioMediaCondition(bucketId, queryMimeCondition, durationCondition, sizeCondition);
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

    private static String getPageSelectionArgsForVideoMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).append(") AND ");
        if (bucketId == -1) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForAudioMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
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
            case SelectMimeType.TYPE_ALL:
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
            case SelectMimeType.TYPE_IMAGE:
                // Get photo
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, bucketId);
            case SelectMimeType.TYPE_VIDEO:
                // Get video
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, bucketId);
            case SelectMimeType.TYPE_AUDIO:
                // Get audio
                return getSelectionArgsForPageSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO, bucketId);
        }
        return null;
    }


    private String getSelection() {
        String durationCondition = getDurationCondition();
        String fileSizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(durationCondition, fileSizeCondition, queryMimeCondition);
            case SelectMimeType.TYPE_IMAGE:
                // Get Images
                return getSelectionArgsForImageMediaCondition(fileSizeCondition, queryMimeCondition);
            case SelectMimeType.TYPE_VIDEO:
                // Access to video
                return getSelectionArgsForVideoMediaCondition(durationCondition, queryMimeCondition);
            case SelectMimeType.TYPE_AUDIO:
                // Access to the audio
                return getSelectionArgsForAudioMediaCondition(durationCondition, queryMimeCondition);
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (config.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                // Get all
                return getSelectionArgsForAllMediaType();
            case SelectMimeType.TYPE_IMAGE:
                // Get photo
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case SelectMimeType.TYPE_VIDEO:
                // Get video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case SelectMimeType.TYPE_AUDIO:
                // Get audio
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
    }
}
