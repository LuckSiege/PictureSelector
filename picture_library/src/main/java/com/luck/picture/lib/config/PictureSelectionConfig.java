package com.luck.picture.lib.config;

import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;

import com.luck.picture.lib.R;
import com.luck.picture.lib.camera.CustomCameraView;
import com.luck.picture.lib.engine.CacheResourcesEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnCustomCameraInterfaceListener;
import com.luck.picture.lib.listener.OnCustomImagePreviewCallback;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.listener.OnVideoSelectedPlayCallback;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2017-05-24 17:02
 * @describe：PictureSelector Config
 */

public final class PictureSelectionConfig implements Parcelable {
    public int chooseMode;
    public boolean camera;
    public boolean isSingleDirectReturn;
    public PictureParameterStyle style;
    public PictureCropParameterStyle cropStyle;
    public PictureWindowAnimationStyle windowAnimationStyle;
    public String compressSavePath;
    public String suffixType;
    public boolean focusAlpha;
    public String renameCompressFileName;
    public String renameCropFileName;
    public String specifiedFormat;
    public int requestedOrientation;
    public int buttonFeatures;
    public boolean isCameraAroundState;
    public boolean isAndroidQTransform;
    @StyleRes
    public int themeStyleId;
    public int selectionMode;
    public int maxSelectNum;
    public int minSelectNum;
    public int maxVideoSelectNum;
    public int minVideoSelectNum;
    public int videoQuality;
    public int cropCompressQuality;
    public int videoMaxSecond;
    public int videoMinSecond;
    public int recordVideoSecond;
    public int recordVideoMinSecond;
    public int minimumCompressSize;
    public int imageSpanCount;
    public int aspect_ratio_x;
    public int aspect_ratio_y;
    public int cropWidth;
    public int cropHeight;
    public int compressQuality;
    public float filterFileSize;
    public int language;
    public boolean isMultipleRecyclerAnimation;
    public boolean isMultipleSkipCrop;
    public boolean isWeChatStyle;
    public boolean isUseCustomCamera;
    public boolean zoomAnim;
    public boolean isCompress;
    public boolean isOriginalControl;
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
    @ColorInt
    public int circleDimmedColor;
    @ColorInt
    public int circleDimmedBorderColor;
    public int circleStrokeWidth;
    public boolean showCropFrame;
    public boolean showCropGrid;
    public boolean hideBottomControls;
    public boolean rotateEnabled;
    public boolean scaleEnabled;
    public boolean previewEggs;
    public boolean synOrAsy;
    public boolean returnEmpty;
    public boolean isDragFrame;
    public boolean isNotPreviewDownload;
    public boolean isWithVideoImage;
    public UCropOptions uCropOptions;
    public static ImageEngine imageEngine;
    public static CacheResourcesEngine cacheResourcesEngine;
    public static OnResultCallbackListener listener;
    public static OnVideoSelectedPlayCallback customVideoPlayCallback;
    public static OnCustomImagePreviewCallback onCustomImagePreviewCallback;
    public static OnCustomCameraInterfaceListener onCustomCameraInterfaceListener;
    public List<LocalMedia> selectionMedias;
    public String cameraFileName;
    public boolean isCheckOriginalImage;
    @Deprecated
    public int overrideWidth;
    @Deprecated
    public int overrideHeight;
    @Deprecated
    public float sizeMultiplier;
    @Deprecated
    public boolean isChangeStatusBarFontColor;
    @Deprecated
    public boolean isOpenStyleNumComplete;
    @Deprecated
    public boolean isOpenStyleCheckNumMode;
    @Deprecated
    public int titleBarBackgroundColor;
    @Deprecated
    public int pictureStatusBarColor;
    @Deprecated
    public int cropTitleBarBackgroundColor;
    @Deprecated
    public int cropStatusBarColorPrimaryDark;
    @Deprecated
    public int cropTitleColor;
    @Deprecated
    public int upResId;
    @Deprecated
    public int downResId;
    public String outPutCameraPath;

    public String originalPath;
    public String cameraPath;
    public int cameraMimeType;
    public int pageSize;
    public boolean isPageStrategy;
    public boolean isFilterInvalidFile;
    public boolean isMaxSelectEnabledMask;
    public int animationMode;
    public boolean isAutomaticTitleRecyclerTop;
    public boolean isCallbackMode;
    public boolean isAndroidQChangeWH;
    public boolean isAndroidQChangeVideoWH;
    public boolean isQuickCapture;
    /**
     * 内测专用###########
     */
    public boolean isFallbackVersion;
    public boolean isFallbackVersion2;
    public boolean isFallbackVersion3;

    protected void initDefaultValue() {
        chooseMode = PictureMimeType.ofImage();
        camera = false;
        themeStyleId = R.style.picture_default_style;
        selectionMode = PictureConfig.MULTIPLE;
        maxSelectNum = 9;
        minSelectNum = 0;
        maxVideoSelectNum = 0;
        minVideoSelectNum = 0;
        videoQuality = 1;
        language = -1;
        cropCompressQuality = 90;
        videoMaxSecond = 0;
        videoMinSecond = 0;
        filterFileSize = -1;
        recordVideoSecond = 60;
        recordVideoMinSecond = 0;
        compressQuality = 80;
        minimumCompressSize = PictureConfig.MAX_COMPRESS_SIZE;
        imageSpanCount = 4;
        isCompress = false;
        isOriginalControl = false;
        aspect_ratio_x = 0;
        aspect_ratio_y = 0;
        cropWidth = 0;
        cropHeight = 0;
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        buttonFeatures = CustomCameraView.BUTTON_STATE_BOTH;  //初始化按钮为可录制可拍照
        isCameraAroundState = false;
        isWithVideoImage = false;
        isAndroidQTransform = true;
        isCamera = true;
        isGif = false;
        focusAlpha = false;
        isCheckOriginalImage = false;
        isSingleDirectReturn = false;
        enablePreview = true;
        enPreviewVideo = true;
        enablePreviewAudio = true;
        checkNumMode = false;
        isNotPreviewDownload = false;
        openClickSound = false;
        isFallbackVersion = false;
        isFallbackVersion2 = true;
        isFallbackVersion3 = true;
        enableCrop = false;
        isWeChatStyle = false;
        isUseCustomCamera = false;
        isMultipleSkipCrop = true;
        isMultipleRecyclerAnimation = true;
        freeStyleCropEnabled = false;
        circleDimmedLayer = false;
        showCropFrame = true;
        showCropGrid = true;
        hideBottomControls = true;
        rotateEnabled = true;
        scaleEnabled = true;
        previewEggs = false;
        returnEmpty = false;
        synOrAsy = true;
        zoomAnim = true;
        circleDimmedColor = 0;
        circleDimmedBorderColor = 0;
        circleStrokeWidth = 1;
        isDragFrame = true;
        compressSavePath = "";
        suffixType = "";
        cameraFileName = "";
        specifiedFormat = "";
        renameCompressFileName = "";
        renameCropFileName = "";
        selectionMedias = new ArrayList<>();
        uCropOptions = null;
        style = null;
        cropStyle = null;
        windowAnimationStyle = null;
        titleBarBackgroundColor = 0;
        pictureStatusBarColor = 0;
        cropTitleBarBackgroundColor = 0;
        cropStatusBarColorPrimaryDark = 0;
        cropTitleColor = 0;
        upResId = 0;
        downResId = 0;
        isChangeStatusBarFontColor = false;
        isOpenStyleNumComplete = false;
        isOpenStyleCheckNumMode = false;
        outPutCameraPath = "";
        sizeMultiplier = 0.5f;
        overrideWidth = 0;
        overrideHeight = 0;
        originalPath = "";
        cameraPath = "";
        cameraMimeType = -1;
        pageSize = PictureConfig.MAX_PAGE_SIZE;
        isPageStrategy = true;
        isFilterInvalidFile = false;
        isMaxSelectEnabledMask = false;
        animationMode = -1;
        isAutomaticTitleRecyclerTop = true;
        isCallbackMode = false;
        isAndroidQChangeWH = true;
        isAndroidQChangeVideoWH = false;
        isQuickCapture = true;
    }

    public static PictureSelectionConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static PictureSelectionConfig getCleanInstance() {
        PictureSelectionConfig selectionSpec = getInstance();
        selectionSpec.initDefaultValue();
        return selectionSpec;
    }

    private static final class InstanceHolder {
        private static final PictureSelectionConfig INSTANCE = new PictureSelectionConfig();
    }

    public PictureSelectionConfig() {
    }

    /**
     * 释放监听器
     */
    public static void destroy() {
        PictureSelectionConfig.listener = null;
        PictureSelectionConfig.customVideoPlayCallback = null;
        PictureSelectionConfig.onCustomImagePreviewCallback = null;
        PictureSelectionConfig.onCustomCameraInterfaceListener = null;
        PictureSelectionConfig.onCustomCameraInterfaceListener = null;
        PictureSelectionConfig.cacheResourcesEngine = null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.chooseMode);
        dest.writeByte(this.camera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSingleDirectReturn ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.style, flags);
        dest.writeParcelable(this.cropStyle, flags);
        dest.writeParcelable(this.windowAnimationStyle, flags);
        dest.writeString(this.compressSavePath);
        dest.writeString(this.suffixType);
        dest.writeByte(this.focusAlpha ? (byte) 1 : (byte) 0);
        dest.writeString(this.renameCompressFileName);
        dest.writeString(this.renameCropFileName);
        dest.writeString(this.specifiedFormat);
        dest.writeInt(this.requestedOrientation);
        dest.writeInt(this.buttonFeatures);
        dest.writeByte(this.isCameraAroundState ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAndroidQTransform ? (byte) 1 : (byte) 0);
        dest.writeInt(this.themeStyleId);
        dest.writeInt(this.selectionMode);
        dest.writeInt(this.maxSelectNum);
        dest.writeInt(this.minSelectNum);
        dest.writeInt(this.maxVideoSelectNum);
        dest.writeInt(this.minVideoSelectNum);
        dest.writeInt(this.videoQuality);
        dest.writeInt(this.cropCompressQuality);
        dest.writeInt(this.videoMaxSecond);
        dest.writeInt(this.videoMinSecond);
        dest.writeInt(this.recordVideoSecond);
        dest.writeInt(this.recordVideoMinSecond);
        dest.writeInt(this.minimumCompressSize);
        dest.writeInt(this.imageSpanCount);
        dest.writeInt(this.aspect_ratio_x);
        dest.writeInt(this.aspect_ratio_y);
        dest.writeInt(this.cropWidth);
        dest.writeInt(this.cropHeight);
        dest.writeInt(this.compressQuality);
        dest.writeFloat(this.filterFileSize);
        dest.writeInt(this.language);
        dest.writeByte(this.isMultipleRecyclerAnimation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMultipleSkipCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isWeChatStyle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUseCustomCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.zoomAnim ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCompress ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOriginalControl ? (byte) 1 : (byte) 0);
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
        dest.writeInt(this.circleDimmedColor);
        dest.writeInt(this.circleDimmedBorderColor);
        dest.writeInt(this.circleStrokeWidth);
        dest.writeByte(this.showCropFrame ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCropGrid ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideBottomControls ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rotateEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.scaleEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.previewEggs ? (byte) 1 : (byte) 0);
        dest.writeByte(this.synOrAsy ? (byte) 1 : (byte) 0);
        dest.writeByte(this.returnEmpty ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDragFrame ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNotPreviewDownload ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isWithVideoImage ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.uCropOptions, flags);
        dest.writeTypedList(this.selectionMedias);
        dest.writeString(this.cameraFileName);
        dest.writeByte(this.isCheckOriginalImage ? (byte) 1 : (byte) 0);
        dest.writeInt(this.overrideWidth);
        dest.writeInt(this.overrideHeight);
        dest.writeFloat(this.sizeMultiplier);
        dest.writeByte(this.isChangeStatusBarFontColor ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOpenStyleNumComplete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOpenStyleCheckNumMode ? (byte) 1 : (byte) 0);
        dest.writeInt(this.titleBarBackgroundColor);
        dest.writeInt(this.pictureStatusBarColor);
        dest.writeInt(this.cropTitleBarBackgroundColor);
        dest.writeInt(this.cropStatusBarColorPrimaryDark);
        dest.writeInt(this.cropTitleColor);
        dest.writeInt(this.upResId);
        dest.writeInt(this.downResId);
        dest.writeString(this.outPutCameraPath);
        dest.writeString(this.originalPath);
        dest.writeString(this.cameraPath);
        dest.writeInt(this.cameraMimeType);
        dest.writeInt(this.pageSize);
        dest.writeByte(this.isPageStrategy ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFilterInvalidFile ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMaxSelectEnabledMask ? (byte) 1 : (byte) 0);
        dest.writeInt(this.animationMode);
        dest.writeByte(this.isAutomaticTitleRecyclerTop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCallbackMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAndroidQChangeWH ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAndroidQChangeVideoWH ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isQuickCapture ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFallbackVersion ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFallbackVersion2 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFallbackVersion3 ? (byte) 1 : (byte) 0);
    }

    protected PictureSelectionConfig(Parcel in) {
        this.chooseMode = in.readInt();
        this.camera = in.readByte() != 0;
        this.isSingleDirectReturn = in.readByte() != 0;
        this.style = in.readParcelable(PictureParameterStyle.class.getClassLoader());
        this.cropStyle = in.readParcelable(PictureCropParameterStyle.class.getClassLoader());
        this.windowAnimationStyle = in.readParcelable(PictureWindowAnimationStyle.class.getClassLoader());
        this.compressSavePath = in.readString();
        this.suffixType = in.readString();
        this.focusAlpha = in.readByte() != 0;
        this.renameCompressFileName = in.readString();
        this.renameCropFileName = in.readString();
        this.specifiedFormat = in.readString();
        this.requestedOrientation = in.readInt();
        this.buttonFeatures = in.readInt();
        this.isCameraAroundState = in.readByte() != 0;
        this.isAndroidQTransform = in.readByte() != 0;
        this.themeStyleId = in.readInt();
        this.selectionMode = in.readInt();
        this.maxSelectNum = in.readInt();
        this.minSelectNum = in.readInt();
        this.maxVideoSelectNum = in.readInt();
        this.minVideoSelectNum = in.readInt();
        this.videoQuality = in.readInt();
        this.cropCompressQuality = in.readInt();
        this.videoMaxSecond = in.readInt();
        this.videoMinSecond = in.readInt();
        this.recordVideoSecond = in.readInt();
        this.recordVideoMinSecond = in.readInt();
        this.minimumCompressSize = in.readInt();
        this.imageSpanCount = in.readInt();
        this.aspect_ratio_x = in.readInt();
        this.aspect_ratio_y = in.readInt();
        this.cropWidth = in.readInt();
        this.cropHeight = in.readInt();
        this.compressQuality = in.readInt();
        this.filterFileSize = in.readFloat();
        this.language = in.readInt();
        this.isMultipleRecyclerAnimation = in.readByte() != 0;
        this.isMultipleSkipCrop = in.readByte() != 0;
        this.isWeChatStyle = in.readByte() != 0;
        this.isUseCustomCamera = in.readByte() != 0;
        this.zoomAnim = in.readByte() != 0;
        this.isCompress = in.readByte() != 0;
        this.isOriginalControl = in.readByte() != 0;
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
        this.circleDimmedColor = in.readInt();
        this.circleDimmedBorderColor = in.readInt();
        this.circleStrokeWidth = in.readInt();
        this.showCropFrame = in.readByte() != 0;
        this.showCropGrid = in.readByte() != 0;
        this.hideBottomControls = in.readByte() != 0;
        this.rotateEnabled = in.readByte() != 0;
        this.scaleEnabled = in.readByte() != 0;
        this.previewEggs = in.readByte() != 0;
        this.synOrAsy = in.readByte() != 0;
        this.returnEmpty = in.readByte() != 0;
        this.isDragFrame = in.readByte() != 0;
        this.isNotPreviewDownload = in.readByte() != 0;
        this.isWithVideoImage = in.readByte() != 0;
        this.uCropOptions = in.readParcelable(UCropOptions.class.getClassLoader());
        this.selectionMedias = in.createTypedArrayList(LocalMedia.CREATOR);
        this.cameraFileName = in.readString();
        this.isCheckOriginalImage = in.readByte() != 0;
        this.overrideWidth = in.readInt();
        this.overrideHeight = in.readInt();
        this.sizeMultiplier = in.readFloat();
        this.isChangeStatusBarFontColor = in.readByte() != 0;
        this.isOpenStyleNumComplete = in.readByte() != 0;
        this.isOpenStyleCheckNumMode = in.readByte() != 0;
        this.titleBarBackgroundColor = in.readInt();
        this.pictureStatusBarColor = in.readInt();
        this.cropTitleBarBackgroundColor = in.readInt();
        this.cropStatusBarColorPrimaryDark = in.readInt();
        this.cropTitleColor = in.readInt();
        this.upResId = in.readInt();
        this.downResId = in.readInt();
        this.outPutCameraPath = in.readString();
        this.originalPath = in.readString();
        this.cameraPath = in.readString();
        this.cameraMimeType = in.readInt();
        this.pageSize = in.readInt();
        this.isPageStrategy = in.readByte() != 0;
        this.isFilterInvalidFile = in.readByte() != 0;
        this.isMaxSelectEnabledMask = in.readByte() != 0;
        this.animationMode = in.readInt();
        this.isAutomaticTitleRecyclerTop = in.readByte() != 0;
        this.isCallbackMode = in.readByte() != 0;
        this.isAndroidQChangeWH = in.readByte() != 0;
        this.isAndroidQChangeVideoWH = in.readByte() != 0;
        this.isQuickCapture = in.readByte() != 0;
        this.isFallbackVersion = in.readByte() != 0;
        this.isFallbackVersion2 = in.readByte() != 0;
        this.isFallbackVersion3 = in.readByte() != 0;
    }

    public static final Creator<PictureSelectionConfig> CREATOR = new Creator<PictureSelectionConfig>() {
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
