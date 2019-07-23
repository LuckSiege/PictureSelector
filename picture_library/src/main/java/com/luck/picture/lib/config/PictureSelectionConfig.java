package com.luck.picture.lib.config;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.StyleRes;

import com.luck.picture.lib.R;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureSelectionConfig implements Parcelable {
    public int mimeType;
    public boolean camera;
    public String outputCameraPath;
    public String compressSavePath;
    public String suffixType;
    @StyleRes
    public int themeStyleId;
    public int selectionMode;
    public int maxSelectNum;
    public int minSelectNum;
    public int videoQuality;
    public int cropCompressQuality;
    public int videoMaxSecond;
    public int videoMinSecond;
    public int recordVideoSecond;
    public int minimumCompressSize;
    public int imageSpanCount;
    public int overrideWidth;
    public int overrideHeight;
    public int aspect_ratio_x;
    public int aspect_ratio_y;
    public float sizeMultiplier;
    public int cropWidth;
    public int cropHeight;
    public boolean zoomAnim;
    public boolean isCompress;
    public boolean isCamera;
    public boolean isGif;
    public boolean enablePreview;
    public boolean enPreviewVideo;
    public boolean enablePreviewAudio;
    public boolean checkNumMode;
    public boolean openClickSound;
    public boolean enableCrop;
    public boolean freeStyleCropEnabled;
    public boolean circleDimmedLayer;
    public boolean showCropFrame;
    public boolean showCropGrid;
    public boolean hideBottomControls;
    public boolean rotateEnabled;
    public boolean scaleEnabled;
    public boolean previewEggs;
    public boolean synOrAsy;
    public boolean isDragFrame;

    public List<LocalMedia> selectionMedias;

    private void reset() {
        mimeType = PictureConfig.TYPE_IMAGE;
        camera = false;
        themeStyleId = R.style.picture_default_style;
        selectionMode = PictureConfig.MULTIPLE;
        maxSelectNum = 9;
        minSelectNum = 0;
        videoQuality = 1;
        cropCompressQuality = 90;
        videoMaxSecond = 0;
        videoMinSecond = 0;
        recordVideoSecond = 60;
        minimumCompressSize = PictureConfig.MAX_COMPRESS_SIZE;
        imageSpanCount = 4;
        overrideWidth = 0;
        overrideHeight = 0;
        isCompress = false;
        aspect_ratio_x = 0;
        aspect_ratio_y = 0;
        cropWidth = 0;
        cropHeight = 0;
        isCamera = true;
        isGif = false;
        enablePreview = true;
        enPreviewVideo = true;
        enablePreviewAudio = true;
        checkNumMode = false;
        openClickSound = false;
        enableCrop = false;
        freeStyleCropEnabled = false;
        circleDimmedLayer = false;
        showCropFrame = true;
        showCropGrid = true;
        hideBottomControls = true;
        rotateEnabled = true;
        scaleEnabled = true;
        previewEggs = false;
        synOrAsy = true;
        zoomAnim = true;
        isDragFrame = true;
        outputCameraPath = "";
        compressSavePath = "";
        suffixType = PictureFileUtils.POSTFIX;
        sizeMultiplier = 0.5f;
        selectionMedias = new ArrayList<>();
    }

    public static PictureSelectionConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static PictureSelectionConfig getCleanInstance() {
        PictureSelectionConfig selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private static final class InstanceHolder {
        private static final PictureSelectionConfig INSTANCE = new PictureSelectionConfig();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mimeType);
        dest.writeByte(this.camera ? (byte) 1 : (byte) 0);
        dest.writeString(this.outputCameraPath);
        dest.writeString(this.compressSavePath);
        dest.writeString(this.suffixType);
        dest.writeInt(this.themeStyleId);
        dest.writeInt(this.selectionMode);
        dest.writeInt(this.maxSelectNum);
        dest.writeInt(this.minSelectNum);
        dest.writeInt(this.videoQuality);
        dest.writeInt(this.cropCompressQuality);
        dest.writeInt(this.videoMaxSecond);
        dest.writeInt(this.videoMinSecond);
        dest.writeInt(this.recordVideoSecond);
        dest.writeInt(this.minimumCompressSize);
        dest.writeInt(this.imageSpanCount);
        dest.writeInt(this.overrideWidth);
        dest.writeInt(this.overrideHeight);
        dest.writeInt(this.aspect_ratio_x);
        dest.writeInt(this.aspect_ratio_y);
        dest.writeFloat(this.sizeMultiplier);
        dest.writeInt(this.cropWidth);
        dest.writeInt(this.cropHeight);
        dest.writeByte(this.zoomAnim ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCompress ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGif ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enablePreview ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enPreviewVideo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enablePreviewAudio ? (byte) 1 : (byte) 0);
        dest.writeByte(this.checkNumMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openClickSound ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.freeStyleCropEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.circleDimmedLayer ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCropFrame ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCropGrid ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideBottomControls ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rotateEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.scaleEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.previewEggs ? (byte) 1 : (byte) 0);
        dest.writeByte(this.synOrAsy ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDragFrame ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.selectionMedias);
    }

    public PictureSelectionConfig() {
    }

    protected PictureSelectionConfig(Parcel in) {
        this.mimeType = in.readInt();
        this.camera = in.readByte() != 0;
        this.outputCameraPath = in.readString();
        this.compressSavePath = in.readString();
        this.suffixType = in.readString();
        this.themeStyleId = in.readInt();
        this.selectionMode = in.readInt();
        this.maxSelectNum = in.readInt();
        this.minSelectNum = in.readInt();
        this.videoQuality = in.readInt();
        this.cropCompressQuality = in.readInt();
        this.videoMaxSecond = in.readInt();
        this.videoMinSecond = in.readInt();
        this.recordVideoSecond = in.readInt();
        this.minimumCompressSize = in.readInt();
        this.imageSpanCount = in.readInt();
        this.overrideWidth = in.readInt();
        this.overrideHeight = in.readInt();
        this.aspect_ratio_x = in.readInt();
        this.aspect_ratio_y = in.readInt();
        this.sizeMultiplier = in.readFloat();
        this.cropWidth = in.readInt();
        this.cropHeight = in.readInt();
        this.zoomAnim = in.readByte() != 0;
        this.isCompress = in.readByte() != 0;
        this.isCamera = in.readByte() != 0;
        this.isGif = in.readByte() != 0;
        this.enablePreview = in.readByte() != 0;
        this.enPreviewVideo = in.readByte() != 0;
        this.enablePreviewAudio = in.readByte() != 0;
        this.checkNumMode = in.readByte() != 0;
        this.openClickSound = in.readByte() != 0;
        this.enableCrop = in.readByte() != 0;
        this.freeStyleCropEnabled = in.readByte() != 0;
        this.circleDimmedLayer = in.readByte() != 0;
        this.showCropFrame = in.readByte() != 0;
        this.showCropGrid = in.readByte() != 0;
        this.hideBottomControls = in.readByte() != 0;
        this.rotateEnabled = in.readByte() != 0;
        this.scaleEnabled = in.readByte() != 0;
        this.previewEggs = in.readByte() != 0;
        this.synOrAsy = in.readByte() != 0;
        this.isDragFrame = in.readByte() != 0;
        this.selectionMedias = in.createTypedArrayList(LocalMedia.CREATOR);
    }

    public static final Parcelable.Creator<PictureSelectionConfig> CREATOR = new Parcelable.Creator<PictureSelectionConfig>() {
        @Override
        public PictureSelectionConfig createFromParcel(Parcel source) {
            return new PictureSelectionConfig(source);
        }

        @Override
        public PictureSelectionConfig[] newArray(int size) {
            return new PictureSelectionConfig[size];
        }
    };
}
