package com.yalantis.ucrop.entity;


import java.io.Serializable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.entity
 * email：893855882@qq.com
 * data：16/12/31
 */
public class LocalMedia implements Serializable {
    private String path;
    private String compressPath;
    private String cutPath;
    private long duration;
    private long lastUpdateAt;
    private boolean isChecked;
    private boolean isCut;
    public int position;
    private int num;
    private int type;
    private boolean compressed;

    public LocalMedia(String path, long lastUpdateAt, long duration, int type) {
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
        this.type = type;
    }

    public LocalMedia(String path, long duration, long lastUpdateAt,
                      boolean isChecked, int position, int num, int type) {
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.type = type;
    }

    public LocalMedia() {
    }

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
