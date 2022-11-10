package com.luck.picture.lib.permissions;

import android.Manifest;

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

    /**
     * 当前申请权限
     */
    public static String[] CURRENT_REQUEST_PERMISSION = new String[]{};

    /**
     * 相机权限
     */
    public final static String[] CAMERA = new String[]{Manifest.permission.CAMERA};

    /**
     * 获取外部读写权限
     */
    public static String[] getReadWritePermissionArray(int chooseMode) {
        if (SdkVersionUtils.isTIRAMISU()) {
            if (chooseMode == SelectMimeType.ofImage()) {
                return new String[]{READ_MEDIA_IMAGES};
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return new String[]{READ_MEDIA_VIDEO};
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return new String[]{READ_MEDIA_AUDIO};
            } else {
                return new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
            }
        }
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    /**
     * 获取外部写入权限
     */
    public static String[] getWritePermissionArray(int chooseMode) {
        if (SdkVersionUtils.isTIRAMISU()) {
            if (chooseMode == SelectMimeType.ofImage()) {
                return new String[]{READ_MEDIA_IMAGES};
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return new String[]{READ_MEDIA_VIDEO};
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return new String[]{READ_MEDIA_AUDIO};
            } else {
                return new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
            }
        }
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }
}
