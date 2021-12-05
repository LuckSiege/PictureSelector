package com.luck.picture.lib.utils;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;

import java.util.Collections;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/11 6:11 下午
 * @describe：排序类
 */
public class SortUtils {
    /**
     * Sort by the number of files
     *
     * @param imageFolders
     */
    public static void sortFolder(List<LocalMediaFolder> imageFolders) {
        Collections.sort(imageFolders, (lhs, rhs) -> {
            if (lhs.getData() == null || rhs.getData() == null) {
                return 0;
            }
            int lSize = lhs.getFolderTotalNum();
            int rSize = rhs.getFolderTotalNum();
            return Integer.compare(rSize, lSize);
        });
    }


    /**
     * Sort by the add Time of files
     *
     * @param list
     */
    public static void sortLocalMediaAddedTime(List<LocalMedia> list) {
        Collections.sort(list, (lhs, rhs) -> {
            long lAddedTime = lhs.getDateAddedTime();
            long rAddedTime = rhs.getDateAddedTime();
            return Long.compare(rAddedTime, lAddedTime);
        });
    }
}
