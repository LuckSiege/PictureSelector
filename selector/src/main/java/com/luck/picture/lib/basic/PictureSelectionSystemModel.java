package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.luck.picture.lib.PictureSelectorSystemFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.FileSizeUnit;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.SandboxFileEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectFilterListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.Arrays;

/**
 * @author：luck
 * @date：2022/1/17 5:52 下午
 * @describe：PictureSelectionSystemModel
 */
public final class PictureSelectionSystemModel {
    private final SelectorConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionSystemModel(PictureSelector selector, int chooseMode) {
        this.selector = selector;
        selectionConfig = new SelectorConfig();
        SelectorProviders.getInstance().addSelectorConfigQueue(selectionConfig);
        selectionConfig.chooseMode = chooseMode;
        selectionConfig.isPreviewFullScreenMode = false;
        selectionConfig.isPreviewZoomEffect = false;
    }

    /**
     * @param selectionMode PictureSelector Selection model
     *                      and {@link SelectModeConfig.MULTIPLE} or {@link SelectModeConfig.SINGLE}
     *                      <p>
     *                      Use {@link SelectModeConfig}
     *                      </p>
     * @return
     */
    public PictureSelectionSystemModel setSelectionMode(int selectionMode) {
        selectionConfig.selectionMode = selectionMode;
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
    public PictureSelectionSystemModel isOriginalControl(boolean isOriginalControl) {
        selectionConfig.isCheckOriginalImage = isOriginalControl;
        return this;
    }

    /**
     * Skip crop mimeType
     *
     * @param mimeTypes Use example {@link { image/gift or image/webp ... }}
     * @return
     */
    public PictureSelectionSystemModel setSkipCropMimeType(String... mimeTypes) {
        if (mimeTypes != null && mimeTypes.length > 0) {
            selectionConfig.skipCropList.addAll(Arrays.asList(mimeTypes));
        }
        return this;
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     * @return
     */
    public PictureSelectionSystemModel isOriginalSkipCompress(boolean isOriginalSkipCompress) {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress;
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
    public PictureSelectionSystemModel setCompressEngine(CompressEngine engine) {
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
    public PictureSelectionSystemModel setCompressEngine(CompressFileEngine engine) {
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
    public PictureSelectionSystemModel setCropEngine(CropEngine engine) {
        selectionConfig.cropEngine = engine;
        return this;
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    public PictureSelectionSystemModel setCropEngine(CropFileEngine engine) {
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
    public PictureSelectionSystemModel setSandboxFileEngine(SandboxFileEngine engine) {
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
    public PictureSelectionSystemModel setSandboxFileEngine(UriToFileTransformEngine engine) {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine;
            selectionConfig.isSandboxFileEngine = true;
        } else {
            selectionConfig.isSandboxFileEngine = false;
        }
        return this;
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     *
     * @param isNewKeyBackMode
     */
    public PictureSelectionSystemModel isNewKeyBackMode(boolean isNewKeyBackMode) {
        selectionConfig.isNewKeyBackMode = isNewKeyBackMode;
        return this;
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    public PictureSelectionSystemModel setSelectMaxFileSize(long fileKbSize) {
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
    public PictureSelectionSystemModel setSelectMinFileSize(long fileKbSize) {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize;
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB;
        }
        return this;
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    public PictureSelectionSystemModel setSelectMaxDurationSecond(int maxDurationSecond) {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000;
        return this;
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    public PictureSelectionSystemModel setSelectMinDurationSecond(int minDurationSecond) {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000;
        return this;
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    public PictureSelectionSystemModel setPermissionsInterceptListener(OnPermissionsInterceptListener listener) {
        selectionConfig.onPermissionsEventListener = listener;
        return this;
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    public PictureSelectionSystemModel setPermissionDescriptionListener(OnPermissionDescriptionListener listener) {
        selectionConfig.onPermissionDescriptionListener = listener;
        return this;
    }

    /**
     *  Permission denied
     *
     * @param listener
     * @return
     */
    public PictureSelectionSystemModel setPermissionDeniedListener(OnPermissionDeniedListener listener) {
        selectionConfig.onPermissionDeniedListener = listener;
        return this;
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    public PictureSelectionSystemModel setSelectLimitTipsListener(OnSelectLimitTipsListener listener) {
        selectionConfig.onSelectLimitTipsListener = listener;
        return this;
    }

    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param listener
     * @return
     */
    public PictureSelectionSystemModel setSelectFilterListener(OnSelectFilterListener listener) {
        selectionConfig.onSelectFilterListener = listener;
        return this;
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    public PictureSelectionSystemModel setAddBitmapWatermarkListener(OnBitmapWatermarkEventListener listener) {
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
    public PictureSelectionSystemModel setVideoThumbnailListener(OnVideoThumbnailEventListener listener) {
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
    public PictureSelectionSystemModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        selectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * Call the system library to obtain resources
     * <p>
     * Using the system gallery library, some API functions will not be supported
     * </p>
     *
     * @param call
     */
    public void forSystemResult(OnResultCallbackListener<LocalMedia> call) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (call == null) {
                throw new NullPointerException("OnResultCallbackListener cannot be null");
            }
            selectionConfig.onResultCallListener = call;
            selectionConfig.isResultListenerBack = true;
            selectionConfig.isActivityResultBack = false;
            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            Fragment fragment = fragmentManager.findFragmentByTag(PictureSelectorSystemFragment.TAG);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            FragmentInjectManager.injectSystemRoomFragment(fragmentManager,
                    PictureSelectorSystemFragment.TAG, PictureSelectorSystemFragment.newInstance());
        }
    }


    /**
     * Call the system library to obtain resources
     * <p>
     * Using the system gallery library, some API functions will not be supported
     * </p>
     * <p>
     * The {@link IBridgePictureBehavior} interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     * </p>
     */
    public void forSystemResult() {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (!(activity instanceof IBridgePictureBehavior)) {
                throw new NullPointerException("Use only forSystemResult();," +
                        "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior.class);
            }
            selectionConfig.isActivityResultBack = true;
            selectionConfig.onResultCallListener = null;
            selectionConfig.isResultListenerBack = false;

            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            Fragment fragment = fragmentManager.findFragmentByTag(PictureSelectorSystemFragment.TAG);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
            FragmentInjectManager.injectSystemRoomFragment(fragmentManager,
                    PictureSelectorSystemFragment.TAG, PictureSelectorSystemFragment.newInstance());
        }
    }


    /**
     * Start PictureSelector
     *
     * @param requestCode
     */
    public void forSystemResultActivity(int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            selectionConfig.isResultListenerBack = false;
            selectionConfig.isActivityResultBack = true;
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            intent.putExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, PictureConfig.MODE_TYPE_SYSTEM_SOURCE);
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
     *
     * @param launcher use {@link Activity.registerForActivityResult( ActivityResultContract , ActivityResultCallback )}
     */
    public void forSystemResultActivity(ActivityResultLauncher<Intent> launcher) {
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
            intent.putExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, PictureConfig.MODE_TYPE_SYSTEM_SOURCE);
            launcher.launch(intent);
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0);
        }
    }

    /**
     * Start PictureSelector
     *
     * @param call
     */
    public void forSystemResultActivity(OnResultCallbackListener<LocalMedia> call) {
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
            intent.putExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, PictureConfig.MODE_TYPE_SYSTEM_SOURCE);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0);
        }
    }
}
