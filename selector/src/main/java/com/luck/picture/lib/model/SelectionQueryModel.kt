package com.luck.picture.lib.model

import android.text.TextUtils
import androidx.annotation.IntRange
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.constant.FileSizeUnitConstant
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.interfaces.OnQueryFilterListener
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.loader.impl.MediaPagingLoaderImpl
import com.luck.picture.lib.provider.SelectorProviders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.NotNull

/**
 * @author：luck
 * @date：2017-5-24 22:30
 * @describe：SelectionQueryModel
 */
class SelectionQueryModel constructor(
    private var selector: PictureSelector, mediaType: MediaType
) {
    private var config: SelectorConfig = SelectorConfig()

    init {
        this.config.mediaType = mediaType
        SelectorProviders.getInstance().addConfigQueue(config)
    }

    /**
     * Set up a custom media data loader
     */
    fun setCustomMediaLoader(loader: MediaLoader?): SelectionQueryModel {
        this.config.dataLoader = loader
        return this
    }

    /**
     * Number of pages
     * @param pageSize return result count
     */
    fun setPageSize(
        @IntRange(from = 1, to = Long.MAX_VALUE) pageSize: Int
    ): SelectionQueryModel {
        this.config.pageSize = pageSize
        return this
    }

    /**
     * filter gif format data
     */
    fun isGif(isGif: Boolean): SelectionQueryModel {
        this.config.isGif = isGif
        return this
    }


    /**
     * Display gif type resources
     *
     * filter webp format data
     */
    fun isWebp(isWebp: Boolean): SelectionQueryModel {
        this.config.isWebp = isWebp
        return this
    }

    /**
     * Display gif type resources
     *
     * filter bmp format data
     */
    fun isBmp(isBmp: Boolean): SelectionQueryModel {
        this.config.isBmp = isBmp
        return this
    }

    /**
     * Display gif type resources
     *
     * filter heic format data
     */
    fun isHeic(isHeic: Boolean): SelectionQueryModel {
        this.config.isHeic = isHeic
        return this
    }

    /**
     * Query the specified application file directory
     * @param dir Root directory
     * @param isOnlySandboxDir Only query the [SelectorConfig.sandboxDir] directory
     */
    fun setQuerySandboxDir(
        @NotNull dir: String,
        isOnlySandboxDir: Boolean
    ): SelectionQueryModel {
        this.config.sandboxDir = dir
        this.config.isOnlySandboxDir = isOnlySandboxDir
        return this
    }

    /**
     * Filter maximum files
     * @param sizeKb file size in kb
     */
    fun setFilterMaxFileSize(sizeKb: Long): SelectionQueryModel {
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
    fun setFilterMinFileSize(sizeKb: Long): SelectionQueryModel {
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
    fun setFilterVideoMaxSecond(second: Long): SelectionQueryModel {
        this.config.filterVideoMaxSecond = second * 1000
        return this
    }

    /**
     * Filter the shortest video
     * @param second unit seconds
     */
    fun setFilterVideoMinSecond(second: Long): SelectionQueryModel {
        this.config.filterVideoMinSecond = second * 1000
        return this
    }

    /**
     * sort order
     * @param sortOrder example use [MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
     *  MediaStore.MediaColumns.DATE_MODIFIED + " ASC";]
     */
    fun setQuerySortOrder(sortOrder: String?): SelectionQueryModel {
        this.config.sortOrder = sortOrder
        return this
    }

    /**
     * Filter out multimedia data that does not comply with rules
     */
    fun setOnQueryFilterListener(l: OnQueryFilterListener?): SelectionQueryModel {
        this.config.mListenerInfo.onQueryFilterListener = l
        return this
    }


    /**
     * Only query image format media resources
     * @param format Use [LocalMedia.mimeType]
     * for example [image/jpeg... more]
     */
    fun setOnlyQueryImageFormat(vararg format: String): SelectionQueryModel {
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
    fun setOnlyQueryVideoFormat(vararg format: String): SelectionQueryModel {
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
    fun setOnlyQueryAudioFormat(vararg format: String): SelectionQueryModel {
        format.forEach continuing@{ mimeType ->
            if (TextUtils.isEmpty(mimeType)) {
                return@continuing
            }
            this.config.onlyQueryAudioFormat.add(mimeType)
        }
        return this
    }


    /**
     * build local media Loader
     */
    fun buildMediaLoader(): MediaLoader {
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); #Activity is empty")
        return config.dataLoader ?: MediaPagingLoaderImpl(activity.application)
    }

    /**
     * Query all album lists
     */
    suspend fun loadAllAlbum(): MutableList<LocalMediaAlbum> {
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); #Activity is empty")
        val albumList = mutableListOf<LocalMediaAlbum>()
        withContext(Dispatchers.IO) {
            val mediaLoader = config.dataLoader ?: MediaPagingLoaderImpl(activity.application)
            albumList.addAll(mediaLoader.loadMediaAlbum())
        }
        return albumList
    }

    /**
     * Query media data，If it is in pagination mode, only the first page of data will be returned，
     * But the quantity can be controlled through the [SelectorConfig.pageSize] parameter
     */
    suspend fun loadMedia(): MutableList<LocalMedia> {
        return loadMedia(SelectorConstant.DEFAULT_MAX_PAGE_SIZE)
    }

    /**
     * Query media data，If it is in pagination mode, only the first page of data will be returned，
     * But the quantity can be controlled through the [SelectorConfig.pageSize] parameter
     */
    private suspend fun loadMedia(
        @IntRange(
            from = 1,
            to = Long.MAX_VALUE
        ) pageSize: Int
    ): MutableList<LocalMedia> {
        val activity = selector.getActivity()
            ?: throw NullPointerException("PictureSelector.create(); #Activity is empty")
        val mediaList = mutableListOf<LocalMedia>()
        withContext(Dispatchers.IO) {
            val mediaLoader = config.dataLoader ?: MediaPagingLoaderImpl(activity.application)
            mediaList.addAll(mediaLoader.loadMedia(pageSize))
        }
        return mediaList
    }
}