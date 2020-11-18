package com.luck.picture.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.tools.DoubleUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：PictureSelector
 */

public final class PictureSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private PictureSelector(Activity activity) {
        this(activity, null);
    }

    private PictureSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private PictureSelector(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    public static PictureSelector create(Activity activity) {
        return new PictureSelector(activity);
    }

    /**
     * Start PictureSelector for Fragment.
     *
     * @param fragment
     * @return PictureSelector instance.
     */
    public static PictureSelector create(Fragment fragment) {
        return new PictureSelector(fragment);
    }

    /**
     * @param chooseMode Select the type of picture you want，all or Picture or Video .
     * @return LocalMedia PictureSelectionModel
     * Use {@link PictureMimeType.ofAll(),ofImage(),ofVideo(),ofAudio()}.
     */
    public PictureSelectionModel openGallery(int chooseMode) {
        return new PictureSelectionModel(this, chooseMode);
    }

    /**
     * @param chooseMode Select the type of picture you want，Picture or Video.
     * @return LocalMedia PictureSelectionModel
     * Use {@link PictureMimeType.ofImage(),ofVideo()}.
     */
    public PictureSelectionModel openCamera(int chooseMode) {
        return new PictureSelectionModel(this, chooseMode, true);
    }

    /**
     * 外部预览时设置样式
     *
     * @param themeStyle
     * @return
     */
    public PictureSelectionModel themeStyle(int themeStyle) {
        return new PictureSelectionModel(this, PictureMimeType.ofImage())
                .theme(themeStyle);
    }

    /**
     * 外部预览时动态代码设置样式
     *
     * @param style
     * @return
     */
    public PictureSelectionModel setPictureStyle(PictureParameterStyle style) {
        return new PictureSelectionModel(this, PictureMimeType.ofImage())
                .setPictureStyle(style);
    }

    /**
     * @param data
     * @return Selector Multiple LocalMedia
     */
    public static List<LocalMedia> obtainMultipleResult(Intent data) {
        if (data != null) {
            List<LocalMedia> result = data.getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
            return result == null ? new ArrayList<>() : result;
        }
        return new ArrayList<>();
    }

    /**
     * @param data
     * @return Put image Intent Data
     */
    public static Intent putIntentResult(List<LocalMedia> data) {
        return new Intent().putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION,
                (ArrayList<? extends Parcelable>) data);
    }

    /**
     * @param bundle
     * @return get Selector  LocalMedia
     */
    public static List<LocalMedia> obtainSelectorList(Bundle bundle) {
        if (bundle != null) {
           return bundle.getParcelableArrayList(PictureConfig.EXTRA_SELECT_LIST);
        }
        return null;
    }

    /**
     * @param selectedImages
     * @return put Selector  LocalMedia
     */
    public static void saveSelectorList(Bundle outState, List<LocalMedia> selectedImages) {
        outState.putParcelableArrayList(PictureConfig.EXTRA_SELECT_LIST,
                (ArrayList<? extends Parcelable>) selectedImages);
    }

    /**
     * set preview image
     *
     * @param position
     * @param medias
     */
    public void externalPicturePreview(int position, List<LocalMedia> medias, int enterAnimation) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureExternalPreviewActivity.class);
                intent.putParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST,
                        (ArrayList<? extends Parcelable>) medias);
                intent.putExtra(PictureConfig.EXTRA_POSITION, position);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(enterAnimation != 0
                        ? enterAnimation : R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview image
     *
     * @param position
     * @param medias
     * @param directory_path
     */
    public void externalPicturePreview(int position, String directory_path, List<LocalMedia> medias, int enterAnimation) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureExternalPreviewActivity.class);
                intent.putParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (ArrayList<? extends Parcelable>) medias);
                intent.putExtra(PictureConfig.EXTRA_POSITION, position);
                intent.putExtra(PictureConfig.EXTRA_DIRECTORY_PATH, directory_path);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(enterAnimation != 0
                        ? enterAnimation : R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview video
     *
     * @param path
     */
    public void externalPictureVideo(String path) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureVideoPlayActivity.class);
                intent.putExtra(PictureConfig.EXTRA_VIDEO_PATH, path);
                intent.putExtra(PictureConfig.EXTRA_PREVIEW_VIDEO, true);
                getActivity().startActivity(intent);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview audio
     *
     * @param path
     */
    public void externalPictureAudio(String path) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PicturePlayAudioActivity.class);
                intent.putExtra(PictureConfig.EXTRA_AUDIO_PATH, path);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.picture_anim_enter, 0);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * @return Activity.
     */
    @Nullable
    Activity getActivity() {
        return mActivity.get();
    }

    /**
     * @return Fragment.
     */
    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
