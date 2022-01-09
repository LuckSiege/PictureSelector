package com.luck.picture.lib.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * @author：luck
 * @date：2017-5-24 16:21
 * @describe：Media Entity
 * <a href="https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-LocalMedia%E8%AF%B4%E6%98%8E">
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
     * app sandbox path
     */
    private String sandboxPath;
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
     * bucketId
     */
    private long bucketId = PictureConfig.ALL;

    /**
     * media create time
     */
    private long dateAddedTime;

    /**
     * custom data
     * <p>
     * User defined data can be expanded freely
     * </p>
     */
    private String customData;

    /**
     * isMaxSelectEnabledMask
     * # For internal use only
     */
    private boolean isMaxSelectEnabledMask;

    /**
     * isGalleryEnabledMask
     * # For internal use only
     */
    private boolean isGalleryEnabledMask;

    /**
     * Whether the image has been edited
     * # For internal use only
     */
    private boolean isEditorImage;

    public LocalMedia() {

    }


    protected LocalMedia(Parcel in) {
        id = in.readLong();
        path = in.readString();
        realPath = in.readString();
        originalPath = in.readString();
        compressPath = in.readString();
        cutPath = in.readString();
        sandboxPath = in.readString();
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
        bucketId = in.readLong();
        dateAddedTime = in.readLong();
        customData = in.readString();
        isMaxSelectEnabledMask = in.readByte() != 0;
        isGalleryEnabledMask = in.readByte() != 0;
        isEditorImage = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(realPath);
        dest.writeString(originalPath);
        dest.writeString(compressPath);
        dest.writeString(cutPath);
        dest.writeString(sandboxPath);
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
        dest.writeLong(bucketId);
        dest.writeLong(dateAddedTime);
        dest.writeString(customData);
        dest.writeByte((byte) (isMaxSelectEnabledMask ? 1 : 0));
        dest.writeByte((byte) (isGalleryEnabledMask ? 1 : 0));
        dest.writeByte((byte) (isEditorImage ? 1 : 0));
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

    /**
     * 构造网络资源下的LocalMedia
     *
     * @param url      网络url
     * @param mimeType 资源类型 {@link PictureMimeType.ofJPEG() # PictureMimeType.ofGIF() ...}
     * @return
     */
    public static LocalMedia generateLocalMedia(String url, String mimeType) {
        LocalMedia media = new LocalMedia();
        media.setPath(url);
        media.setMimeType(mimeType);
        return media;
    }

    /**
     * 构造LocalMedia
     *
     * @param id               资源id
     * @param path             资源路径
     * @param absolutePath     资源绝对路径
     * @param fileName         文件名
     * @param parentFolderName 文件所在相册目录名称
     * @param duration         视频/音频时长
     * @param chooseModel      相册选择模式
     * @param mimeType         资源类型
     * @param width            资源宽
     * @param height           资源高
     * @param size             资源大小
     * @param bucketId         文件目录id
     * @param dateAdded        资源添加时间
     * @return
     */
    public static LocalMedia parseLocalMedia(long id, String path, String absolutePath,
                                             String fileName, String parentFolderName,
                                             long duration, int chooseModel, String mimeType,
                                             int width, int height, long size, long bucketId, long dateAdded) {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setId(id);
        localMedia.setPath(path);
        localMedia.setRealPath(absolutePath);
        localMedia.setFileName(fileName);
        localMedia.setParentFolderName(parentFolderName);
        localMedia.setDuration(duration);
        localMedia.setChooseModel(chooseModel);
        localMedia.setMimeType(mimeType);
        localMedia.setWidth(width);
        localMedia.setHeight(height);
        localMedia.setSize(size);
        localMedia.setBucketId(bucketId);
        localMedia.setDateAddedTime(dateAdded);
        return localMedia;
    }


    /**
     * 当前匹配上的对象
     */
    private LocalMedia compareLocalMedia;

    /**
     * 获取当前匹配上的对象
     */
    public LocalMedia getCompareLocalMedia() {
        return compareLocalMedia;
    }

    /**
     * 重写equals进行值的比较
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalMedia)) return false;
        LocalMedia media = (LocalMedia) o;
        boolean isCompare = TextUtils.equals(getPath(), media.getPath()) || getId() == media.getId();
        compareLocalMedia = isCompare ? media : null;
        return isCompare;
    }

    /**
     * get real and effective resource path
     *
     * @return
     */
    public String getAvailablePath() {
        String path;
        if (isCompressed()) {
            path = getCompressPath();
        } else if (isCut()) {
            path = getCutPath();
        } else if (isToSandboxPath()) {
            path = getSandboxPath();
        } else {
            path = getPath();
        }
        return path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
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

    public String getSandboxPath() {
        return sandboxPath;
    }

    public void setSandboxPath(String sandboxPath) {
        this.sandboxPath = sandboxPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCut() {
        return isCut && !TextUtils.isEmpty(getCutPath());
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
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getChooseModel() {
        return chooseModel;
    }

    public void setChooseModel(int chooseModel) {
        this.chooseModel = chooseModel;
    }

    public boolean isCompressed() {
        return compressed && !TextUtils.isEmpty(getCompressPath());
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isOriginal() {
        return isOriginal && !TextUtils.isEmpty(getOriginalPath());
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
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

    public boolean isEditorImage() {
        return isEditorImage && !TextUtils.isEmpty(getCutPath());
    }

    public void setEditorImage(boolean editorImage) {
        isEditorImage = editorImage;
    }

    public long getDateAddedTime() {
        return dateAddedTime;
    }

    public void setDateAddedTime(long dateAddedTime) {
        this.dateAddedTime = dateAddedTime;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public boolean isToSandboxPath(){
        return !TextUtils.isEmpty(getSandboxPath());
    }

    public boolean isGalleryEnabledMask() {
        return isGalleryEnabledMask;
    }

    public void setGalleryEnabledMask(boolean galleryEnabledMask) {
        isGalleryEnabledMask = galleryEnabledMask;
    }
}
