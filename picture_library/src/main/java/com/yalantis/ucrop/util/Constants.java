package com.yalantis.ucrop.util;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.util
 * email：893855882@qq.com
 * data：16/12/31
 */
public class Constants {
    // 裁剪模式
    public static final int COPY_MODEL_DEFAULT = 0;
    public static final int COPY_MODEL_1_1 = 11;
    public static final int COPY_MODEL_3_4 = 34;
    public static final int COPY_MODEL_3_2 = 32;
    public static final int COPY_MODEL_16_9 = 169;

    public final static int MODE_MULTIPLE = 1;// 多选
    public final static int MODE_SINGLE = 2;// 单选


    public static final int READ_EXTERNAL_STORAGE = 0x01;
    public static final int CAMERA = 0x02;

    public static final int SELECT_MAX_NUM = 9;

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String ACTION_ADD_PHOTO = "app.action.addImage";
    public static final String ACTION_REMOVE_PHOTO = "app.action.removeImage";
    public static final String ACTION_FINISH = "app.action.finish";


    public final static String BUNDLE_CAMERA_PATH = "CameraPath";
    public final static String EXTRA_SELECT_MODE = "SelectMode";
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";
    public final static String EXTRA_ENABLE_PREVIEW_VIDEO = "EnablePreviewVideo";
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";
    public final static String EXTRA_TYPE = "type";
    public final static String EXTRA_CROP_MODE = "cropMode";
    public final static String EXTRA_FOLDERS = "folders";

}
