package com.luck.picture.lib.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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
     * first data mime type
     */
    private String firstMimeType;

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


    public LocalMediaFolder() {
    }

    protected LocalMediaFolder(Parcel in) {
        bucketId = in.readLong();
        name = in.readString();
        firstImagePath = in.readString();
        firstMimeType = in.readString();
        imageNum = in.readInt();
        checkedNum = in.readInt();
        isChecked = in.readByte() != 0;
        ofAllType = in.readInt();
        isCameraFolder = in.readByte() != 0;
        data = in.createTypedArrayList(LocalMedia.CREATOR);
        currentDataPage = in.readInt();
        isHasMore = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(bucketId);
        dest.writeString(name);
        dest.writeString(firstImagePath);
        dest.writeString(firstMimeType);
        dest.writeInt(imageNum);
        dest.writeInt(checkedNum);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(ofAllType);
        dest.writeByte((byte) (isCameraFolder ? 1 : 0));
        dest.writeTypedList(data);
        dest.writeInt(currentDataPage);
        dest.writeByte((byte) (isHasMore ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalMediaFolder> CREATOR = new Creator<LocalMediaFolder>() {
        @Override
        public LocalMediaFolder createFromParcel(Parcel in) {
            return new LocalMediaFolder(in);
        }

        @Override
        public LocalMediaFolder[] newArray(int size) {
            return new LocalMediaFolder[size];
        }
    };

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "unknown" : name;
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

    public String getFirstMimeType() {
        return firstMimeType;
    }

    public void setFirstMimeType(String firstMimeType) {
        this.firstMimeType = firstMimeType;
    }
}
