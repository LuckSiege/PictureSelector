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
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ValueOf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final String NOT_GIF = "!='image/gif'";
    /**
     * Filter out recordings that are less than 500 milliseconds long
     */
    private static final int AUDIO_DURATION = 500;
    private Context mContext;
    private boolean isAndroidQ;
    private PictureSelectionConfig config;
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
            MediaStore.MediaColumns.BUCKET_ID};

    /**
     * Image
     */
    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;
    /**
     * Queries for images with the specified suffix
     */
    private static final String SELECTION_SPECIFIED_FORMAT = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * Query criteria (audio and video)
     *
     * @param time_condition
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    /**
     * Query (video)
     *
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition() {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    }

    /**
     * Query conditions in all modes
     *
     * @param time_condition
     * @param isGif
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
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


    public LocalMediaLoader(Context context, PictureSelectionConfig config) {
        this.mContext = context.getApplicationContext();
        this.isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        this.config = config;
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
                    data.moveToFirst();
                    do {
                        long id = data.getLong
                                (data.getColumnIndexOrThrow(PROJECTION[0]));

                        String absolutePath = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[1]));

                        String url = isAndroidQ ? getRealPathAndroid_Q(id) : absolutePath;

                        String mimeType = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[2]));

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
                                (data.getColumnIndexOrThrow(PROJECTION[3]));

                        int height = data.getInt
                                (data.getColumnIndexOrThrow(PROJECTION[4]));

                        long duration = data.getLong
                                (data.getColumnIndexOrThrow(PROJECTION[5]));

                        long size = data.getLong
                                (data.getColumnIndexOrThrow(PROJECTION[6]));

                        String folderName = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[7]));

                        String fileName = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[8]));

                        long bucketId = data.getLong(data.getColumnIndexOrThrow(PROJECTION[9]));

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
                                (id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucketId);
                        LocalMediaFolder folder = getImageFolder(url, folderName, imageFolders);
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
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), config.isGif);
            case PictureConfig.TYPE_IMAGE:
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // Gets the image of the specified type
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return config.isGif ? SELECTION : SELECTION_NOT_GIF;
            case PictureConfig.TYPE_VIDEO:
                // Access to video
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // Gets the image of the specified type
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return getSelectionArgsForSingleMediaCondition();
            case PictureConfig.TYPE_AUDIO:
                // Access to the audio
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // Gets the image of the specified type
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
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
                // Get Image
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // Get Video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
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
     * Android Q
     *
     * @param id
     * @return
     */
    private String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(ValueOf.toString(id)).build().toString();
    }

    /**
     * Create folder
     *
     * @param path
     * @param imageFolders
     * @param folderName
     * @return
     */
    private LocalMediaFolder getImageFolder(String path, String folderName, List<LocalMediaFolder> imageFolders) {
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
            newFolder.setFirstImagePath(path);
            imageFolders.add(newFolder);
            return newFolder;
        } else {
            // Fault-tolerant processing
            File imageFile = new File(path);
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
            newFolder.setFirstImagePath(path);
            imageFolders.add(newFolder);
            return newFolder;
        }
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

}
