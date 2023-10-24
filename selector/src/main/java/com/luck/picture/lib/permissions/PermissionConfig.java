package com.luck.picture.lib.permissions;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.utils.SdkVersionUtils;

/**
 * @author：luck
 * @date：2021/12/11 8:24 下午
 * @describe：PermissionConfig
 */
public class PermissionConfig {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final String READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final String READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final String READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO;
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public static final String READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
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
        if (SdkVersionUtils.isUPSIDE_DOWN_CAKE()){
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (chooseMode == SelectMimeType.ofImage()) {
                if (targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    return new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_IMAGES};
                }else if (targetSdkVersion == Build.VERSION_CODES.TIRAMISU){
                    return new String[]{READ_MEDIA_IMAGES};
                }else {
                    return new String[]{READ_EXTERNAL_STORAGE};
                }
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                if (targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    return new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_VIDEO};
                }else if (targetSdkVersion == Build.VERSION_CODES.TIRAMISU){
                    return new String[]{READ_MEDIA_VIDEO};
                }else {
                    return new String[]{READ_EXTERNAL_STORAGE};
                }
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return targetSdkVersion >= Build.VERSION_CODES.TIRAMISU
                        ? new String[]{READ_MEDIA_AUDIO}
                        : new String[]{READ_EXTERNAL_STORAGE};
            } else {
                if (targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                    return new String[]{READ_MEDIA_VISUAL_USER_SELECTED,READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
                }else if (targetSdkVersion == Build.VERSION_CODES.TIRAMISU){
                    return new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
                }else {
                    return new String[]{READ_EXTERNAL_STORAGE};
                }
            }
        }else if (SdkVersionUtils.isTIRAMISU()) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (chooseMode == SelectMimeType.ofImage()) {
                return targetSdkVersion >= Build.VERSION_CODES.TIRAMISU
                        ? new String[]{READ_MEDIA_IMAGES}
                        : new String[]{READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return targetSdkVersion >= Build.VERSION_CODES.TIRAMISU
                        ? new String[]{READ_MEDIA_VIDEO}
                        : new String[]{READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return targetSdkVersion >= Build.VERSION_CODES.TIRAMISU
                        ? new String[]{READ_MEDIA_AUDIO}
                        : new String[]{READ_EXTERNAL_STORAGE};
            } else {
                return targetSdkVersion >= Build.VERSION_CODES.TIRAMISU
                        ? new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO}
                        : new String[]{READ_EXTERNAL_STORAGE};
            }
        }
        return new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    }

}
