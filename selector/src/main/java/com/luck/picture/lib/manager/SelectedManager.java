package com.luck.picture.lib.manager;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/20 8:57 下午
 * @describe：SelectedManager
 */
public final class SelectedManager {
    public static final int INVALID = -1;
    public static final int ADD_SUCCESS = 0;
    public static final int REMOVE = 1;
    public static final int SUCCESS = 200;

    /**
     * selected result
     */
    private static final ArrayList<LocalMedia> selectedResult = new ArrayList<>();

    public static synchronized void addSelectResult(LocalMedia media) {
        selectedResult.add(media);
    }

    public static synchronized void addAllSelectResult(ArrayList<LocalMedia> result) {
        selectedResult.addAll(result);
    }

    public static synchronized ArrayList<LocalMedia> getSelectedResult() {
        return selectedResult;
    }

    public static int getSelectCount() {
        return selectedResult.size();
    }

    public static String getTopResultMimeType() {
        return selectedResult.size() > 0 ? selectedResult.get(0).getMimeType() : "";
    }

    public static synchronized void clearSelectResult() {
        if (selectedResult.size() > 0) {
            selectedResult.clear();
        }
    }

    /**
     * selected preview result
     */
    private static final ArrayList<LocalMedia> selectedPreviewResult = new ArrayList<>();

    public static ArrayList<LocalMedia> getSelectedPreviewResult() {
        return selectedPreviewResult;
    }

    public static void addSelectedPreviewResult(ArrayList<LocalMedia> list) {
        clearPreviewData();
        selectedPreviewResult.addAll(list);
    }

    public static void clearPreviewData() {
        if (selectedPreviewResult.size() > 0) {
            selectedPreviewResult.clear();
        }
    }


    /**
     * all data source
     */
    private static final ArrayList<LocalMedia> dataSource = new ArrayList<>();

    public static ArrayList<LocalMedia> getDataSource() {
        return dataSource;
    }

    public static void addDataSource(ArrayList<LocalMedia> list) {
        if (list != null) {
            clearDataSource();
            dataSource.addAll(list);
        }
    }

    public static void clearDataSource() {
        if (dataSource.size() > 0) {
            dataSource.clear();
        }
    }

    /**
     * all album data source
     */
    private static final ArrayList<LocalMediaFolder> albumDataSource = new ArrayList<>();

    public static ArrayList<LocalMediaFolder> getAlbumDataSource() {
        return albumDataSource;
    }

    public static void addAlbumDataSource(List<LocalMediaFolder> list) {
        if (list != null) {
            clearAlbumDataSource();
            albumDataSource.addAll(list);
        }
    }

    public static void clearAlbumDataSource() {
        if (albumDataSource.size() > 0) {
            albumDataSource.clear();
        }
    }

    /**
     * current selected album
     */
    private static LocalMediaFolder currentLocalMediaFolder;

    public static void setCurrentLocalMediaFolder(LocalMediaFolder mediaFolder) {
        currentLocalMediaFolder = mediaFolder;
    }

    public static LocalMediaFolder getCurrentLocalMediaFolder() {
        return currentLocalMediaFolder;
    }
}
