package com.luck.picture.lib.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import java.lang.ref.SoftReference

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：PictureSelector
 */

class PictureSelector {

    companion object {
        fun create(context: Context): PictureSelector {
            return PictureSelector(context as Activity)
        }

        fun create(activity: FragmentActivity): PictureSelector {
            return PictureSelector(activity)
        }

        fun create(fragment: Fragment): PictureSelector {
            return PictureSelector(fragment)
        }

        @Suppress("UNCHECKED_CAST")
        fun obtainSelectResults(intent: Intent?): MutableList<LocalMedia> {
            intent?.let {
                return it.getSerializableExtra(SelectorConstant.KEY_EXTRA_RESULT) as MutableList<LocalMedia>
            }
            return mutableListOf()
        }
    }

    /**
     * open gallery source
     * use [SelectorMode]
     */
    fun openGallery(mode: SelectorMode): SelectionMainModel {
        return SelectionMainModel(this, mode)
    }

    /**
     * From system album Select the type of images you want，all or images or video or audio
     * use [SelectorMode]
     */
    fun openSystemGallery(mode: SelectorMode): SelectionSystemModel {
        return SelectionSystemModel(this, mode)
    }

    /**
     * only use camera，images or video or audio
     * use [SelectorMode]
     */
    fun openCamera(mode: SelectorMode): SelectionCameraModel {
        return SelectionCameraModel(this, mode)
    }

    /**
     * query the type of images you want，all or images or video or audio
     * use [SelectorMode]
     */
    fun dataSource(mode: SelectorMode): SelectionQueryModel {
        return SelectionQueryModel(this, mode)
    }

    /**
     * preview mode
     */
    fun openPreview(): SelectionPreviewModel {
        return SelectionPreviewModel(this)
    }

    private var mActivity: SoftReference<Activity>? = null
    private var mFragment: SoftReference<Fragment>? = null

    constructor(activity: Activity) : this(activity, null)

    constructor(fragment: Fragment) : this(fragment.activity, fragment)

    constructor(activity: Activity?, fragment: Fragment?) {
        mActivity = SoftReference(activity)
        mFragment = SoftReference(fragment)
    }

    internal fun getActivity(): Activity? {
        return mActivity?.get()
    }

    internal fun getFragment(): Fragment? {
        return if (mFragment != null) mFragment?.get() else null
    }
}