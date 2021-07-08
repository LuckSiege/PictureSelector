package com.luck.picture.lib.config;

import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;

import com.luck.picture.lib.R;
import com.luck.picture.lib.camera.CustomCameraView;
import com.luck.picture.lib.engine.CacheResourcesEngine;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnChooseLimitCallback;
import com.luck.picture.lib.listener.OnCustomCameraInterfaceListener;
import com.luck.picture.lib.listener.OnCustomImagePreviewCallback;
import com.luck.picture.lib.listener.OnPermissionsObtainCallback;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.listener.OnVideoSelectedPlayCallback;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureSelectorUIStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author：luck
 * @date：2017-05-24 17:02
 * @describe：PictureSelector Config
 */

public final class PictureSelectionConfig implements Parcelable {
    public int chooseMode = PictureMimeType.ofImage();
    public boolean camera = false;
    public boolean isSingleDirectReturn;
    public static PictureSelectorUIStyle uiStyle;
    @Deprecated
    public static PictureParameterStyle style;
    public static PictureCropParameterStyle cropStyle;
    public static PictureWindowAnimationStyle windowAnimationStyle = PictureWindowAnimationStyle.ofDefaultWindowAnimationStyle();
    public String compressSavePath;
    @Deprecated
    public String suffixType;
    public String cameraImageFormat;
    public String cameraVideoFormat;
    public String cameraAudioFormat;
    @Deprecated
    public boolean focusAlpha;
    public String renameCompressFileName;
    public String renameCropFileName;
    @Deprecated
    public String specifiedFormat;
    public int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    public int buttonFeatures = CustomCameraView.BUTTON_STATE_BOTH;
    public int captureLoadingColor;
    public boolean isCameraAroundState;
    public boolean isAndroidQTransform;
    @StyleRes
    public int themeStyleId = R.style.picture_default_style;
    public int selectionMode = PictureConfig.MULTIPLE;
    public int maxSelectNum = 9;
    public int minSelectNum = 0;
    public int maxVideoSelectNum = 1;
    public int minVideoSelectNum = 0;
    public int videoQuality = 1;
    public int cropCompressQuality = 90;
    public int videoMaxSecond;
    public int videoMinSecond;
    public int recordVideoSecond = 60;
    public int recordVideoMinSecond;
    public int minimumCompressSize = PictureConfig.MAX_COMPRESS_SIZE;
    public int imageSpanCount = PictureConfig.DEFAULT_SPAN_COUNT;
    public int aspect_ratio_x;
    public int aspect_ratio_y;
    public int cropWidth;
    public int cropHeight;
    public int compressQuality = 80;
    @Deprecated
    public float filterFileSize;
    public long filterMaxFileSize;
    public long filterMinFileSize = 1024;
    public int language;
    public boolean isMultipleRecyclerAnimation;
    public boolean isMultipleSkipCrop;
    public boolean isWeChatStyle;
    public boolean isUseCustomCamera;
    public boolean zoomAnim;
    public boolean isCompress;
    public boolean isOriginalControl;
    public boolean isDisplayOriginalSize;
    public boolean isEditorImage;
    public boolean isCamera = true;
    public boolean isGif;
    public boolean isWebp;
    public boolean isBmp;
    public boolean enablePreview;
    public boolean enPreviewVideo;
    public boolean enablePreviewAudio;
    public boolean checkNumMode;
    public boolean openClickSound;
    public boolean enableCrop;
    public boolean freeStyleCropEnabled;
    public boolean isDragCenter;
    public boolean circleDimmedLayer;
    @ColorInt
    public int circleDimmedColor;
    @ColorInt
    public int circleDimmedBorderColor;
    public int circleStrokeWidth;
    public int freeStyleCropMode;
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
    public UCrop.Options uCropOptions;
    public static ImageEngine imageEngine;
    public static CompressEngine compressEngine;
    public static CacheResourcesEngine cacheResourcesEngine;
    public static OnResultCallbackListener<LocalMedia> listener;
    public static OnVideoSelectedPlayCallback<LocalMedia> customVideoPlayCallback;
    public static OnCustomImagePreviewCallback<LocalMedia> onCustomImagePreviewCallback;
    public static OnCustomCameraInterfaceListener onCustomCameraInterfaceListener;
    public static OnPermissionsObtainCallback onPermissionsObtainCallback;
    public static OnChooseLimitCallback onChooseLimitCallback;
    public List<LocalMedia> selectionMedias;
    public HashSet<String> queryMimeTypeHashSet;
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
    public int cameraMimeType = -1;
    public int pageSize = PictureConfig.MAX_PAGE_SIZE;
    public boolean isPageStrategy = true;
    public boolean isFilterInvalidFile;
    public boolean isMaxSelectEnabledMask;
    public int animationMode = -1;
    public boolean isAutomaticTitleRecyclerTop = true;
    public boolean isCallbackMode;
    @Deprecated
    public boolean isAndroidQChangeWH;
    @Deprecated
    public boolean isAndroidQChangeVideoWH;
    public boolean isQuickCapture = true;
    public boolean isCameraRotateImage = true;
    public boolean isAutoRotating = true;
    public boolean isSyncCover = false;
    public String cropCompressFormat;
    public boolean isAutoScalePreviewImage = true;
    /**
     * 内测专用###########
     */
    public boolean isFallbackVersion;
    public boolean isFallbackVersion2;
    public boolean isFallbackVersion3;


    protected PictureSelectionConfig(Parcel in) {
        chooseMode = in.readInt();
        camera = in.readByte() != 0;
        isSingleDirectReturn = in.readByte() != 0;
        compressSavePath = in.readString();
        suffixType = in.readString();
        cameraImageFormat = in.readString();
        cameraVideoFormat = in.readString();
        cameraAudioFormat = in.readString();
        focusAlpha = in.readByte() != 0;
        renameCompressFileName = in.readString();
        renameCropFileName = in.readString();
        specifiedFormat = in.readString();
        requestedOrientation = in.readInt();
        buttonFeatures = in.readInt();
        captureLoadingColor = in.readInt();
        isCameraAroundState = in.readByte() != 0;
        isAndroidQTransform = in.readByte() != 0;
        themeStyleId = in.readInt();
        selectionMode = in.readInt();
        maxSelectNum = in.readInt();
        minSelectNum = in.readInt();
        maxVideoSelectNum = in.readInt();
        minVideoSelectNum = in.readInt();
        videoQuality = in.readInt();
        cropCompressQuality = in.readInt();
        videoMaxSecond = in.readInt();
        videoMinSecond = in.readInt();
        recordVideoSecond = in.readInt();
        recordVideoMinSecond = in.readInt();
        minimumCompressSize = in.readInt();
        imageSpanCount = in.readInt();
        aspect_ratio_x = in.readInt();
        aspect_ratio_y = in.readInt();
        cropWidth = in.readInt();
        cropHeight = in.readInt();
        compressQuality = in.readInt();
        filterFileSize = in.readFloat();
        filterMaxFileSize = in.readLong();
        filterMinFileSize = in.readLong();
        language = in.readInt();
        isMultipleRecyclerAnimation = in.readByte() != 0;
        isMultipleSkipCrop = in.readByte() != 0;
        isWeChatStyle = in.readByte() != 0;
        isUseCustomCamera = in.readByte() != 0;
        zoomAnim = in.readByte() != 0;
        isCompress = in.readByte() != 0;
        isOriginalControl = in.readByte() != 0;
        isDisplayOriginalSize = in.readByte() != 0;
        isEditorImage = in.readByte() != 0;
        isCamera = in.readByte() != 0;
        isGif = in.readByte() != 0;
        isWebp = in.readByte() != 0;
        isBmp = in.readByte() != 0;
        enablePreview = in.readByte() != 0;
        enPreviewVideo = in.readByte() != 0;
        enablePreviewAudio = in.readByte() != 0;
        checkNumMode = in.readByte() != 0;
        openClickSound = in.readByte() != 0;
        enableCrop = in.readByte() != 0;
        freeStyleCropEnabled = in.readByte() != 0;
        isDragCenter = in.readByte() != 0;
        circleDimmedLayer = in.readByte() != 0;
        circleDimmedColor = in.readInt();
        circleDimmedBorderColor = in.readInt();
        circleStrokeWidth = in.readInt();
        freeStyleCropMode = in.readInt();
        showCropFrame = in.readByte() != 0;
        showCropGrid = in.readByte() != 0;
        hideBottomControls = in.readByte() != 0;
        rotateEnabled = in.readByte() != 0;
        scaleEnabled = in.readByte() != 0;
        previewEggs = in.readByte() != 0;
        synOrAsy = in.readByte() != 0;
        returnEmpty = in.readByte() != 0;
        isDragFrame = in.readByte() != 0;
        isNotPreviewDownload = in.readByte() != 0;
        isWithVideoImage = in.readByte() != 0;
        selectionMedias = in.createTypedArrayList(LocalMedia.CREATOR);
        cameraFileName = in.readString();
        isCheckOriginalImage = in.readByte() != 0;
        overrideWidth = in.readInt();
        overrideHeight = in.readInt();
        sizeMultiplier = in.readFloat();
        isChangeStatusBarFontColor = in.readByte() != 0;
        isOpenStyleNumComplete = in.readByte() != 0;
        isOpenStyleCheckNumMode = in.readByte() != 0;
        titleBarBackgroundColor = in.readInt();
        pictureStatusBarColor = in.readInt();
        cropTitleBarBackgroundColor = in.readInt();
        cropStatusBarColorPrimaryDark = in.readInt();
        cropTitleColor = in.readInt();
        upResId = in.readInt();
        downResId = in.readInt();
        outPutCameraPath = in.readString();
        originalPath = in.readString();
        cameraPath = in.readString();
        cameraMimeType = in.readInt();
        pageSize = in.readInt();
        isPageStrategy = in.readByte() != 0;
        isFilterInvalidFile = in.readByte() != 0;
        isMaxSelectEnabledMask = in.readByte() != 0;
        animationMode = in.readInt();
        isAutomaticTitleRecyclerTop = in.readByte() != 0;
        isCallbackMode = in.readByte() != 0;
        isAndroidQChangeWH = in.readByte() != 0;
        isAndroidQChangeVideoWH = in.readByte() != 0;
        isQuickCapture = in.readByte() != 0;
        isCameraRotateImage = in.readByte() != 0;
        isAutoRotating = in.readByte() != 0;
        isSyncCover = in.readByte() != 0;
        cropCompressFormat = in.readString();
        isAutoScalePreviewImage = in.readByte() != 0;
        isFallbackVersion = in.readByte() != 0;
        isFallbackVersion2 = in.readByte() != 0;
        isFallbackVersion3 = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chooseMode);
        dest.writeByte((byte) (camera ? 1 : 0));
        dest.writeByte((byte) (isSingleDirectReturn ? 1 : 0));
        dest.writeString(compressSavePath);
        dest.writeString(suffixType);
        dest.writeString(cameraImageFormat);
        dest.writeString(cameraVideoFormat);
        dest.writeString(cameraAudioFormat);
        dest.writeByte((byte) (focusAlpha ? 1 : 0));
        dest.writeString(renameCompressFileName);
        dest.writeString(renameCropFileName);
        dest.writeString(specifiedFormat);
        dest.writeInt(requestedOrientation);
        dest.writeInt(buttonFeatures);
        dest.writeInt(captureLoadingColor);
        dest.writeByte((byte) (isCameraAroundState ? 1 : 0));
        dest.writeByte((byte) (isAndroidQTransform ? 1 : 0));
        dest.writeInt(themeStyleId);
        dest.writeInt(selectionMode);
        dest.writeInt(maxSelectNum);
        dest.writeInt(minSelectNum);
        dest.writeInt(maxVideoSelectNum);
        dest.writeInt(minVideoSelectNum);
        dest.writeInt(videoQuality);
        dest.writeInt(cropCompressQuality);
        dest.writeInt(videoMaxSecond);
        dest.writeInt(videoMinSecond);
        dest.writeInt(recordVideoSecond);
        dest.writeInt(recordVideoMinSecond);
        dest.writeInt(minimumCompressSize);
        dest.writeInt(imageSpanCount);
        dest.writeInt(aspect_ratio_x);
        dest.writeInt(aspect_ratio_y);
        dest.writeInt(cropWidth);
        dest.writeInt(cropHeight);
        dest.writeInt(compressQuality);
        dest.writeFloat(filterFileSize);
        dest.writeLong(filterMaxFileSize);
        dest.writeLong(filterMinFileSize);
        dest.writeInt(language);
        dest.writeByte((byte) (isMultipleRecyclerAnimation ? 1 : 0));
        dest.writeByte((byte) (isMultipleSkipCrop ? 1 : 0));
        dest.writeByte((byte) (isWeChatStyle ? 1 : 0));
        dest.writeByte((byte) (isUseCustomCamera ? 1 : 0));
        dest.writeByte((byte) (zoomAnim ? 1 : 0));
        dest.writeByte((byte) (isCompress ? 1 : 0));
        dest.writeByte((byte) (isOriginalControl ? 1 : 0));
        dest.writeByte((byte) (isDisplayOriginalSize ? 1 : 0));
        dest.writeByte((byte) (isEditorImage ? 1 : 0));
        dest.writeByte((byte) (isCamera ? 1 : 0));
        dest.writeByte((byte) (isGif ? 1 : 0));
        dest.writeByte((byte) (isWebp ? 1 : 0));
        dest.writeByte((byte) (isBmp ? 1 : 0));
        dest.writeByte((byte) (enablePreview ? 1 : 0));
        dest.writeByte((byte) (enPreviewVideo ? 1 : 0));
        dest.writeByte((byte) (enablePreviewAudio ? 1 : 0));
        dest.writeByte((byte) (checkNumMode ? 1 : 0));
        dest.writeByte((byte) (openClickSound ? 1 : 0));
        dest.writeByte((byte) (enableCrop ? 1 : 0));
        dest.writeByte((byte) (freeStyleCropEnabled ? 1 : 0));
        dest.writeByte((byte) (isDragCenter ? 1 : 0));
        dest.writeByte((byte) (circleDimmedLayer ? 1 : 0));
        dest.writeInt(circleDimmedColor);
        dest.writeInt(circleDimmedBorderColor);
        dest.writeInt(circleStrokeWidth);
        dest.writeInt(freeStyleCropMode);
        dest.writeByte((byte) (showCropFrame ? 1 : 0));
        dest.writeByte((byte) (showCropGrid ? 1 : 0));
        dest.writeByte((byte) (hideBottomControls ? 1 : 0));
        dest.writeByte((byte) (rotateEnabled ? 1 : 0));
        dest.writeByte((byte) (scaleEnabled ? 1 : 0));
        dest.writeByte((byte) (previewEggs ? 1 : 0));
        dest.writeByte((byte) (synOrAsy ? 1 : 0));
        dest.writeByte((byte) (returnEmpty ? 1 : 0));
        dest.writeByte((byte) (isDragFrame ? 1 : 0));
        dest.writeByte((byte) (isNotPreviewDownload ? 1 : 0));
        dest.writeByte((byte) (isWithVideoImage ? 1 : 0));
        dest.writeTypedList(selectionMedias);
        dest.writeString(cameraFileName);
        dest.writeByte((byte) (isCheckOriginalImage ? 1 : 0));
        dest.writeInt(overrideWidth);
        dest.writeInt(overrideHeight);
        dest.writeFloat(sizeMultiplier);
        dest.writeByte((byte) (isChangeStatusBarFontColor ? 1 : 0));
        dest.writeByte((byte) (isOpenStyleNumComplete ? 1 : 0));
        dest.writeByte((byte) (isOpenStyleCheckNumMode ? 1 : 0));
        dest.writeInt(titleBarBackgroundColor);
        dest.writeInt(pictureStatusBarColor);
        dest.writeInt(cropTitleBarBackgroundColor);
        dest.writeInt(cropStatusBarColorPrimaryDark);
        dest.writeInt(cropTitleColor);
        dest.writeInt(upResId);
        dest.writeInt(downResId);
        dest.writeString(outPutCameraPath);
        dest.writeString(originalPath);
        dest.writeString(cameraPath);
        dest.writeInt(cameraMimeType);
        dest.writeInt(pageSize);
        dest.writeByte((byte) (isPageStrategy ? 1 : 0));
        dest.writeByte((byte) (isFilterInvalidFile ? 1 : 0));
        dest.writeByte((byte) (isMaxSelectEnabledMask ? 1 : 0));
        dest.writeInt(animationMode);
        dest.writeByte((byte) (isAutomaticTitleRecyclerTop ? 1 : 0));
        dest.writeByte((byte) (isCallbackMode ? 1 : 0));
        dest.writeByte((byte) (isAndroidQChangeWH ? 1 : 0));
        dest.writeByte((byte) (isAndroidQChangeVideoWH ? 1 : 0));
        dest.writeByte((byte) (isQuickCapture ? 1 : 0));
        dest.writeByte((byte) (isCameraRotateImage ? 1 : 0));
        dest.writeByte((byte) (isAutoRotating ? 1 : 0));
        dest.writeByte((byte) (isSyncCover ? 1 : 0));
        dest.writeString(cropCompressFormat);
        dest.writeByte((byte) (isAutoScalePreviewImage ? 1 : 0));
        dest.writeByte((byte) (isFallbackVersion ? 1 : 0));
        dest.writeByte((byte) (isFallbackVersion2 ? 1 : 0));
        dest.writeByte((byte) (isFallbackVersion3 ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PictureSelectionConfig> CREATOR = new Creator<PictureSelectionConfig>() {
        @Override
        public PictureSelectionConfig createFromParcel(Parcel in) {
            return new PictureSelectionConfig(in);
        }

        @Override
        public PictureSelectionConfig[] newArray(int size) {
            return new PictureSelectionConfig[size];
        }
    };

    protected void initDefaultValue() {
        chooseMode = PictureMimeType.ofImage();
        camera = false;
        themeStyleId = R.style.picture_default_style;
        selectionMode = PictureConfig.MULTIPLE;
        uiStyle = null;
        style = null;
        cropStyle = null;
        maxSelectNum = 9;
        minSelectNum = 0;
        maxVideoSelectNum = 1;
        minVideoSelectNum = 0;
        videoQuality = 1;
        language = -1;
        cropCompressQuality = 90;
        videoMaxSecond = 0;
        videoMinSecond = 0;
        filterFileSize = 0;
        filterMaxFileSize = 0;
        filterMinFileSize = 1024;
        recordVideoSecond = 60;
        recordVideoMinSecond = 0;
        compressQuality = 80;
        imageSpanCount = PictureConfig.DEFAULT_SPAN_COUNT;
        isCompress = false;
        isOriginalControl = false;
        aspect_ratio_x = 0;
        aspect_ratio_y = 0;
        cropWidth = 0;
        cropHeight = 0;
        isCameraAroundState = false;
        isWithVideoImage = false;
        isAndroidQTransform = false;
        isCamera = true;
        isGif = false;
        isWebp = true;
        isBmp = true;
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
        isDragCenter = false;
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
        cameraImageFormat = "";
        cameraVideoFormat = "";
        cameraAudioFormat = "";
        cameraFileName = "";
        specifiedFormat = "";
        renameCompressFileName = "";
        renameCropFileName = "";
        queryMimeTypeHashSet = null;
        selectionMedias = new ArrayList<>();
        uCropOptions = null;
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
        isCameraRotateImage = true;
        isAutoRotating = true;
        isSyncCover = !SdkVersionUtils.checkedAndroid_Q();
        cropCompressFormat = "";
        isAutoScalePreviewImage = true;
        freeStyleCropMode = -1;
        isEditorImage = false;
        isDisplayOriginalSize = true;
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
        PictureSelectionConfig.onPermissionsObtainCallback = null;
        PictureSelectionConfig.onChooseLimitCallback = null;
        PictureSelectionConfig.cacheResourcesEngine = null;
        PictureSelectionConfig.imageEngine = null;
        PictureSelectionConfig.compressEngine = null;
    }


}
