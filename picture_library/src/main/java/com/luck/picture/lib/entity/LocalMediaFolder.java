package com.luck.picture.lib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2016-12-31 15:21
 * @describe：MediaFolder Entity
 */

public class LocalMediaFolder implements Parcelable {
    /**
     * bucketId
     */
    private long bucketId = -1;
    /**
     * Folder name
     */
    private String name;
    /**
     * Folder first path
     */
    private String firstImagePath;
    /**
     * Folder media num
     */
    private int imageNum;
    /**
     * If the selected num
     */
    private int checkedNum;
    /**
     * If the selected
     */
    private boolean isChecked;

    /**
     * type
     */
    private int ofAllType = -1;
    /**
     * Whether or not the camera
     */
    private boolean isCameraFolder;

    /**
     * data
     */
    private List<LocalMedia> data = new ArrayList<>();

    /**
     * # Internal use
     * setCurrentDataPage
     */
    private int currentDataPage;

    /**
     * # Internal use
     * is load more
     */
    private boolean isHasMore;

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getCheckedNum() {
        return checkedNum;
    }

    public void setCheckedNum(int checkedNum) {
        this.checkedNum = checkedNum;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getOfAllType() {
        return ofAllType;
    }

    public void setOfAllType(int ofAllType) {
        this.ofAllType = ofAllType;
    }

    public boolean isCameraFolder() {
        return isCameraFolder;
    }

    public void setCameraFolder(boolean cameraFolder) {
        isCameraFolder = cameraFolder;
    }

    public List<LocalMedia> getData() {
        return data;
    }

    public void setData(List<LocalMedia> data) {
        this.data = data;
    }

    public int getCurrentDataPage() {
        return currentDataPage;
    }

    public void setCurrentDataPage(int currentDataPage) {
        this.currentDataPage = currentDataPage;
    }

    public boolean isHasMore() {
        return isHasMore;
    }

    public void setHasMore(boolean hasMore) {
        isHasMore = hasMore;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.bucketId);
        dest.writeString(this.name);
        dest.writeString(this.firstImagePath);
        dest.writeInt(this.imageNum);
        dest.writeInt(this.checkedNum);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.ofAllType);
        dest.writeByte(this.isCameraFolder ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.data);
        dest.writeInt(this.currentDataPage);
        dest.writeByte(this.isHasMore ? (byte) 1 : (byte) 0);
    }

    public LocalMediaFolder() {
    }

    protected LocalMediaFolder(Parcel in) {
        this.bucketId = in.readLong();
        this.name = in.readString();
        this.firstImagePath = in.readString();
        this.imageNum = in.readInt();
        this.checkedNum = in.readInt();
        this.isChecked = in.readByte() != 0;
        this.ofAllType = in.readInt();
        this.isCameraFolder = in.readByte() != 0;
        this.data = in.createTypedArrayList(LocalMedia.CREATOR);
        this.currentDataPage = in.readInt();
        this.isHasMore = in.readByte() != 0;
    }

    public static final Creator<LocalMediaFolder> CREATOR = new Creator<LocalMediaFolder>() {
        @Override
        public LocalMediaFolder createFromParcel(Parcel source) {
            return new LocalMediaFolder(source);
        }

        @Override
        public LocalMediaFolder[] newArray(int size) {
            return new LocalMediaFolder[size];
        }
    };
}
