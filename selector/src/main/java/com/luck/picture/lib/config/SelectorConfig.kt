package com.luck.picture.lib.config

import android.content.pm.ActivityInfo
import android.graphics.Color
import com.luck.picture.lib.R
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.interfaces.ListenerInfo
import com.luck.picture.lib.interfaces.MagicalInterpolator
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.style.StatusBarStyle
import com.luck.picture.lib.style.WindowAnimStyle

/**
 * @author：luck
 * @date：2023-4-17 22:30
 * @describe：SelectorConfig
 */
class SelectorConfig {
    var statusBarStyle = StatusBarStyle()
    var windowAnimStyle = WindowAnimStyle()
    var mediaType = MediaType.ALL
    var allCameraMediaType = MediaType.ALL
    var selectionMode = SelectionMode.MULTIPLE
    var imageSpanCount = SelectorConstant.DEFAULT_GRID_ITEM_COUNT
    var totalCount = SelectorConstant.DEFAULT_MAX_SELECT_NUM
    var minSelectNum = 0
    var maxVideoSelectNum = 0
    var minVideoSelectNum = 0
    var filterMaxFileSize = 0L
    var filterMinFileSize = 0L
    var filterVideoMaxSecond = 0L
    var filterVideoMinSecond = 0L
    var activityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    var isGif = false
    var isBmp = false
    var isHeic = false
    var isWebp = false
    var isAutoPlay = false
    var isOnlyCamera = false
    var systemGallery = false
    var isLoopAutoPlay = false
    var isAsTotalCount = false
    var isQuickCapture = false
    var isDisplayCamera = true
    var isNewKeyBackMode = true
    var isActivityResult = false
    var isOnlySandboxDir = false
    var isOriginalControl = false
    var isPauseResumePlay = false
    var isEmptyResultBack = false
    var isDisplayTimeAxis = true
    var isFastSlidingSelect = true
    var isAllWithImageVideo = false
    var isForegroundService = false
    var isPreviewZoomEffect = false
    var isEnablePreviewImage = true
    var isEnablePreviewVideo = true
    var isEnablePreviewAudio = true
    var isMaxSelectEnabledMask = false
    var isPreviewFullScreenMode = false
    var previewWrap = PreviewDataWrap()
    var pageSize = SelectorConstant.DEFAULT_MAX_PAGE_SIZE
    var sortOrder: String? = null
    var skipCropFormat = hashSetOf<String>()
    var onlyQueryImageFormat = hashSetOf<String>()
    var onlyQueryVideoFormat = hashSetOf<String>()
    var onlyQueryAudioFormat = hashSetOf<String>()
    var layoutSource = hashMapOf<LayoutSource, Int>()
    var selectedSource = mutableListOf<LocalMedia>()
    var language = Language.SYSTEM_LANGUAGE
    var defaultLanguage = Language.SYSTEM_LANGUAGE
    var defaultAlbumName: String? = null
    var sandboxDir: String? = null
    var imageOutputDir: String? = null
    var videoOutputDir: String? = null
    var audioOutputDir: String? = null
    var registry = Registry()
    var cropEngine: CropEngine? = null
    var imageEngine: ImageEngine? = null
    var dataLoader: MediaLoader? = null
    var mediaConverterEngine: MediaConverterEngine? = null
    var magicalInterpolator: MagicalInterpolator? = null
    var mListenerInfo = ListenerInfo()

    fun getSelectCount(): Int {
        return if (isAsTotalCount) totalCount else totalCount + maxVideoSelectNum
    }

    init {
        initDefault()
    }

    private fun initDefault() {
        this.registry.clear()
        this.statusBarStyle.of(false, Color.parseColor("#393a3e"), Color.parseColor("#393a3e"))
        this.windowAnimStyle.of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
        this.mediaType = MediaType.ALL
        this.allCameraMediaType = MediaType.ALL
        this.selectionMode = SelectionMode.MULTIPLE
        this.imageSpanCount = SelectorConstant.DEFAULT_GRID_ITEM_COUNT
        this.totalCount = SelectorConstant.DEFAULT_MAX_SELECT_NUM
        this.maxVideoSelectNum = 0
        this.minVideoSelectNum = 0
        this.minSelectNum = 0
        this.activityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        this.isGif = false
        this.isWebp = false
        this.isBmp = false
        this.isHeic = false
        this.isOnlyCamera = false
        this.isEmptyResultBack = false
        this.isMaxSelectEnabledMask = false
        this.isDisplayTimeAxis = true
        this.isFastSlidingSelect = true
        this.isQuickCapture = false
        this.isDisplayCamera = true
        this.isNewKeyBackMode = true;
        this.isLoopAutoPlay = false
        this.systemGallery = false
        this.isAutoPlay = false
        this.isPauseResumePlay = false
        this.isAsTotalCount = false
        this.isAllWithImageVideo = false
        this.isEnablePreviewImage = true
        this.isEnablePreviewVideo = true
        this.isEnablePreviewAudio = true
        this.isOnlySandboxDir = false
        this.isOriginalControl = false
        this.isPreviewZoomEffect = false
        this.isPreviewFullScreenMode = false
        this.isActivityResult = false
        this.isForegroundService = false
        this.pageSize = SelectorConstant.DEFAULT_MAX_PAGE_SIZE
        this.filterMaxFileSize = 0L
        this.filterMinFileSize = 0L
        this.filterVideoMaxSecond = 0L
        this.filterVideoMinSecond = 0L
        this.sortOrder = null
        this.layoutSource.clear()
        this.onlyQueryImageFormat.clear()
        this.onlyQueryVideoFormat.clear()
        this.onlyQueryAudioFormat.clear()
        this.selectedSource.clear()
        this.skipCropFormat.clear()
        this.language = Language.SYSTEM_LANGUAGE
        this.defaultLanguage = Language.SYSTEM_LANGUAGE
        this.defaultAlbumName = null
        this.previewWrap.reset()
        this.sandboxDir = null
        this.imageOutputDir = null
        this.videoOutputDir = null
        this.audioOutputDir = null
    }

    fun destroy() {
        this.dataLoader = null
        this.cropEngine = null
        this.imageEngine = null
        this.magicalInterpolator = null
        this.mediaConverterEngine = null
        this.registry.clear()
        this.previewWrap.reset()
        this.mListenerInfo.destroy()
    }
}