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
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ValueOf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author：luck
 * @data：2016/12/31 19:12
 * @describe: Local media database query class
 */
@Deprecated
public final class LocalMediaLoader {
    private static final String TAG = LocalMediaLoader.class.getSimpleName();
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF_UNKNOWN = "!='image/*'";
    private static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN + ")";

    private final Context mContext;
    private final boolean isAndroidQ;
    private final PictureSelectionConfig config;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    /**
     * Media file database field
     */
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED};

    /**
     * Video or Audio mode conditions
     *
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForVideoOrAudioMediaCondition(String sizeCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + sizeCondition;
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, String sizeCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? AND ").append(timeCondition).append(") AND ").append(sizeCondition).toString();
        return stringBuilder.toString();
    }

    /**
     * Query conditions in image modes
     *
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForImageMediaCondition(String sizeCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + sizeCondition;
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


    public LocalMediaLoader(Context context) {
        this.mContext = context.getApplicationContext();
        this.isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        this.config = PictureSelectionConfig.getInstance();
    }

    /**
     * Query the local gallery data
     *
     * @return
     */
    public List<LocalMediaFolder> loadAllMedia() {
        Cursor data = mContext.getContentResolver().query(QUERY_URI, PROJECTION, getSelection(), getSelectionArgs(), ORDER_BY);
        try {
            if (data != null) {
                List<LocalMediaFolder> imageFolders = new ArrayList<>();
                LocalMediaFolder allImageFolder = new LocalMediaFolder();
                List<LocalMedia> latelyImages = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
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

                    data.moveToFirst();
                    do {
                        long id = data.getLong(idColumn);
                        String mimeType = data.getString(mimeTypeColumn);
                        mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                        String absolutePath = data.getString(dataColumn);
                        String url = isAndroidQ ? PictureMimeType.getRealPathUri(id,mimeType) : absolutePath;
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
                        long bucketId = data.getLong(bucketIdColumn);
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
                                (id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucketId, data.getLong(dateAddedColumn));
                        LocalMediaFolder folder = getImageFolder(url,mimeType, folderName, imageFolders);
                        folder.setBucketId(image.getBucketId());
                        List<LocalMedia> images = folder.getData();
                        images.add(image);
                        folder.setImageNum(folder.getImageNum() + 1);
                        folder.setBucketId(image.getBucketId());
                        latelyImages.add(image);
                        int imageNum = allImageFolder.getImageNum();
                        allImageFolder.setImageNum(imageNum + 1);

                    } while (data.moveToNext());

                    if (latelyImages.size() > 0) {
                        sortFolder(imageFolders);
                        imageFolders.add(0, allImageFolder);
                        allImageFolder.setFirstImagePath
                                (latelyImages.get(0).getPath());
                        allImageFolder.setFirstMimeType(latelyImages.get(0).getMimeType());
                        String title = config.chooseMode == PictureMimeType.ofAudio() ?
                                mContext.getString(R.string.picture_all_audio)
                                : mContext.getString(R.string.picture_camera_roll);
                        allImageFolder.setName(title);
                        allImageFolder.setBucketId(-1);
                        allImageFolder.setOfAllType(config.chooseMode);
                        allImageFolder.setCameraFolder(true);
                        allImageFolder.setData(latelyImages);
                    }
                }
                return imageFolders;
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
        return null;
    }

    private String getSelection() {
        String durationCondition = getDurationCondition();
        String fileSizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(durationCondition, fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_IMAGE:
                // Gets the image
                return getSelectionArgsForImageMediaCondition(fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_VIDEO:
                // Access to video
                return getSelectionArgsForVideoOrAudioMediaCondition(fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_AUDIO:
                // Access to the audio
                return getSelectionArgsForVideoOrAudioMediaCondition(durationCondition, queryMimeCondition);
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get All
                return getSelectionArgsForAllMediaType();
            case PictureConfig.TYPE_IMAGE:
                // Get Image
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // Get Video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
                // Get Audio
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
    }

    /**
     * Sort by the number of files
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
     * Create folder
     *
     * @param firstPath
     * @param firstMimeType
     * @param imageFolders
     * @param folderName
     * @return
     */
    private LocalMediaFolder getImageFolder(String firstPath,String firstMimeType, String folderName, List<LocalMediaFolder> imageFolders) {
        if (!config.isFallbackVersion) {
            for (LocalMediaFolder folder : imageFolders) {
                // Under the same folder, return yourself, otherwise create a new folder
                String name = folder.getName();
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                if (name.equals(folderName)) {
                    return folder;
                }
            }
            LocalMediaFolder newFolder = new LocalMediaFolder();
            newFolder.setName(folderName);
            newFolder.setFirstImagePath(firstPath);
            newFolder.setFirstMimeType(firstMimeType);
            imageFolders.add(newFolder);
            return newFolder;
        } else {
            // Fault-tolerant processing
            File imageFile = new File(firstPath);
            File folderFile = imageFile.getParentFile();
            for (LocalMediaFolder folder : imageFolders) {
                // Under the same folder, return yourself, otherwise create a new folder
                String name = folder.getName();
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                if (folderFile != null && name.equals(folderFile.getName())) {
                    return folder;
                }
            }
            LocalMediaFolder newFolder = new LocalMediaFolder();
            newFolder.setName(folderFile != null ? folderFile.getName() : "");
            newFolder.setFirstImagePath(firstPath);
            newFolder.setFirstMimeType(firstMimeType);
            imageFolders.add(newFolder);
            return newFolder;
        }
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

}
