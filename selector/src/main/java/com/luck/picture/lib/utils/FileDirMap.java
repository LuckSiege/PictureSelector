package com.luck.picture.lib.utils;

import android.content.Context;
import android.os.Environment;

import com.luck.picture.lib.config.SelectMimeType;

import java.io.File;
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
            String path;
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalFilesDir != null && externalFilesDir.exists()) {
                path = externalFilesDir.getPath();
            } else {
                path = context.getCacheDir().getPath();
            }
            dirMap.put(SelectMimeType.TYPE_IMAGE, path);
        }
        if (null == dirMap.get(SelectMimeType.TYPE_VIDEO)) {
            String path;
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (externalFilesDir != null && externalFilesDir.exists()) {
                path = externalFilesDir.getPath();
            } else {
                path = context.getCacheDir().getPath();
            }
            dirMap.put(SelectMimeType.TYPE_VIDEO, path);
        }
        if (null == dirMap.get(SelectMimeType.TYPE_AUDIO)) {
            String path;
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (externalFilesDir != null && externalFilesDir.exists()) {
                path = externalFilesDir.getPath();
            } else {
                path = context.getCacheDir().getPath();
            }
            dirMap.put(SelectMimeType.TYPE_AUDIO, path);
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
