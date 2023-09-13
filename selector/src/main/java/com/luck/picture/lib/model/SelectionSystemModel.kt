package com.luck.picture.lib.model

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.SelectorSystemFragment
import com.luck.picture.lib.SelectorTransparentActivity
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.utils.DoubleUtils

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionSystemModel
 */
class SelectionSystemModel constructor(
    private var selector: PictureSelector,
    mediaType: MediaType
) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        this.config.mediaType = mediaType
        this.config.isPreviewZoomEffect = false
        this.config.isPreviewFullScreenMode = false
        SelectorProviders.getInstance().addConfigQueue(config)
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun <V> registry(@NonNull targetClass: Class<V>): SelectionSystemModel {
        this.config.registry.register(targetClass)
        return this
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun registry(@NonNull registry: Registry): SelectionSystemModel {
        this.config.registry = registry
        return this
    }

    /**
     * Customizing PictureSelector
     *  User unbind targetClass
     */
    fun <Model> unregister(@NonNull targetClass: Class<Model>): SelectionSystemModel {
        this.config.registry.unregister(targetClass)
        return this
    }

    /**
     * Set Selection Mode
     * Use [SelectionMode.SINGLE]#[SelectionMode.ONLY_SINGLE]#[SelectionMode.MULTIPLE]
     */
    fun setSelectionMode(selectionMode: SelectionMode): SelectionSystemModel {
        this.config.selectionMode = selectionMode
        return this
    }

    /**
     * Skip crop resource formatting
     */
    fun setSkipCropFormat(vararg format: String): SelectionSystemModel {
        this.config.skipCropFormat.addAll(format.toMutableList())
        return this
    }

    /**
     * Custom permissions
     */
    fun setOnPermissionsApplyListener(l: OnPermissionApplyListener?): SelectionSystemModel {
        this.config.mListenerInfo.onPermissionApplyListener = l
        return this
    }

    /**
     * Permission usage instructions
     */
    fun setOnPermissionDescriptionListener(l: OnPermissionDescriptionListener?): SelectionSystemModel {
        this.config.mListenerInfo.onPermissionDescriptionListener = l
        return this
    }

    /**
     * Permission denied processing
     */
    fun setOnPermissionDeniedListener(l: OnPermissionDeniedListener?): SelectionSystemModel {
        this.config.mListenerInfo.onPermissionDeniedListener = l
        return this
    }

    /**
     * Custom loading
     */
    fun setOnCustomLoadingListener(loading: OnCustomLoadingListener?): SelectionSystemModel {
        this.config.mListenerInfo.onCustomLoadingListener = loading
        return this
    }


    /**
     * Media Resource Converter Engine
     */
    fun setMediaConverterEngine(engine: MediaConverterEngine?): SelectionSystemModel {
        this.config.mediaConverterEngine = engine
        return this
    }

    /**
     * Cropping
     */
    fun setCropEngine(engine: CropEngine?): SelectionSystemModel {
        this.config.cropEngine = engine
        return this
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     */
    fun isNewKeyBackMode(isNewKeyBackMode: Boolean): SelectionSystemModel {
        this.config.isNewKeyBackMode = isNewKeyBackMode
        return this
    }

    /**
     * Starting the system graph library using the [OnResultCallbackListener] method
     */
    fun forResult(call: OnResultCallbackListener?) {
        forResult(call, SelectorConstant.UNKNOWN, null, false)
    }

    /**
     * Starting the system graph library using the [OnResultCallbackListener] method
     * @param attachActivity [SelectorSystemFragment] attach to Activity
     */
    fun forResult(call: OnResultCallbackListener?, attachActivity: Boolean) {
        forResult(call, SelectorConstant.UNKNOWN, null, attachActivity)
    }

    /**
     * Starting the System Library Using ActivityResult Method
     */
    fun forResult(requestCode: Int) {
        forResult(null, requestCode, null, true)
    }

    /**
     * Launch the system graph library using ActivityResultLauncher method
     */
    fun forResult(launcher: ActivityResultLauncher<Intent>) {
        forResult(null, SelectorConstant.UNKNOWN, launcher, true)
    }

    private fun forResult(
        call: OnResultCallbackListener?,
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?,
        attachActivity: Boolean
    ) {
        if (DoubleUtils.isFastDoubleClick()) {
            return
        }
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
        config.systemGallery = true
        if (attachActivity) {
            val intent = Intent(activity, SelectorTransparentActivity::class.java)
            if (call != null) {
                config.mListenerInfo.onResultCallbackListener = call
                activity.startActivity(intent)
            } else if (launcher != null) {
                config.isActivityResult = true
                launcher.launch(intent)
            } else if (requestCode != SelectorConstant.UNKNOWN) {
                config.isActivityResult = true
                val fragment = selector.getFragment()
                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode)
                } else {
                    activity.startActivityForResult(intent, requestCode)
                }
            } else {
                throw IllegalStateException(".forResult(); did not specify a corresponding result listening type callback")
            }
        } else {
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            if (call != null) {
                config.mListenerInfo.onResultCallbackListener = call
            } else {
                throw IllegalStateException(".forResult(); did not specify a corresponding result listening type callback")
            }
            val instance = ClassFactory.NewInstance()
                .create(this.config.registry.get(SelectorSystemFragment::class.java))
            val fragment = fragmentManager.findFragmentByTag(instance.getFragmentTag())
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
            FragmentInjectManager.injectSystemRoomFragment(
                activity as FragmentActivity,
                instance.getFragmentTag(),
                instance
            )
        }
    }
}