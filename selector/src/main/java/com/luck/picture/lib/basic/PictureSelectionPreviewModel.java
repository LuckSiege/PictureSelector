package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.magical.BuildRecycleItemViewParams;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DoubleUtils;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2022/1/17 6:10 下午
 * @describe：PictureSelectionPreviewModel
 */
public final class PictureSelectionPreviewModel {
    private final SelectorConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionPreviewModel(PictureSelector selector) {
        this.selector = selector;
        selectionConfig = new SelectorConfig();
        SelectorProviders.getInstance().addSelectorConfigQueue(selectionConfig);
        selectionConfig.isPreviewZoomEffect = false;
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
    public PictureSelectionPreviewModel setImageEngine(ImageEngine engine) {
        selectionConfig.imageEngine = engine;
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
    public PictureSelectionPreviewModel setVideoPlayerEngine(VideoPlayerEngine engine) {
        selectionConfig.videoPlayerEngine = engine;
        return this;
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
    public PictureSelectionPreviewModel setSelectorUIStyle(PictureSelectorStyle uiStyle) {
        if (uiStyle != null) {
            selectionConfig.selectorStyle = uiStyle;
        }
        return this;
    }

    /**
     * Set App Language
     *
     * @param language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionPreviewModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionPreviewModel setDefaultLanguage(int defaultLanguage) {
        selectionConfig.defaultLanguage = defaultLanguage;
        return this;
    }

    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setInjectLayoutResourceListener(OnInjectLayoutResourceListener listener) {
        selectionConfig.isInjectLayoutResource = listener != null;
        selectionConfig.onLayoutResourceListener = listener;
        return this;
    }

    /**
     * View lifecycle listener
     *
     * @param viewLifecycle
     * @return
     */
    public PictureSelectionPreviewModel setAttachViewLifecycle(IBridgeViewLifecycle viewLifecycle) {
        selectionConfig.viewLifecycle = viewLifecycle;
        return this;
    }

    /**
     * Using the system player
     *
     * @param isUseSystemVideoPlayer
     */
    public PictureSelectionPreviewModel isUseSystemVideoPlayer(boolean isUseSystemVideoPlayer) {
        selectionConfig.isUseSystemVideoPlayer = isUseSystemVideoPlayer;
        return this;
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     * @return
     */
    public PictureSelectionPreviewModel isPreviewFullScreenMode(boolean isFullScreenModel) {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param listView  Use {@link RecyclerView,ListView}
     */
    public PictureSelectionPreviewModel isPreviewZoomEffect(boolean isPreviewZoomEffect, ViewGroup listView) {
        return isPreviewZoomEffect(isPreviewZoomEffect, selectionConfig.isPreviewFullScreenMode, listView);
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isEnableVideoSize Use {@link .isSyncWidthAndHeight()}
     */
    @Deprecated
    public PictureSelectionPreviewModel isEnableVideoSize(boolean isEnableVideoSize) {
        selectionConfig.isSyncWidthAndHeight = isEnableVideoSize;
        return this;
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isSyncWidthAndHeight
     * @return
     */
    public PictureSelectionPreviewModel isSyncWidthAndHeight(boolean isSyncWidthAndHeight) {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param isFullScreenModel
     * @param listView   Use {@link RecyclerView,ListView}
     */
    public PictureSelectionPreviewModel isPreviewZoomEffect(boolean isPreviewZoomEffect, boolean isFullScreenModel, ViewGroup listView) {
        if (listView instanceof RecyclerView || listView instanceof ListView) {
            if (isPreviewZoomEffect) {
                if (isFullScreenModel) {
                    BuildRecycleItemViewParams.generateViewParams(listView, 0);
                } else {
                    BuildRecycleItemViewParams.generateViewParams(listView, DensityUtil.getStatusBarHeight(selector.getActivity()));
                }
            }
            selectionConfig.isPreviewZoomEffect = isPreviewZoomEffect;
        } else {
            throw new IllegalArgumentException(listView.getClass().getCanonicalName()
                    + " Must be " + RecyclerView.class + " or " + ListView.class);
        }
        return this;
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     * @return
     */
    public PictureSelectionPreviewModel isAutoVideoPlay(boolean isAutoPlay) {
        selectionConfig.isAutoVideoPlay = isAutoPlay;
        return this;
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     * @return
     */
    public PictureSelectionPreviewModel isLoopAutoVideoPlay(boolean isLoopAutoPlay) {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay;
        return this;
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     * @return
     */
    public PictureSelectionPreviewModel isVideoPauseResumePlay(boolean isPauseResumePlay) {
        selectionConfig.isPauseResumePlay = isPauseResumePlay;
        return this;
    }

    /**
     * Intercept external preview click events, and users can implement their own preview framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setExternalPreviewEventListener(OnExternalPreviewEventListener listener) {
        selectionConfig.onExternalPreviewEventListener = listener;
        return this;
    }

    /**
     * startActivityPreview(); Preview mode, custom preview callback
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setInjectActivityPreviewFragment(OnInjectActivityPreviewListener listener) {
        selectionConfig.onInjectActivityPreviewListener = listener;
        return this;
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        selectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * @param isHidePreviewDownload Previews do not show downloads
     * @return
     */
    public PictureSelectionPreviewModel isHidePreviewDownload(boolean isHidePreviewDownload) {
        selectionConfig.isHidePreviewDownload = isHidePreviewDownload;
        return this;
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     *
     * @param isNewKeyBackMode
     */
    public PictureSelectionPreviewModel isNewKeyBackMode(boolean isNewKeyBackMode) {
        selectionConfig.isNewKeyBackMode = isNewKeyBackMode;
        return this;
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition
     * @param isDisplayDelete
     * @param list
     */
    public void startFragmentPreview(int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        startFragmentPreview(null, currentPosition, isDisplayDelete, list);
    }

    /**
     * preview LocalMedia
     *
     * @param previewFragment PictureSelectorPreviewFragment
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     */
    public void startFragmentPreview(PictureSelectorPreviewFragment previewFragment, int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            if (list == null || list.size() == 0) {
                throw new NullPointerException("preview data is null");
            }
            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            String fragmentTag;
            if (previewFragment != null) {
                fragmentTag = previewFragment.getFragmentTag();
            } else {
                fragmentTag = PictureSelectorPreviewFragment.TAG;
                previewFragment = PictureSelectorPreviewFragment.newInstance();
            }
            if (ActivityCompatHelper.checkFragmentNonExits((FragmentActivity) activity, fragmentTag)) {
                ArrayList<LocalMedia> previewData = new ArrayList<>(list);
                previewFragment.setExternalPreviewData(currentPosition, previewData.size(), previewData, isDisplayDelete);
                FragmentInjectManager.injectSystemRoomFragment(fragmentManager, fragmentTag, previewFragment);
            }
        }
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     *                        <p>
     *                        You can do it {@link .setInjectActivityPreviewFragment()} interface, custom Preview
     *                        </p>
     */
    public void startActivityPreview(int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            if (list == null || list.size() == 0) {
                throw new NullPointerException("preview data is null");
            }
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            selectionConfig.addSelectedPreviewResult(list);
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW, true);
            intent.putExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE);
            intent.putExtra(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition);
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, isDisplayDelete);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivity(intent);
            } else {
                activity.startActivity(intent);
            }
            if (selectionConfig.isPreviewZoomEffect) {
                activity.overridePendingTransition(R.anim.ps_anim_fade_in, R.anim.ps_anim_fade_in);
            } else {
                PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.selectorStyle.getWindowAnimationStyle();
                activity.overridePendingTransition(windowAnimationStyle.activityEnterAnimation, R.anim.ps_anim_fade_in);
            }
        }
    }

}
