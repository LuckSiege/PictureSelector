package com.luck.lib.camerax;

/**
 * @author：luck
 * @date：2021/11/29 7:14 下午
 * @describe：CustomCameraConfig
 */
public class CustomCameraConfig {
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
     * 默认最小录制时间
     */
    public static final int DEFAULT_MIN_RECORD_VIDEO = 1500;


    /**
     * 图片加载引擎
     */
    private static ImageEngine imageEngine;

    public static ImageEngine getImageEngine() {
        return imageEngine;
    }

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     * @return
     */
    public static void imageEngine(ImageEngine engine) {
        if (CustomCameraConfig.imageEngine != engine) {
            CustomCameraConfig.imageEngine = engine;
        }
    }

    /**
     * 释放监听器
     */
    public static void destroy() {
        CustomCameraConfig.imageEngine = null;
    }
}
