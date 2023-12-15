package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.config.VideoQuality;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ExtendLoaderEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.SandboxFileEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnGridItemSelectAnimListener;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener;
import com.luck.picture.lib.interfaces.OnPreviewInterceptListener;
import com.luck.picture.lib.interfaces.OnQueryFilterListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectAnimListener;
import com.luck.picture.lib.interfaces.OnSelectFilterListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author：luck
 * @date：2017-5-24 21:30
 * @describe：PictureSelectionModel
 */

public final class PictureSelectionModel {
    private final SelectorConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionModel(PictureSelector selector, int chooseMode) {
        this.selector = selector;
        selectionConfig = new SelectorConfig();
        SelectorProviders.getInstance().addSelectorConfigQueue(selectionConfig);
        selectionConfig.chooseMode = chooseMode;
        setMaxVideoSelectNum(selectionConfig.maxVideoSelectNum);
    }

    /**
     * PictureSelector theme style settings
     *
     * @param uiStyle <p>
     *                Use {@link  PictureSelectorStyle
     *                It consists of the following parts and can be set separately}
     *                {@link com.luck.picture.lib.style.TitleBarStyle}
     *                {@link com.luck.picture.lib.style.AlbumWindowStyle}
     *                {@link com.luck.picture.lib.style.SelectMainStyle}
     *                {@link com.luck.picture.lib.style.BottomNavBarStyle}
     *                {@link com.luck.picture.lib.style.PictureWindowAnimationStyle}
     *                <p/>
     *  PictureSelectorStyle
     */
    public PictureSelectionModel setSelectorUIStyle(PictureSelectorStyle uiStyle) {
        if (uiStyle != null) {
            selectionConfig.selectorStyle = uiStyle;
        }
        return this;
    }

    /**
     * Set App Language
     *
     * @param language {@link LanguageConfig}
     *  PictureSelectionModel
     */
    public PictureSelectionModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language {@link LanguageConfig}
     *  PictureSelectionModel
     */
    public PictureSelectionModel setDefaultLanguage(int defaultLanguage) {
        selectionConfig.defaultLanguage = defaultLanguage;
        return this;
    }

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     *               <p>
     *               <a href="https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java">
     *               </p>
     *
     */
    public PictureSelectionModel setImageEngine(ImageEngine engine) {
        selectionConfig.imageEngine = engine;
        return this;
    }

    /**
     * Set up player engine
     *  <p>
     *   Used to preview custom player instances，MediaPlayer by default
     *  </p>
     * @param engine
     *
     */
    public PictureSelectionModel setVideoPlayerEngine(VideoPlayerEngine engine) {
        selectionConfig.videoPlayerEngine = engine;
        return this;
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * Please use {@link CompressFileEngine}
     *
     */
    @Deprecated
    public PictureSelectionModel setCompressEngine(CompressEngine engine) {
        selectionConfig.compressEngine = engine;
        selectionConfig.isCompressEngine = true;
        return this;
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     *
     */
    public PictureSelectionModel setCompressEngine(CompressFileEngine engine) {
        selectionConfig.compressFileEngine = engine;
        selectionConfig.isCompressEngine = true;
        return this;
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * Please Use {@link CropFileEngine}
     *
     */
    @Deprecated
    public PictureSelectionModel setCropEngine(CropEngine engine) {
        selectionConfig.cropEngine = engine;
        return this;
    }


    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     *
     */
    public PictureSelectionModel setCropEngine(CropFileEngine engine) {
        selectionConfig.cropFileEngine = engine;
        return this;
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     * Please Use {@link UriToFileTransformEngine}
     *
     *
     */
    @Deprecated
    public PictureSelectionModel setSandboxFileEngine(SandboxFileEngine engine) {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.sandboxFileEngine = engine;
            selectionConfig.isSandboxFileEngine = true;
        } else {
            selectionConfig.isSandboxFileEngine = false;
        }
        return this;
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     *
     */
    public PictureSelectionModel setSandboxFileEngine(UriToFileTransformEngine engine) {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine;
            selectionConfig.isSandboxFileEngine = true;
        } else {
            selectionConfig.isSandboxFileEngine = false;
        }
        return this;
    }

    /**
     * Users can implement some interfaces to access their own query data
     * The premise is that you need to comply with the model specification of PictureSelector
     * {@link ExtendLoaderEngine}
     * {@link LocalMediaFolder}
     * {@link LocalMedia}
     * <p>
     * Use {@link #.setLoaderFactoryEngine();}
     * </p>
     *
     * @param engine
     *
     */
    @Deprecated
    public PictureSelectionModel setExtendLoaderEngine(ExtendLoaderEngine engine) {
        selectionConfig.loaderDataEngine = engine;
        selectionConfig.isLoaderDataEngine = true;
        return this;
    }

    /**
     * Users can implement some interfaces to access their own query data
     * The premise is that you need to comply with the model specification of PictureSelector
     * {@link IBridgeLoaderFactory}
     * {@link LocalMediaFolder}
     * {@link LocalMedia}
     *
     * @param engine
     *
     */
    public PictureSelectionModel setLoaderFactoryEngine(IBridgeLoaderFactory loaderFactory) {
        selectionConfig.loaderFactory = loaderFactory;
        selectionConfig.isLoaderFactoryEngine = true;
        return this;
    }

    /**
     * An interpolator defines the rate of change of an animation.
     * This allows the basic animation effects (alpha, scale, translate, rotate) to be accelerated, decelerated, repeated, etc.
     * Use {@link
     * .isPreviewZoomEffect(true);
     * }
     */
    public PictureSelectionModel setMagicalEffectInterpolator(InterpolatorFactory interpolatorFactory) {
        selectionConfig.interpolatorFactory = interpolatorFactory;
        return this;
    }

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param listener
     *
     */
    public PictureSelectionModel setCameraInterceptListener(OnCameraInterceptListener listener) {
        selectionConfig.onCameraInterceptListener = listener;
        return this;
    }


    /**
     * Intercept Record Audio click events, and users can implement their own Record Audio framework
     *
     * @param listener
     *
     */
    public PictureSelectionModel setRecordAudioInterceptListener(OnRecordAudioInterceptListener listener) {
        selectionConfig.onRecordAudioListener = listener;
        return this;
    }


    /**
     * Intercept preview click events, and users can implement their own preview framework
     *
     * @param listener
     *
     */
    public PictureSelectionModel setPreviewInterceptListener(OnPreviewInterceptListener listener) {
        selectionConfig.onPreviewInterceptListener = listener;
        return this;
    }


    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     *
     */
    public PictureSelectionModel setInjectLayoutResourceListener(OnInjectLayoutResourceListener listener) {
        selectionConfig.isInjectLayoutResource = listener != null;
        selectionConfig.onLayoutResourceListener = listener;
        return this;
    }

    /**
     * Intercept media edit click events, and users can implement their own edit media framework
     *
     * @param listener
     *
     */
    public PictureSelectionModel setEditMediaInterceptListener(OnMediaEditInterceptListener listener) {
        selectionConfig.onEditMediaEventListener = listener;
        return this;
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     *
     */
    public PictureSelectionModel setPermissionsInterceptListener(OnPermissionsInterceptListener listener) {
        selectionConfig.onPermissionsEventListener = listener;
        return this;
    }

    /**
     * permission description
     *
     * @param listener
     *
     */
    public PictureSelectionModel setPermissionDescriptionListener(OnPermissionDescriptionListener listener) {
        selectionConfig.onPermissionDescriptionListener = listener;
        return this;
    }

    /**
     *  Permission denied
     *
     * @param listener
     *
     */
    public PictureSelectionModel setPermissionDeniedListener(OnPermissionDeniedListener listener) {
        selectionConfig.onPermissionDeniedListener = listener;
        return this;
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    public PictureSelectionModel setSelectLimitTipsListener(OnSelectLimitTipsListener listener) {
        selectionConfig.onSelectLimitTipsListener = listener;
        return this;
    }

    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param listener
     *
     */
    public PictureSelectionModel setSelectFilterListener(OnSelectFilterListener listener) {
        selectionConfig.onSelectFilterListener = listener;
        return this;
    }

    /**
     * You need to filter out what doesn't meet the standards
     *
     * @param listener
     *
     */
    public PictureSelectionModel setQueryFilterListener(OnQueryFilterListener listener) {
        selectionConfig.onQueryFilterListener = listener;
        return this;
    }

    /**
     * Animate the selected item in the list
     *
     * @param listener
     *
     */
    public PictureSelectionModel setGridItemSelectAnimListener(OnGridItemSelectAnimListener listener) {
        selectionConfig.onItemSelectAnimListener = listener;
        return this;
    }

    /**
     * Animate the selected item
     *
     * @param listener
     *
     */
    public PictureSelectionModel setSelectAnimListener(OnSelectAnimListener listener) {
        selectionConfig.onSelectAnimListener = listener;
        return this;
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     *
     */
    public PictureSelectionModel setAddBitmapWatermarkListener(OnBitmapWatermarkEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onBitmapWatermarkListener = listener;
        }
        return this;
    }

    /**
     * Process video thumbnails
     *
     * @param listener
     *
     */
    public PictureSelectionModel setVideoThumbnailListener(OnVideoThumbnailEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onVideoThumbnailEventListener = listener;
        }
        return this;
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     *
     */
    public PictureSelectionModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        selectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     *
     */
    public PictureSelectionModel isCameraForegroundService(boolean isForeground) {
        selectionConfig.isCameraForegroundService = isForeground;
        return this;
    }

    /**
     * Android 10 preloads data first, then asynchronously obtains album list
     * <p>
     * Please consult the developer for detailed reasons
     * </p>
     *
     * @param isPreloadFirst Enable preload by default
     */
    public PictureSelectionModel isPreloadFirst(boolean isPreloadFirst) {
        selectionConfig.isPreloadFirst = isPreloadFirst;
        return this;
    }

    /**
     * Using the system player
     *
     * @param isUseSystemVideoPlayer
     */
    public PictureSelectionModel isUseSystemVideoPlayer(boolean isUseSystemVideoPlayer) {
        selectionConfig.isUseSystemVideoPlayer = isUseSystemVideoPlayer;
        return this;
    }

    /**
     * Change the desired orientation of this activity.  If the activity
     * is currently in the foreground or otherwise impacting the screen
     * orientation, the screen will immediately be changed (possibly causing
     * the activity to be restarted). Otherwise, this will be used the next
     * time the activity is visible.
     *
     * @param requestedOrientation An orientation constant as used in
     *                             {@link android.content.pm.ActivityInfo.screenOrientation ActivityInfo.screenOrientation}.
     */
    public PictureSelectionModel setRequestedOrientation(int requestedOrientation) {
        selectionConfig.requestedOrientation = requestedOrientation;
        return this;
    }


    /**
     * @param selectionMode PictureSelector Selection model
     *                      and {@link SelectModeConfig.MULTIPLE} or {@link SelectModeConfig.SINGLE}
     *                      <p>
     *                      Use {@link SelectModeConfig}
     *                      </p>
     *
     */
    public PictureSelectionModel setSelectionMode(int selectionMode) {
        selectionConfig.selectionMode = selectionMode;
        selectionConfig.maxSelectNum = selectionConfig.selectionMode ==
                SelectModeConfig.SINGLE ? 1 : selectionConfig.maxSelectNum;
        return this;
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     *
     * @param isNewKeyBackMode
     */
    public PictureSelectionModel isNewKeyBackMode(boolean isNewKeyBackMode) {
        selectionConfig.isNewKeyBackMode = isNewKeyBackMode;
        return this;
    }

    /**
     * You can select pictures and videos at the same time
     *
     * @param isWithVideoImage Whether the pictures and videos can be selected together
     *
     */
    public PictureSelectionModel isWithSelectVideoImage(boolean isWithVideoImage) {
        selectionConfig.isWithVideoImage = selectionConfig.chooseMode == SelectMimeType.ofAll() && isWithVideoImage;
        return this;
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param ofAllCameraType {@link SelectMimeType.ofImage or SelectMimeType.ofVideo}
     *                        The default is ofAll() mode
     *
     */
    public PictureSelectionModel setOfAllCameraType(int ofAllCameraType) {
        selectionConfig.ofAllCameraType = ofAllCameraType;
        return this;
    }

    /**
     * When the maximum number of choices is reached, does the list enable the mask effect
     *
     * @param isMaxSelectEnabledMask
     *
     */
    public PictureSelectionModel isMaxSelectEnabledMask(boolean isMaxSelectEnabledMask) {
        selectionConfig.isMaxSelectEnabledMask = isMaxSelectEnabledMask;
        return this;
    }

    /**
     * Do you need to display the original controller
     * <p>
     * It needs to be used with setSandboxFileEngine
     * {@link LocalMedia .setOriginalPath()}
     * </p>
     *
     * @param isOriginalControl
     *
     */
    public PictureSelectionModel isOriginalControl(boolean isOriginalControl) {
        selectionConfig.isOriginalControl = isOriginalControl;
        return this;
    }

    /**
     * If SyncCover
     *
     * @param isSyncCover
     *
     */
    public PictureSelectionModel isSyncCover(boolean isSyncCover) {
        selectionConfig.isSyncCover = isSyncCover;
        return this;
    }

    /**
     * Select the maximum number of files
     *
     * @param maxSelectNum PictureSelector max selection
     *
     */
    public PictureSelectionModel setMaxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = selectionConfig.selectionMode == SelectModeConfig.SINGLE ? 1 : maxSelectNum;
        return this;
    }

    /**
     * Select the minimum number of files
     *
     * @param minSelectNum PictureSelector min selection
     *
     */
    public PictureSelectionModel setMinSelectNum(int minSelectNum) {
        selectionConfig.minSelectNum = minSelectNum;
        return this;
    }


    /**
     * By clicking the title bar consecutively, RecyclerView automatically rolls back to the top
     *
     * @param isAutomaticTitleRecyclerTop
     *
     */
    public PictureSelectionModel isAutomaticTitleRecyclerTop(boolean isAutomaticTitleRecyclerTop) {
        selectionConfig.isAutomaticTitleRecyclerTop = isAutomaticTitleRecyclerTop;
        return this;
    }


    /**
     * @param Select whether to return directly
     *
     */
    public PictureSelectionModel isDirectReturnSingle(boolean isDirectReturn) {
        if (isDirectReturn) {
            selectionConfig.isFastSlidingSelect = false;
        }
        selectionConfig.isDirectReturnSingle = selectionConfig.selectionMode == SelectModeConfig.SINGLE && isDirectReturn;
        return this;
    }


    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     *
     */
    public PictureSelectionModel isPageStrategy(boolean isPageStrategy) {
        selectionConfig.isPageStrategy = isPageStrategy;
        return this;
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize       Maximum number of pages {@link PageSize is preferably no less than 20}
     *
     */
    public PictureSelectionModel isPageStrategy(boolean isPageStrategy, int pageSize) {
        selectionConfig.isPageStrategy = isPageStrategy;
        selectionConfig.pageSize = pageSize < PictureConfig.MIN_PAGE_SIZE ? PictureConfig.MAX_PAGE_SIZE : pageSize;
        return this;
    }


    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param isFilterInvalidFile Whether to filter invalid files {@link Some of the query performance is consumed,Especially on the Q version}
     *
     */
    @Deprecated
    public PictureSelectionModel isPageStrategy(boolean isPageStrategy, boolean isFilterInvalidFile) {
        selectionConfig.isPageStrategy = isPageStrategy;
        selectionConfig.isFilterInvalidFile = isFilterInvalidFile;
        return this;
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize            Maximum number of pages {@link  PageSize is preferably no less than 20}
     * @param isFilterInvalidFile Whether to filter invalid files {@link Some of the query performance is consumed,Especially on the Q version}
     *
     */
    @Deprecated
    public PictureSelectionModel isPageStrategy(boolean isPageStrategy, int pageSize, boolean isFilterInvalidFile) {
        selectionConfig.isPageStrategy = isPageStrategy;
        selectionConfig.pageSize = pageSize < PictureConfig.MIN_PAGE_SIZE ? PictureConfig.MAX_PAGE_SIZE : pageSize;
        selectionConfig.isFilterInvalidFile = isFilterInvalidFile;
        return this;
    }

    /**
     * View lifecycle listener
     *
     * @param viewLifecycle
     *
     */
    public PictureSelectionModel setAttachViewLifecycle(IBridgeViewLifecycle viewLifecycle) {
        selectionConfig.viewLifecycle = viewLifecycle;
        return this;
    }

    /**
     * The video quality output mode is only for system recording, and there are only two modes: poor quality or high quality
     *
     * @param videoQuality video quality and 0 or 1
     *                     Use {@link VideoQuality}
     *                     <p>
     *                     There are limitations, only high or low
     *                     </p>
     *
     */
    @Deprecated
    public PictureSelectionModel setVideoQuality(int videoQuality) {
        selectionConfig.videoQuality = videoQuality;
        return this;
    }

    /**
     * Set the first default album name
     *
     * @param defaultAlbumName
     *
     */
    public PictureSelectionModel setDefaultAlbumName(String defaultAlbumName) {
        selectionConfig.defaultAlbumName = defaultAlbumName;
        return this;
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     *
     */
    public PictureSelectionModel setCameraImageFormat(String imageFormat) {
        selectionConfig.cameraImageFormat = imageFormat;
        return this;
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     *
     */
    public PictureSelectionModel setCameraImageFormatForQ(String imageFormat) {
        selectionConfig.cameraImageFormatForQ = imageFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     *
     */
    public PictureSelectionModel setCameraVideoFormat(String videoFormat) {
        selectionConfig.cameraVideoFormat = videoFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     *
     */
    public PictureSelectionModel setCameraVideoFormatForQ(String videoFormat) {
        selectionConfig.cameraVideoFormatForQ = videoFormat;
        return this;
    }


    /**
     * filter max seconds video
     *
     * @param videoMaxSecond filter video max second
     *
     */
    public PictureSelectionModel setFilterVideoMaxSecond(int videoMaxSecond) {
        selectionConfig.filterVideoMaxSecond = videoMaxSecond * 1000;
        return this;
    }

    /**
     * filter min seconds video
     *
     * @param videoMinSecond filter video min second
     *
     */
    public PictureSelectionModel setFilterVideoMinSecond(int videoMinSecond) {
        selectionConfig.filterVideoMinSecond = videoMinSecond * 1000;
        return this;
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     *
     */
    public PictureSelectionModel setSelectMaxDurationSecond(int maxDurationSecond) {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000;
        return this;
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     *
     */
    public PictureSelectionModel setSelectMinDurationSecond(int minDurationSecond) {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000;
        return this;
    }

    /**
     * The max duration of video recording. If it is system recording, there may be compatibility problems
     *
     * @param maxSecond video record second
     *
     */
    public PictureSelectionModel setRecordVideoMaxSecond(int maxSecond) {
        selectionConfig.recordVideoMaxSecond = maxSecond;
        return this;
    }


    /**
     * Select the maximum video number of files
     *
     * @param maxVideoSelectNum PictureSelector video max selection
     *
     */
    public PictureSelectionModel setMaxVideoSelectNum(int maxVideoSelectNum) {
        selectionConfig.maxVideoSelectNum = selectionConfig.chooseMode == SelectMimeType.ofVideo() ? 0 : maxVideoSelectNum;
        return this;
    }

    /**
     * Select the minimum video number of files
     *
     * @param minVideoSelectNum PictureSelector video min selection
     *
     */
    public PictureSelectionModel setMinVideoSelectNum(int minVideoSelectNum) {
        selectionConfig.minVideoSelectNum = minVideoSelectNum;
        return this;
    }

    /**
     * Select the minimum audio number of files
     *
     * @param minAudioSelectNum PictureSelector audio min selection
     *
     */
    public PictureSelectionModel setMinAudioSelectNum(int minAudioSelectNum) {
        selectionConfig.minAudioSelectNum = minAudioSelectNum;
        return this;
    }

    /**
     * @param minSecond video record second
     *
     */
    public PictureSelectionModel setRecordVideoMinSecond(int minSecond) {
        selectionConfig.recordVideoMinSecond = minSecond;
        return this;
    }

    /**
     * @param imageSpanCount PictureSelector image span count
     *
     */
    public PictureSelectionModel setImageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }

    /**
     * @param isEmptyReturn No data can be returned
     *
     */
    public PictureSelectionModel isEmptyResultReturn(boolean isEmptyReturn) {
        selectionConfig.isEmptyResultReturn = isEmptyReturn;
        return this;
    }


    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     *
     */
    public PictureSelectionModel isQuickCapture(boolean isQuickCapture) {
        selectionConfig.isQuickCapture = isQuickCapture;
        return this;
    }

    /**
     * @param isDisplayCamera Whether to open camera button
     *
     */
    public PictureSelectionModel isDisplayCamera(boolean isDisplayCamera) {
        selectionConfig.isDisplayCamera = isDisplayCamera;
        return this;
    }

    /**
     * @param outPutCameraDir Camera output path
     *                        <p>Audio mode setting is not supported</p>
     *
     */
    public PictureSelectionModel setOutputCameraDir(String outPutCameraDir) {
        selectionConfig.outPutCameraDir = outPutCameraDir;
        return this;
    }

    /**
     * @param outPutAudioDir Audio output path
     *
     */
    public PictureSelectionModel setOutputAudioDir(String outPutAudioDir) {
        selectionConfig.outPutAudioDir = outPutAudioDir;
        return this;
    }

    /**
     * Camera IMAGE custom local file name
     * # Such as xxx.png
     *
     * @param fileName
     *
     */
    public PictureSelectionModel setOutputCameraImageFileName(String fileName) {
        selectionConfig.outPutCameraImageFileName = fileName;
        return this;
    }

    /**
     * Camera VIDEO custom local file name
     * # Such as xxx.png
     *
     * @param fileName
     *
     */
    public PictureSelectionModel setOutputCameraVideoFileName(String fileName) {
        selectionConfig.outPutCameraVideoFileName = fileName;
        return this;
    }

    /**
     * Camera VIDEO custom local file name
     * # Such as xxx.amr
     *
     * @param fileName
     *
     */
    public PictureSelectionModel setOutputAudioFileName(String fileName) {
        selectionConfig.outPutAudioFileName = fileName;
        return this;
    }

    /**
     * Query the pictures or videos in the specified directory
     *
     * @param dir Camera out path
     *            <p>
     *            Normally, it should be consistent with {@link SelectorConfig.setOutputCameraDir()};
     *            </p>
     *
     *            <p>
     *            If build.version.sdk_INT < 29,{@link SelectorConfig.setQuerySandboxDir();}
     *            Do not set the external storage path,
     *            which may cause the problem of picture duplication
     *            </p>
     *
     */
    public PictureSelectionModel setQuerySandboxDir(String dir) {
        selectionConfig.sandboxDir = dir;
        return this;
    }

    /**
     * Only the resources in the specified directory are displayed
     * <p>
     * Only Display setQuerySandboxDir();  Source
     * <p/>
     *
     * @param isOnlySandboxDir true or Only Display {@link SelectorConfig.setQuerySandboxDir();}
     *
     */
    public PictureSelectionModel isOnlyObtainSandboxDir(boolean isOnlySandboxDir) {
        selectionConfig.isOnlySandboxDir = isOnlySandboxDir;
        return this;
    }

    /**
     * Displays the creation timeline of the resource
     *
     * @param isDisplayTimeAxis
     *
     */
    public PictureSelectionModel isDisplayTimeAxis(boolean isDisplayTimeAxis) {
        selectionConfig.isDisplayTimeAxis = isDisplayTimeAxis;
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     *
     */
    public PictureSelectionModel setFilterMaxFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMaxFileSize = fileKbSize;
        } else {
            selectionConfig.filterMaxFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter min file size
     *
     */
    public PictureSelectionModel setFilterMinFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMinFileSize = fileKbSize;
        } else {
            selectionConfig.filterMinFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }


    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     *
     */
    public PictureSelectionModel setSelectMaxFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMaxFileSize = fileKbSize;
        } else {
            selectionConfig.selectMaxFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter min file size
     *
     */
    public PictureSelectionModel setSelectMinFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize;
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * query only mimeType
     *
     * @param values Use example {@link { image/jpeg or video/mp4 ... }}
     */
    public PictureSelectionModel setQueryOnlyMimeType(String... values) {
        for (String mimeType : values) {
            if (PictureMimeType.isHasImage(mimeType)) {
                if (!selectionConfig.queryOnlyImageList.contains(mimeType)) {
                    selectionConfig.queryOnlyImageList.add(mimeType);
                }
            } else if (PictureMimeType.isHasVideo(mimeType)) {
                if (!selectionConfig.queryOnlyVideoList.contains(mimeType)) {
                    selectionConfig.queryOnlyVideoList.add(mimeType);
                }
            } else if (PictureMimeType.isHasAudio(mimeType)) {
                if (!selectionConfig.queryOnlyAudioList.contains(mimeType)) {
                    selectionConfig.queryOnlyAudioList.add(mimeType);
                }
            }
        }
        return this;
    }


    /**
     * Skip crop mimeType
     *
     * @param mimeTypes Use example {@link { image/gift or image/webp ... }}
     *
     */
    public PictureSelectionModel setSkipCropMimeType(String... mimeTypes) {
        if (mimeTypes != null && mimeTypes.length > 0) {
            selectionConfig.skipCropList.addAll(Arrays.asList(mimeTypes));
        }
        return this;
    }

    /**
     * query local data source sort
     * {@link MediaStore.MediaColumns.DATE_MODIFIED # DATE_ADDED # _ID}
     * <p>
     * example:
     * MediaStore.MediaColumns.DATE_MODIFIED + " DESC";  or MediaStore.MediaColumns.DATE_MODIFIED + " ASC";
     * </p>
     *
     * @param sortOrder
     *
     */
    public PictureSelectionModel setQuerySortOrder(String sortOrder) {
        if (!TextUtils.isEmpty(sortOrder)) {
            selectionConfig.sortOrder = sortOrder;
        }
        return this;
    }

    /**
     * @param isGif Whether to open gif
     *
     */
    public PictureSelectionModel isGif(boolean isGif) {
        selectionConfig.isGif = isGif;
        return this;
    }

    /**
     * @param isWebp Whether to open .webp
     *
     */
    public PictureSelectionModel isWebp(boolean isWebp) {
        selectionConfig.isWebp = isWebp;
        return this;
    }

    /**
     * @param isBmp Whether to open .isBmp
     *
     */
    public PictureSelectionModel isBmp(boolean isBmp) {
        selectionConfig.isBmp = isBmp;
        return this;
    }

    /**
     * @param isHeic Whether to open .isHeic
     *
     */
    public PictureSelectionModel isHeic(boolean isHeic) {
        selectionConfig.isHeic = isHeic;
        return this;
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     *
     */
    public PictureSelectionModel isPreviewFullScreenMode(boolean isFullScreenModel) {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     *
     */
    public PictureSelectionModel isPreviewZoomEffect(boolean isPreviewZoomEffect) {
        if (selectionConfig.chooseMode == SelectMimeType.ofAudio()) {
            selectionConfig.isPreviewZoomEffect = false;
        } else {
            selectionConfig.isPreviewZoomEffect = isPreviewZoomEffect;
        }
        return this;
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isEnableVideoSize Use {@link .isSyncWidthAndHeight()}
     */
    @Deprecated
    public PictureSelectionModel isEnableVideoSize(boolean isEnableVideoSize) {
        selectionConfig.isSyncWidthAndHeight = isEnableVideoSize;
        return this;
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isSyncWidthAndHeight
     *
     */
    public PictureSelectionModel isSyncWidthAndHeight(boolean isSyncWidthAndHeight) {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight;
        return this;
    }

    /**
     * Do you want to preview play the audio file?
     *
     * @param isPreviewAudio
     *
     */
    public PictureSelectionModel isPreviewAudio(boolean isPreviewAudio) {
        selectionConfig.isEnablePreviewAudio = isPreviewAudio;
        return this;
    }

    /**
     * @param isPreviewImage Do you want to preview the picture?
     *
     */
    public PictureSelectionModel isPreviewImage(boolean isPreviewImage) {
        selectionConfig.isEnablePreviewImage = isPreviewImage;
        return this;
    }


    /**
     * @param isPreviewVideo Do you want to preview the video?
     *
     */
    public PictureSelectionModel isPreviewVideo(boolean isPreviewVideo) {
        selectionConfig.isEnablePreviewVideo = isPreviewVideo;
        return this;
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     *
     */
    public PictureSelectionModel isAutoVideoPlay(boolean isAutoPlay) {
        selectionConfig.isAutoVideoPlay = isAutoPlay;
        return this;
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     *
     */
    public PictureSelectionModel isLoopAutoVideoPlay(boolean isLoopAutoPlay) {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay;
        return this;
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     *
     */
    public PictureSelectionModel isVideoPauseResumePlay(boolean isPauseResumePlay) {
        selectionConfig.isPauseResumePlay = isPauseResumePlay;
        return this;
    }

    /**
     * Whether to sync the number of resources under the latest album in paging mode with filter conditions
     *
     * @param isPageSyncAsCount
     */
    public PictureSelectionModel isPageSyncAlbumCount(boolean isPageSyncAsCount) {
        selectionConfig.isPageSyncAsCount = isPageSyncAsCount;
        return this;
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     *
     */
    public PictureSelectionModel isOriginalSkipCompress(boolean isOriginalSkipCompress) {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress;
        return this;
    }

    /**
     * Filter the validity of file size or duration of audio and video
     *
     * @param isFilterSizeDuration
     *
     */
    public PictureSelectionModel isFilterSizeDuration(boolean isFilterSizeDuration) {
        selectionConfig.isFilterSizeDuration = isFilterSizeDuration;
        return this;
    }

    /**
     * Quick slide selection results
     *
     * @param isFastSlidingSelect
     *
     */
    public PictureSelectionModel isFastSlidingSelect(boolean isFastSlidingSelect) {
        if (selectionConfig.isDirectReturnSingle) {
            selectionConfig.isFastSlidingSelect = false;
        } else {
            selectionConfig.isFastSlidingSelect = isFastSlidingSelect;
        }
        return this;
    }

    /**
     * @param isClickSound Whether to open click voice
     *
     */
    public PictureSelectionModel isOpenClickSound(boolean isClickSound) {
        selectionConfig.isOpenClickSound = isClickSound;
        return this;
    }

    /**
     * Set camera direction (after default image)
     */
    public PictureSelectionModel isCameraAroundState(boolean isCameraAroundState) {
        selectionConfig.isCameraAroundState = isCameraAroundState;
        return this;
    }

    /**
     * Camera image rotation, automatic correction
     */
    public PictureSelectionModel isCameraRotateImage(boolean isCameraRotateImage) {
        selectionConfig.isCameraRotateImage = isCameraRotateImage;
        return this;
    }

    /**
     * Zoom animation is required when selecting an asset
     */
    public PictureSelectionModel isSelectZoomAnim(boolean isSelectZoomAnim) {
        selectionConfig.isSelectZoomAnim = isSelectZoomAnim;
        return this;
    }

    /**
     * @param selectedList Select the selected picture set
     *
     */
    public PictureSelectionModel setSelectedData(List<LocalMedia> selectedList) {
        if (selectedList == null) {
            return this;
        }
        if (selectionConfig.selectionMode == SelectModeConfig.SINGLE && selectionConfig.isDirectReturnSingle) {
            selectionConfig.selectedResult.clear();
        } else {
            selectionConfig.addAllSelectResult(new ArrayList<>(selectedList));
        }
        return this;
    }

    /**
     * Photo album list animation {}
     * Use {@link AnimationType#ALPHA_IN_ANIMATION or SLIDE_IN_BOTTOM_ANIMATION} directly.
     *
     * @param animationMode
     *
     */
    public PictureSelectionModel setRecyclerAnimationMode(int animationMode) {
        selectionConfig.animationMode = animationMode;
        return this;
    }

    /**
     * Start PictureSelector
     *
     * @param call
     */
    public void forResult(OnResultCallbackListener<LocalMedia> call) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (call == null) {
                throw new NullPointerException("OnResultCallbackListener cannot be null");
            }
            // 绑定回调监听
            selectionConfig.isResultListenerBack = true;
            selectionConfig.isActivityResultBack = false;
            selectionConfig.onResultCallListener = call;
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            activity.startActivity(intent);
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.selectorStyle.getWindowAnimationStyle();
            activity.overridePendingTransition(windowAnimationStyle.activityEnterAnimation, R.anim.ps_anim_fade_in);
        }
    }


    /**
     * Start PictureSelector
     *
     * @param requestCode
     */
    public void forResult(int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            selectionConfig.isResultListenerBack = false;
            selectionConfig.isActivityResultBack = true;
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.selectorStyle.getWindowAnimationStyle();
            activity.overridePendingTransition(windowAnimationStyle.activityEnterAnimation, R.anim.ps_anim_fade_in);
        }
    }


    /**
     * ActivityResultLauncher PictureSelector
     *
     * @param launcher use {@link Activity.registerForActivityResult(ActivityResultContract, ActivityResultCallback)}
     */
    public void forResult(ActivityResultLauncher<Intent> launcher) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (launcher == null) {
                throw new NullPointerException("ActivityResultLauncher cannot be null");
            }
            selectionConfig.isResultListenerBack = false;
            selectionConfig.isActivityResultBack = true;
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            launcher.launch(intent);
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.selectorStyle.getWindowAnimationStyle();
            activity.overridePendingTransition(windowAnimationStyle.activityEnterAnimation, R.anim.ps_anim_fade_in);
        }
    }

    /**
     * build PictureSelectorFragment
     * <p>
     * The {@link IBridgePictureBehavior} interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     * </p>
     */
    public PictureSelectorFragment build() {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        if (!(activity instanceof IBridgePictureBehavior)) {
            throw new NullPointerException("Use only build PictureSelectorFragment," +
                    "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior.class);
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = false;
        selectionConfig.isActivityResultBack = true;
        selectionConfig.onResultCallListener = null;
        return new PictureSelectorFragment();
    }

    /**
     * build and launch PictureSelector
     *
     * @param containerViewId fragment container id
     * @param call
     */
    public PictureSelectorFragment buildLaunch(int containerViewId, OnResultCallbackListener<LocalMedia> call) {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        if (call == null) {
            throw new NullPointerException("OnResultCallbackListener cannot be null");
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = true;
        selectionConfig.isActivityResultBack = false;
        selectionConfig.onResultCallListener = call;
        FragmentManager fragmentManager = null;
        if (activity instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        }
        if (fragmentManager == null) {
            throw new NullPointerException("FragmentManager cannot be null");
        }
        PictureSelectorFragment selectorFragment = new PictureSelectorFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(selectorFragment.getFragmentTag());
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentManager.beginTransaction()
                .add(containerViewId, selectorFragment, selectorFragment.getFragmentTag())
                .addToBackStack(selectorFragment.getFragmentTag())
                .commitAllowingStateLoss();
        return selectorFragment;
    }
}
