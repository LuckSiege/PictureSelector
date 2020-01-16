package com.yalantis.ucrop.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：luck
 * @data：2017/05/30 晚上23:00
 * @描述: CutInfo
 */
public class CutInfo implements Parcelable {
    /**
     * File ID
     */
    private long id;
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

    /**
     * 视频时长
     */
    private long duration;

    /**
     * 网络图片下载临时存放位置
     */
    private Uri httpOutUri;


    /**
     * The real path，But you can't get access from AndroidQ
     */
    private String realPath;


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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getHttpOutUri() {
        return httpOutUri;
    }

    public void setHttpOutUri(Uri httpOutUri) {
        this.httpOutUri = httpOutUri;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.path);
        dest.writeString(this.cutPath);
        dest.writeString(this.androidQToPath);
        dest.writeInt(this.offsetX);
        dest.writeInt(this.offsetY);
        dest.writeInt(this.imageWidth);
        dest.writeInt(this.imageHeight);
        dest.writeByte(this.isCut ? (byte) 1 : (byte) 0);
        dest.writeString(this.mimeType);
        dest.writeFloat(this.resultAspectRatio);
        dest.writeLong(this.duration);
        dest.writeParcelable(this.httpOutUri, flags);
        dest.writeString(this.realPath);
    }

    protected CutInfo(Parcel in) {
        this.id = in.readLong();
        this.path = in.readString();
        this.cutPath = in.readString();
        this.androidQToPath = in.readString();
        this.offsetX = in.readInt();
        this.offsetY = in.readInt();
        this.imageWidth = in.readInt();
        this.imageHeight = in.readInt();
        this.isCut = in.readByte() != 0;
        this.mimeType = in.readString();
        this.resultAspectRatio = in.readFloat();
        this.duration = in.readLong();
        this.httpOutUri = in.readParcelable(Uri.class.getClassLoader());
        this.realPath = in.readString();
    }

    public static final Creator<CutInfo> CREATOR = new Creator<CutInfo>() {
        @Override
        public CutInfo createFromParcel(Parcel source) {
            return new CutInfo(source);
        }

        @Override
        public CutInfo[] newArray(int size) {
            return new CutInfo[size];
        }
    };
}
