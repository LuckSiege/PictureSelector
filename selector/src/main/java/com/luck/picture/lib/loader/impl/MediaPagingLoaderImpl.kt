package com.luck.picture.lib.loader.impl

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import com.luck.picture.lib.R
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.loader.*
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.MediaUtils.getMimeType
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.SdkVersionUtils.isQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.math.max

/**
 * @author：luck
 * @date：2023-4-18 22:30
 * @describe：Paging mode loader
 */
open class MediaPagingLoaderImpl(val application: Application) : MediaLoader() {

    private val config = SelectorProviders.getInstance().getConfig()

    override fun getQueryUri(): Uri {
        return MediaStore.Files.getContentUri("external")
    }

    override fun getProjection(): Array<String> {
        return PROJECTION
    }

    override fun getAlbumSelection(): String {
        val duration = getDurationCondition()
        val fileSize = getFileSizeCondition()
        return when (config.mediaType) {
            MediaType.ALL -> { // query the image or video
                "($MEDIA_TYPE=?${getImageMimeTypeCondition()} OR $MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration) AND $fileSize"
            }
            MediaType.IMAGE -> { // query the image
                "$MEDIA_TYPE=?${getImageMimeTypeCondition()} AND $fileSize"
            }
            MediaType.VIDEO -> { // query the video
                "$MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration"
            }
            MediaType.AUDIO -> { // query the audio
                "$MEDIA_TYPE=?${getAudioMimeTypeCondition()} AND $duration"
            }
        }
    }

    override fun getSelection(bucketId: Long): String {
        val duration = getDurationCondition()
        val fileSize = getFileSizeCondition()
        when (config.mediaType) {
            MediaType.ALL -> { // query the image or video
                return if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) {
                    "($MEDIA_TYPE=?${getImageMimeTypeCondition()} OR $MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration) AND $fileSize"
                } else {
                    "($MEDIA_TYPE=?${getImageMimeTypeCondition()} OR $MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration) AND $fileSize AND $BUCKET_ID=?"
                }
            }
            MediaType.IMAGE -> { // query the image
                return if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) {
                    "($MEDIA_TYPE=?${getImageMimeTypeCondition()}) AND $fileSize"
                } else {
                    "($MEDIA_TYPE=?${getImageMimeTypeCondition()}) AND $fileSize AND $BUCKET_ID=?"
                }
            }
            MediaType.VIDEO -> { // query the video
                return if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) {
                    "($MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration) AND $fileSize"
                } else {
                    "($MEDIA_TYPE=?${getVideoMimeTypeCondition()} AND $duration) AND $fileSize AND $BUCKET_ID=?"
                }
            }
            MediaType.AUDIO -> { // query the audio
                return if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) {
                    "($MEDIA_TYPE=?${getAudioMimeTypeCondition()} AND $duration) AND $fileSize"
                } else {
                    "($MEDIA_TYPE=?${getAudioMimeTypeCondition()} AND $duration) AND $fileSize AND $BUCKET_ID=?"
                }
            }
        }
    }

    override fun getSelectionArgs(): Array<String> {
        when (config.mediaType) {
            MediaType.ALL -> {
                return arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
                )
            }
            MediaType.IMAGE -> {
                return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
            }
            MediaType.VIDEO -> {
                return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
            }
            MediaType.AUDIO -> {
                return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString())
            }
        }
    }

    override fun getSortOrder(): String? {
        return if (TextUtils.isEmpty(config.sortOrder)) MediaStore.MediaColumns.DATE_MODIFIED + " DESC" else config.sortOrder
    }

    override fun parse(media: LocalMedia, data: Cursor): LocalMedia {
        media.id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
        media.bucketId = data.getLong(data.getColumnIndexOrThrow(BUCKET_ID))
        media.displayName =
            data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
        media.bucketDisplayName = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME))
        media.absolutePath =
            data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
        media.mimeType =
            data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
        if (MediaUtils.hasMimeTypeOfUnknown(media.mimeType)) {
            val mimeType = getMimeType(media.absolutePath)
            media.mimeType = if (TextUtils.isEmpty(mimeType)) media.mimeType else mimeType
        }
        media.path =
            if (isQ()) MediaUtils.getRealPathUri(media.id, media.mimeType) else media.absolutePath
        media.orientation = data.getInt(data.getColumnIndexOrThrow(ORIENTATION))
        media.duration = data.getLong(data.getColumnIndexOrThrow(DURATION))
        media.size = data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
        media.dateAdded =
            data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
        if (media.orientation == 90 || media.orientation == 270) {
            media.width = data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
            media.height = data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
        } else {
            media.width = data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
            media.height = data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
        }
        return media
    }

    override suspend fun loadMediaAlbum(): MutableList<LocalMediaAlbum> {
        val albumList = mutableListOf<LocalMediaAlbum>()
        withContext(Dispatchers.IO) {
            application.contentResolver.query(
                getQueryUri(),
                getProjection(),
                getAlbumSelection(),
                getSelectionArgs(), getSortOrder()
            )?.use { data ->
                if (data.count > 0) {
                    var totalCount = 0L
                    val mediaUnique = LocalMedia()
                    val bucketSet = hashSetOf<Long>()
                    val countMap = hashMapOf<Long, Long>()
                    data.moveToFirst()
                    do {
                        val media = parse(mediaUnique, data)
                        if (config.mListenerInfo.onQueryFilterListener?.onFilter(media) == true) {
                            continue
                        }
                        var newCount = countMap[media.bucketId]
                        if (newCount == null) {
                            newCount = 1L
                        } else {
                            newCount++
                        }
                        countMap[media.bucketId] = newCount
                        if (bucketSet.contains(media.bucketId)) {
                            continue
                        }
                        val mediaAlbum = LocalMediaAlbum()
                        mediaAlbum.bucketId = media.bucketId
                        mediaAlbum.bucketDisplayName = media.bucketDisplayName
                        mediaAlbum.bucketDisplayCover = media.path
                        mediaAlbum.bucketDisplayMimeType = media.mimeType
                        albumList += mediaAlbum
                        bucketSet.add(media.bucketId)
                    } while (data.moveToNext())

                    // create custom sandbox dir media album
                    config.sandboxDir?.let { sandboxDir ->
                        val mediaList = loadAppInternalDir(sandboxDir)
                        if (mediaList.isNotEmpty()) {
                            mediaList.first().let { firstMedia ->
                                val sandboxMediaAlbum = LocalMediaAlbum()
                                sandboxMediaAlbum.bucketId = firstMedia.bucketId
                                sandboxMediaAlbum.bucketDisplayName = firstMedia.bucketDisplayName
                                sandboxMediaAlbum.bucketDisplayCover = firstMedia.path
                                sandboxMediaAlbum.bucketDisplayMimeType = firstMedia.mimeType
                                sandboxMediaAlbum.totalCount = mediaList.size
                                sandboxMediaAlbum.source.addAll(mediaList.toMutableList())
                                albumList.add(sandboxMediaAlbum)
                                countMap[firstMedia.bucketId] = mediaList.size.toLong()
                            }
                        }
                    }

                    // calculate album count
                    albumList.forEach { mediaAlbum ->
                        countMap[mediaAlbum.bucketId]?.let { count ->
                            mediaAlbum.totalCount = count.toInt()
                            totalCount += mediaAlbum.totalCount
                        }
                    }

                    // create all media album
                    val allMediaAlbum = LocalMediaAlbum()
                    val bucketDisplayName =
                        config.defaultAlbumName ?: if (config.mediaType == MediaType.AUDIO)
                            application.getString(R.string.ps_all_audio) else application.getString(
                            R.string.ps_camera_roll
                        )
                    allMediaAlbum.bucketDisplayName = bucketDisplayName
                    allMediaAlbum.bucketId = SelectorConstant.DEFAULT_ALL_BUCKET_ID
                    allMediaAlbum.totalCount = totalCount.toInt()
                    albumList.first().let { firstAlbum ->
                        allMediaAlbum.bucketDisplayCover = firstAlbum.bucketDisplayCover
                        allMediaAlbum.bucketDisplayMimeType = firstAlbum.bucketDisplayMimeType
                    }
                    albumList.add(0, allMediaAlbum)
                }
                // close cursor
                data.close()
            }
        }
        return albumList.apply {
            this.sortByDescending { it.totalCount }
        }
    }

    override suspend fun loadMedia(pageSize: Int): MutableList<LocalMedia> {
        return loadMediaMore(SelectorConstant.DEFAULT_ALL_BUCKET_ID, 1, pageSize)
    }

    override suspend fun loadMedia(bucketId: Long, pageSize: Int): MutableList<LocalMedia> {
        val mediaList = loadMediaMore(bucketId, 1, pageSize)
        config.sandboxDir?.let { sandboxDir ->
            mediaList.addAll(loadAppInternalDir(sandboxDir))
        }
        return mediaList
    }

    override suspend fun loadMediaMore(
        bucketId: Long,
        page: Int,
        pageSize: Int
    ): MutableList<LocalMedia> {
        val mediaList = mutableListOf<LocalMedia>()
        withContext(Dispatchers.IO) {
            if (SdkVersionUtils.isR()) {
                application.contentResolver.query(
                    getQueryUri(), getProjection(), MediaUtils.createQueryArgsBundle(
                        getSelection(bucketId),
                        if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) getSelectionArgs() else getSelectionArgs().plusElement(
                            bucketId.toString()
                        ), pageSize, (page - 1) * pageSize, getSortOrder()
                    ), null
                )?.use { cursor ->
                    if (cursor.count > 0) {
                        while (cursor.moveToNext()) {
                            val media = parse(LocalMedia(), cursor)
                            if (config.mListenerInfo.onQueryFilterListener?.onFilter(media) == true) {
                                continue
                            }
                            mediaList += media
                        }
                    }
                }
            } else {
                application.contentResolver.query(
                    getQueryUri(),
                    getProjection(),
                    getSelection(bucketId),
                    if (bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID) getSelectionArgs() else getSelectionArgs().plusElement(
                        bucketId.toString()
                    ),
                    getSortOrder() + " limit " + pageSize + " offset " + (page - 1) * pageSize
                )?.use { cursor ->
                    if (cursor.count > 0) {
                        while (cursor.moveToNext()) {
                            val media = parse(LocalMedia(), cursor)
                            if (config.mListenerInfo.onQueryFilterListener?.onFilter(media) == true) {
                                continue
                            }
                            mediaList += media
                        }
                    }
                    // close cursor
                    cursor.close()
                }
            }
        }
        return mediaList
    }

    override suspend fun loadAppInternalDir(sandboxDir: String): MutableList<LocalMedia> {
        val mediaList = mutableListOf<LocalMedia>()
        val sandboxFile = File(sandboxDir)
        val listFiles = sandboxFile.listFiles { file -> !file.isDirectory && file.length() > 0 }
            ?: return mediaList
        listFiles.forEach continuing@{ file ->
            val media = LocalMedia()
            val mimeType = getMimeType(file.absolutePath)
            if (config.mediaType == MediaType.IMAGE) {
                if (!MediaUtils.hasMimeTypeOfImage(mimeType)) {
                    return@continuing
                }
            } else if (config.mediaType == MediaType.VIDEO) {
                if (!MediaUtils.hasMimeTypeOfVideo(mimeType)) {
                    return@continuing
                }
            } else if (config.mediaType == MediaType.AUDIO) {
                if (!MediaUtils.hasMimeTypeOfAudio(mimeType)) {
                    return@continuing
                }
            }
            if (!config.isGif) {
                if (MediaUtils.isHasGif(mimeType)) {
                    return@continuing
                }
            }
            if (!config.isWebp) {
                if (MediaUtils.isHasWebp(mimeType)) {
                    return@continuing
                }
            }
            if (!config.isBmp) {
                if (MediaUtils.isHasBMP(mimeType)) {
                    return@continuing
                }
            }
            if (!config.isHeic) {
                if (MediaUtils.isHasHeic(mimeType)) {
                    return@continuing
                }
            }
            media.id = file.name.hashCode().toLong()
            media.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
            media.displayName = file.name
            media.bucketDisplayName = sandboxFile.name
            media.absolutePath = file.absolutePath
            media.mimeType = mimeType
            media.path = media.absolutePath
            val mediaInfo = MediaUtils.getMediaInfo(application, media.mimeType, file.absolutePath)
            media.orientation = mediaInfo.orientation
            media.duration = mediaInfo.duration
            media.width = mediaInfo.width
            media.height = mediaInfo.height
            media.size = file.length()
            media.dateAdded = file.lastModified()
            if (config.mListenerInfo.onQueryFilterListener?.onFilter(media) == true) {
                return@continuing
            }
            mediaList += media
        }
        return mediaList.apply {
            this.sortByDescending { it.dateAdded }
        }
    }

    /**
     * Get video (maximum or minimum time)
     */
    open fun getDurationCondition(): String {
        val maxS =
            if (config.filterVideoMaxSecond == 0L) Long.MAX_VALUE else config.filterVideoMaxSecond
        return String.format(
            Locale.CHINA,
            "%d <%s $DURATION and $DURATION <= %d",
            max(0L, config.filterVideoMinSecond),
            "=",
            maxS
        )
    }

    /**
     * Get media size (maxFileSize or miniFileSize)
     */
    open fun getFileSizeCondition(): String {
        val maxS =
            if (config.filterMaxFileSize == 0L) Long.MAX_VALUE else config.filterMaxFileSize
        return String.format(
            Locale.CHINA,
            "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
            max(0, config.filterMinFileSize), "=", maxS
        )
    }

    /**
     * Only query image format media resources
     */
    open fun getImageMimeTypeCondition(): String {
        val stringBuilder = StringBuilder()
        config.onlyQueryImageFormat.forEachIndexed { i, mimeType ->
            stringBuilder.append(if (i == 0) " AND " else " OR ")
                .append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(mimeType)
                .append("'")
        }
        if (!config.isGif && !config.onlyQueryImageFormat.contains(MediaUtils.ofGIF())) {
            stringBuilder.append(NOT_GIF)
        }
        if (!config.isWebp && !config.onlyQueryImageFormat.contains(MediaUtils.ofWebp())) {
            stringBuilder.append(NOT_WEBP)
        }
        if (!config.isBmp && !config.onlyQueryImageFormat.contains(MediaUtils.ofBMP())
            && !config.onlyQueryImageFormat.contains(MediaUtils.ofXMSBMP())
            && !config.onlyQueryImageFormat.contains(MediaUtils.ofVNDBMP())
        ) {
            stringBuilder.append(NOT_BMP).append(NOT_XMS_BMP).append(NOT_VND_WAP_BMP)
        }
        if (!config.isHeic && !config.onlyQueryImageFormat.contains(MediaUtils.ofHeic())) {
            stringBuilder.append(NOT_HEIC)
        }
        return stringBuilder.toString()
    }

    /**
     * Only query video format media resources
     */
    open fun getVideoMimeTypeCondition(): String {
        val stringBuilder = StringBuilder()
        config.onlyQueryVideoFormat.forEachIndexed { i, mimeType ->
            stringBuilder.append(if (i == 0) " AND " else " OR ")
                .append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(mimeType)
                .append("'")
        }
        return stringBuilder.toString()
    }

    /**
     * Only query audio format media resources
     */
    open fun getAudioMimeTypeCondition(): String {
        val stringBuilder = StringBuilder()
        config.onlyQueryAudioFormat.forEachIndexed { i, mimeType ->
            stringBuilder.append(if (i == 0) " AND " else " OR ")
                .append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(mimeType)
                .append("'")
        }
        return stringBuilder.toString()
    }
}