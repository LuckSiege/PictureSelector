package com.luck.lib.camerax;

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
    public static final int DEFAULT_MAX_RECORD_VIDEO = 60 * 1000;

    /**
     * 默认最小录制时间
     */
    public static final int DEFAULT_MIN_RECORD_VIDEO = 1500;


    /**
     * 图片加载引擎
     */
    public static CameraImageEngine imageEngine;

    /**
     * 释放监听器
     */
    public static void destroy() {
        CustomCameraConfig.imageEngine = null;
    }
}
