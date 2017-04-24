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
 * data：2017/4/24
 */

public class FunctionOptions implements Serializable {

    private int type; // 图片 or 视频
    private int cropMode; // 裁剪模式
    private int maxSelectNum; // 多选最大可选数量
    private int selectMode; // 单选 or 多选
    private boolean isShowCamera = true; // 是否显示相机
    private boolean enablePreview = true; // 是否预览图片
    private boolean enableCrop; // 是否裁剪图片，只针对单选图片有效
    private boolean isPreviewVideo; // 是否可预览视频(播放)
    private int imageSpanCount = 4; // 列表每行显示个数
    private int themeStyle; // 标题栏背景色;
    private int checkedBoxDrawable;// 图片选择默认样式
    private int cropW; // 裁剪宽度  如果值大于图片原始宽高 将返回原图大小
    private int cropH;// 裁剪高度  如果值大于图片原始宽高 将返回原图大小
    private int recordVideoSecond;// 录视频秒数
    private int recordVideoDefinition;// 视频清晰度
    private boolean isCompress = false;// 是否压缩图片，默认不压缩
    private boolean isCheckNumMode;// 是否显示QQ风格选择图片
    private int previewColor; // 底部预览字体颜色
    private int completeColor; // 底部完成字体颜色
    private int bottomBgColor; // 底部背景色
    private int previewBottomBgColor; // 预览底部背景色
    private int compressQuality;// 图片裁剪质量,默认无损
    private List<LocalMedia> selectMedia = new ArrayList<>();// 已选择的图片
    private int compressFlag; // 1 系统自带压缩 2 luban压缩
    private int compressW; // 压缩宽
    private int compressH; // 压缩高
    private int grade;// 压缩档次
    private int maxB;
    private boolean isGif;
    /**
     * 是否启用像素压缩
     */
    private boolean isEnablePixelCompress = true;
    /**
     * 是否启用质量压缩
     */
    private boolean isEnableQualityCompress = true;

    public FunctionOptions() {
        super();

    }

    public int getType() {
        if (type == 0) {
            // 默认图片选择
            type = FunctionConfig.TYPE_IMAGE;
        }
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCropMode() {
        return cropMode;
    }

    public void setCropMode(int copyMode) {
        this.cropMode = copyMode;
    }

    public int getMaxSelectNum() {
        if (maxSelectNum == 0) {
            maxSelectNum = FunctionConfig.SELECT_MAX_NUM;
        }
        return maxSelectNum;
    }

    public void setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
    }

    public int getSelectMode() {
        if (selectMode == 0) {
            selectMode = FunctionConfig.MODE_MULTIPLE;
        }
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

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
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

    public int getImageSpanCount() {
        return imageSpanCount;
    }

    public void setImageSpanCount(int imageSpanCount) {
        this.imageSpanCount = imageSpanCount;
    }

    public int getThemeStyle() {
        if (themeStyle == 0) {
            themeStyle = Color.parseColor("#393a3e");
        }
        return themeStyle;
    }

    public void setThemeStyle(int themeStyle) {
        this.themeStyle = themeStyle;
    }

    public int getCheckedBoxDrawable() {
        // 如果是QQ选择风格
        if (isCheckNumMode) {
            checkedBoxDrawable = R.drawable.checkbox_num_selector;
        }
        if (checkedBoxDrawable == 0) {
            checkedBoxDrawable = R.drawable.checkbox_selector;
        }
        return checkedBoxDrawable;
    }

    public void setCheckedBoxDrawable(int checkedBoxDrawable) {
        this.checkedBoxDrawable = checkedBoxDrawable;
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

    public int getRecordVideoSecond() {
        return recordVideoSecond;
    }

    public void setRecordVideoSecond(int recordVideoSecond) {
        this.recordVideoSecond = recordVideoSecond;
    }

    public int getRecordVideoDefinition() {
        return recordVideoDefinition;
    }

    public void setRecordVideoDefinition(int recordVideoDefinition) {
        this.recordVideoDefinition = recordVideoDefinition;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public boolean isCheckNumMode() {
        if (isCheckNumMode) {

        }
        return isCheckNumMode;
    }

    public void setCheckNumMode(boolean checkNumMode) {
        isCheckNumMode = checkNumMode;
    }

    public int getPreviewColor() {
        if (previewColor == 0) {
            previewColor = Color.parseColor("#FA632D");
        }
        return previewColor;
    }

    public void setPreviewColor(int previewColor) {
        this.previewColor = previewColor;
    }

    public int getCompleteColor() {
        if (completeColor == 0) {
            completeColor = Color.parseColor("#FA632D");
        }
        return completeColor;
    }

    public void setCompleteColor(int completeColor) {
        this.completeColor = completeColor;
    }

    public int getBottomBgColor() {
        if (bottomBgColor == 0) {
            bottomBgColor = Color.parseColor("#fafafa");
        }
        return bottomBgColor;
    }

    public void setBottomBgColor(int bottomBgColor) {
        this.bottomBgColor = bottomBgColor;
    }

    public int getPreviewBottomBgColor() {
        if (previewBottomBgColor == 0) {
            previewBottomBgColor = Color.parseColor("#dd393a3e");
        }
        return previewBottomBgColor;
    }

    public void setPreviewBottomBgColor(int previewBottomBgColor) {
        this.previewBottomBgColor = previewBottomBgColor;
    }

    public int getCompressQuality() {
        if (compressQuality == 0) {
            compressQuality = 100;
        }
        return compressQuality;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    public List<LocalMedia> getSelectMedia() {
        return selectMedia;
    }

    public void setSelectMedia(List<LocalMedia> selectMedia) {
        this.selectMedia = selectMedia;
    }

    public int getCompressFlag() {
        if (compressFlag == 0) {
            compressFlag = 1;
        }
        return compressFlag;
    }

    public void setCompressFlag(int compressFlag) {
        this.compressFlag = compressFlag;
    }

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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getMaxB() {
        if (maxB == 0) {
            maxB = FunctionConfig.MAX_COMPRESS_SIZE;
        }
        return maxB;
    }

    public void setMaxB(int maxB) {
        this.maxB = maxB;
    }

    public boolean isEnablePixelCompress() {
        return isEnablePixelCompress;
    }

    public void setEnablePixelCompress(boolean enablePixelCompress) {
        isEnablePixelCompress = enablePixelCompress;
    }

    public void setEnableQualityCompress(boolean enableQualityCompress) {
        isEnableQualityCompress = enableQualityCompress;
    }

    public boolean isEnableQualityCompress() {
        return isEnableQualityCompress;
    }


    public static class Builder {
        private FunctionOptions options;

        public Builder() {
            options = new FunctionOptions();
        }

        public Builder setType(int type) {
            options.setType(type);
            return this;
        }

        public Builder setCropMode(int crop) {
            options.setCropMode(crop);
            return this;
        }

        public Builder setMaxSelectNum(int maxSize) {
            options.setMaxSelectNum(maxSize);
            return this;
        }

        public Builder setSelectMode(int selectMode) {
            options.setSelectMode(selectMode);
            return this;
        }

        public Builder setShowCamera(boolean showCamera) {
            options.setShowCamera(showCamera);
            return this;
        }

        public Builder setEnablePreview(boolean isEnablePreview) {
            options.setEnablePreview(isEnablePreview);
            return this;
        }

        public Builder setEnableCrop(boolean isEnableCrop) {
            options.setEnableCrop(isEnableCrop);
            return this;
        }

        public Builder setPreviewVideo(boolean isPreviewVideo) {
            options.setPreviewVideo(isPreviewVideo);
            return this;
        }

        public Builder setImageSpanCount(int spanCount) {
            options.setImageSpanCount(spanCount);
            return this;
        }

        public Builder setThemeStyle(int themeStyle) {
            options.setThemeStyle(themeStyle);
            return this;
        }

        public Builder setCheckedBoxDrawable(int checkedBoxDrawable) {
            options.setCheckedBoxDrawable(checkedBoxDrawable);
            return this;
        }

        public Builder setCropW(int cropW) {
            options.setCropW(cropW);
            return this;
        }

        public Builder setCropH(int cropH) {
            options.setCropH(cropH);
            return this;
        }

        public Builder setGif(boolean isGif) {
            options.setGif(isGif);
            return this;
        }

        public Builder setRecordVideoSecond(int recordVideoSecond) {
            options.setRecordVideoSecond(recordVideoSecond);
            return this;
        }

        public Builder setRecordVideoDefinition(int recordVideoDefinition) {
            options.setRecordVideoDefinition(recordVideoDefinition);
            return this;
        }

        public Builder setCompress(boolean isCompress) {
            options.setCompress(isCompress);
            return this;
        }

        public Builder setCheckNumMode(boolean checkNumMode) {
            options.setCheckNumMode(checkNumMode);
            return this;
        }

        public Builder setPreviewColor(int previewColor) {
            options.setPreviewColor(previewColor);
            return this;
        }

        public Builder setCompleteColor(int completeColor) {
            options.setCompleteColor(completeColor);
            return this;
        }

        public Builder setBottomBgColor(int bottomBgColor) {
            options.setBottomBgColor(bottomBgColor);
            return this;
        }

        public Builder setPreviewBottomBgColor(int previewBottomBgColor) {
            options.setPreviewBottomBgColor(previewBottomBgColor);
            return this;
        }

        public Builder setCompressQuality(int compressQuality) {
            options.setCompressQuality(compressQuality);
            return this;
        }

        public Builder setSelectMedia(List<LocalMedia> selectMedia) {
            options.setSelectMedia(selectMedia);
            return this;
        }

        public Builder setCompressFlag(int compressFlag) {
            options.setCompressFlag(compressFlag);
            return this;
        }

        public Builder setCompressW(int compressW) {
            options.setCompressW(compressW);
            return this;
        }

        public Builder setCompressH(int compressH) {
            options.setCompressH(compressH);
            return this;
        }

        public Builder setGrade(int grade) {
            options.setGrade(grade);
            return this;
        }

        public Builder setMaxB(int maxB) {
            options.setMaxB(maxB);
            return this;
        }

        public Builder setEnablePixelCompress(boolean enablePixelCompress) {
            options.setEnablePixelCompress(enablePixelCompress);
            return this;
        }

        public Builder setEnableQualityCompress(boolean enableQualityCompress) {
            options.setEnableQualityCompress(enableQualityCompress);
            return this;
        }

        public FunctionOptions create() {
            return options;
        }
    }
}
