package com.luck.lib.camerax;

import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener;

/**
 * @author：luck
 * @date：2021/11/29 7:14 下午
 * @describe：CustomCameraConfig
 */
public final class CustomCameraConfig {
    /**
     * 两者都可以
     */
    public static final int BUTTON_STATE_BOTH = 0;

    /**
     * 只能拍照
     */
    public static final int BUTTON_STATE_ONLY_CAPTURE = 1;

    /**
     * 只能录像
     */
    public static final int BUTTON_STATE_ONLY_RECORDER = 2;


    /**
     * 默认最大录制时间
     */
    public static final int DEFAULT_MAX_RECORD_VIDEO = 60 * 1000 + 500;

    /**
     * 默认最小录制时间
     */
    public static final int DEFAULT_MIN_RECORD_VIDEO = 1500;


    public static final String SP_NAME = "PictureSpUtils";

    /**
     * 图片加载引擎
     */
    public static CameraImageEngine imageEngine;

    /**
     * 自定义权限说明
     */
    public static OnSimpleXPermissionDescriptionListener explainListener;

    /**
     * 权限拒绝回调
     */
    public static OnSimpleXPermissionDeniedListener deniedListener;

    /**
     * 释放监听器
     */
    public static void destroy() {
        CustomCameraConfig.imageEngine = null;
        CustomCameraConfig.explainListener = null;
        CustomCameraConfig.deniedListener = null;
    }
}
