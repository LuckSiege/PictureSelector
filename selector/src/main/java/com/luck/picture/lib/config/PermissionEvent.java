package com.luck.picture.lib.config;

/**
 * @author：luck
 * @date：2022/3/25 1:41 下午
 * @describe：PermissionEvent
 */
public class PermissionEvent {
    public static final int EVENT_SOURCE_DATA = -1;
    public static final int EVENT_SYSTEM_SOURCE_DATA = -2;
    public static final int EVENT_IMAGE_CAMERA = SelectMimeType.ofImage();
    public static final int EVENT_VIDEO_CAMERA = SelectMimeType.ofVideo();
}
