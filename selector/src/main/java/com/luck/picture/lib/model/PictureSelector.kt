package com.luck.picture.lib.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.MediaType
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

        fun obtainSelectResults(intent: Intent?): ArrayList<LocalMedia> {
            intent?.let {
                return intent.getParcelableArrayListExtra<LocalMedia>(SelectorConstant.KEY_EXTRA_RESULT) as ArrayList<LocalMedia>
            }
            return arrayListOf()
        }
    }

    /**
     * open gallery source
     * use [MediaType]
     */
    fun openGallery(mediaType: MediaType): SelectionMainModel {
        return SelectionMainModel(this, mediaType)
    }

    /**
     * From system album Select the type of images you want，all or images or video or audio
     * use [MediaType]
     */
    fun openSystemGallery(mediaType: MediaType): SelectionSystemModel {
        return SelectionSystemModel(this, mediaType)
    }

    /**
     * only use camera，images or video or audio
     * use [MediaType]
     */
    fun openCamera(mediaType: MediaType): SelectionCameraModel {
        return SelectionCameraModel(this, mediaType)
    }

    /**
     * query the type of images you want，all or images or video or audio
     * use [MediaType]
     */
    fun dataSource(mediaType: MediaType): SelectionQueryModel {
        return SelectionQueryModel(this, mediaType)
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