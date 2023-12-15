package com.luck.picture.lib.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorConfig;
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


    public LocalMediaPageLoader(Context context, SelectorConfig config) {
        super(context, config);
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @param queryMimeTypeOptions
     * @return
     */
    private String getSelectionArgsForAllMediaCondition(String timeCondition,
                                                        String sizeCondition,
                                                        String queryImageMimeType,
                                                        String queryVideoMimeType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("(")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryImageMimeType)
                .append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryVideoMimeType)
                .append(" AND ")
                .append(timeCondition)
                .append(")")
                .append(" AND ")
                .append(sizeCondition);
        if (isWithAllQuery()) {
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
    private String getSelectionArgsForImageMediaCondition(String fileSizeCondition, String queryMimeTypeOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isWithAllQuery()) {
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
    private String getSelectionArgsForVideoMediaCondition(String durationCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isWithAllQuery()) {
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
    private String getSelectionArgsForAudioMediaCondition(String durationCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isWithAllQuery()) {
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
        return bucketId == PictureConfig.ALL ? new String[]{String.valueOf(mediaType)} : new String[]{String.valueOf(mediaType), ValueOf.toString(bucketId)};
    }

    @Override
    public String getAlbumFirstCover(long bucketId) {
        Cursor data = null;
        try {
            if (SdkVersionUtils.isR()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), 1, 0, getSortOrder());
                data = getContext().getContentResolver().query(QUERY_URI, new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.MIME_TYPE,
                        MediaStore.MediaColumns.DATA}, queryArgs, null);
            } else {
                String orderBy = getSortOrder() + " limit 1 offset 0";
                data = getContext().getContentResolver().query(QUERY_URI, new String[]{
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


    @Override
    public void loadPageMediaData(long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaData>() {

            @Override
            public MediaData doInBackground() {
                Cursor data = null;
                try {
                    if (SdkVersionUtils.isR()) {
                        Bundle queryArgs = MediaUtils.createQueryArgsBundle(getPageSelection(bucketId), getPageSelectionArgs(bucketId), pageSize, (page - 1) * pageSize, getSortOrder());
                        data = getContext().getContentResolver().query(QUERY_URI, PROJECTION, queryArgs, null);
                    } else {
                        String orderBy = page == PictureConfig.ALL ? getSortOrder() : getSortOrder() + " limit " + pageSize + " offset " + (page - 1) * pageSize;
                        data = getContext().getContentResolver().query(QUERY_URI, PROJECTION, getPageSelection(bucketId), getPageSelectionArgs(bucketId), orderBy);
                    }
                    if (data != null) {
                        ArrayList<LocalMedia> result = new ArrayList<>();
                        if (data.getCount() > 0) {
                            data.moveToFirst();
                            do {
                                LocalMedia media = parseLocalMedia(data, false);
                                if (media == null) {
                                    continue;
                                }
                                result.add(media);

                            } while (data.moveToNext());
                        }
                        if (bucketId == PictureConfig.ALL && page == 1) {
                            List<LocalMedia> list = SandboxFileLoader.loadInAppSandboxFile(getContext(), getConfig().sandboxDir);
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
                return SandboxFileLoader.loadInAppSandboxFolderFile(getContext(), getConfig().sandboxDir);
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
                Cursor data = getContext().getContentResolver().query(QUERY_URI, isWithAllQuery() ? PROJECTION : ALL_PROJECTION,
                        getSelection(), getSelectionArgs(), getSortOrder());
                try {
                    if (data != null) {
                        int count = data.getCount();
                        int totalCount = 0;
                        List<LocalMediaFolder> mediaFolders = new ArrayList<>();
                        if (count > 0) {
                            if (isWithAllQuery()) {
                                Map<Long, Long> countMap = new HashMap<>();
                                Set<Long> hashSet = new HashSet<>();
                                while (data.moveToNext()) {
                                    if (getConfig().isPageSyncAsCount) {
                                        LocalMedia media = parseLocalMedia(data, true);
                                        if (media == null) {
                                            continue;
                                        }
                                        media.recycle();
                                    }
                                    long bucketId = data.getLong(data.getColumnIndexOrThrow(COLUMN_BUCKET_ID));
                                    Long newCount = countMap.get(bucketId);
                                    if (newCount == null) {
                                        newCount = 1L;
                                    } else {
                                        newCount++;
                                    }
                                    countMap.put(bucketId, newCount);

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
                                }
                                for (LocalMediaFolder mediaFolder : mediaFolders) {
                                    int size = ValueOf.toInt(countMap.get(mediaFolder.getBucketId()));
                                    mediaFolder.setFolderTotalNum(size);
                                    totalCount += size;
                                }
                            } else {
                                data.moveToFirst();
                                do {
                                    String url = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                                    String bucketDisplayName = data.getString(data.getColumnIndexOrThrow(COLUMN_BUCKET_DISPLAY_NAME));
                                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                                    long bucketId = data.getLong(data.getColumnIndexOrThrow(COLUMN_BUCKET_ID));
                                    int size = data.getInt(data.getColumnIndexOrThrow(COLUMN_COUNT));
                                    LocalMediaFolder mediaFolder = new LocalMediaFolder();
                                    mediaFolder.setBucketId(bucketId);
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
                                    .loadInAppSandboxFolderFile(getContext(), getConfig().sandboxDir);
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
                                        String path = PictureFileUtils.getPath(getContext(), Uri.parse(allMediaFolder.getFirstImagePath()));
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
                            if (totalCount == 0) {
                                return mediaFolders;
                            }
                            SortUtils.sortFolder(mediaFolders);
                            allMediaFolder.setFolderTotalNum(totalCount);
                            allMediaFolder.setBucketId(PictureConfig.ALL);
                            String folderName;
                            if (TextUtils.isEmpty(getConfig().defaultAlbumName)) {
                                folderName = getConfig().chooseMode == SelectMimeType.ofAudio()
                                        ? getContext().getString(R.string.ps_all_audio) : getContext().getString(R.string.ps_camera_roll);
                            } else {
                                folderName = getConfig().defaultAlbumName;
                            }
                            allMediaFolder.setFolderName(folderName);
                            mediaFolders.add(0, allMediaFolder);
                            if (getConfig().isSyncCover) {
                                if (getConfig().chooseMode == SelectMimeType.ofAll()) {
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
                LocalMedia.destroyPool();
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
            String firstCover = getAlbumFirstCover(mediaFolder.getBucketId());
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
        switch (getConfig().chooseMode) {
            case SelectMimeType.TYPE_ALL:
                //  Gets the all
                return getPageSelectionArgsForAllMediaCondition(bucketId, getImageMimeTypeCondition(),getVideoMimeTypeCondition(), durationCondition, sizeCondition);
            case SelectMimeType.TYPE_IMAGE:
                // Gets the image of the specified type
                return getPageSelectionArgsForImageMediaCondition(bucketId, getImageMimeTypeCondition(), sizeCondition);
            case SelectMimeType.TYPE_VIDEO:
                //  Gets the video or video
                return getPageSelectionArgsForVideoMediaCondition(bucketId, getVideoMimeTypeCondition(), durationCondition, sizeCondition);
            case SelectMimeType.TYPE_AUDIO:
                //  Gets the video or audio
                return getPageSelectionArgsForAudioMediaCondition(bucketId, getAudioMimeTypeCondition(), durationCondition, sizeCondition);
        }
        return null;
    }

    private static String getPageSelectionArgsForAllMediaCondition(long bucketId,
                                                                   String queryImageMimeType,
                                                                   String queryVideoMimeType,
                                                                   String durationCondition,
                                                                   String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryImageMimeType)
                .append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryVideoMimeType)
                .append(" AND ")
                .append(durationCondition)
                .append(")")
                .append(" AND ");
        if (bucketId == PictureConfig.ALL) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForImageMediaCondition(long bucketId, String queryMimeCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?");
        if (bucketId == PictureConfig.ALL) {
            return stringBuilder.append(queryMimeCondition).append(") AND ").append(sizeCondition).toString();
        } else {
            return stringBuilder.append(queryMimeCondition).append(") AND ").append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForVideoMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).append(") AND ");
        if (bucketId == PictureConfig.ALL) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private static String getPageSelectionArgsForAudioMediaCondition(long bucketId, String queryMimeCondition, String durationCondition, String sizeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" AND ").append(durationCondition).append(") AND ");
        if (bucketId == PictureConfig.ALL) {
            return stringBuilder.append(sizeCondition).toString();
        } else {
            return stringBuilder.append(COLUMN_BUCKET_ID).append("=? AND ").append(sizeCondition).toString();
        }
    }

    private String[] getPageSelectionArgs(long bucketId) {
        switch (getConfig().chooseMode) {
            case SelectMimeType.TYPE_ALL:
                if (bucketId == PictureConfig.ALL) {
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

    @Override
    protected String getSelection() {
        String durationCondition = getDurationCondition();
        String fileSizeCondition = getFileSizeCondition();
        switch (getConfig().chooseMode) {
            case SelectMimeType.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(durationCondition, fileSizeCondition,
                        getImageMimeTypeCondition(), getVideoMimeTypeCondition());
            case SelectMimeType.TYPE_IMAGE:
                // Get Images
                return getSelectionArgsForImageMediaCondition(fileSizeCondition, getImageMimeTypeCondition());
            case SelectMimeType.TYPE_VIDEO:
                // Access to video
                return getSelectionArgsForVideoMediaCondition(durationCondition, getVideoMimeTypeCondition());
            case SelectMimeType.TYPE_AUDIO:
                // Access to the audio
                return getSelectionArgsForAudioMediaCondition(durationCondition, getAudioMimeTypeCondition());
        }
        return null;
    }

    @Override
    protected String[] getSelectionArgs() {
        switch (getConfig().chooseMode) {
            case SelectMimeType.TYPE_ALL:
                // Get all
                return new String[]{
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
            case SelectMimeType.TYPE_IMAGE:
                // Get photo
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
            case SelectMimeType.TYPE_VIDEO:
                // Get video
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
            case SelectMimeType.TYPE_AUDIO:
                // Get audio
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)};
        }
        return null;
    }

    @Override
    protected String getSortOrder() {
        return TextUtils.isEmpty(getConfig().sortOrder) ? ORDER_BY : getConfig().sortOrder;
    }

    /**
     * 查询方式
     */
    private boolean isWithAllQuery() {
        if (SdkVersionUtils.isQ()) {
            return true;
        } else {
            return getConfig().isPageSyncAsCount;
        }
    }

    @Override
    protected LocalMedia parseLocalMedia(Cursor data, boolean isUsePool) {
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
        long id = data.getLong(idColumn);
        String mimeType = data.getString(mimeTypeColumn);
        String absolutePath = data.getString(dataColumn);
        String url = SdkVersionUtils.isQ() ? MediaUtils.getRealPathUri(id, mimeType) : absolutePath;
        mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
        if (getConfig().isFilterInvalidFile) {
            if (PictureMimeType.isHasImage(mimeType)) {
                if (!TextUtils.isEmpty(absolutePath) && !PictureFileUtils.isImageFileExists(absolutePath)) {
                    return null;
                }
            } else {
                if (!PictureFileUtils.isFileExists(absolutePath)) {
                    return null;
                }
            }
        }
        // Here, it is solved that some models obtain mimeType and return the format of image / *,
        // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
        if (mimeType.endsWith("image/*")) {
            mimeType = MediaUtils.getMimeTypeFromMediaUrl(absolutePath);
            if (!getConfig().isGif) {
                if (PictureMimeType.isHasGif(mimeType)) {
                    return null;
                }
            }
        }
        if (mimeType.endsWith("image/*")) {
            return null;
        }
        if (!getConfig().isWebp) {
            if (mimeType.startsWith(PictureMimeType.ofWEBP())) {
                return null;
            }
        }
        if (!getConfig().isBmp) {
            if (PictureMimeType.isHasBmp(mimeType)) {
                return null;
            }
        }
        if (!getConfig().isHeic) {
            if (PictureMimeType.isHasHeic(mimeType)) {
                return null;
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
        long bucketId = data.getLong(bucketIdColumn);
        long dateAdded = data.getLong(dateAddedColumn);
        if (TextUtils.isEmpty(fileName)) {
            fileName = PictureMimeType.getUrlToFileName(absolutePath);
        }
        if (getConfig().isFilterSizeDuration && size > 0 && size < FileSizeUnit.KB) {
            // Filter out files less than 1KB
            return null;
        }
        if (PictureMimeType.isHasVideo(mimeType) || PictureMimeType.isHasAudio(mimeType)) {
            if (getConfig().filterVideoMinSecond > 0 && duration < getConfig().filterVideoMinSecond) {
                // If you set the minimum number of seconds of video to display
                return null;
            }
            if (getConfig().filterVideoMaxSecond > 0 && duration > getConfig().filterVideoMaxSecond) {
                // If you set the maximum number of seconds of video to display
                return null;
            }
            if (getConfig().isFilterSizeDuration && duration <= 0) {
                //If the length is 0, the corrupted video is processed and filtered out
                return null;
            }
        }
        LocalMedia media = isUsePool ? LocalMedia.obtain() : LocalMedia.create();
        media.setId(id);
        media.setBucketId(bucketId);
        media.setPath(url);
        media.setRealPath(absolutePath);
        media.setFileName(fileName);
        media.setParentFolderName(folderName);
        media.setDuration(duration);
        media.setChooseModel(getConfig().chooseMode);
        media.setMimeType(mimeType);
        media.setWidth(width);
        media.setHeight(height);
        media.setSize(size);
        media.setDateAddedTime(dateAdded);
        if (mConfig.onQueryFilterListener != null) {
            if (mConfig.onQueryFilterListener.onFilter(media)) {
                return null;
            }
        }
        return media;
    }
}
