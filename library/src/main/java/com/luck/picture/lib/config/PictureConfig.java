package com.luck.picture.lib.config;

/**
 * @author：luck
 * @data：2017/5/24 1:00
 * @describe : constant
 */
public final class PictureConfig {

    public final static String EXTRA_MEDIA_KEY = "mediaKey";
    public final static String EXTRA_PREVIEW_DELETE_POSITION = "position";
    public final static String EXTRA_CONFIG = "PictureSelectorConfig";
    public final static String CAMERA_FACING = "android.intent.extras.CAMERA_FACING";

    public final static String EXTRA_ALL_FOLDER_SIZE = "all_folder_size";
    public final static String EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture";

    public final static int MAX_PAGE_SIZE = 60;

    public final static int MIN_PAGE_SIZE = 10;

    public final static int NORMAL = -1;

    public final static int CAMERA_BEFORE = 1;

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;

    @Deprecated
    public final static int TYPE_AUDIO = 3;

    public final static long MB = 1048576;

    public final static int MAX_COMPRESS_SIZE = 100;

    public final static int DEFAULT_SPAN_COUNT = 4;


    public final static int SINGLE = 1;
    public final static int MULTIPLE = 2;

    public final static int REQUEST_CAMERA = 909;

    public final static int ADAPTER_TYPE_CAMERA = 1;
    public final static int ADAPTER_TYPE_IMAGE = 2;
    public final static int ADAPTER_TYPE_VIDEO = 3;
    public final static int ADAPTER_TYPE_AUDIO = 4;
}
