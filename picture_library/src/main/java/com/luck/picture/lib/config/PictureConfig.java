package com.luck.picture.lib.config;

/**
 * @author：luck
 * @data：2017/5/24 下午1:00
 * @描述: 常量类
 */
public final class PictureConfig {
    public static final int APPLY_STORAGE_PERMISSIONS_CODE = 1;
    public static final int APPLY_CAMERA_PERMISSIONS_CODE = 2;
    public static final int APPLY_AUDIO_PERMISSIONS_CODE = 3;

    public final static String EXTRA_PREVIEW_DELETE_POSITION = "position";
    public final static String FC_TAG = "picture";
    public final static String EXTRA_RESULT_SELECTION = "extra_result_media";
    public final static String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public final static String EXTRA_SELECT_LIST = "selectList";
    public final static String EXTRA_POSITION = "position";
    public final static String DIRECTORY_PATH = "directory_path";
    public final static String BUNDLE_CAMERA_PATH = "CameraPath";
    public final static String BUNDLE_ORIGINAL_PATH = "OriginalPath";
    public final static String EXTRA_BOTTOM_PREVIEW = "bottom_preview";
    public final static String EXTRA_CONFIG = "PictureSelectorConfig";

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_AUDIO = 3;

    public static final int MAX_COMPRESS_SIZE = 100;
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;

    public final static int SINGLE = 1;
    public final static int MULTIPLE = 2;

    public final static int CHOOSE_REQUEST = 188;
    public final static int REQUEST_CAMERA = 909;
}
