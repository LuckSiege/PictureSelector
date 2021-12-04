package com.luck.picture.lib.manager;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/20 8:57 下午
 * @describe：SelectedManager
 */
public class SelectedManager {
    public static final int INVALID = -1;
    public static final int ADD_SUCCESS = 0;
    public static final int REMOVE = 1;


    /**
     * selected result
     */
    private static final List<LocalMedia> selectedResult = new ArrayList<>();

    public static void addResult(LocalMedia media) {
        selectedResult.add(media);
    }

    public static List<LocalMedia> getSelectedResult() {
        return selectedResult;
    }

    public static int getCount() {
        return selectedResult.size();
    }

    public static String getTopResultMimeType() {
        return selectedResult.size() > 0 ? selectedResult.get(0).getMimeType() : "";
    }

    public static void removeResult(LocalMedia media) {
        selectedResult.remove(media);
    }

    public static void clear() {
        selectedResult.clear();
    }



    /**
     * selected external preview result
     */
    private static final List<LocalMedia> selectedPreviewResult = new ArrayList<>();

    public static List<LocalMedia> getSelectedPreviewResult() {
        return selectedPreviewResult;
    }

    public static void clearExternalPreviewData() {
        selectedPreviewResult.clear();
    }
}
