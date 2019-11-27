package com.yalantis.ucrop.model;

import java.io.Serializable;

/**
 * @author：luck
 * @data：2017/05/30 晚上23:00
 * @描述: CutInfo
 */
public class CutInfo implements Serializable {
    /**
     * 原图
     */
    private String path;
    /**
     * 裁剪路径
     */
    private String cutPath;
    /**
     * Android Q特有地址
     */
    private String androidQToPath;
    /**
     * 裁剪比例
     */
    private int offsetX;
    /**
     * 裁剪比例
     */
    private int offsetY;
    /**
     * 图片宽
     */
    private int imageWidth;
    /**
     * 图片高
     */
    private int imageHeight;
    /**
     * 是否裁剪
     */
    private boolean isCut;

    /**
     * 资源类型
     */
    private String mimeType;

    private float resultAspectRatio;

    public CutInfo() {
    }

    public CutInfo(String path, boolean isCut) {
        this.path = path;
        this.isCut = isCut;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public float getResultAspectRatio() {
        return resultAspectRatio;
    }

    public void setResultAspectRatio(float resultAspectRatio) {
        this.resultAspectRatio = resultAspectRatio;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public String getAndroidQToPath() {
        return androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
    }
}
