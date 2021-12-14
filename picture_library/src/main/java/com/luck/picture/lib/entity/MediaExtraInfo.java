package com.luck.picture.lib.entity;

/**
 * @author：luck
 * @date：2021/5/18 7:30 PM
 * @describe：MediaExtraInfo
 */
public class MediaExtraInfo {
    /**
     * width
     */
    private int width;
    /**
     * height
     */
    private int height;
    /**
     * duration
     */
    private long duration;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaExtraInfo{" +
                "width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                '}';
    }
}
