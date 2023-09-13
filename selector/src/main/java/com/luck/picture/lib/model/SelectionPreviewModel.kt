package com.luck.picture.lib.model

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.R
import com.luck.picture.lib.SelectorExternalPreviewFragment
import com.luck.picture.lib.SelectorPreviewFragment
import com.luck.picture.lib.SelectorTransparentActivity
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.OnCustomAnimationListener
import com.luck.picture.lib.interfaces.OnCustomLoadingListener
import com.luck.picture.lib.interfaces.OnExternalPreviewListener
import com.luck.picture.lib.interfaces.OnFragmentLifecycleListener
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.magical.RecycleItemViewParams
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.style.StatusBarStyle
import com.luck.picture.lib.style.WindowAnimStyle
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.DoubleUtils
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionPreviewModel
 */
class SelectionPreviewModel constructor(private var selector: PictureSelector) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        SelectorProviders.getInstance().addConfigQueue(config)
    }


    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun <V> registry(@NonNull targetClass: Class<V>): SelectionPreviewModel {
        this.config.registry.register(targetClass)
        return this
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     * @param key Use [LayoutSource]
     * @param resource resource Denotes that an integer parameter, field or method
     */
    fun <V> registry(
        @NonNull targetClass: Class<V>, key: LayoutSource,
        @LayoutRes resource: Int
    ): SelectionPreviewModel {
        this.config.registry.register(targetClass)
        this.inflateCustomLayout(key, resource)
        return this
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun registry(@NonNull registry: Registry): SelectionPreviewModel {
        this.config.registry = registry
        return this
    }

    /**
     * Customizing PictureSelector
     *  User unbind fragmentClass
     */
    fun <Model> unregister(@NonNull fragmentClass: Class<Model>): SelectionPreviewModel {
        this.config.registry.unregister(fragmentClass)
        return this
    }

    /**
     * User implements custom layout, but ID must be consistent and View cannot be deleted
     * @param key Use [LayoutSource]
     * @param resource Denotes that an integer parameter, field or method
     * return value is expected to be a layout resource reference ([R.layout.ps_fragment_selector]).
     */
    fun inflateCustomLayout(key: LayoutSource, @LayoutRes resource: Int): SelectionPreviewModel {
        this.config.layoutSource[key] = resource
        return this
    }

    /**
     * PictureSelector statusBar theme style settings
     * @param statusBar [StatusBarStyle]
     */
    fun setStatusBarStyle(statusBar: StatusBarStyle): SelectionPreviewModel {
        this.config.statusBarStyle = statusBar
        return this
    }

    /**
     * PictureSelector window anim style settings
     * @param windowAnimStyle [WindowAnimStyle]
     */
    fun setWindowAnimStyle(windowAnimStyle: WindowAnimStyle): SelectionPreviewModel {
        this.config.windowAnimStyle = windowAnimStyle
        return this
    }

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     */
    fun setImageEngine(engine: ImageEngine?): SelectionPreviewModel {
        this.config.imageEngine = engine
        return this
    }

    /**
     * Whether to play video and audio automatically when previewing
     *
     * @param isAutoPlay
     */
    fun isAutoPlay(isAutoPlay: Boolean): SelectionPreviewModel {
        this.config.isAutoPlay = isAutoPlay
        return this
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     */
    fun isLoopAutoVideoPlay(isLoopAutoPlay: Boolean): SelectionPreviewModel {
        this.config.isLoopAutoPlay = isLoopAutoPlay
        return this
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     */
    fun isVideoPauseResumePlay(isPauseResumePlay: Boolean): SelectionPreviewModel {
        this.config.isPauseResumePlay = isPauseResumePlay
        return this
    }

    /**
     * Set App Language
     *
     * @param language use [Language]
     */
    fun setLanguage(language: Language): SelectionPreviewModel {
        this.config.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param language default language [Language]
     */
    fun setDefaultLanguage(language: Language): SelectionPreviewModel {
        this.config.defaultLanguage = language
        return this
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     */
    fun isPreviewFullScreenMode(isFullScreenModel: Boolean): SelectionPreviewModel {
        this.config.isPreviewFullScreenMode = isFullScreenModel
        return this
    }


    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewEffect
     * @param isFullScreen
     * @param listView   [androidx.recyclerview.widget.RecyclerView] or [ListView]
     */
    fun isPreviewZoomEffect(
        isPreviewEffect: Boolean,
        isFullScreen: Boolean,
        listView: ViewGroup
    ): SelectionPreviewModel {
        this.config.isPreviewFullScreenMode = isFullScreen
        this.config.isPreviewZoomEffect = isPreviewEffect
        if (isPreviewEffect) {
            RecycleItemViewParams.build(
                listView,
                if (isFullScreen) 0 else DensityUtil.getStatusBarHeight(listView.context)
            )
        }
        return this
    }


    /**
     * Preview Delete
     */
    fun isDisplayDelete(isDisplayDelete: Boolean): SelectionPreviewModel {
        this.config.previewWrap.isDisplayDelete = isDisplayDelete
        return this
    }

    /**
     * Support long press download
     */
    fun isLongPressDownload(isDownload: Boolean): SelectionPreviewModel {
        this.config.previewWrap.isDownload = isDownload
        return this
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     */
    fun isNewKeyBackMode(isNewKeyBackMode: Boolean): SelectionPreviewModel {
        this.config.isNewKeyBackMode = isNewKeyBackMode
        return this
    }

    /**
     * View lifecycle listener
     */
    fun setOnFragmentLifecycleListener(l: OnFragmentLifecycleListener?): SelectionPreviewModel {
        this.config.mListenerInfo.onFragmentLifecycleListener = l
        return this
    }

    /**
     * PictureSelector custom animation
     */
    fun setOnCustomAnimationListener(l: OnCustomAnimationListener?): SelectionPreviewModel {
        this.config.mListenerInfo.onCustomAnimationListener = l
        return this
    }


    /**
     * External preview listening
     */
    fun setOnExternalPreviewListener(l: OnExternalPreviewListener?): SelectionPreviewModel {
        this.config.mListenerInfo.onExternalPreviewListener = l
        return this
    }

    /**
     * Custom loading
     */
    fun setOnCustomLoadingListener(loading: OnCustomLoadingListener?): SelectionPreviewModel {
        this.config.mListenerInfo.onCustomLoadingListener = loading
        return this
    }

    fun forPreviewUrl(position: Int, strings: MutableList<String>, attachActivity: Boolean) {
        val source: MutableList<LocalMedia> = mutableListOf()
        strings.forEach { path ->
            val media = LocalMedia()
            media.path = path
            when {
                MediaUtils.isHasHttp(path) -> {
                    media.mimeType = MediaUtils.getUrlMimeType(path)
                }
                MediaUtils.isContent(path) -> {
                    val absolutePath = MediaUtils.getPath(selector.getActivity()!!, Uri.parse(path))
                    media.mimeType = MediaUtils.getMimeType(absolutePath)
                    media.absolutePath = absolutePath
                }
                else -> {
                    media.mimeType = MediaUtils.getMimeType(path)
                    media.absolutePath = path
                }
            }
            source.add(media)
        }
        return forPreview(position, source, attachActivity)
    }

    fun forPreview(position: Int, source: MutableList<LocalMedia>) {
        return forPreview(position, source, false)
    }

    /**
     * Preview
     * Use default Activity hosting to preview Fragments
     * @param position preview start position
     * @param source preview data source
     * @param attachActivity Preview Fragment Attach to Activity
     */
    fun forPreview(position: Int, source: MutableList<LocalMedia>, attachActivity: Boolean) {
        if (DoubleUtils.isFastDoubleClick()) {
            return
        }
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
        if (source.isEmpty()) {
            throw NullPointerException("Preview source not null")
        }
        if (position >= source.size) {
            throw NullPointerException("#position# cannot be greater than #source.size#")
        }
        if (config.imageEngine == null && config.mediaType != MediaType.AUDIO) {
            throw NullPointerException("Please set the API # .setImageEngine(${ImageEngine::class.simpleName});")
        }
        if (MediaUtils.hasMimeTypeOfAudio(source[position].mimeType)) {
            config.isPreviewZoomEffect = false
        }
        config.previewWrap.source = source.toMutableList()
        config.previewWrap.position = position
        config.previewWrap.isExternalPreview = true
        config.previewWrap.totalCount = source.size
        if (attachActivity) {
            val intent = Intent(activity, SelectorTransparentActivity::class.java)
            val fragment = selector.getFragment()
            if (fragment != null) {
                fragment.startActivity(intent)
            } else {
                activity.startActivity(intent)
            }
            if (config.isPreviewZoomEffect) {
                activity.overridePendingTransition(R.anim.ps_anim_fade_in, R.anim.ps_anim_fade_in)
            } else {
                activity.overridePendingTransition(
                    config.windowAnimStyle.getEnterAnimRes(),
                    R.anim.ps_anim_fade_in
                )
            }
        } else {
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            val registry = this.config.registry
            val factory = ClassFactory.NewInstance()
            var instance =
                factory.create(registry.get(SelectorPreviewFragment::class.java))
            if (instance::class.java.isAssignableFrom(SelectorPreviewFragment::class.java)) {
                // No custom registry, use default external preview component
                instance =
                    factory.create(registry.get(SelectorExternalPreviewFragment::class.java))
            }
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