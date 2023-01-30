package com.luck.picture.lib.utils;

import android.content.Context;
import android.os.Environment;

import com.luck.picture.lib.config.SelectMimeType;

import java.util.HashMap;

/**
 * @author：luck
 * @date：2022/9/20 7:57 下午
 * @describe：FileDirMap
 */
public final class FileDirMap {
    private static final HashMap<Integer, String> dirMap = new HashMap<>();

    public static void init(Context context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        if (null == dirMap.get(SelectMimeType.TYPE_IMAGE)) {
            dirMap.put(SelectMimeType.TYPE_IMAGE, context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
        }
        if (null == dirMap.get(SelectMimeType.TYPE_VIDEO)) {
            dirMap.put(SelectMimeType.TYPE_VIDEO, context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath());
        }
        if (null == dirMap.get(SelectMimeType.TYPE_AUDIO)) {
            dirMap.put(SelectMimeType.TYPE_AUDIO, context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath());
        }
    }

    public static String getFileDirPath(Context context, int type) {
        String dir = dirMap.get(type);
        if (null == dir) {
            init(context);
            dir = dirMap.get(type);
        }
        return dir;
    }

    public static void clear() {
        dirMap.clear();
    }
}
