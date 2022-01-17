package com.luck.picture.lib.entity;

/**
 * @author：luck
 * @date：2021/5/18 7:30 PM
 * @describe：MediaExtraInfo
 */
public class MediaExtraInfo {
    /**
     * videoThumbnail
     */
    private String videoThumbnail;
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

    /**
     * orientation
     */
    private String orientation;

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

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

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "MediaExtraInfo{" +
                "videoThumbnail='" + videoThumbnail + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", orientation='" + orientation + '\'' +
                '}';
    }
}
