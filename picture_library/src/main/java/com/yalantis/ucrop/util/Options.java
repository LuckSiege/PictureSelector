package com.yalantis.ucrop.util;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.util
 * email：893855882@qq.com
 * data：17/1/5
 */
public class Options {

    private int type = 1; // 获取相册类型; 1 图片 2 视频
    private int copyMode = Constants.COPY_MODEL_DEFAULT; // 裁剪模式; 默认、1:1、3:4、3:2、16:9
    private int maxSelectNum = Constants.SELECT_MAX_NUM; // 多选最大可选数量
    private int selectMode = Constants.MODE_MULTIPLE; // 单选 or 多选
    private boolean isShowCamera = true; // 是否显示相机
    private boolean enablePreview = true; // 是否预览图片
    private boolean enableCrop; // 是否裁剪图片，只针对单选图片有效
    private boolean isPreviewVideo; // 是否可预览视频(播放)

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
