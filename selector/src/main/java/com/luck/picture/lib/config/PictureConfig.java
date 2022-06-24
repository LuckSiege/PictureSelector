package com.luck.picture.lib.config;

import com.luck.picture.lib.BuildConfig;

/**
 * @author：luck
 * @data：2017/5/24 1:00
 * @describe : constant
 */
public final class PictureConfig {

    public static final String SP_NAME = "PictureSpUtils";

    private static final String KEY = BuildConfig.LIBRARY_PACKAGE_NAME;

    public final static String EXTRA_RESULT_SELECTION = "extra_result_media";

    public final static String EXTRA_PICTURE_SELECTOR_CONFIG = KEY + ".PictureSelectorConfig";

    public final static String CAMERA_FACING = "android.intent.extras.CAMERA_FACING";

    public final static String EXTRA_ALL_FOLDER_SIZE = KEY + ".all_folder_size";


    public final static String EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture";

    public final static String EXTRA_EXTERNAL_PREVIEW = KEY + ".external_preview";

    public final static String EXTRA_DISPLAY_CAMERA = KEY + ".display_camera";

    public final static String EXTRA_BOTTOM_PREVIEW = KEY + ".bottom_preview";

    public final static String EXTRA_CURRENT_ALBUM_NAME = KEY + ".current_album_name";

    public final static String EXTRA_CURRENT_PAGE = KEY + ".current_page";

    public final static String EXTRA_CURRENT_BUCKET_ID = KEY + ".current_bucketId";

    public final static String EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE = KEY + ".external_preview_display_delete";

    public final static String EXTRA_PREVIEW_CURRENT_POSITION = KEY + ".current_preview_position";

    public final static String EXTRA_PREVIEW_CURRENT_ALBUM_TOTAL = KEY + ".current_album_total";

    public final static String EXTRA_CURRENT_CHOOSE_MODE = KEY + ".current_choose_mode";

    public final static String EXTRA_MODE_TYPE_SOURCE = KEY + ".mode_type_source";

    public final static int MAX_PAGE_SIZE = 60;

    public final static int MIN_PAGE_SIZE = 10;

    public final static int CAMERA_BEFORE = 1;



    public final static int DEFAULT_SPAN_COUNT = 4;

    public final static int REQUEST_CAMERA = 909;

    public final static int CHOOSE_REQUEST = 188;

    public final static int REQUEST_GO_SETTING = 1102;

    public final static int ALL = -1;

    public final static int UNSET = -1;

    public final static int MODE_TYPE_SYSTEM_SOURCE = 1;
    public final static int MODE_TYPE_EXTERNAL_PREVIEW_SOURCE = 2;
}
