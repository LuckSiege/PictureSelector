package com.luck.picture.lib.model

import android.content.Intent
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.*
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.constant.FileSizeUnitConstant
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.magical.MagicalView
import com.luck.picture.lib.magical.RecycleItemViewParams
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.style.StatusBarStyle
import com.luck.picture.lib.style.WindowAnimStyle
import com.luck.picture.lib.utils.DensityUtil.getStatusBarHeight
import com.luck.picture.lib.utils.DoubleUtils
import org.jetbrains.annotations.NotNull

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionMainModel
 */
class SelectionMainModel constructor(private var selector: PictureSelector, mediaType: MediaType) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        config.mediaType = mediaType
        SelectorProviders.getInstance().addConfigQueue(config)
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun <V> registry(@NonNull targetClass: Class<V>): SelectionMainModel {
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
    ): SelectionMainModel {
        this.config.registry.register(targetClass)
        this.inflateCustomLayout(key, resource)
        return this
    }

    /**
     * Customizing PictureSelector
     * Users can implement custom PictureSelectors, such as photo albums,
     * previewing, taking photos, recording, and other related functions
     */
    fun registry(@NonNull registry: Registry): SelectionMainModel {
        this.config.registry = registry
        return this
    }

    /**
     * Customizing PictureSelector
     *  User unbind fragmentClass
     */
    fun <Model> unregister(@NonNull targetClass: Class<Model>): SelectionMainModel {
        this.config.registry.unregister(targetClass)
        return this
    }

    /**
     * User implements custom layout, but ID must be consistent and View cannot be deleted
     * @param key Use [LayoutSource]
     * @param resource Denotes that an integer parameter, field or method
     * return value is expected to be a layout resource reference ([R.layout.ps_fragment_selector]).
     */
    fun inflateCustomLayout(key: LayoutSource, @LayoutRes resource: Int): SelectionMainModel {
        this.config.layoutSource[key] = resource
        return this
    }

    /**
     * PictureSelector statusBar theme style settings
     * @param statusBar [StatusBarStyle]
     */
    fun setStatusBarStyle(statusBar: StatusBarStyle): SelectionMainModel {
        this.config.statusBarStyle = statusBar
        return this
    }

    /**
     * PictureSelector window anim style settings
     * @param windowAnimStyle [WindowAnimStyle]
     */
    fun setWindowAnimStyle(windowAnimStyle: WindowAnimStyle): SelectionMainModel {
        this.config.windowAnimStyle = windowAnimStyle
        return this
    }

    /**
     * Set up a custom media data loader
     */
    fun setCustomMediaLoader(loader: MediaLoader?): SelectionMainModel {
        this.config.dataLoader = loader
        return this
    }

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     */
    fun setImageEngine(engine: ImageEngine?): SelectionMainModel {
        this.config.imageEngine = engine
        return this
    }

    /**
     * Cropping
     */
    fun setCropEngine(engine: CropEngine?): SelectionMainModel {
        this.config.cropEngine = engine
        return this
    }

    /**
     * Media Resource Converter Engine
     */
    fun setMediaConverterEngine(engine: MediaConverterEngine?): SelectionMainModel {
        this.config.mediaConverterEngine = engine
        return this
    }

    /**
     * Edit Media Resource
     */
    fun setOnEditorMediaListener(l: OnEditorMediaListener?): SelectionMainModel {
        this.config.mListenerInfo.onEditorMediaListener = l
        return this
    }

    /**
     * Media List Animation wrap
     */
    fun setOnAnimationAdapterWrapListener(l: OnAnimationAdapterWrapListener?): SelectionMainModel {
        this.config.mListenerInfo.onAnimationAdapterWrapListener = l
        return this
    }

    /**
     * Custom recording callback listening
     */
    fun setOnRecordAudioListener(l: OnRecordAudioListener?): SelectionMainModel {
        this.config.mListenerInfo.onRecordAudioListener = l
        return this
    }

    /**
     * Custom camera callback listening
     */
    fun setOnCustomCameraListener(l: OnCustomCameraListener?): SelectionMainModel {
        this.config.mListenerInfo.onCustomCameraListener = l
        return this
    }

    /**
     * Use custom file name
     */
    fun setOnReplaceFileNameListener(l: OnReplaceFileNameListener?): SelectionMainModel {
        this.config.mListenerInfo.onReplaceFileNameListener = l
        return this
    }

    /**
     * Select Filter
     */
    fun setOnSelectFilterListener(l: OnSelectFilterListener?): SelectionMainModel {
        this.config.mListenerInfo.onSelectFilterListener = l
        return this
    }

    /**
     * Confirm Filter
     */
    fun setOnConfirmListener(l: OnConfirmListener?): SelectionMainModel {
        this.config.mListenerInfo.onConfirmListener = l
        return this
    }

    /**
     * Filter out multimedia data that does not comply with rules
     */
    fun setOnQueryFilterListener(l: OnQueryFilterListener?): SelectionMainModel {
        this.config.mListenerInfo.onQueryFilterListener = l
        return this
    }

    /**
     * Number sorting template
     */
    fun isNewNumTemplate(isNewNumTemplate: Boolean): SelectionMainModel {
        if (isNewNumTemplate) {
            this.registry(SelectorNumberMainFragment::class.java)
            this.registry(SelectorNumberPreviewFragment::class.java)
        } else {
            this.unregister(SelectorNumberMainFragment::class.java)
            this.unregister(SelectorNumberPreviewFragment::class.java)
        }
        return this
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     */
    fun isLoopAutoVideoPlay(isLoopAutoPlay: Boolean): SelectionMainModel {
        this.config.isLoopAutoPlay = isLoopAutoPlay
        return this
    }

    /**
     * Whether to play video and audio automatically when previewing
     *
     * @param isAutoPlay
     */
    fun isAutoPlay(isAutoPlay: Boolean): SelectionMainModel {
        this.config.isAutoPlay = isAutoPlay
        return this
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     */
    fun isVideoPauseResumePlay(isPauseResumePlay: Boolean): SelectionMainModel {
        this.config.isPauseResumePlay = isPauseResumePlay
        return this
    }

    /**
     * Displays the creation timeline of the resource
     *
     * @param isDisplayTimeAxis
     */
    fun isDisplayTimeAxis(isDisplayTimeAxis: Boolean): SelectionMainModel {
        this.config.isDisplayTimeAxis = isDisplayTimeAxis
        return this
    }

    /**
     * Empty results can also close the selector
     *
     * @param isEmptyResultBack
     */
    fun isEmptyResultBack(isEmptyResultBack: Boolean): SelectionMainModel {
        this.config.isEmptyResultBack = isEmptyResultBack
        return this
    }

    /**
     * Quick slide selection results
     *
     * @param isFastSlidingSelect
     */
    fun isFastSlidingSelect(isFastSlidingSelect: Boolean): SelectionMainModel {
        if (this.config.selectionMode == SelectionMode.ONLY_SINGLE) {
            this.config.isFastSlidingSelect = false
        } else {
            this.config.isFastSlidingSelect = isFastSlidingSelect
        }
        return this
    }

    /**
     * Custom image storage dir
     */
    fun setOutputImageDir(imageOutputDir: String): SelectionMainModel {
        this.config.imageOutputDir = imageOutputDir
        return this
    }

    /**
     * Custom video storage dir
     */
    fun setOutputVideoDir(videoOutputDir: String): SelectionMainModel {
        this.config.videoOutputDir = videoOutputDir
        return this
    }

    /**
     * Custom audio storage dir
     */
    fun setOutputAudioDir(audioOutputDir: String): SelectionMainModel {
        this.config.audioOutputDir = audioOutputDir
        return this
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param allCameraMediaType [MediaType.VIDEO]#[MediaType.IMAGE]
     * The default is [MediaType.ALL] mode
     */
    fun setAllOfCameraMode(allCameraMediaType: MediaType): SelectionMainModel {
        this.config.allCameraMediaType = allCameraMediaType
        return this
    }

    /**
     * Set App Language
     *
     * @param language use [Language]
     */
    fun setLanguage(language: Language): SelectionMainModel {
        this.config.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param language default language [Language]
     */
    fun setDefaultLanguage(language: Language): SelectionMainModel {
        this.config.defaultLanguage = language
        return this
    }

    /**
     * Set Selection Mode
     * Use [SelectionMode.SINGLE]#[SelectionMode.ONLY_SINGLE]#[SelectionMode.MULTIPLE]
     */
    fun setSelectionMode(selectionMode: SelectionMode): SelectionMainModel {
        this.config.selectionMode = selectionMode
        return this
    }

    /**
     * Maximum number of displays per line
     */
    fun setImageSpanCount(imageSpanCount: Int): SelectionMainModel {
        this.config.imageSpanCount = imageSpanCount
        return this
    }

    /**
     * Number of pages
     * @param pageSize return result count
     */
    fun setPageSize(
        @IntRange(from = 1, to = Long.MAX_VALUE) pageSize: Int
    ): SelectionMainModel {
        this.config.pageSize = pageSize
        return this
    }

    /**
     * Select the maximum number of files
     *
     * @param totalCount  max select count
     */
    fun setMaxSelectNum(totalCount: Int): SelectionMainModel {
        return setMaxSelectNum(totalCount, 0, false)
    }

    /**
     * Select the maximum number of files
     *
     * @param totalCount max total count
     * @param maxVideoNum max video count
     * @param isAsTotalCount Merge ImageNum, VideoNum, AudioNum into totalCount,
     * isAsTotalCount=true; # (imageCount + videoCount) = totalCount #
     * isAsTotalCount=false; # totalCount = (imageCount + videoCount) #
     */
    fun setMaxSelectNum(
        totalCount: Int,
        maxVideoNum: Int,
        isAsTotalCount: Boolean
    ): SelectionMainModel {
        this.config.totalCount = totalCount
        if (this.config.mediaType == MediaType.ALL) {
            this.config.maxVideoSelectNum = maxVideoNum
            this.config.isAsTotalCount = isAsTotalCount
            this.config.isAllWithImageVideo = maxVideoNum > 0
        }
        return this
    }

    /**
     * Select the minimum number of files
     *
     * @param minSelectNum  min image or audio selectNum
     */
    fun setMinSelectNum(minSelectNum: Int): SelectionMainModel {
        this.config.minSelectNum = minSelectNum
        return this
    }

    /**
     * Select the minimum number of files
     *
     * @param minVideoSelectNum PictureSelector min video selectNum
     */
    fun setMinVideoSelectNum(minVideoSelectNum: Int): SelectionMainModel {
        this.config.minVideoSelectNum = minVideoSelectNum
        return this
    }


    /**
     * Filter maximum files
     * @param sizeKb file size in kb
     */
    fun setFilterMaxFileSize(sizeKb: Long): SelectionMainModel {
        if (sizeKb >= FileSizeUnitConstant.MB) {
            this.config.filterMaxFileSize = sizeKb;
        } else {
            this.config.filterMaxFileSize = sizeKb * FileSizeUnitConstant.KB;
        }
        return this
    }

    /**
     * Filter minimum files
     * @param sizeKb file size in kb
     */
    fun setFilterMinFileSize(sizeKb: Long): SelectionMainModel {
        if (sizeKb >= FileSizeUnitConstant.MB) {
            this.config.filterMinFileSize = sizeKb;
        } else {
            this.config.filterMinFileSize = sizeKb * FileSizeUnitConstant.KB;
        }
        return this
    }

    /**
     * Filter the longest video
     * @param second unit seconds
     */
    fun setFilterVideoMaxSecond(second: Long): SelectionMainModel {
        this.config.filterVideoMaxSecond = second * 1000
        return this
    }

    /**
     * Filter the shortest video
     * @param second unit seconds
     */
    fun setFilterVideoMinSecond(second: Long): SelectionMainModel {
        this.config.filterVideoMinSecond = second * 1000
        return this
    }

    /**
     * sort order
     * @param sortOrder example use [MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
     *  MediaStore.MediaColumns.DATE_MODIFIED + " ASC";]
     */
    fun setQuerySortOrder(sortOrder: String?): SelectionMainModel {
        this.config.sortOrder = sortOrder
        return this
    }

    /**
     * Skip crop resource formatting
     *
     * @param format example [LocalMedia.mimeType] [image/jpeg]
     */
    fun setSkipCropFormat(vararg format: String): SelectionMainModel {
        this.config.skipCropFormat.addAll(format.toMutableList())
        return this
    }

    /**
     * Only query image format media resources
     * @param format Use [LocalMedia.mimeType]
     * for example [image/jpeg... more]
     */
    fun setOnlyQueryImageFormat(vararg format: String): SelectionMainModel {
        format.forEach continuing@{ mimeType ->
            if (TextUtils.isEmpty(mimeType)) {
                return@continuing
            }
            this.config.onlyQueryImageFormat.add(mimeType)
        }
        return this
    }

    /**
     * Only query video format media resources
     * @param format Use [LocalMedia.mimeType]
     * for example [video/mp4... more]
     */
    fun setOnlyQueryVideoFormat(vararg format: String): SelectionMainModel {
        format.forEach continuing@{ mimeType ->
            if (TextUtils.isEmpty(mimeType)) {
                return@continuing
            }
            this.config.onlyQueryVideoFormat.add(mimeType)
        }
        return this
    }

    /**
     * Only query audio format media resources
     * @param format Use [LocalMedia.mimeType]
     * for example [audio/amr... more]
     */
    fun setOnlyQueryAudioFormat(vararg format: String): SelectionMainModel {
        format.forEach continuing@{ mimeType ->
            if (TextUtils.isEmpty(mimeType)) {
                return@continuing
            }
            this.config.onlyQueryAudioFormat.add(mimeType)
        }
        return this
    }

    /**
     * Show camera options
     * @param isDisplayCamera return true show or return false hide
     */
    fun isDisplayCamera(isDisplayCamera: Boolean): SelectionMainModel {
        this.config.isDisplayCamera = isDisplayCamera
        return this
    }

    /**
     * Show white mask
     */
    fun isMaxSelectEnabledMask(isMaxSelectEnabledMask: Boolean): SelectionMainModel {
        this.config.isMaxSelectEnabledMask = isMaxSelectEnabledMask
        return this
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     */
    fun isCameraForegroundService(isForeground: Boolean): SelectionMainModel {
        this.config.isForegroundService = isForeground
        return this
    }

    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     */
    fun isQuickCapture(isQuickCapture: Boolean): SelectionMainModel {
        this.config.isQuickCapture = isQuickCapture
        return this
    }

    /**
     * Compatible with Fragment fallback scheme, default to true
     */
    fun isNewKeyBackMode(isNewKeyBackMode: Boolean): SelectionMainModel {
        this.config.isNewKeyBackMode = isNewKeyBackMode
        return this
    }

    /**
     * Display gif type resources
     *
     * filter gif format data
     */
    fun isGif(isGif: Boolean): SelectionMainModel {
        this.config.isGif = isGif
        return this
    }

    /**
     * Display gif type resources
     *
     * filter webp format data
     */
    fun isWebp(isWebp: Boolean): SelectionMainModel {
        this.config.isWebp = isWebp
        return this
    }

    /**
     * Display gif type resources
     *
     * filter bmp format data
     */
    fun isBmp(isBmp: Boolean): SelectionMainModel {
        this.config.isBmp = isBmp
        return this
    }

    /**
     * Display gif type resources
     *
     * filter heic format data
     */
    fun isHeic(isHeic: Boolean): SelectionMainModel {
        this.config.isHeic = isHeic
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
    fun setRequestedOrientation(activityOrientation: Int): SelectionMainModel {
        this.config.activityOrientation = activityOrientation
        return this
    }

    /**
     * Preview Full Screen Mode
     * @param isFullScreenModel
     */
    fun isPreviewFullScreenMode(isFullScreenModel: Boolean): SelectionMainModel {
        this.config.isPreviewFullScreenMode = isFullScreenModel
        return this
    }

    /**
     * Set Selected Data Source
     */
    fun setSelectedData(source: MutableList<LocalMedia>): SelectionMainModel {
        if (this.config.selectionMode == SelectionMode.ONLY_SINGLE) {
            this.config.selectedSource.clear()
        } else {
            this.config.selectedSource.addAll(source.toMutableList())
        }
        return this
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewEffect
     * @param isFullScreen
     */
    fun isPreviewZoomEffect(
        isPreviewEffect: Boolean,
        isFullScreen: Boolean
    ): SelectionMainModel {
        if (this.config.mediaType != MediaType.AUDIO) {
            this.config.isPreviewZoomEffect = isPreviewEffect
        }
        this.config.isPreviewFullScreenMode = isFullScreen
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
    ): SelectionMainModel {
        if (this.config.mediaType != MediaType.AUDIO) {
            this.config.isPreviewZoomEffect = isPreviewEffect
            if (isPreviewEffect) {
                RecycleItemViewParams.build(
                    listView,
                    if (isFullScreen) 0 else getStatusBarHeight(listView.context)
                )
            }
        }
        this.config.isPreviewFullScreenMode = isFullScreen
        return this
    }

    /**
     * Set default album name
     * @param defaultAlbumName
     */
    fun setDefaultAlbumName(defaultAlbumName: String): SelectionMainModel {
        this.config.defaultAlbumName = defaultAlbumName
        return this
    }

    /**
     * Query the specified application file directory
     * @param dir Root directory
     * @param isOnlySandboxDir Only query the [SelectorConfig.sandboxDir] directory
     */
    fun setQuerySandboxDir(@NotNull dir: String, isOnlySandboxDir: Boolean): SelectionMainModel {
        this.config.sandboxDir = dir
        this.config.isOnlySandboxDir = isOnlySandboxDir
        return this
    }

    /**
     * If you need to preview the audio
     *
     * @param isPreviewAudio
     */
    fun isPreviewAudio(isPreviewAudio: Boolean): SelectionMainModel {
        this.config.isEnablePreviewAudio = isPreviewAudio
        return this
    }

    /**
     * If you need to preview the image
     * @param isPreviewImage Do you want to preview the picture?
     */
    fun isPreviewImage(isPreviewImage: Boolean): SelectionMainModel {
        this.config.isEnablePreviewImage = isPreviewImage
        return this
    }

    /**
     * If you need to preview the video
     * @param isPreviewVideo Do you want to preview the video?
     */
    fun isPreviewVideo(isPreviewVideo: Boolean): SelectionMainModel {
        this.config.isEnablePreviewVideo = isPreviewVideo
        return this
    }

    /**
     * Enable original image options
     *
     * @param isOriginalControl Enable original image options
     */
    fun isOriginalControl(isOriginalControl: Boolean): SelectionMainModel {
        this.config.isOriginalControl = isOriginalControl
        return this
    }

    /**
     * View lifecycle listener
     */
    fun setOnFragmentLifecycleListener(l: OnFragmentLifecycleListener?): SelectionMainModel {
        this.config.mListenerInfo.onFragmentLifecycleListener = l
        return this
    }

    /**
     * PictureSelector custom animation
     */
    fun setOnCustomAnimationListener(l: OnCustomAnimationListener?): SelectionMainModel {
        this.config.mListenerInfo.onCustomAnimationListener = l
        return this
    }

    /**
     * Custom loading
     */
    fun setOnCustomLoadingListener(loading: OnCustomLoadingListener?): SelectionMainModel {
        this.config.mListenerInfo.onCustomLoadingListener = loading
        return this
    }

    /**
     * Custom permissions
     */
    fun setOnPermissionsApplyListener(l: OnPermissionApplyListener?): SelectionMainModel {
        this.config.mListenerInfo.onPermissionApplyListener = l
        return this
    }

    /**
     * Permission usage instructions
     */
    fun setOnPermissionDescriptionListener(l: OnPermissionDescriptionListener?): SelectionMainModel {
        this.config.mListenerInfo.onPermissionDescriptionListener = l
        return this
    }

    /**
     * Permission denied processing
     */
    fun setOnPermissionDeniedListener(l: OnPermissionDeniedListener?): SelectionMainModel {
        this.config.mListenerInfo.onPermissionDeniedListener = l
        return this
    }

    /**
     * [MagicalView] Animation Interpolator
     */
    fun setMagicalInterpolator(interpolator: MagicalInterpolator?): SelectionMainModel {
        this.config.magicalInterpolator = interpolator
        return this
    }

    fun forResult(requestCode: Int) {
        forResult(null, requestCode, null)
    }

    fun forResult(call: OnResultCallbackListener) {
        forResult(call, SelectorConstant.UNKNOWN, null)
    }

    fun forResult(launcher: ActivityResultLauncher<Intent>) {
        forResult(null, SelectorConstant.UNKNOWN, launcher)
    }

    private fun forResult(
        call: OnResultCallbackListener?,
        requestCode: Int,
        launcher: ActivityResultLauncher<Intent>?
    ) {
        if (DoubleUtils.isFastDoubleClick()) {
            return
        }
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); # Activity is empty")
        if (config.imageEngine == null && config.mediaType != MediaType.AUDIO) {
            throw NullPointerException("Please set the API # .setImageEngine(${ImageEngine::class.simpleName});")
        }
        val intent = Intent(activity, SelectorSupporterActivity::class.java)
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
            config.windowAnimStyle.getEnterAnimRes(),
            R.anim.ps_anim_fade_in
        )
    }

    /**
     * Attach the gallery to any specified view layer
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
            .create(this.config.registry.get(SelectorMainFragment::class.java))
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