package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
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
import com.luck.picture.lib.manager.SelectedManager;
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
    private final PictureSelectionConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionModel(PictureSelector selector, int chooseMode) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
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
     * @return PictureSelectorStyle
     */
    public PictureSelectionModel setSelectorUIStyle(PictureSelectorStyle uiStyle) {
        if (uiStyle != null) {
            PictureSelectionConfig.selectorStyle = uiStyle;
        }
        return this;
    }

    /**
     * Set App Language
     *
     * @param language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language {@link LanguageConfig}
     * @return PictureSelectionModel
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
     * @return
     */
    public PictureSelectionModel setImageEngine(ImageEngine engine) {
        PictureSelectionConfig.imageEngine = engine;
        return this;
    }

    /**
     * Set up player engine
     *  <p>
     *   Used to preview custom player instances，MediaPlayer by default
     *  </p>
     * @param engine
     * @return
     */
    public PictureSelectionModel setVideoPlayerEngine(VideoPlayerEngine engine) {
        PictureSelectionConfig.videoPlayerEngine = engine;
        return this;
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * Please use {@link CompressFileEngine}
     * @return
     */
    @Deprecated
    public PictureSelectionModel setCompressEngine(CompressEngine engine) {
        PictureSelectionConfig.compressEngine = engine;
        selectionConfig.isCompressEngine = true;
        return this;
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * @return
     */
    public PictureSelectionModel setCompressEngine(CompressFileEngine engine) {
        PictureSelectionConfig.compressFileEngine = engine;
        selectionConfig.isCompressEngine = true;
        return this;
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * Please Use {@link CropFileEngine}
     * @return
     */
    @Deprecated
    public PictureSelectionModel setCropEngine(CropEngine engine) {
        PictureSelectionConfig.cropEngine = engine;
        return this;
    }


    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    public PictureSelectionModel setCropEngine(CropFileEngine engine) {
        PictureSelectionConfig.cropFileEngine = engine;
        return this;
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     * Please Use {@link UriToFileTransformEngine}
     * @return
     *
     */
    @Deprecated
    public PictureSelectionModel setSandboxFileEngine(SandboxFileEngine engine) {
        if (SdkVersionUtils.isQ()) {
            PictureSelectionConfig.sandboxFileEngine = engine;
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
     * @return
     */
    public PictureSelectionModel setSandboxFileEngine(UriToFileTransformEngine engine) {
        if (SdkVersionUtils.isQ()) {
            PictureSelectionConfig.uriToFileTransformEngine = engine;
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
     * @return
     */
    @Deprecated
    public PictureSelectionModel setExtendLoaderEngine(ExtendLoaderEngine engine) {
        PictureSelectionConfig.loaderDataEngine = engine;
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
     * @return
     */
    public PictureSelectionModel setLoaderFactoryEngine(IBridgeLoaderFactory loaderFactory) {
        PictureSelectionConfig.loaderFactory = loaderFactory;
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
        PictureSelectionConfig.interpolatorFactory = interpolatorFactory;
        return this;
    }

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setCameraInterceptListener(OnCameraInterceptListener listener) {
        PictureSelectionConfig.onCameraInterceptListener = listener;
        return this;
    }


    /**
     * Intercept Record Audio click events, and users can implement their own Record Audio framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setRecordAudioInterceptListener(OnRecordAudioInterceptListener listener) {
        PictureSelectionConfig.onRecordAudioListener = listener;
        return this;
    }


    /**
     * Intercept preview click events, and users can implement their own preview framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setPreviewInterceptListener(OnPreviewInterceptListener listener) {
        PictureSelectionConfig.onPreviewInterceptListener = listener;
        return this;
    }


    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setInjectLayoutResourceListener(OnInjectLayoutResourceListener listener) {
        selectionConfig.isInjectLayoutResource = listener != null;
        PictureSelectionConfig.onLayoutResourceListener = listener;
        return this;
    }

    /**
     * Intercept media edit click events, and users can implement their own edit media framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setEditMediaInterceptListener(OnMediaEditInterceptListener listener) {
        PictureSelectionConfig.onEditMediaEventListener = listener;
        return this;
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setPermissionsInterceptListener(OnPermissionsInterceptListener listener) {
        PictureSelectionConfig.onPermissionsEventListener = listener;
        return this;
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setPermissionDescriptionListener(OnPermissionDescriptionListener listener) {
        PictureSelectionConfig.onPermissionDescriptionListener = listener;
        return this;
    }

    /**
     *  Permission denied
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setPermissionDeniedListener(OnPermissionDeniedListener listener) {
        PictureSelectionConfig.onPermissionDeniedListener = listener;
        return this;
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    public PictureSelectionModel setSelectLimitTipsListener(OnSelectLimitTipsListener listener) {
        PictureSelectionConfig.onSelectLimitTipsListener = listener;
        return this;
    }

    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setSelectFilterListener(OnSelectFilterListener listener) {
        PictureSelectionConfig.onSelectFilterListener = listener;
        return this;
    }

    /**
     * You need to filter out what doesn't meet the standards
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setQueryFilterListener(OnQueryFilterListener listener) {
        PictureSelectionConfig.onQueryFilterListener = listener;
        return this;
    }

    /**
     * Animate the selected item in the list
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setGridItemSelectAnimListener(OnGridItemSelectAnimListener listener) {
        PictureSelectionConfig.onItemSelectAnimListener = listener;
        return this;
    }

    /**
     * Animate the selected item
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setSelectAnimListener(OnSelectAnimListener listener) {
        PictureSelectionConfig.onSelectAnimListener = listener;
        return this;
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setAddBitmapWatermarkListener(OnBitmapWatermarkEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            PictureSelectionConfig.onBitmapWatermarkListener = listener;
        }
        return this;
    }

    /**
     * Process video thumbnails
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setVideoThumbnailListener(OnVideoThumbnailEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            PictureSelectionConfig.onVideoThumbnailEventListener = listener;
        }
        return this;
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    public PictureSelectionModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        PictureSelectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     * @return
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
     * @return
     */
    public PictureSelectionModel setSelectionMode(int selectionMode) {
        selectionConfig.selectionMode = selectionMode;
        selectionConfig.maxSelectNum = selectionConfig.selectionMode ==
                SelectModeConfig.SINGLE ? 1 : selectionConfig.maxSelectNum;
        return this;
    }


    /**
     * You can select pictures and videos at the same time
     *
     * @param isWithVideoImage Whether the pictures and videos can be selected together
     * @return
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
     * @return
     */
    public PictureSelectionModel setOfAllCameraType(int ofAllCameraType) {
        selectionConfig.ofAllCameraType = ofAllCameraType;
        return this;
    }

    /**
     * When the maximum number of choices is reached, does the list enable the mask effect
     *
     * @param isMaxSelectEnabledMask
     * @return
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
     * @return
     */
    public PictureSelectionModel isOriginalControl(boolean isOriginalControl) {
        selectionConfig.isOriginalControl = isOriginalControl;
        return this;
    }

    /**
     * If SyncCover
     *
     * @param isSyncCover
     * @return
     */
    public PictureSelectionModel isSyncCover(boolean isSyncCover) {
        selectionConfig.isSyncCover = isSyncCover;
        return this;
    }

    /**
     * Select the maximum number of files
     *
     * @param maxSelectNum PictureSelector max selection
     * @return
     */
    public PictureSelectionModel setMaxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = selectionConfig.selectionMode == SelectModeConfig.SINGLE ? 1 : maxSelectNum;
        return this;
    }

    /**
     * Select the minimum number of files
     *
     * @param minSelectNum PictureSelector min selection
     * @return
     */
    public PictureSelectionModel setMinSelectNum(int minSelectNum) {
        selectionConfig.minSelectNum = minSelectNum;
        return this;
    }


    /**
     * By clicking the title bar consecutively, RecyclerView automatically rolls back to the top
     *
     * @param isAutomaticTitleRecyclerTop
     * @return
     */
    public PictureSelectionModel isAutomaticTitleRecyclerTop(boolean isAutomaticTitleRecyclerTop) {
        selectionConfig.isAutomaticTitleRecyclerTop = isAutomaticTitleRecyclerTop;
        return this;
    }


    /**
     * @param Select whether to return directly
     * @return
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
     * @return
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
     * @return
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
     * @return
     */
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
     * @return
     */
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
     * @return
     */
    public PictureSelectionModel setAttachViewLifecycle(IBridgeViewLifecycle viewLifecycle) {
        PictureSelectionConfig.viewLifecycle = viewLifecycle;
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
     * @return
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
     * @return
     */
    public PictureSelectionModel setDefaultAlbumName(String defaultAlbumName) {
        selectionConfig.defaultAlbumName = defaultAlbumName;
        return this;
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    public PictureSelectionModel setCameraImageFormat(String imageFormat) {
        selectionConfig.cameraImageFormat = imageFormat;
        return this;
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    public PictureSelectionModel setCameraImageFormatForQ(String imageFormat) {
        selectionConfig.cameraImageFormatForQ = imageFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    public PictureSelectionModel setCameraVideoFormat(String videoFormat) {
        selectionConfig.cameraVideoFormat = videoFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    public PictureSelectionModel setCameraVideoFormatForQ(String videoFormat) {
        selectionConfig.cameraVideoFormatForQ = videoFormat;
        return this;
    }


    /**
     * filter max seconds video
     *
     * @param videoMaxSecond filter video max second
     * @return
     */
    public PictureSelectionModel setFilterVideoMaxSecond(int videoMaxSecond) {
        selectionConfig.filterVideoMaxSecond = videoMaxSecond * 1000;
        return this;
    }

    /**
     * filter min seconds video
     *
     * @param videoMinSecond filter video min second
     * @return
     */
    public PictureSelectionModel setFilterVideoMinSecond(int videoMinSecond) {
        selectionConfig.filterVideoMinSecond = videoMinSecond * 1000;
        return this;
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    public PictureSelectionModel setSelectMaxDurationSecond(int maxDurationSecond) {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000;
        return this;
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    public PictureSelectionModel setSelectMinDurationSecond(int minDurationSecond) {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000;
        return this;
    }

    /**
     * The max duration of video recording. If it is system recording, there may be compatibility problems
     *
     * @param maxSecond video record second
     * @return
     */
    public PictureSelectionModel setRecordVideoMaxSecond(int maxSecond) {
        selectionConfig.recordVideoMaxSecond = maxSecond;
        return this;
    }


    /**
     * Select the maximum video number of files
     *
     * @param maxVideoSelectNum PictureSelector video max selection
     * @return
     */
    public PictureSelectionModel setMaxVideoSelectNum(int maxVideoSelectNum) {
        selectionConfig.maxVideoSelectNum = selectionConfig.chooseMode == SelectMimeType.ofVideo() ? 0 : maxVideoSelectNum;
        return this;
    }

    /**
     * Select the minimum video number of files
     *
     * @param minVideoSelectNum PictureSelector video min selection
     * @return
     */
    public PictureSelectionModel setMinVideoSelectNum(int minVideoSelectNum) {
        selectionConfig.minVideoSelectNum = minVideoSelectNum;
        return this;
    }

    /**
     * Select the minimum audio number of files
     *
     * @param minAudioSelectNum PictureSelector audio min selection
     * @return
     */
    public PictureSelectionModel setMinAudioSelectNum(int minAudioSelectNum) {
        selectionConfig.minAudioSelectNum = minAudioSelectNum;
        return this;
    }

    /**
     * @param minSecond video record second
     * @return
     */
    public PictureSelectionModel setRecordVideoMinSecond(int minSecond) {
        selectionConfig.recordVideoMinSecond = minSecond;
        return this;
    }

    /**
     * @param imageSpanCount PictureSelector image span count
     * @return
     */
    public PictureSelectionModel setImageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }

    /**
     * @param isEmptyReturn No data can be returned
     * @return
     */
    public PictureSelectionModel isEmptyResultReturn(boolean isEmptyReturn) {
        selectionConfig.isEmptyResultReturn = isEmptyReturn;
        return this;
    }


    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     * @return
     */
    public PictureSelectionModel isQuickCapture(boolean isQuickCapture) {
        selectionConfig.isQuickCapture = isQuickCapture;
        return this;
    }

    /**
     * @param isDisplayCamera Whether to open camera button
     * @return
     */
    public PictureSelectionModel isDisplayCamera(boolean isDisplayCamera) {
        selectionConfig.isDisplayCamera = isDisplayCamera;
        return this;
    }

    /**
     * @param outPutCameraDir Camera output path
     *                        <p>Audio mode setting is not supported</p>
     * @return
     */
    public PictureSelectionModel setOutputCameraDir(String outPutCameraDir) {
        selectionConfig.outPutCameraDir = outPutCameraDir;
        return this;
    }

    /**
     * @param outPutAudioDir Audio output path
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     *            Normally, it should be consistent with {@link PictureSelectionConfig.setOutputCameraDir()};
     *            </p>
     *
     *            <p>
     *            If build.version.sdk_INT < 29,{@link PictureSelectionConfig.setQuerySandboxDir();}
     *            Do not set the external storage path,
     *            which may cause the problem of picture duplication
     *            </p>
     * @return
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
     * @param isOnlySandboxDir true or Only Display {@link PictureSelectionConfig.setQuerySandboxDir();}
     * @return
     */
    public PictureSelectionModel isOnlyObtainSandboxDir(boolean isOnlySandboxDir) {
        selectionConfig.isOnlySandboxDir = isOnlySandboxDir;
        return this;
    }

    /**
     * Displays the creation timeline of the resource
     *
     * @param isDisplayTimeAxis
     * @return
     */
    public PictureSelectionModel isDisplayTimeAxis(boolean isDisplayTimeAxis) {
        selectionConfig.isDisplayTimeAxis = isDisplayTimeAxis;
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
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
     * @return
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
     * @return
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
     * @return
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
     * @param mimeTypes Use example {@link { image/jpeg or image/png ... }}
     * @return
     */
    public PictureSelectionModel setQueryOnlyMimeType(String... mimeTypes) {
        if (mimeTypes != null && mimeTypes.length > 0) {
            selectionConfig.queryOnlyList.addAll(Arrays.asList(mimeTypes));
        }
        return this;
    }


    /**
     * Skip crop mimeType
     *
     * @param mimeTypes Use example {@link { image/gift or image/webp ... }}
     * @return
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
     * @return
     */
    public PictureSelectionModel setQuerySortOrder(String sortOrder) {
        if (!TextUtils.isEmpty(sortOrder)) {
            selectionConfig.sortOrder = sortOrder;
        }
        return this;
    }

    /**
     * @param isGif Whether to open gif
     * @return
     */
    public PictureSelectionModel isGif(boolean isGif) {
        selectionConfig.isGif = isGif;
        return this;
    }

    /**
     * @param isWebp Whether to open .webp
     * @return
     */
    public PictureSelectionModel isWebp(boolean isWebp) {
        selectionConfig.isWebp = isWebp;
        return this;
    }

    /**
     * @param isBmp Whether to open .isBmp
     * @return
     */
    public PictureSelectionModel isBmp(boolean isBmp) {
        selectionConfig.isBmp = isBmp;
        return this;
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     * @return
     */
    public PictureSelectionModel isPreviewFullScreenMode(boolean isFullScreenModel) {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @return
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
     * @return
     */
    public PictureSelectionModel isSyncWidthAndHeight(boolean isSyncWidthAndHeight) {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight;
        return this;
    }

    /**
     * Do you want to preview play the audio file?
     *
     * @param isPreviewAudio
     * @return
     */
    public PictureSelectionModel isPreviewAudio(boolean isPreviewAudio) {
        selectionConfig.isEnablePreviewAudio = isPreviewAudio;
        return this;
    }

    /**
     * @param isPreviewImage Do you want to preview the picture?
     * @return
     */
    public PictureSelectionModel isPreviewImage(boolean isPreviewImage) {
        selectionConfig.isEnablePreviewImage = isPreviewImage;
        return this;
    }


    /**
     * @param isPreviewVideo Do you want to preview the video?
     * @return
     */
    public PictureSelectionModel isPreviewVideo(boolean isPreviewVideo) {
        selectionConfig.isEnablePreviewVideo = isPreviewVideo;
        return this;
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     * @return
     */
    public PictureSelectionModel isAutoVideoPlay(boolean isAutoPlay) {
        selectionConfig.isAutoVideoPlay = isAutoPlay;
        return this;
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     * @return
     */
    public PictureSelectionModel isLoopAutoVideoPlay(boolean isLoopAutoPlay) {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay;
        return this;
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     * @return
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
     * @return
     */
    public PictureSelectionModel isOriginalSkipCompress(boolean isOriginalSkipCompress) {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress;
        return this;
    }

    /**
     * Filter the validity of file size or duration of audio and video
     *
     * @param isFilterSizeDuration
     * @return
     */
    public PictureSelectionModel isFilterSizeDuration(boolean isFilterSizeDuration) {
        selectionConfig.isFilterSizeDuration = isFilterSizeDuration;
        return this;
    }

    /**
     * Quick slide selection results
     *
     * @param isFastSlidingSelect
     * @return
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
     * @return
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
     * @return
     */
    public PictureSelectionModel setSelectedData(List<LocalMedia> selectedList) {
        if (selectedList == null) {
            return this;
        }
        if (selectionConfig.selectionMode == SelectModeConfig.SINGLE && selectionConfig.isDirectReturnSingle) {
            SelectedManager.clearSelectResult();
        } else {
            SelectedManager.addAllSelectResult(new ArrayList<>(selectedList));
        }
        return this;
    }

    /**
     * Photo album list animation {}
     * Use {@link AnimationType#ALPHA_IN_ANIMATION or SLIDE_IN_BOTTOM_ANIMATION} directly.
     *
     * @param animationMode
     * @return
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
            PictureSelectionConfig.onResultCallListener = call;
            if (PictureSelectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            activity.startActivity(intent);
            PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
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
            if (PictureSelectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
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
            if (PictureSelectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            Intent intent = new Intent(activity, PictureSelectorSupporterActivity.class);
            launcher.launch(intent);
            PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
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
        PictureSelectionConfig.onResultCallListener = null;
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
        PictureSelectionConfig.onResultCallListener = call;
        FragmentManager fragmentManager = null;
        if (activity instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        } else if (activity instanceof FragmentActivity) {
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
