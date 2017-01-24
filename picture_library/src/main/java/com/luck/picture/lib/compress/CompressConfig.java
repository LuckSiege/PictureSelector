package com.luck.picture.lib.compress;

import java.io.Serializable;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：16/12/31
 */

public class CompressConfig implements Serializable {

    /**
     * 长或宽不超过的最大像素,单位px
     */
    private int maxPixel = 1200;
    /**
     * 压缩到的最大大小，单位B
     */
    private int maxSize = 100 * 1024;

    /**
     * 是否启用像素压缩
     */
    private boolean enablePixelCompress = true;
    /**
     * 是否启用质量压缩
     */
    private boolean enableQualityCompress = true;

    /**
     * 是否保留原文件
     */
    private boolean enableReserveRaw = true;

    /**
     * Luban压缩配置
     */
    private LubanOptions lubanOptions;

    public static CompressConfig ofDefaultConfig() {
        return new CompressConfig();
    }

    public static CompressConfig ofLuban(LubanOptions options) {
        return new CompressConfig(options);
    }

    private CompressConfig() {
    }

    private CompressConfig(LubanOptions options) {
        this.lubanOptions = options;
    }

    public LubanOptions getLubanOptions() {
        return lubanOptions;
    }

    public int getMaxPixel() {
        return maxPixel;
    }

    public CompressConfig setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isEnablePixelCompress() {
        return enablePixelCompress;
    }

    public void enablePixelCompress(boolean enablePixelCompress) {
        this.enablePixelCompress = enablePixelCompress;
    }

    public boolean isEnableQualityCompress() {
        return enableQualityCompress;
    }

    public void enableQualityCompress(boolean enableQualityCompress) {
        this.enableQualityCompress = enableQualityCompress;
    }

    public boolean isEnableReserveRaw() {
        return enableReserveRaw;
    }

    public void enableReserveRaw(boolean enableReserveRaw) {
        this.enableReserveRaw = enableReserveRaw;
    }

    public static class Builder {
        private CompressConfig config;

        public Builder() {
            config = new CompressConfig();
        }

        public Builder setMaxSize(int maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }

        public Builder setMaxPixel(int maxPixel) {
            config.setMaxPixel(maxPixel);
            return this;
        }

        public Builder enablePixelCompress(boolean enablePixelCompress) {
            config.enablePixelCompress(enablePixelCompress);
            return this;
        }

        public Builder enableQualityCompress(boolean enableQualityCompress) {
            config.enableQualityCompress(enableQualityCompress);
            return this;
        }

        public Builder enableReserveRaw(boolean enableReserveRaw) {
            config.enableReserveRaw(enableReserveRaw);
            return this;
        }

        public CompressConfig create() {
            return config;
        }
    }
}

