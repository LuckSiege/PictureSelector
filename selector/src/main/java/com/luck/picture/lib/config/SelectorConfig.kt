package com.luck.picture.lib.config

import android.content.pm.ActivityInfo
import com.luck.picture.lib.registry.Registry
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.engine.MediaConverterEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.interfaces.ListenerInfo
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.style.SelectorStyle

/**
 * @author：luck
 * @date：2023-4-17 22:30
 * @describe：SelectorConfig
 */
class SelectorConfig {
    var selectorStyle = SelectorStyle()
    var selectorMode = SelectorMode.ALL
    var allCameraMode = SelectorMode.ALL
    var selectionMode = SelectionMode.MULTIPLE
    var imageSpanCount = SelectorConstant.DEFAULT_GRID_ITEM_COUNT
    var totalCount = SelectorConstant.DEFAULT_MAX_SELECT_NUM
    var maxVideoSelectNum = 0
    var minVideoSelectNum = 0
    var minSelectNum = 0
    var activityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    var isAsTotalCount = false
    var isAllWithImageVideo = false
    var isGif = false
    var isWebp = false
    var isBmp = false
    var isMaxSelectEnabledMask = false
    var isEmptyResultBack = false
    var isOnlyCamera = false
    var isDisplayTimeAxis = true
    var isFastSlidingSelect = true
    var isDisplayCamera = true
    var isQuickCapture = false
    var isPauseResumePlay = false
    var isAutoPlay = false
    var isLoopAutoPlay = false
    var systemGallery = false
    var isEnablePreviewImage = true
    var isEnablePreviewVideo = true
    var isEnablePreviewAudio = true
    var isOnlySandboxDir = false
    var isOriginalControl = false
    var isPreviewZoomEffect = false
    var isPreviewFullScreenMode = false
    var isActivityResult = false
    var isForegroundService = false
    var filterMaxFileSize = 0L
    var filterMinFileSize = 0L
    var filterVideoMaxSecond = 0L
    var filterVideoMinSecond = 0L
    var previewWrap: PreviewDataWrap = PreviewDataWrap()
    var pageSize = SelectorConstant.DEFAULT_MAX_PAGE_SIZE
    var sortOrder: String? = null
    var skipCropFormat = hashSetOf<String>()
    var onlyQueryImageFormat = hashSetOf<String>()
    var onlyQueryVideoFormat = hashSetOf<String>()
    var onlyQueryAudioFormat = hashSetOf<String>()
    var layoutSource = hashMapOf<LayoutSource, Int>()
    var selectedSource = mutableListOf<LocalMedia>()
    var language: Language = Language.SYSTEM_LANGUAGE
    var defaultLanguage: Language = Language.SYSTEM_LANGUAGE
    var defaultAlbumName: String? = null
    var sandboxDir: String? = null
    var imageOutputDir: String? = null
    var videoOutputDir: String? = null
    var audioOutputDir: String? = null
    var registry: Registry = Registry()
    var cropEngine: CropEngine? = null
    var imageEngine: ImageEngine? = null
    var dataLoader: MediaLoader? = null
    var mediaConverterEngine: MediaConverterEngine? = null
    var mListenerInfo = ListenerInfo()

    fun getSelectCount(): Int {
        return if (isAsTotalCount) totalCount else totalCount + maxVideoSelectNum
    }

    init {
        initDefault()
    }

    private fun initDefault() {
        this.registry.clear()
        this.selectorStyle.defaultStyle()
        this.selectorMode = SelectorMode.ALL
        this.allCameraMode = SelectorMode.ALL
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
        this.isOnlyCamera = false
        this.isEmptyResultBack = false
        this.isMaxSelectEnabledMask = false
        this.isDisplayTimeAxis = true
        this.isFastSlidingSelect = true
        this.isQuickCapture = false
        this.isDisplayCamera = true
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
        this.mediaConverterEngine = null
        this.registry.clear()
        this.previewWrap.reset()
        this.mListenerInfo.destroy()
    }
}