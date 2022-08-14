package com.luck.picture.lib.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：PictureSelector
 */

public final class PictureSelector {

    private final SoftReference<Activity> mActivity;
    private final SoftReference<Fragment> mFragment;

    private PictureSelector(Activity activity) {
        this(activity, null);
    }

    private PictureSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private PictureSelector(Activity activity, Fragment fragment) {
        mActivity = new SoftReference<>(activity);
        mFragment = new SoftReference<>(fragment);
    }

    /**
     * Start PictureSelector for context.
     *
     * @param context
     * @return PictureSelector instance.
     */
    public static PictureSelector create(Context context) {
        return new PictureSelector((Activity) context);
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    public static PictureSelector create(AppCompatActivity activity) {
        return new PictureSelector(activity);
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    public static PictureSelector create(FragmentActivity activity) {
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
     * @param chooseMode Select the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionModel
     * Use {@link SelectMimeType}
     */
    public PictureSelectionModel openGallery(int chooseMode) {
        return new PictureSelectionModel(this, chooseMode);
    }

    /**
     * @param chooseMode only use camera，images or video or audio
     * @return LocalMedia PictureSelectionModel
     * Use {@link SelectMimeType}
     */
    public PictureSelectionCameraModel openCamera(int chooseMode) {
        return new PictureSelectionCameraModel(this, chooseMode);
    }

    /**
     * @param chooseMode Select the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionSystemModel
     * Use {@link SelectMimeType}
     * <p>
     * openSystemGallery mode only supports some APIs
     * </p>
     */
    public PictureSelectionSystemModel openSystemGallery(int chooseMode) {
        return new PictureSelectionSystemModel(this, chooseMode);
    }

    /**
     * @param selectMimeType query the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionQueryModel
     * Use {@link SelectMimeType}
     * <p>
     * only query {@link LocalMedia} data source
     * </p>
     */
    public PictureSelectionQueryModel dataSource(int selectMimeType) {
        return new PictureSelectionQueryModel(this, selectMimeType);
    }

    /**
     * Preview mode to preview images or videos or audio
     *
     * @return
     */
    public PictureSelectionPreviewModel openPreview() {
        return new PictureSelectionPreviewModel(this);
    }

    /**
     * set result
     *
     * @param data result
     * @return
     */
    public static Intent putIntentResult(ArrayList<LocalMedia> data) {
        return new Intent().putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, data);
    }

    /**
     * @param intent
     * @return get Selector  LocalMedia
     */
    public static ArrayList<LocalMedia> obtainSelectorList(Intent intent) {
        if (intent == null) {
            return new ArrayList<>();
        }
        ArrayList<LocalMedia> result = intent.getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
        return result != null ? result : new ArrayList<>();
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
