package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.luck.picture.lib.PictureOnlyCameraFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.config.VideoQuality;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.SandboxFileEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2022/1/18 9:33 上午
 * @describe：PictureSelectionCameraModel
 */
public final class PictureSelectionCameraModel {
    private final SelectorConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionCameraModel(PictureSelector selector, int chooseMode) {
        this.selector = selector;
        selectionConfig = new SelectorConfig();
        SelectorProviders.getInstance().addSelectorConfigQueue(selectionConfig);
        selectionConfig.chooseMode = chooseMode;
        selectionConfig.isOnlyCamera = true;
        selectionConfig.isDisplayTimeAxis = false;
        selectionConfig.isPreviewFullScreenMode = false;
        selectionConfig.isPreviewZoomEffect = false;
        selectionConfig.isOpenClickSound = false;
    }

    /**
     * Set App Language
     *
     * @param language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionCameraModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionCameraModel setDefaultLanguage(int defaultLanguage) {
        selectionConfig.defaultLanguage = defaultLanguage;
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
    public PictureSelectionCameraModel setCompressEngine(CompressEngine engine) {
        selectionConfig.compressEngine = engine;
        selectionConfig.isCompressEngine = true;
        return this;
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * @return
     */
    public PictureSelectionCameraModel setCompressEngine(CompressFileEngine engine) {
        selectionConfig.compressFileEngine = engine;
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
    public PictureSelectionCameraModel setCropEngine(CropEngine engine) {
        selectionConfig.cropEngine = engine;
        return this;
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    public PictureSelectionCameraModel setCropEngine(CropFileEngine engine) {
        selectionConfig.cropFileEngine = engine;
        return this;
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     * Please Use {@link UriToFileTransformEngine}
     * @return
     */
    @Deprecated
    public PictureSelectionCameraModel setSandboxFileEngine(SandboxFileEngine engine) {
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
     * @return
     */
    public PictureSelectionCameraModel setSandboxFileEngine(UriToFileTransformEngine engine) {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine;
            selectionConfig.isSandboxFileEngine = true;
        } else {
            selectionConfig.isSandboxFileEngine = false;
        }
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
    public PictureSelectionCameraModel setRequestedOrientation(int requestedOrientation) {
        selectionConfig.requestedOrientation = requestedOrientation;
        return this;
    }


    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setCameraInterceptListener(OnCameraInterceptListener listener) {
        selectionConfig.onCameraInterceptListener = listener;
        return this;
    }

    /**
     * Intercept Record Audio click events, and users can implement their own Record Audio framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setRecordAudioInterceptListener(OnRecordAudioInterceptListener listener) {
        selectionConfig.onRecordAudioListener = listener;
        return this;
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setPermissionsInterceptListener(OnPermissionsInterceptListener listener) {
        selectionConfig.onPermissionsEventListener = listener;
        return this;
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setPermissionDescriptionListener(OnPermissionDescriptionListener listener) {
        selectionConfig.onPermissionDescriptionListener = listener;
        return this;
    }

    /**
     *  Permission denied
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setPermissionDeniedListener(OnPermissionDeniedListener listener) {
        selectionConfig.onPermissionDeniedListener = listener;
        return this;
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    public PictureSelectionCameraModel setSelectLimitTipsListener(OnSelectLimitTipsListener listener) {
        selectionConfig.onSelectLimitTipsListener = listener;
        return this;
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setAddBitmapWatermarkListener(OnBitmapWatermarkEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onBitmapWatermarkListener = listener;
        }
        return this;
    }

    /**
     * Process video thumbnails
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setVideoThumbnailListener(OnVideoThumbnailEventListener listener) {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onVideoThumbnailEventListener = listener;
        }
        return this;
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    public PictureSelectionCameraModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        selectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     * @return
     */
    public PictureSelectionCameraModel isCameraForegroundService(boolean isForeground) {
        selectionConfig.isCameraForegroundService = isForeground;
        return this;
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     *
     * @param isNewKeyBackMode
     */
    public PictureSelectionCameraModel isNewKeyBackMode(boolean isNewKeyBackMode) {
        selectionConfig.isNewKeyBackMode = isNewKeyBackMode;
        return this;
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param ofAllCameraType {@link SelectMimeType.ofImage or SelectMimeType.ofVideo}
     *                        The default is ofAll() mode
     * @return
     */
    public PictureSelectionCameraModel setOfAllCameraType(int ofAllCameraType) {
        selectionConfig.ofAllCameraType = ofAllCameraType;
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
    public PictureSelectionCameraModel isOriginalControl(boolean isOriginalControl) {
        selectionConfig.isOriginalControl = isOriginalControl;
        selectionConfig.isCheckOriginalImage = isOriginalControl;
        return this;
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     * @return
     */
    public PictureSelectionCameraModel isOriginalSkipCompress(boolean isOriginalSkipCompress) {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress;
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
    public PictureSelectionCameraModel setVideoQuality(int videoQuality) {
        selectionConfig.videoQuality = videoQuality;
        return this;
    }

    /**
     * Select the maximum number of files
     *
     * @param maxSelectNum PictureSelector max selection
     */
    private PictureSelectionCameraModel setMaxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = selectionConfig.selectionMode == SelectModeConfig.SINGLE ? 1 : maxSelectNum;
        return this;
    }

    /**
     * Select the maximum video number of files
     *
     * @param maxVideoSelectNum PictureSelector video max selection
     */
    public PictureSelectionCameraModel setMaxVideoSelectNum(int maxVideoSelectNum) {
        selectionConfig.maxVideoSelectNum = selectionConfig.chooseMode == SelectMimeType.ofVideo() ? 0 : maxVideoSelectNum;
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    public PictureSelectionCameraModel setSelectMaxFileSize(long fileKbSize) {
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
    public PictureSelectionCameraModel setSelectMinFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize;
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }


    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    public PictureSelectionCameraModel setCameraImageFormat(String imageFormat) {
        selectionConfig.cameraImageFormat = imageFormat;
        return this;
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    public PictureSelectionCameraModel setCameraImageFormatForQ(String imageFormat) {
        selectionConfig.cameraImageFormatForQ = imageFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    public PictureSelectionCameraModel setCameraVideoFormat(String videoFormat) {
        selectionConfig.cameraVideoFormat = videoFormat;
        return this;
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    public PictureSelectionCameraModel setCameraVideoFormatForQ(String videoFormat) {
        selectionConfig.cameraVideoFormatForQ = videoFormat;
        return this;
    }

    /**
     * The max duration of video recording. If it is system recording, there may be compatibility problems
     *
     * @param maxSecond video record second
     * @return
     */
    public PictureSelectionCameraModel setRecordVideoMaxSecond(int maxSecond) {
        selectionConfig.recordVideoMaxSecond = maxSecond;
        return this;
    }

    /**
     * @param minSecond video record second
     * @return
     */
    public PictureSelectionCameraModel setRecordVideoMinSecond(int minSecond) {
        selectionConfig.recordVideoMinSecond = minSecond;
        return this;
    }


    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    public PictureSelectionCameraModel setSelectMaxDurationSecond(int maxDurationSecond) {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000;
        return this;
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    public PictureSelectionCameraModel setSelectMinDurationSecond(int minDurationSecond) {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000;
        return this;
    }


    /**
     * @param outPutCameraDir Camera output path
     *                        <p>Audio mode setting is not supported</p>
     * @return
     */
    public PictureSelectionCameraModel setOutputCameraDir(String outPutCameraDir) {
        selectionConfig.outPutCameraDir = outPutCameraDir;
        return this;
    }

    /**
     * @param outPutAudioDir Audio output path
     * @return
     */
    public PictureSelectionCameraModel setOutputAudioDir(String outPutAudioDir) {
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
    public PictureSelectionCameraModel setOutputCameraImageFileName(String fileName) {
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
    public PictureSelectionCameraModel setOutputCameraVideoFileName(String fileName) {
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
    public PictureSelectionCameraModel setOutputAudioFileName(String fileName) {
        selectionConfig.outPutAudioFileName = fileName;
        return this;
    }


    /**
     * @param selectedList Select the selected picture set
     * @return
     */
    public PictureSelectionCameraModel setSelectedData(List<LocalMedia> selectedList) {
        if (selectedList == null) {
            return this;
        }
        setMaxSelectNum(selectedList.size() + 1);
        setMaxVideoSelectNum(selectedList.size() + 1);
        if (selectionConfig.selectionMode == SelectModeConfig.SINGLE && selectionConfig.isDirectReturnSingle) {
            selectionConfig.selectedResult.clear();
        } else {
            selectionConfig.addAllSelectResult(new ArrayList<>(selectedList));
        }
        return this;
    }

    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     * @return
     */
    public PictureSelectionCameraModel isQuickCapture(boolean isQuickCapture) {
        selectionConfig.isQuickCapture = isQuickCapture;
        return this;
    }

    /**
     * Set camera direction (after default image)
     */
    public PictureSelectionCameraModel isCameraAroundState(boolean isCameraAroundState) {
        selectionConfig.isCameraAroundState = isCameraAroundState;
        return this;
    }

    /**
     * Camera image rotation, automatic correction
     */
    public PictureSelectionCameraModel isCameraRotateImage(boolean isCameraRotateImage) {
        selectionConfig.isCameraRotateImage = isCameraRotateImage;
        return this;
    }

    /**
     * Start PictureSelector
     * <p>
     * The {@link IBridgePictureBehavior} interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     * </p>
     * <p>
     * If the navigation component manages fragments,
     * it is recommended to use {@link PictureSelectionCameraModel.forResultActivity()} in openCamera mode
     * </p>
     */
    public void forResult() {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            selectionConfig.isResultListenerBack = false;
            selectionConfig.isActivityResultBack = true;
            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            if (!(activity instanceof IBridgePictureBehavior)) {
                throw new NullPointerException("Use only camera openCamera mode," +
                        "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior.class);
            }
            Fragment fragment = fragmentManager.findFragmentByTag(PictureOnlyCameraFragment.TAG);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            FragmentInjectManager.injectSystemRoomFragment(fragmentManager,
                    PictureOnlyCameraFragment.TAG, PictureOnlyCameraFragment.newInstance());
        }
    }


    /**
     * Start PictureSelector Camera
     * <p>
     * If the navigation component manages fragments,
     * it is recommended to use {@link PictureSelectionCameraModel.forResultActivity()} in openCamera mode
     * </p>
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
            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            Fragment fragment = fragmentManager.findFragmentByTag(PictureOnlyCameraFragment.TAG);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            FragmentInjectManager.injectSystemRoomFragment(fragmentManager,
                    PictureOnlyCameraFragment.TAG, PictureOnlyCameraFragment.newInstance());
        }
    }


    /**
     * build PictureOnlyCameraFragment
     * <p>
     * The {@link IBridgePictureBehavior} interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     * </p>
     */
    public PictureOnlyCameraFragment build() {
        Activity activity = selector.getActivity();
        if (activity == null) {
            throw new NullPointerException("Activity cannot be null");
        }
        if (!(activity instanceof IBridgePictureBehavior)) {
            throw new NullPointerException("Use only build PictureOnlyCameraFragment," +
                    "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior.class);
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = false;
        selectionConfig.isActivityResultBack = true;
        selectionConfig.onResultCallListener = null;
        return new PictureOnlyCameraFragment();
    }


    /**
     * build and launch PictureSelector Camera
     *
     * @param containerViewId fragment container id
     * @param call
     */
    public PictureOnlyCameraFragment buildLaunch(int containerViewId, OnResultCallbackListener<LocalMedia> call) {
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
        PictureOnlyCameraFragment onlyCameraFragment = new PictureOnlyCameraFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(onlyCameraFragment.getFragmentTag());
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        fragmentManager.beginTransaction()
                .add(containerViewId, onlyCameraFragment, onlyCameraFragment.getFragmentTag())
                .addToBackStack(onlyCameraFragment.getFragmentTag())
                .commitAllowingStateLoss();
        return onlyCameraFragment;
    }

    /**
     * Start PictureSelector
     * <p>
     * If you are in the Navigation Fragment scene, you must use this method
     * </p>
     *
     * @param requestCode
     */
    public void forResultActivity(int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            selectionConfig.isResultListenerBack = false;
            selectionConfig.isActivityResultBack = true;
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0);
        }
    }

    /**
     * ActivityResultLauncher PictureSelector
     * <p>
     *     If you are in the Navigation Fragment scene, you must use this method
     * </p>
     *
     * @param launcher use {@link Activity.registerForActivityResult( ActivityResultContract , ActivityResultCallback )}
     */
    public void forResultActivity(ActivityResultLauncher<Intent> launcher) {
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
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            launcher.launch(intent);
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0);
        }
    }

    /**
     * Start PictureSelector
     * <p>
     * If you are in the Navigation Fragment scene, you must use this method
     * </>
     *
     * @param call
     */
    public void forResultActivity(OnResultCallbackListener<LocalMedia> call) {
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
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0);
        }
    }
}
