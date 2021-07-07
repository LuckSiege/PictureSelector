package com.luck.picture.lib.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureConfig;

/**
 * @author：luck
 * @date：2017-5-24 16:21
 * @describe：Media Entity
 * <a href="https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-%E8%B7%AF%E5%BE%84%E8%AF%B4%E6%98%8E">
 */

public class LocalMedia implements Parcelable {
    /**
     * file to ID
     */
    private long id;
    /**
     * original path
     */
    private String path;

    /**
     * The real path，But you can't get access from AndroidQ
     */
    private String realPath;

    /**
     * # Check the original button to get the return value
     * original path
     */
    private String originalPath;
    /**
     * compress path
     */
    private String compressPath;
    /**
     * cut path
     */
    private String cutPath;

    /**
     * Note: this field is only returned in Android Q version
     * <p>
     * Android Q image or video path
     */
    private String androidQToPath;
    /**
     * video duration
     */
    private long duration;
    /**
     * If the selected
     * # Internal use
     */
    private boolean isChecked;
    /**
     * If the cut
     */
    private boolean isCut;
    /**
     * media position of list
     */
    public int position;
    /**
     * The media number of qq choose styles
     */
    private int num;
    /**
     * The media resource type
     */
    private String mimeType;

    /**
     * Gallery selection mode
     */
    private int chooseModel;

    /**
     * If the compressed
     */
    private boolean compressed;
    /**
     * image or video width
     * <p>
     * # If zero occurs, the developer needs to handle it extra
     */
    private int width;
    /**
     * image or video height
     * <p>
     * # If zero occurs, the developer needs to handle it extra
     */
    private int height;

    /**
     * Crop the width of the picture
     */
    private int cropImageWidth;

    /**
     * Crop the height of the picture
     */
    private int cropImageHeight;

    /**
     * Crop ratio x
     */
    private int cropOffsetX;
    /**
     * Crop ratio y
     */
    private int cropOffsetY;

    /**
     * Crop Aspect Ratio
     */
    private float cropResultAspectRatio;

    /**
     * file size
     */
    private long size;

    /**
     * Whether the original image is displayed
     */
    private boolean isOriginal;

    /**
     * file name
     */
    private String fileName;

    /**
     * Parent  Folder Name
     */
    private String parentFolderName;

    /**
     * orientation info
     * # For internal use only
     */
    @Deprecated
    private int orientation = -1;

    /**
     * loadLongImageStatus
     * # For internal use only
     */
    public int loadLongImageStatus = PictureConfig.NORMAL;

    /**
     * isLongImage
     * # For internal use only
     */
    public boolean isLongImage;

    /**
     * bucketId
     */
    private long bucketId = -1;

    /**
     * isMaxSelectEnabledMask
     * # For internal use only
     */
    private boolean isMaxSelectEnabledMask;

    /**
     * Whether the image has been edited
     * # For internal use only
     */
    private boolean isEditorImage;

    /**
     * media create time
     */
    private long dateAddedTime;

    public LocalMedia() {

    }

    public LocalMedia(long id, String path, String absolutePath, String fileName, String parentFolderName, long duration, int chooseModel,
                      String mimeType, int width, int height, long size, long bucketId, long dateAddedColumn) {
        this.id = id;
        this.path = path;
        this.realPath = absolutePath;
        this.fileName = fileName;
        this.parentFolderName = parentFolderName;
        this.duration = duration;
        this.chooseModel = chooseModel;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
        this.bucketId = bucketId;
        this.dateAddedTime = dateAddedColumn;
    }

    public LocalMedia(String path, long duration, boolean isChecked, int position, int num, int chooseModel) {
        this.path = path;
        this.duration = duration;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.chooseModel = chooseModel;
    }


    protected LocalMedia(Parcel in) {
        id = in.readLong();
        path = in.readString();
        realPath = in.readString();
        originalPath = in.readString();
        compressPath = in.readString();
        cutPath = in.readString();
        androidQToPath = in.readString();
        duration = in.readLong();
        isChecked = in.readByte() != 0;
        isCut = in.readByte() != 0;
        position = in.readInt();
        num = in.readInt();
        mimeType = in.readString();
        chooseModel = in.readInt();
        compressed = in.readByte() != 0;
        width = in.readInt();
        height = in.readInt();
        cropImageWidth = in.readInt();
        cropImageHeight = in.readInt();
        cropOffsetX = in.readInt();
        cropOffsetY = in.readInt();
        cropResultAspectRatio = in.readFloat();
        size = in.readLong();
        isOriginal = in.readByte() != 0;
        fileName = in.readString();
        parentFolderName = in.readString();
        orientation = in.readInt();
        loadLongImageStatus = in.readInt();
        isLongImage = in.readByte() != 0;
        bucketId = in.readLong();
        isMaxSelectEnabledMask = in.readByte() != 0;
        isEditorImage = in.readByte() != 0;
        dateAddedTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(realPath);
        dest.writeString(originalPath);
        dest.writeString(compressPath);
        dest.writeString(cutPath);
        dest.writeString(androidQToPath);
        dest.writeLong(duration);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isCut ? 1 : 0));
        dest.writeInt(position);
        dest.writeInt(num);
        dest.writeString(mimeType);
        dest.writeInt(chooseModel);
        dest.writeByte((byte) (compressed ? 1 : 0));
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(cropImageWidth);
        dest.writeInt(cropImageHeight);
        dest.writeInt(cropOffsetX);
        dest.writeInt(cropOffsetY);
        dest.writeFloat(cropResultAspectRatio);
        dest.writeLong(size);
        dest.writeByte((byte) (isOriginal ? 1 : 0));
        dest.writeString(fileName);
        dest.writeString(parentFolderName);
        dest.writeInt(orientation);
        dest.writeInt(loadLongImageStatus);
        dest.writeByte((byte) (isLongImage ? 1 : 0));
        dest.writeLong(bucketId);
        dest.writeByte((byte) (isMaxSelectEnabledMask ? 1 : 0));
        dest.writeByte((byte) (isEditorImage ? 1 : 0));
        dest.writeLong(dateAddedTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        @Override
        public LocalMedia createFromParcel(Parcel in) {
            return new LocalMedia(in);
        }

        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };

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

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public String getAndroidQToPath() {
        return androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMimeType() {
        return TextUtils.isEmpty(mimeType) ? "image/jpeg" : mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
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

    public int getChooseModel() {
        return chooseModel;
    }

    public void setChooseModel(int chooseModel) {
        this.chooseModel = chooseModel;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
    }

    @Deprecated
    public int getOrientation() {
        return orientation;
    }

    @Deprecated
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public boolean isMaxSelectEnabledMask() {
        return isMaxSelectEnabledMask;
    }

    public void setMaxSelectEnabledMask(boolean maxSelectEnabledMask) {
        isMaxSelectEnabledMask = maxSelectEnabledMask;
    }

    public long getDateAddedTime() {
        return dateAddedTime;
    }

    public void setDateAddedTime(long dateAddedTime) {
        this.dateAddedTime = dateAddedTime;
    }

    public int getCropImageWidth() {
        return cropImageWidth;
    }

    public void setCropImageWidth(int cropImageWidth) {
        this.cropImageWidth = cropImageWidth;
    }

    public int getCropImageHeight() {
        return cropImageHeight;
    }

    public void setCropImageHeight(int cropImageHeight) {
        this.cropImageHeight = cropImageHeight;
    }

    public int getCropOffsetX() {
        return cropOffsetX;
    }

    public void setCropOffsetX(int cropOffsetX) {
        this.cropOffsetX = cropOffsetX;
    }

    public int getCropOffsetY() {
        return cropOffsetY;
    }

    public void setCropOffsetY(int cropOffsetY) {
        this.cropOffsetY = cropOffsetY;
    }

    public float getCropResultAspectRatio() {
        return cropResultAspectRatio;
    }

    public void setCropResultAspectRatio(float cropResultAspectRatio) {
        this.cropResultAspectRatio = cropResultAspectRatio;
    }

    public boolean isEditorImage() {
        return isEditorImage;
    }

    public void setEditorImage(boolean editorImage) {
        isEditorImage = editorImage;
    }

    @Override
    public String toString() {
        return "LocalMedia{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", realPath='" + realPath + '\'' +
                ", originalPath='" + originalPath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", cutPath='" + cutPath + '\'' +
                ", androidQToPath='" + androidQToPath + '\'' +
                ", duration=" + duration +
                ", isChecked=" + isChecked +
                ", isCut=" + isCut +
                ", position=" + position +
                ", num=" + num +
                ", mimeType='" + mimeType + '\'' +
                ", chooseModel=" + chooseModel +
                ", compressed=" + compressed +
                ", width=" + width +
                ", height=" + height +
                ", cropImageWidth=" + cropImageWidth +
                ", cropImageHeight=" + cropImageHeight +
                ", cropOffsetX=" + cropOffsetX +
                ", cropOffsetY=" + cropOffsetY +
                ", cropResultAspectRatio=" + cropResultAspectRatio +
                ", size=" + size +
                ", isOriginal=" + isOriginal +
                ", fileName='" + fileName + '\'' +
                ", parentFolderName='" + parentFolderName + '\'' +
                ", orientation=" + orientation +
                ", loadLongImageStatus=" + loadLongImageStatus +
                ", isLongImage=" + isLongImage +
                ", bucketId=" + bucketId +
                ", isMaxSelectEnabledMask=" + isMaxSelectEnabledMask +
                ", isEditorImage=" + isEditorImage +
                ", dateAddedTime=" + dateAddedTime +
                '}';
    }
}
