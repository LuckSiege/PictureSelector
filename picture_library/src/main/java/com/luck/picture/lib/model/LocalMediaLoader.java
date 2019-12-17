package com.luck.picture.lib.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

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
import java.util.List;
import java.util.Locale;

/**
 * @author：luck
 * @data：2016/12/31 19:12
 * @描述: Local media database query class
 */

public class LocalMediaLoader implements Handler.Callback {
    private static final int MSG_QUERY_MEDIA_SUCCESS = 0;
    private static final int MSG_QUERY_MEDIA_ERROR = -1;
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF = "!='image/gif'";
    /**
     * 过滤掉小于500毫秒的录音
     */
    private static final int AUDIO_DURATION = 500;
    private Context mContext;
    private boolean isAndroidQ;
    private PictureSelectionConfig config;
    private Handler mHandler;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    /**
     * 媒体文件数据库字段
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
            MediaStore.MediaColumns.DISPLAY_NAME};

    /**
     * 图片
     */
    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;
    /**
     * 查询指定后缀名的图片
     */
    private static final String SELECTION_SPECIFIED_FORMAT = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * 查询条件(音视频)
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
     * 查询(视频)
     *
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition() {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    }

    /**
     * 全部模式下条件
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


    public LocalMediaLoader(Context context, PictureSelectionConfig config) {
        this.mContext = context.getApplicationContext();
        this.isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        this.config = config;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
    }


    public void loadAllMedia() {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
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

                            String path = isAndroidQ ? getRealPathAndroid_Q(id) : data.getString
                                    (data.getColumnIndexOrThrow(PROJECTION[1]));

                            String mimeType = data.getString
                                    (data.getColumnIndexOrThrow(PROJECTION[2]));

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

                            if (config.filterFileSize > 0) {
                                if (size > config.filterFileSize * FILE_SIZE_UNIT) {
                                    continue;
                                }
                            }
                            if (PictureMimeType.eqVideo(mimeType)) {

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
                                    (id, path, fileName, duration, config.chooseMode, mimeType, width, height, size);
                            LocalMediaFolder folder = getImageFolder(path, folderName, imageFolders);
                            List<LocalMedia> images = folder.getImages();
                            images.add(image);
                            folder.setImageNum(folder.getImageNum() + 1);
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
                            allImageFolder.setOfAllType(config.chooseMode);
                            allImageFolder.setCameraFolder(true);
                            allImageFolder.setImages(latelyImages);
                        }
                    }
                    // 线程切换
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_QUERY_MEDIA_SUCCESS, imageFolders));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_QUERY_MEDIA_ERROR));
                }
            } catch (Exception e) {
                if (mHandler != null) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_QUERY_MEDIA_ERROR));
                }
                e.printStackTrace();
            }
        });
    }

    private String getSelection() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
                // 获取全部，不包括音频
                return getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), config.isGif);
            case PictureConfig.TYPE_IMAGE:
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定类型的图片
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return config.isGif ? SELECTION : SELECTION_NOT_GIF;
            case PictureConfig.TYPE_VIDEO:
                // 获取视频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定类型的图片
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return getSelectionArgsForSingleMediaCondition();
            case PictureConfig.TYPE_AUDIO:
                // 获取音频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定类型的图片
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
                // 只获取图片
                String[] MEDIA_TYPE_IMAGE = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                return MEDIA_TYPE_IMAGE;
            case PictureConfig.TYPE_VIDEO:
                // 只获取视频
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
                String[] MEDIA_TYPE_AUDIO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
                return MEDIA_TYPE_AUDIO;
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
            if (lhs.getImages() == null || rhs.getImages() == null) {
                return 0;
            }
            int lsize = lhs.getImageNum();
            int rsize = rhs.getImageNum();
            return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
        });
    }

    /**
     * 适配Android Q
     *
     * @param id
     * @return
     */
    private String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(ValueOf.toString(id)).build().toString();
    }

    /**
     * 创建相应文件夹
     *
     * @param path
     * @param imageFolders
     * @param folderName
     * @return
     */
    private LocalMediaFolder getImageFolder(String path, String folderName, List<LocalMediaFolder> imageFolders) {
        if (!config.isFallbackVersion) {
            for (LocalMediaFolder folder : imageFolders) {
                // 同一个文件夹下，返回自己，否则创建新文件夹
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
            // 容错处理
            File imageFile = new File(path);
            File folderFile = imageFile.getParentFile();
            for (LocalMediaFolder folder : imageFolders) {
                // 同一个文件夹下，返回自己，否则创建新文件夹
                String name = folder.getName();
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                if (name.equals(folderFile.getName())) {
                    return folder;
                }
            }
            LocalMediaFolder newFolder = new LocalMediaFolder();
            newFolder.setName(folderFile.getName());
            newFolder.setFirstImagePath(path);
            imageFolders.add(newFolder);
            return newFolder;
        }
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

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (mCompleteListener == null) return false;
        switch (msg.what) {
            case MSG_QUERY_MEDIA_SUCCESS:
                mCompleteListener.loadComplete((List<LocalMediaFolder>) msg.obj);
                break;
            case MSG_QUERY_MEDIA_ERROR:
                mCompleteListener.loadMediaDataError();
                break;
        }
        return false;
    }

    private LocalMediaLoadListener mCompleteListener;

    public void setCompleteListener(LocalMediaLoadListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public interface LocalMediaLoadListener {

        void loadComplete(List<LocalMediaFolder> folders);

        void loadMediaDataError();
    }
}
