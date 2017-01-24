package com.luck.picture.lib.model;

import android.graphics.Color;

import com.luck.picture.lib.R;
import com.yalantis.ucrop.entity.LocalMedia;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.model
 * email：893855882@qq.com
 * data：17/1/16
 */
public class FunctionConfig implements Serializable {

    // 裁剪模式
    public static final int COPY_MODEL_DEFAULT = 0;
    public static final int COPY_MODEL_1_1 = 11;
    public static final int COPY_MODEL_3_4 = 34;
    public static final int COPY_MODEL_3_2 = 32;
    public static final int COPY_MODEL_16_9 = 169;

    public final static int MODE_MULTIPLE = 1;// 多选
    public final static int MODE_SINGLE = 2;// 单选

    public static final int ORDINARY = 0;// 普通 低质量
    public static final int HIGH = 1;// 清晰

    public static final int COPY_WIDTH = 0;
    public static final int COPY_HEIGHT = 0;
    public final static int REQUEST_IMAGE = 88;
    public final static int REQUEST_CAMERA = 99;
    public final static int REQUEST_PREVIEW = 100;
    public static final int READ_EXTERNAL_STORAGE = 0x01;
    public static final int CAMERA = 0x02;

    public static final int SELECT_MAX_NUM = 9;
    public static final int MAX_COMPRESS_SIZE = 102400;
    public static final String EXTRA_THIS_CONFIG = "function_config";
    public static final String EXTRA_IS_TOP_ACTIVITY = "isTopActivity";
    public static final String EXTRA_BOTTOM_PREVIEW = "bottom_preview";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_CUT_INDEX = "cutIndex";
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";

    public final static String FOLDER_NAME = "folderName";
    public final static String REQUEST_OUTPUT = "outputList";
    public final static String BUNDLE_CAMERA_PATH = "CameraPath";
    public final static String EXTRA_SELECT_MODE = "SelectMode";
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";
    public final static String EXTRA_ENABLE_PREVIEW_VIDEO = "EnablePreviewVideo";
    public final static String EXTRA_RESULT = "select_result";
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";
    public final static String EXTRA_MAX_SPAN_COUNT = "spanCount";
    public final static String EXTRA_TYPE = "type";
    public final static String EXTRA_CROP_MODE = "cropMode";
    public final static String BACKGROUND_COLOR = "backgroundColor";
    public final static String CHECKED_DRAWABLE = "cb_drawable";
    public final static String EXTRA_COMPRESS = "isCompress";
    public final static String EXTRA_VIDEO_SECOND = "videoSecond";


    public final static String EXTRA_CROP_W = "crop_w";
    public final static String EXTRA_CROP_H = "crop_h";
    public final static String EXTRA_DEFINITION = "definition";
    public final static String EXTRA_IS_CHECKED_NUM = "checkedNum";
    public final static String EXTRA_PREVIEW_COLOR = "previewColor";
    public final static String EXTRA_COMPLETE_COLOR = "completeColor";
    public final static String EXTRA_BOTTOM_BG_COLOR = "bottomBgColor";
    public final static String EXTRA_PREVIEW_BOTTOM_BG_COLOR = "previewBottomBgColor";
    public final static String EXTRA_COMPRESS_QUALITY = "compressQuality";


    private int type = 1; // 获取相册类型; 1 图片 2 视频
    private int copyMode = COPY_MODEL_DEFAULT; // 裁剪模式; 默认、1:1、3:4、3:2、16:9
    private int maxSelectNum = SELECT_MAX_NUM; // 多选最大可选数量
    private int selectMode = MODE_MULTIPLE; // 单选 or 多选
    private boolean isShowCamera = true; // 是否显示相机
    private boolean enablePreview = true; // 是否预览图片
    private boolean enableCrop; // 是否裁剪图片，只针对单选图片有效
    private boolean isPreviewVideo; // 是否可预览视频(播放)
    private int imageSpanCount = 4; // 列表每行显示个数
    private int themeStyle = Color.parseColor("#393a3e"); // 标题栏背景色;
    private int checkedBoxDrawable = R.drawable.checkbox_selector;// 图片选择默认样式
    private int cropW = COPY_WIDTH; // 裁剪宽度  如果值大于图片原始宽高 将返回原图大小
    private int cropH = COPY_HEIGHT;// 裁剪高度  如果值大于图片原始宽高 将返回原图大小
    private int recordVideoSecond = 0;// 录视频秒数
    private int recordVideoDefinition = 0;// 视频清晰度
    private boolean isCompress = false;// 是否压缩图片，默认不压缩
    private boolean isCheckNumMode = false;// 是否显示QQ风格选择图片
    private int previewColor = Color.parseColor("#FA632D"); // 底部预览字体颜色
    private int completeColor = Color.parseColor("#FA632D"); // 底部完成字体颜色
    private int bottomBgColor = Color.parseColor("#fafafa"); // 底部背景色
    protected int previewBottomBgColor = Color.parseColor("#dd393a3e"); // 预览底部背景色
    protected int compressQuality = 100;// 图片裁剪质量,默认无损
    protected List<LocalMedia> selectMedia = new ArrayList<>();// 已选择的图片
    protected int compressFlag = 1; // 1 系统自带压缩 2 luban压缩
    protected int compressW;
    protected int compressH;

    public int getCompressW() {
        return compressW;
    }

    public void setCompressW(int compressW) {
        this.compressW = compressW;
    }

    public int getCompressH() {
        return compressH;
    }

    public void setCompressH(int compressH) {
        this.compressH = compressH;
    }

    public int getCompressFlag() {
        return compressFlag;
    }

    public void setCompressFlag(int compressFlag) {
        this.compressFlag = compressFlag;
    }

    /**
     * 是否启用像素压缩
     */
    protected boolean isEnablePixelCompress = true;
    /**
     * 是否启用质量压缩
     */
    protected boolean isEnableQualityCompress = true;

    public boolean isEnableQualityCompress() {
        return isEnableQualityCompress;
    }

    public void setEnableQualityCompress(boolean enableQualityCompress) {
        isEnableQualityCompress = enableQualityCompress;
    }

    public boolean isEnablePixelCompress() {
        return isEnablePixelCompress;
    }

    public void setEnablePixelCompress(boolean enablePixelCompress) {
        isEnablePixelCompress = enablePixelCompress;
    }

    public List<LocalMedia> getSelectMedia() {
        return selectMedia;
    }

    public void setSelectMedia(List<LocalMedia> selectMedia) {
        this.selectMedia = selectMedia;
    }

    public int getCompressQuality() {
        return compressQuality;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    public int getPreviewBottomBgColor() {
        return previewBottomBgColor;
    }

    public void setPreviewBottomBgColor(int previewBottomBgColor) {
        this.previewBottomBgColor = previewBottomBgColor;
    }

    public int getBottomBgColor() {
        return bottomBgColor;
    }

    public void setBottomBgColor(int bottomBgColor) {
        this.bottomBgColor = bottomBgColor;
    }

    public int getPreviewColor() {
        return previewColor;
    }

    public void setPreviewColor(int previewColor) {
        this.previewColor = previewColor;
    }

    public int getCompleteColor() {
        return completeColor;
    }

    public void setCompleteColor(int completeColor) {
        this.completeColor = completeColor;
    }

    public boolean isCheckNumMode() {
        return isCheckNumMode;
    }

    public void setCheckNumMode(boolean checkNumMode) {
        isCheckNumMode = checkNumMode;
    }

    public int getRecordVideoDefinition() {
        return recordVideoDefinition;
    }

    public void setRecordVideoDefinition(int recordVideoDefinition) {
        this.recordVideoDefinition = recordVideoDefinition;
    }

    public int getRecordVideoSecond() {
        return recordVideoSecond;
    }

    public void setRecordVideoSecond(int recordVideoSecond) {
        this.recordVideoSecond = recordVideoSecond;
    }

    public int getImageSpanCount() {
        return imageSpanCount;
    }

    public void setImageSpanCount(int imageSpanCount) {
        this.imageSpanCount = imageSpanCount;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public int getCropW() {
        return cropW;
    }

    public void setCropW(int cropW) {
        this.cropW = cropW;
    }

    public int getCropH() {
        return cropH;
    }

    public void setCropH(int cropH) {
        this.cropH = cropH;
    }

    public int getCheckedBoxDrawable() {
        return checkedBoxDrawable;
    }

    public void setCheckedBoxDrawable(int checkedBoxDrawable) {
        this.checkedBoxDrawable = checkedBoxDrawable;
    }

    public int getThemeStyle() {
        return themeStyle;
    }

    public void setThemeStyle(int themeStyle) {
        this.themeStyle = themeStyle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCopyMode() {
        return copyMode;
    }

    public void setCopyMode(int copyMode) {
        this.copyMode = copyMode;
    }

    public int getMaxSelectNum() {
        return maxSelectNum;
    }

    public void setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public boolean isEnablePreview() {
        return enablePreview;
    }

    public void setEnablePreview(boolean enablePreview) {
        this.enablePreview = enablePreview;
    }

    public boolean isEnableCrop() {
        return enableCrop;
    }

    public void setEnableCrop(boolean enableCrop) {
        this.enableCrop = enableCrop;
    }

    public boolean isPreviewVideo() {
        return isPreviewVideo;
    }

    public void setPreviewVideo(boolean previewVideo) {
        isPreviewVideo = previewVideo;
    }

}
