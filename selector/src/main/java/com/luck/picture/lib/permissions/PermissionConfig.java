package com.luck.picture.lib.permissions;

import android.Manifest;
import android.content.Context;

import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.utils.SdkVersionUtils;

/**
 * @author：luck
 * @date：2021/12/11 8:24 下午
 * @describe：PermissionConfig
 */
public class PermissionConfig {

    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    /**
     * 当前申请权限
     */
    public static String[] CURRENT_REQUEST_PERMISSION = new String[]{};

    /**
     * 相机权限
     */
    public final static String[] CAMERA = new String[]{Manifest.permission.CAMERA};

    /**
     * 获取外部读取权限
     */
    public static String[] getReadPermissionArray(Context context, int chooseMode) {
        if (SdkVersionUtils.isTIRAMISU()) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (chooseMode == SelectMimeType.ofImage()) {
                return targetSdkVersion >= SdkVersionUtils.TIRAMISU
                        ? new String[]{READ_MEDIA_IMAGES}
                        : new String[]{READ_MEDIA_IMAGES, READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return targetSdkVersion >= SdkVersionUtils.TIRAMISU
                        ? new String[]{READ_MEDIA_VIDEO}
                        : new String[]{READ_MEDIA_VIDEO, READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return targetSdkVersion >= SdkVersionUtils.TIRAMISU
                        ? new String[]{READ_MEDIA_AUDIO}
                        : new String[]{READ_MEDIA_AUDIO, READ_EXTERNAL_STORAGE};
            } else {
                return targetSdkVersion >= SdkVersionUtils.TIRAMISU
                        ? new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO}
                        : new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_EXTERNAL_STORAGE};
            }
        }
        return new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    }

}
