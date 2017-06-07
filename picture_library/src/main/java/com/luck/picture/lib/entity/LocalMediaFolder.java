package com.luck.picture.lib.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.entity
 * email：893855882@qq.com
 * data：16/12/31
 */
public class LocalMediaFolder implements Serializable {
    private String name;
    private String path;
    private String firstImagePath;
    private int imageNum;
    private int checkedNum;
    private boolean isChecked;
    private List<LocalMedia> images = new ArrayList<LocalMedia>();


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<LocalMedia> images) {
        this.images = images;
    }

    public int getCheckedNum() {
        return checkedNum;
    }

    public void setCheckedNum(int checkedNum) {
        this.checkedNum = checkedNum;
    }
}
