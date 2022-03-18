package com.luck.picture.lib.config;

import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;

import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.ExtendLoaderEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.SandboxFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener;
import com.luck.picture.lib.interfaces.OnPreviewInterceptListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectFilterListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.magical.BuildRecycleItemViewParams;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2017-05-24 17:02
 * @describe：PictureSelector Config
 */

public final class PictureSelectionConfig implements Parcelable {
    public int chooseMode;
    public boolean isOnlyCamera;
    public boolean isDirectReturnSingle;
    public String cameraImageFormat;
    public String cameraVideoFormat;
    public String cameraImageFormatForQ;
    public String cameraVideoFormatForQ;
    public int requestedOrientation;
    public boolean isCameraAroundState;
    public int selectionMode;
    public int maxSelectNum;
    public int minSelectNum;
    public int maxVideoSelectNum;
    public int minVideoSelectNum;
    public int minAudioSelectNum;
    public int videoQuality;
    public int filterVideoMaxSecond;
    public int filterVideoMinSecond;
    public int selectMaxDurationSecond;
    public int selectMinDurationSecond;
    public int recordVideoMaxSecond;
    public int recordVideoMinSecond;
    public int imageSpanCount;
    public long filterMaxFileSize;
    public long filterMinFileSize;
    public long selectMaxFileSize;
    public long selectMinFileSize;
    public int language;
    public boolean isDisplayCamera;
    public boolean isGif;
    public boolean isWebp;
    public boolean isBmp;
    public boolean isEnablePreviewImage;
    public boolean isEnablePreviewVideo;
    public boolean isEnablePreviewAudio;
    public boolean isPreviewFullScreenMode;
    public boolean isPreviewZoomEffect;
    public boolean isOpenClickSound;
    public boolean isEmptyResultReturn;
    public boolean isHidePreviewDownload;
    public boolean isWithVideoImage;
    public List<String> queryOnlyList;
    public List<String> skipCropList;
    public boolean isCheckOriginalImage;
    public String outPutCameraImageFileName;
    public String outPutCameraVideoFileName;
    public String outPutAudioFileName;
    public String outPutCameraDir;
    public String outPutAudioDir;
    public String sandboxDir;
    public String originalPath;
    public String cameraPath;
    public String sortOrder;
    public int pageSize;
    public boolean isPageStrategy;
    public boolean isFilterInvalidFile;
    public boolean isMaxSelectEnabledMask;
    public int animationMode;
    public boolean isAutomaticTitleRecyclerTop;
    public boolean isQuickCapture;
    public boolean isCameraRotateImage;
    public boolean isAutoRotating;
    public boolean isSyncCover;
    public int ofAllCameraType;
    public boolean isOnlySandboxDir;
    public boolean isCameraForegroundService;
    public boolean isResultListenerBack;
    public boolean isInjectLayoutResource;
    public boolean isActivityResultBack;
    public boolean isCompressEngine;
    public boolean isLoaderDataEngine;
    public boolean isSandboxFileEngine;
    public boolean isOriginalControl;
    public boolean isDisplayTimeAxis;
    public boolean isFastSlidingSelect;
    public boolean isSelectZoomAnim;

    public static ImageEngine imageEngine;
    public static CompressEngine compressEngine;
    public static CropEngine cropEngine;
    public static SandboxFileEngine sandboxFileEngine;
    public static ExtendLoaderEngine loaderDataEngine;
    public static PictureSelectorStyle selectorStyle;
    public static OnCameraInterceptListener onCameraInterceptListener;
    public static OnSelectLimitTipsListener onSelectLimitTipsListener;
    public static OnResultCallbackListener<LocalMedia> onResultCallListener;
    public static OnExternalPreviewEventListener onExternalPreviewEventListener;
    public static OnMediaEditInterceptListener onEditMediaEventListener;
    public static OnPermissionsInterceptListener onPermissionsEventListener;
    public static OnInjectLayoutResourceListener onLayoutResourceListener;
    public static OnPreviewInterceptListener onPreviewInterceptListener;
    public static OnSelectFilterListener onSelectFilterListener;
    public static OnPermissionDescriptionListener onPermissionDescriptionListener;
    public static OnPermissionDeniedListener onPermissionDeniedListener;
    public static OnRecordAudioInterceptListener onRecordAudioListener;


    protected PictureSelectionConfig(Parcel in) {
        chooseMode = in.readInt();
        isOnlyCamera = in.readByte() != 0;
        isDirectReturnSingle = in.readByte() != 0;
        cameraImageFormat = in.readString();
        cameraVideoFormat = in.readString();
        cameraImageFormatForQ = in.readString();
        cameraVideoFormatForQ = in.readString();
        requestedOrientation = in.readInt();
        isCameraAroundState = in.readByte() != 0;
        selectionMode = in.readInt();
        maxSelectNum = in.readInt();
        minSelectNum = in.readInt();
        maxVideoSelectNum = in.readInt();
        minVideoSelectNum = in.readInt();
        minAudioSelectNum = in.readInt();
        videoQuality = in.readInt();
        filterVideoMaxSecond = in.readInt();
        filterVideoMinSecond = in.readInt();
        selectMaxDurationSecond = in.readInt();
        selectMinDurationSecond = in.readInt();
        recordVideoMaxSecond = in.readInt();
        recordVideoMinSecond = in.readInt();
        imageSpanCount = in.readInt();
        filterMaxFileSize = in.readLong();
        filterMinFileSize = in.readLong();
        selectMaxFileSize = in.readLong();
        selectMinFileSize = in.readLong();
        language = in.readInt();
        isDisplayCamera = in.readByte() != 0;
        isGif = in.readByte() != 0;
        isWebp = in.readByte() != 0;
        isBmp = in.readByte() != 0;
        isEnablePreviewImage = in.readByte() != 0;
        isEnablePreviewVideo = in.readByte() != 0;
        isEnablePreviewAudio = in.readByte() != 0;
        isPreviewFullScreenMode = in.readByte() != 0;
        isPreviewZoomEffect = in.readByte() != 0;
        isOpenClickSound = in.readByte() != 0;
        isEmptyResultReturn = in.readByte() != 0;
        isHidePreviewDownload = in.readByte() != 0;
        isWithVideoImage = in.readByte() != 0;
        queryOnlyList = in.createStringArrayList();
        skipCropList = in.createStringArrayList();
        isCheckOriginalImage = in.readByte() != 0;
        outPutCameraImageFileName = in.readString();
        outPutCameraVideoFileName = in.readString();
        outPutAudioFileName = in.readString();
        outPutCameraDir = in.readString();
        outPutAudioDir = in.readString();
        sandboxDir = in.readString();
        originalPath = in.readString();
        cameraPath = in.readString();
        sortOrder = in.readString();
        pageSize = in.readInt();
        isPageStrategy = in.readByte() != 0;
        isFilterInvalidFile = in.readByte() != 0;
        isMaxSelectEnabledMask = in.readByte() != 0;
        animationMode = in.readInt();
        isAutomaticTitleRecyclerTop = in.readByte() != 0;
        isQuickCapture = in.readByte() != 0;
        isCameraRotateImage = in.readByte() != 0;
        isAutoRotating = in.readByte() != 0;
        isSyncCover = in.readByte() != 0;
        ofAllCameraType = in.readInt();
        isOnlySandboxDir = in.readByte() != 0;
        isCameraForegroundService = in.readByte() != 0;
        isResultListenerBack = in.readByte() != 0;
        isInjectLayoutResource = in.readByte() != 0;
        isActivityResultBack = in.readByte() != 0;
        isCompressEngine = in.readByte() != 0;
        isLoaderDataEngine = in.readByte() != 0;
        isSandboxFileEngine = in.readByte() != 0;
        isOriginalControl = in.readByte() != 0;
        isDisplayTimeAxis = in.readByte() != 0;
        isFastSlidingSelect = in.readByte() != 0;
        isSelectZoomAnim = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chooseMode);
        dest.writeByte((byte) (isOnlyCamera ? 1 : 0));
        dest.writeByte((byte) (isDirectReturnSingle ? 1 : 0));
        dest.writeString(cameraImageFormat);
        dest.writeString(cameraVideoFormat);
        dest.writeString(cameraImageFormatForQ);
        dest.writeString(cameraVideoFormatForQ);
        dest.writeInt(requestedOrientation);
        dest.writeByte((byte) (isCameraAroundState ? 1 : 0));
        dest.writeInt(selectionMode);
        dest.writeInt(maxSelectNum);
        dest.writeInt(minSelectNum);
        dest.writeInt(maxVideoSelectNum);
        dest.writeInt(minVideoSelectNum);
        dest.writeInt(minAudioSelectNum);
        dest.writeInt(videoQuality);
        dest.writeInt(filterVideoMaxSecond);
        dest.writeInt(filterVideoMinSecond);
        dest.writeInt(selectMaxDurationSecond);
        dest.writeInt(selectMinDurationSecond);
        dest.writeInt(recordVideoMaxSecond);
        dest.writeInt(recordVideoMinSecond);
        dest.writeInt(imageSpanCount);
        dest.writeLong(filterMaxFileSize);
        dest.writeLong(filterMinFileSize);
        dest.writeLong(selectMaxFileSize);
        dest.writeLong(selectMinFileSize);
        dest.writeInt(language);
        dest.writeByte((byte) (isDisplayCamera ? 1 : 0));
        dest.writeByte((byte) (isGif ? 1 : 0));
        dest.writeByte((byte) (isWebp ? 1 : 0));
        dest.writeByte((byte) (isBmp ? 1 : 0));
        dest.writeByte((byte) (isEnablePreviewImage ? 1 : 0));
        dest.writeByte((byte) (isEnablePreviewVideo ? 1 : 0));
        dest.writeByte((byte) (isEnablePreviewAudio ? 1 : 0));
        dest.writeByte((byte) (isPreviewFullScreenMode ? 1 : 0));
        dest.writeByte((byte) (isPreviewZoomEffect ? 1 : 0));
        dest.writeByte((byte) (isOpenClickSound ? 1 : 0));
        dest.writeByte((byte) (isEmptyResultReturn ? 1 : 0));
        dest.writeByte((byte) (isHidePreviewDownload ? 1 : 0));
        dest.writeByte((byte) (isWithVideoImage ? 1 : 0));
        dest.writeStringList(queryOnlyList);
        dest.writeStringList(skipCropList);
        dest.writeByte((byte) (isCheckOriginalImage ? 1 : 0));
        dest.writeString(outPutCameraImageFileName);
        dest.writeString(outPutCameraVideoFileName);
        dest.writeString(outPutAudioFileName);
        dest.writeString(outPutCameraDir);
        dest.writeString(outPutAudioDir);
        dest.writeString(sandboxDir);
        dest.writeString(originalPath);
        dest.writeString(cameraPath);
        dest.writeString(sortOrder);
        dest.writeInt(pageSize);
        dest.writeByte((byte) (isPageStrategy ? 1 : 0));
        dest.writeByte((byte) (isFilterInvalidFile ? 1 : 0));
        dest.writeByte((byte) (isMaxSelectEnabledMask ? 1 : 0));
        dest.writeInt(animationMode);
        dest.writeByte((byte) (isAutomaticTitleRecyclerTop ? 1 : 0));
        dest.writeByte((byte) (isQuickCapture ? 1 : 0));
        dest.writeByte((byte) (isCameraRotateImage ? 1 : 0));
        dest.writeByte((byte) (isAutoRotating ? 1 : 0));
        dest.writeByte((byte) (isSyncCover ? 1 : 0));
        dest.writeInt(ofAllCameraType);
        dest.writeByte((byte) (isOnlySandboxDir ? 1 : 0));
        dest.writeByte((byte) (isCameraForegroundService ? 1 : 0));
        dest.writeByte((byte) (isResultListenerBack ? 1 : 0));
        dest.writeByte((byte) (isInjectLayoutResource ? 1 : 0));
        dest.writeByte((byte) (isActivityResultBack ? 1 : 0));
        dest.writeByte((byte) (isCompressEngine ? 1 : 0));
        dest.writeByte((byte) (isLoaderDataEngine ? 1 : 0));
        dest.writeByte((byte) (isSandboxFileEngine ? 1 : 0));
        dest.writeByte((byte) (isOriginalControl ? 1 : 0));
        dest.writeByte((byte) (isDisplayTimeAxis ? 1 : 0));
        dest.writeByte((byte) (isFastSlidingSelect ? 1 : 0));
        dest.writeByte((byte) (isSelectZoomAnim ? 1 : 0));
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
        chooseMode = SelectMimeType.ofImage();
        isOnlyCamera = false;
        selectionMode = SelectModeConfig.MULTIPLE;
        selectorStyle = new PictureSelectorStyle();
        maxSelectNum = 9;
        minSelectNum = 0;
        maxVideoSelectNum = 1;
        minVideoSelectNum = 0;
        minAudioSelectNum = 0;
        videoQuality = VideoQuality.VIDEO_QUALITY_HIGH;
        language = LanguageConfig.UNKNOWN_LANGUAGE;
        filterVideoMaxSecond = 0;
        filterVideoMinSecond = 1000;
        selectMaxDurationSecond = 0;
        selectMinDurationSecond = 0;
        filterMaxFileSize = 0;
        filterMinFileSize = 1024;
        selectMaxFileSize = 0;
        selectMinFileSize = 0;
        recordVideoMaxSecond = 60;
        recordVideoMinSecond = 0;
        imageSpanCount = PictureConfig.DEFAULT_SPAN_COUNT;
        isCameraAroundState = false;
        isWithVideoImage = false;
        isDisplayCamera = true;
        isGif = false;
        isWebp = true;
        isBmp = true;
        isCheckOriginalImage = false;
        isDirectReturnSingle = false;
        isEnablePreviewImage = true;
        isEnablePreviewVideo = true;
        isEnablePreviewAudio = true;
        isHidePreviewDownload = false;
        isOpenClickSound = false;
        isEmptyResultReturn = false;
        cameraImageFormat = PictureMimeType.JPEG;
        cameraVideoFormat = PictureMimeType.MP4;
        cameraImageFormatForQ = PictureMimeType.MIME_TYPE_IMAGE;
        cameraVideoFormatForQ = PictureMimeType.MIME_TYPE_VIDEO;
        outPutCameraImageFileName = "";
        outPutCameraVideoFileName = "";
        outPutAudioFileName = "";
        queryOnlyList = new ArrayList<>();
        outPutCameraDir = "";
        outPutAudioDir = "";
        sandboxDir = "";
        originalPath = "";
        cameraPath = "";
        pageSize = PictureConfig.MAX_PAGE_SIZE;
        isPageStrategy = true;
        isFilterInvalidFile = false;
        isMaxSelectEnabledMask = false;
        animationMode = -1;
        isAutomaticTitleRecyclerTop = true;
        isQuickCapture = true;
        isCameraRotateImage = true;
        isAutoRotating = true;
        isSyncCover = !SdkVersionUtils.isQ();
        ofAllCameraType = SelectMimeType.ofAll();
        isOnlySandboxDir = false;
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        isCameraForegroundService = true;
        isResultListenerBack = true;
        isActivityResultBack = false;
        isCompressEngine = false;
        isLoaderDataEngine = false;
        isSandboxFileEngine = false;
        isPreviewFullScreenMode = true;
        isPreviewZoomEffect = chooseMode != SelectMimeType.ofAudio();
        isOriginalControl = false;
        isInjectLayoutResource = false;
        isDisplayTimeAxis = true;
        isFastSlidingSelect = false;
        skipCropList = new ArrayList<>();
        sortOrder = "";
        isSelectZoomAnim = true;
    }


    public static PictureSelectionConfig getCleanInstance() {
        PictureSelectionConfig selectionSpec = getInstance();
        selectionSpec.initDefaultValue();
        return selectionSpec;
    }

    private static PictureSelectionConfig mInstance;

    public static PictureSelectionConfig getInstance() {
        if (mInstance == null) {
            synchronized (PictureSelectionConfig.class) {
                if (mInstance == null) {
                    mInstance = new PictureSelectionConfig();
                    mInstance.initDefaultValue();
                }
            }
        }
        return mInstance;
    }

    public PictureSelectionConfig() {
    }

    /**
     * 释放监听器
     */
    public static void destroy() {
        PictureSelectionConfig.imageEngine = null;
        PictureSelectionConfig.compressEngine = null;
        PictureSelectionConfig.cropEngine = null;
        PictureSelectionConfig.sandboxFileEngine = null;
        PictureSelectionConfig.loaderDataEngine = null;
        PictureSelectionConfig.onResultCallListener = null;
        PictureSelectionConfig.onCameraInterceptListener = null;
        PictureSelectionConfig.onExternalPreviewEventListener = null;
        PictureSelectionConfig.onEditMediaEventListener = null;
        PictureSelectionConfig.onPermissionsEventListener = null;
        PictureSelectionConfig.onLayoutResourceListener = null;
        PictureSelectionConfig.onPreviewInterceptListener = null;
        PictureSelectionConfig.onSelectLimitTipsListener = null;
        PictureSelectionConfig.onSelectFilterListener = null;
        PictureSelectionConfig.onPermissionDescriptionListener = null;
        PictureSelectionConfig.onPermissionDeniedListener = null;
        PictureSelectionConfig.onRecordAudioListener = null;
        PictureThreadUtils.cancel(PictureThreadUtils.getIoPool());
        SelectedManager.clearSelectResult();
        BuildRecycleItemViewParams.clear();
        SelectedManager.setCurrentLocalMediaFolder(null);
    }

}
