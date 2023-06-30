package com.luck.pictureselector.custom

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.loader.PROJECTION
import com.luck.picture.lib.utils.MediaUtils
import java.io.File

class CustomMediaLoader(private val application: Application) : MediaLoader() {

    override fun getQueryUri(): Uri {
        return MediaStore.Files.getContentUri("external")
    }

    override fun getProjection(): Array<String>? {
        return PROJECTION
    }

    override fun getAlbumSelection(): String? {
        return null
    }

    override fun getSelection(bucketId: Long): String? {
        return null
    }

    override fun getSelectionArgs(): Array<String>? {
        return null
    }

    override fun getSortOrder(): String? {
        return null
    }

    override fun parse(media: LocalMedia, data: Cursor): LocalMedia {

        return media
    }

    override suspend fun loadMediaAlbum(): MutableList<LocalMediaAlbum> {
        return mutableListOf()
    }

    override suspend fun loadMedia(pageSize: Int): MutableList<LocalMedia> {
        return mutableListOf()
    }

    override suspend fun loadMedia(bucketId: Long, pageSize: Int): MutableList<LocalMedia> {
        return mutableListOf()
    }

    override suspend fun loadMediaMore(
        bucketId: Long,
        page: Int,
        pageSize: Int
    ): MutableList<LocalMedia> {
        return mutableListOf()
    }

    override suspend fun loadAppInternalDir(sandboxDir: String): MutableList<LocalMedia> {
        val mediaList = mutableListOf<LocalMedia>()
        val sandboxFile = File(sandboxDir)
        val listFiles = sandboxFile.listFiles { file -> !file.isDirectory && file.length() > 0 }
            ?: return mediaList
        listFiles.forEach { file ->
            val media = LocalMedia()
            media.id = file.name.hashCode().toLong()
            media.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
            media.displayName = file.name
            media.bucketDisplayName = sandboxFile.name
            media.absolutePath = file.absolutePath
            media.mimeType = MediaUtils.getMimeType(file.absolutePath)
            media.path = media.absolutePath
            val mediaInfo = MediaUtils.getMediaInfo(application, media.mimeType, file.absolutePath)
            media.orientation = mediaInfo.orientation
            media.duration = mediaInfo.duration
            media.width = mediaInfo.width
            media.height = mediaInfo.height
            media.size = file.length()
            media.dateAdded = file.lastModified()
            mediaList += media
        }
        return mediaList
    }
}