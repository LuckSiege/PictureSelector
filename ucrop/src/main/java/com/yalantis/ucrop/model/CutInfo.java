package com.yalantis.ucrop.model;

import java.io.Serializable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.yalantis.ucrop.model
 * email：893855882@qq.com
 * data：2017/5/30
 */

public class CutInfo implements Serializable {
    private String path;
    private String cutPath;
    private int offsetX;
    private int offsetY;
    private int imageWidth;
    private int imageHeight;
    private float resultAspectRatio;
    private boolean isCut;

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

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }
}
