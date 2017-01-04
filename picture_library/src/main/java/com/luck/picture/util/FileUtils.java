package com.luck.picture.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author：luck
 * project：FileUtils
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */

public class FileUtils {
    public static final String POSTFIX = ".JPEG";
    public static final String POST_VIDEO = ".mp4";
    public static final String APP_NAME = "ImageSelector";
    public static final String CAMERA_PATH = "/" + APP_NAME + "/CameraImage/";
    public static final String CROP_PATH = "/" + APP_NAME + "/CropImage/";

    public static File createCameraFile(Context context, int type) {
        return createMediaFile(context, CAMERA_PATH, type);
    }

    public static File createCropFile(Context context, int type) {
        return createMediaFile(context, CROP_PATH, type);
    }

    private static File createMediaFile(Context context, String parentPath, int type) {
        String state = Environment.getExternalStorageState();
        File rootDir = state.equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory() : context.getCacheDir();

        File folderDir = new File(rootDir.getAbsolutePath() + parentPath);
        if (!folderDir.exists() && folderDir.mkdirs()) {

        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = APP_NAME + "_" + timeStamp + "";
        File tmpFile = null;
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                tmpFile = new File(folderDir, fileName + POSTFIX);
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                tmpFile = new File(folderDir, fileName + POST_VIDEO);
                break;
        }
        return tmpFile;
    }
}
