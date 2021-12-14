package com.luck.picture.lib.camera;

/**
 * @author：luck
 * @date：2021/11/8 11:14 上午
 * @describe：自定义相机的拍照模式
 */
public class CustomCameraType {
    /**
     * 只能拍照
     */
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    /**
     * 只能录像
     */
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    /**
     * 两者都可以
     */
    public static final int BUTTON_STATE_BOTH = 0x103;
}
