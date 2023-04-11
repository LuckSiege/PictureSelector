package com.luck.picture.lib.entity;


import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.obj.pool.ObjectPools;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;

import java.io.File;

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
     * watermark path
     */
    private String watermarkPath;

    /**
     * video thumbnail path
     */
    private String videoThumbnailPath;

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
     * Camera generated data source，Only for taking photos once
     */
    private boolean isCameraSource;

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
        watermarkPath = in.readString();
        videoThumbnailPath = in.readString();
        sandboxPath = in.readString();
        duration = in.readLong();
        isChecked = in.readByte() != 0;
        isCut = in.readByte() != 0;
        position = in.readInt();
        num = in.readInt();
        mimeType = in.readString();
        chooseModel = in.readInt();
        isCameraSource = in.readByte() != 0;
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
        dest.writeString(watermarkPath);
        dest.writeString(videoThumbnailPath);
        dest.writeString(sandboxPath);
        dest.writeLong(duration);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isCut ? 1 : 0));
        dest.writeInt(position);
        dest.writeInt(num);
        dest.writeString(mimeType);
        dest.writeInt(chooseModel);
        dest.writeByte((byte) (isCameraSource ? 1 : 0));
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
     * @param mimeType 资源类型 {@link PictureMimeType.ofJPEG() # PictureMimeType.ofGIF()}
     * @return
     */
    public static LocalMedia generateHttpAsLocalMedia(String url) {
        LocalMedia media = LocalMedia.create();
        media.setPath(url);
        media.setMimeType(MediaUtils.getMimeTypeFromMediaHttpUrl(url));
        return media;
    }

    /**
     * 构造网络资源下的LocalMedia
     *
     * @param url      网络url
     * @param mimeType 资源类型 {@link PictureMimeType.ofJPEG() # PictureMimeType.ofGIF()}
     * @return
     */
    public static LocalMedia generateHttpAsLocalMedia(String url, String mimeType) {
        LocalMedia media = LocalMedia.create();
        media.setPath(url);
        media.setMimeType(mimeType);
        return media;
    }

    /**
     * 构造本地资源下的LocalMedia
     *
     * @param context 上下文
     * @param path    本地路径
     * @return
     */
    public static LocalMedia generateLocalMedia(Context context, String path) {
        LocalMedia media = LocalMedia.create();
        File cameraFile = PictureMimeType.isContent(path) ? new File(PictureFileUtils.getPath(context, Uri.parse(path))) : new File(path);
        media.setPath(path);
        media.setRealPath(cameraFile.getAbsolutePath());
        media.setFileName(cameraFile.getName());
        media.setParentFolderName(MediaUtils.generateCameraFolderName(cameraFile.getAbsolutePath()));
        media.setMimeType(MediaUtils.getMimeTypeFromMediaUrl(cameraFile.getAbsolutePath()));
        media.setSize(cameraFile.length());
        media.setDateAddedTime(cameraFile.lastModified() / 1000);
        String realPath = cameraFile.getAbsolutePath();
        if (realPath.contains("Android/data/") || realPath.contains("data/user/")) {
            media.setId(System.currentTimeMillis());
            File parentFile = cameraFile.getParentFile();
            media.setBucketId(parentFile != null ? parentFile.getName().hashCode() : 0L);
        } else {
            Long[] mediaBucketId = MediaUtils.getPathMediaBucketId(context, media.getRealPath());
            media.setId(mediaBucketId[0] == 0 ? System.currentTimeMillis() : mediaBucketId[0]);
            media.setBucketId(mediaBucketId[1]);
        }
        MediaExtraInfo mediaExtraInfo;
        if (PictureMimeType.isHasVideo(media.getMimeType())) {
            mediaExtraInfo = MediaUtils.getVideoSize(context, path);
            media.setWidth(mediaExtraInfo.getWidth());
            media.setHeight(mediaExtraInfo.getHeight());
            media.setDuration(mediaExtraInfo.getDuration());
        } else if (PictureMimeType.isHasAudio(media.getMimeType())) {
            mediaExtraInfo = MediaUtils.getAudioSize(context, path);
            media.setDuration(mediaExtraInfo.getDuration());
        } else {
            mediaExtraInfo = MediaUtils.getImageSize(context, path);
            media.setWidth(mediaExtraInfo.getWidth());
            media.setHeight(mediaExtraInfo.getHeight());
        }
        return media;
    }

    /**
     * 构造网络资源下的LocalMedia
     *
     * @param url      网络url
     * @param mimeType 资源类型 {@link PictureMimeType.ofJPEG() # PictureMimeType.ofGIF()}
     *                 Use {@link LocalMedia.generateHttpAsLocalMedia()}
     * @return
     */
    @Deprecated
    public static LocalMedia generateLocalMedia(String url, String mimeType) {
        LocalMedia media = LocalMedia.create();
        media.setPath(url);
        media.setMimeType(mimeType);
        return media;
    }


    /**
     * 创建LocalMedia对象
     *
     * @return
     */
    public static LocalMedia create() {
        return new LocalMedia();
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
        boolean isCompare = TextUtils.equals(getPath(), media.getPath())
                || TextUtils.equals(getRealPath(), media.getRealPath())
                || getId() == media.getId();
        compareLocalMedia = isCompare ? media : null;
        return isCompare;
    }

    /**
     * get real and effective resource path
     *
     * @return
     */
    public String getAvailablePath() {
        String path = getPath();
        if (isCut()) {
            path = getCutPath();
        }
        if (isCompressed()) {
            path = getCompressPath();
        }
        if (isToSandboxPath()) {
            path = getSandboxPath();
        }
        if (isOriginal()) {
            path = getOriginalPath();
        }
        if (isWatermarkPath()) {
            path = getWatermarkPath();
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

    public boolean isCameraSource() {
        return isCameraSource;
    }

    public void setCameraSource(boolean cameraSource) {
        isCameraSource = cameraSource;
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

    public boolean isWatermarkPath(){
        return !TextUtils.isEmpty(getWatermarkPath());
    }

    public boolean isGalleryEnabledMask() {
        return isGalleryEnabledMask;
    }

    public void setGalleryEnabledMask(boolean galleryEnabledMask) {
        isGalleryEnabledMask = galleryEnabledMask;
    }

    public String getWatermarkPath() {
        return watermarkPath;
    }

    public void setWatermarkPath(String watermarkPath) {
        this.watermarkPath = watermarkPath;
    }

    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    /**
     * 对象池
     */
    private static ObjectPools.SynchronizedPool<LocalMedia> sPool;

    /**
     * 从对象池里取LocalMedia
     */
    public static LocalMedia obtain() {
        if (sPool == null) {
            sPool = new ObjectPools.SynchronizedPool<>();
        }
        LocalMedia media = sPool.acquire();
        if (media == null) {
            return LocalMedia.create();
        } else {
            return media;
        }
    }

    /**
     * 回收对象池
     */
    public void recycle() {
        if (sPool != null) {
            sPool.release(this);
        }
    }

    /**
     * 销毁对象池
     */
    public static void destroyPool() {
        if (sPool != null) {
            sPool.destroy();
            sPool = null;
        }
    }
}
