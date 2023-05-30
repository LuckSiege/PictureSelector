package com.luck.picture.lib.model

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.R
import com.luck.picture.lib.Registry
import com.luck.picture.lib.SelectorSystemFragment
import com.luck.picture.lib.SelectorTransparentActivity
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.provider.SelectorProviders

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionSystemModel
 */
class SelectionSystemModel constructor(
    private var selector: PictureSelector,
    mode: SelectorMode
) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        this.config.selectorMode = mode
        this.config.isPreviewZoomEffect = false
        this.config.isPreviewFullScreenMode = false
        SelectorProviders.getInstance().addSelectorConfigQueue(config)
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun <V> registry(@NonNull fragmentClass: Class<V>): SelectionSystemModel {
        this.config.registry.register(fragmentClass)
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
     *  User unbind fragmentClass
     */
    fun <Model> unregister(@NonNull fragmentClass: Class<Model>): SelectionSystemModel {
        this.config.registry.unregister(fragmentClass)
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
     * Enable original image
     */
    fun isOriginalControl(isOriginalControl: Boolean): SelectionSystemModel {
        this.config.isOriginalControl = isOriginalControl
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
    fun setOnPermissionsInterceptListener(l: OnPermissionsInterceptListener?): SelectionSystemModel {
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
     * Select Filter
     */
    fun setOnSelectFilterListener(l: OnSelectFilterListener?): SelectionSystemModel {
        this.config.mListenerInfo.onSelectFilterListener = l
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
     * Starting the system graph library using the [OnResultCallbackListener] method
     */
    fun forSystemResult(call: OnResultCallbackListener?) {
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
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
        config.systemGallery = true
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

    /**
     * Starting the system graph library using the [OnResultCallbackListener] method
     */
    fun forSystemResultActivity(call: OnResultCallbackListener) {
        forSystemResultActivity(call, SelectorConstant.UNKNOWN, null)
    }

    /**
     * Starting the System Library Using ActivityResult Method
     */
    fun forSystemResultActivity(requestCode: Int) {
        forSystemResultActivity(null, requestCode, null)
    }

    /**
     * Launch the system graph library using ActivityResultLauncher method
     */
    fun forSystemResultActivity(launcher: ActivityResultLauncher<Intent>) {
        forSystemResultActivity(null, SelectorConstant.UNKNOWN, launcher)
    }

    private fun forSystemResultActivity(
        call: OnResultCallbackListener?,
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?
    ) {
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
        config.systemGallery = true
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
        activity.overridePendingTransition(
            config.selectorStyle.getWindowAnimation().getEnterAnim(),
            R.anim.ps_anim_fade_in
        )
    }
}