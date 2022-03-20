package com.luck.picture.lib.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.SortUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2016/12/31 19:12
 * @describe: Local media database query class
 */
public final class LocalMediaLoader extends IBridgeMediaLoader {

    /**
     * Video mode conditions
     *
     * @param durationCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForVideoMediaCondition(String durationCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + durationCondition;
    }

    /**
     * Audio mode conditions
     *
     * @param durationCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForAudioMediaCondition(String durationCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + durationCondition;
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition,
                                                               String sizeCondition,
                                                               String queryMimeCondition) {
        return "(" +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
                queryMimeCondition + " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " +
                timeCondition + ") AND " +
                sizeCondition;
    }

    /**
     * Query conditions in image modes
     *
     * @param fileSizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForImageMediaCondition(String fileSizeCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + fileSizeCondition;
    }

    public LocalMediaLoader(Context context, PictureSelectionConfig config) {
        this.mContext = context;
        this.config = config;
    }

    @Override
    public void loadOnlyInAppDirAllMedia(OnQueryAlbumListener<LocalMediaFolder> listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<LocalMediaFolder>() {

            @Override
            public LocalMediaFolder doInBackground() {
                return SandboxFileLoader.loadInAppSandboxFolderFile(mContext, config.sandboxDir);
            }

            @Override
            public void onSuccess(LocalMediaFolder result) {
                PictureThreadUtils.cancel(this);
                if (listener != null) {
                    listener.onComplete(result);
                }
            }
        });
    }

    @Override
    public void loadAllAlbum(OnQueryAllAlbumListener<LocalMediaFolder> query) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {

            @Override
            public List<LocalMediaFolder> doInBackground() {
                List<LocalMediaFolder> imageFolders = new ArrayList<>();
                Cursor data = mContext.getContentResolver().query(QUERY_URI, PROJECTION,
                        getSelection(), getSelectionArgs(), getSortOrder());
                try {
                    if (data != null) {
                        LocalMediaFolder allImageFolder = new LocalMediaFolder();
                        ArrayList<LocalMedia> latelyImages = new ArrayList<>();
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
                            int orientationColumn = data.getColumnIndexOrThrow(PROJECTION[11]);

                            data.moveToFirst();
                            do {
                                long id = data.getLong(idColumn);
                                String mimeType = data.getString(mimeTypeColumn);
                                mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                                String absolutePath = data.getString(dataColumn);
                                String url = SdkVersionUtils.isQ() ? MediaUtils.getRealPathUri(id, mimeType) : absolutePath;
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
                                long bucketId = data.getLong(bucketIdColumn);

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
                                LocalMedia media = LocalMedia.parseLocalMedia(id, url, absolutePath, fileName, folderName, duration, config.chooseMode, mimeType, width, height, size, bucketId, data.getLong(dateAddedColumn));
                                LocalMediaFolder folder = getImageFolder(url, mimeType, folderName, imageFolders);
                                folder.setBucketId(media.getBucketId());
                                folder.getData().add(media);
                                folder.setFolderTotalNum(folder.getFolderTotalNum() + 1);
                                folder.setBucketId(media.getBucketId());
                                latelyImages.add(media);
                                int imageNum = allImageFolder.getFolderTotalNum();
                                allImageFolder.setFolderTotalNum(imageNum + 1);

                            } while (data.moveToNext());

                            LocalMediaFolder selfFolder = SandboxFileLoader
                                    .loadInAppSandboxFolderFile(mContext, config.sandboxDir);
                            if (selfFolder != null) {
                                imageFolders.add(selfFolder);
                                allImageFolder.setFolderTotalNum(allImageFolder.getFolderTotalNum() + selfFolder.getFolderTotalNum());
                                allImageFolder.setData(selfFolder.getData());
                                latelyImages.addAll(0, selfFolder.getData());
                                if (MAX_SORT_SIZE > selfFolder.getFolderTotalNum()) {
                                    if (latelyImages.size() > MAX_SORT_SIZE) {
                                        SortUtils.sortLocalMediaAddedTime(latelyImages.subList(0, MAX_SORT_SIZE));
                                    } else {
                                        SortUtils.sortLocalMediaAddedTime(latelyImages);
                                    }
                                }
                            }

                            if (latelyImages.size() > 0) {
                                SortUtils.sortFolder(imageFolders);
                                imageFolders.add(0, allImageFolder);
                                allImageFolder.setFirstImagePath
                                        (latelyImages.get(0).getPath());
                                allImageFolder.setFirstMimeType(latelyImages.get(0).getMimeType());
                                String title = config.chooseMode == SelectMimeType.ofAudio() ?
                                        mContext.getString(R.string.ps_all_audio)
                                        : mContext.getString(R.string.ps_camera_roll);
                                allImageFolder.setFolderName(title);
                                allImageFolder.setBucketId(PictureConfig.ALL);
                                allImageFolder.setData(latelyImages);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (data != null && !data.isClosed()) {
                        data.close();
                    }
                }
                return imageFolders;
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

    private String getSelection() {
        String durationCondition = getDurationCondition();
        String fileSizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (config.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(durationCondition, fileSizeCondition, queryMimeCondition);
            case SelectMimeType.TYPE_IMAGE:
                // Gets the image
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
                // Get All
                return getSelectionArgsForAllMediaType();
            case SelectMimeType.TYPE_IMAGE:
                // Get Image
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case SelectMimeType.TYPE_VIDEO:
                // Get Video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case SelectMimeType.TYPE_AUDIO:
                // Get Audio
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
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
    private LocalMediaFolder getImageFolder(String firstPath, String firstMimeType, String folderName, List<LocalMediaFolder> imageFolders) {
        for (LocalMediaFolder folder : imageFolders) {
            // Under the same folder, return yourself, otherwise create a new folder
            String name = folder.getFolderName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            if (TextUtils.equals(name, folderName)) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setFolderName(folderName);
        newFolder.setFirstImagePath(firstPath);
        newFolder.setFirstMimeType(firstMimeType);
        imageFolders.add(newFolder);
        return newFolder;
    }
}
