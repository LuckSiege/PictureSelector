package com.yalantis.ucrop.entity;

import java.io.Serializable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.yalantis.ucrop.entity
 * email：893855882@qq.com
 * data：17/1/7
 */
public class Compress implements Serializable {
    private String path;
    private String compressPath;
    private boolean compressed;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
}
