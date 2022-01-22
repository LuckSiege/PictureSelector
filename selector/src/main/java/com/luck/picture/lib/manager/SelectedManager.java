package com.luck.picture.lib.manager;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/11/20 8:57 下午
 * @describe：SelectedManager
 */
public class SelectedManager {
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
     * selected external preview result
     */
    private static final ArrayList<LocalMedia> selectedPreviewResult = new ArrayList<>();

    public static ArrayList<LocalMedia> getSelectedPreviewResult() {
        return selectedPreviewResult;
    }

    public static void addSelectedPreviewResult(ArrayList<LocalMedia> list) {
        clearExternalPreviewData();
        selectedPreviewResult.addAll(list);
    }

    public static void clearExternalPreviewData() {
        if (selectedPreviewResult.size() > 0) {
            selectedPreviewResult.clear();
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
