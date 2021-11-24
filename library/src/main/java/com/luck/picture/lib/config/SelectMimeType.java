package com.luck.picture.lib.config;

/**
 * @author：luck
 * @date：2021/11/23 6:53 下午
 * @describe：SelectMimeType
 */
public class SelectMimeType {

    /**
     * GET image or video only
     * <p>
     * excluding Audio
     * </p>
     */
    public static int ofAll() {
        return PictureConfig.TYPE_ALL;
    }

    /**
     * GET image only
     */
    public static int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * GET video only
     */
    public static int ofVideo() {
        return PictureConfig.TYPE_VIDEO;
    }

    /**
     * GET audio only
     * <p>
     * # No longer maintain audio related functions,
     * but can continue to use but there will be phone compatibility issues
     * <p>
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @Deprecated
    public static int ofAudio() {
        return PictureConfig.TYPE_AUDIO;
    }

}
