package com.luck.picture.lib.model

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.SelectorCameraFragment
import com.luck.picture.lib.SelectorTransparentActivity
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.utils.DoubleUtils

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionCameraModel
 */
class SelectionCameraModel constructor(
    private var selector: PictureSelector,
    mediaType: MediaType
) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        this.config.mediaType = mediaType
        this.config.isOnlyCamera = true
        this.config.isDisplayTimeAxis = false
        this.config.isPreviewFullScreenMode = false
        this.config.isPreviewZoomEffect = false
        SelectorProviders.getInstance().addConfigQueue(config)
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun <V> registry(@NonNull targetClass: Class<V>): SelectionCameraModel {
        this.config.registry.register(targetClass)
        return this
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun registry(@NonNull registry: Registry): SelectionCameraModel {
        this.config.registry = registry
        return this
    }

    /**
     * Customizing PictureSelector
     *  User unbind fragmentClass
     */
    fun <Model> unregister(@NonNull targetClass: Class<Model>): SelectionCameraModel {
        this.config.registry.unregister(targetClass)
        return this
    }

    /**
     * Cropping
     */
    fun setCropEngine(engine: CropEngine?): SelectionCameraModel {
        this.config.cropEngine = engine
        return this
    }

    /**
     * Set App Language
     *
     * @param language use [Language]
     */
    fun setLanguage(language: Language): SelectionCameraModel {
        this.config.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param language default language [Language]
     */
    fun setDefaultLanguage(language: Language): SelectionCameraModel {
        this.config.defaultLanguage = language
        return this
    }


    /**
     * Media Resource Converter Engine
     */
    fun setMediaConverterEngine(engine: MediaConverterEngine?): SelectionCameraModel {
        this.config.mediaConverterEngine = engine
        return this
    }

    /**
     * Custom recording callback listening
     */
    fun setOnRecordAudioListener(l: OnRecordAudioListener?): SelectionCameraModel {
        this.config.mListenerInfo.onRecordAudioListener = l
        return this
    }

    /**
     * Custom camera callback listening
     */
    fun setOnCustomCameraListener(l: OnCustomCameraListener?): SelectionCameraModel {
        this.config.mListenerInfo.onCustomCameraListener = l
        return this
    }

    /**
     * Use custom file name
     */
    fun setOnReplaceFileNameListener(l: OnReplaceFileNameListener?): SelectionCameraModel {
        this.config.mListenerInfo.onReplaceFileNameListener = l
        return this
    }

    /**
     * Select Filter
     */
    fun setOnSelectFilterListener(filter: OnSelectFilterListener?): SelectionCameraModel {
        this.config.mListenerInfo.onSelectFilterListener = filter
        return this
    }

    /**
     * Custom image storage dir
     */
    fun setOutputImageDir(imageOutputDir: String): SelectionCameraModel {
        this.config.imageOutputDir = imageOutputDir
        return this
    }

    /**
     * Custom video storage dir
     */
    fun setOutputVideoDir(videoOutputDir: String): SelectionCameraModel {
        this.config.videoOutputDir = videoOutputDir
        return this
    }

    /**
     * Custom audio storage dir
     */
    fun setOutputAudioDir(audioOutputDir: String): SelectionCameraModel {
        this.config.audioOutputDir = audioOutputDir
        return this
    }

    /**
     * Skip crop resource formatting
     *
     * @param format example [LocalMedia.mimeType] [image/jpeg]
     */
    fun setSkipCropFormat(vararg format: String): SelectionCameraModel {
        this.config.skipCropFormat.addAll(format.toMutableList())
        return this
    }


    /**
     * Change the desired orientation of this activity.  If the activity
     * is currently in the foreground or otherwise impacting the screen
     * orientation, the screen will immediately be changed (possibly causing
     * the activity to be restarted). Otherwise, this will be used the next
     * time the activity is visible.
     *
     * @param activityOrientation An orientation constant as used in
     * [ActivityInfo.screenOrientation][android.content.pm.ActivityInfo.screenOrientation].
     */
    fun setRequestedOrientation(activityOrientation: Int): SelectionCameraModel {
        this.config.activityOrientation = activityOrientation
        return this
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param allCameraMediaType [MediaType.VIDEO]#[MediaType.IMAGE]
     * The default is [MediaType.ALL] mode
     */
    fun setAllOfCameraMode(allCameraMediaType: MediaType): SelectionCameraModel {
        this.config.allCameraMediaType = allCameraMediaType
        return this
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     */
    fun isCameraForegroundService(isForeground: Boolean): SelectionCameraModel {
        this.config.isForegroundService = isForeground
        return this
    }

    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     */
    fun isQuickCapture(isQuickCapture: Boolean): SelectionCameraModel {
        this.config.isQuickCapture = isQuickCapture
        return this
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     */
    fun isNewKeyBackMode(isNewKeyBackMode: Boolean): SelectionCameraModel {
        this.config.isNewKeyBackMode = isNewKeyBackMode
        return this
    }

    /**
     * Custom loading
     */
    fun setOnCustomLoadingListener(loading: OnCustomLoadingListener?): SelectionCameraModel {
        this.config.mListenerInfo.onCustomLoadingListener = loading
        return this
    }

    /**
     * View lifecycle listener
     */
    fun setOnFragmentLifecycleListener(l: OnFragmentLifecycleListener?): SelectionCameraModel {
        this.config.mListenerInfo.onFragmentLifecycleListener = l
        return this
    }

    /**
     * Custom permissions
     */
    fun setOnPermissionsApplyListener(l: OnPermissionApplyListener?): SelectionCameraModel {
        this.config.mListenerInfo.onPermissionApplyListener = l
        return this
    }

    /**
     * Permission usage instructions
     */
    fun setOnPermissionDescriptionListener(l: OnPermissionDescriptionListener?): SelectionCameraModel {
        this.config.mListenerInfo.onPermissionDescriptionListener = l
        return this
    }

    /**
     * Permission denied processing
     */
    fun setOnPermissionDeniedListener(l: OnPermissionDeniedListener?): SelectionCameraModel {
        this.config.mListenerInfo.onPermissionDeniedListener = l
        return this
    }

    fun forResult(call: OnResultCallbackListener) {
        forResult(SelectorConstant.UNKNOWN, null, call, false)
    }

    fun forResult(call: OnResultCallbackListener, attachActivity: Boolean) {
        forResult(SelectorConstant.UNKNOWN, null, call, attachActivity)
    }

    fun forResult(requestCode: Int) {
        forResult(requestCode, null, null, true)
    }

    fun forResult(launcher: ActivityResultLauncher<Intent>) {
        forResult(SelectorConstant.UNKNOWN, launcher, null, true)
    }

    private fun forResult(
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?,
        call: OnResultCallbackListener?,
        attachActivity: Boolean
    ) {
        if (DoubleUtils.isFastDoubleClick()) {
            return
        }
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
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
            if (call == null) {
                throw IllegalStateException(".forResult(); did not specify a corresponding result listening type callback")
            }
            config.mListenerInfo.onResultCallbackListener = call
            val instance = ClassFactory.NewInstance()
                .create(this.config.registry.get(SelectorCameraFragment::class.java))
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

    /**
     * Attach the camera to any specified view layer
     * @param containerViewId Optional identifier of the container this fragment is to be placed in.
     * If 0, it will not be placed in a container.fragment – The fragment to be added.
     * This fragment must not already be added to the activity.
     */
    fun buildLaunch(@IdRes containerViewId: Int, call: OnResultCallbackListener?) {
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
        val instance = ClassFactory.NewInstance()
            .create(this.config.registry.get(SelectorCameraFragment::class.java))
        val fragment = fragmentManager.findFragmentByTag(instance.getFragmentTag())
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        FragmentInjectManager.injectSystemRoomFragment(
            activity as FragmentActivity,
            containerViewId,
            instance.getFragmentTag(),
            instance
        )
    }
}