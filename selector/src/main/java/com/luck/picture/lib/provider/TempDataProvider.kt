package com.luck.picture.lib.provider

import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.entity.PreviewDataWrap

/**
 * @author：luck
 * @date：2023/6/15 11:09 上午
 * @describe：temporary data provider
 */
class TempDataProvider {

    /**
     * Preview data wrap
     */
    var previewWrap = PreviewDataWrap()

    /**
     * Current Selected album
     */
    var currentMediaAlbum = LocalMediaAlbum.ofDefault()

    /**
     * album data source
     */
    var albumSource = mutableListOf<LocalMediaAlbum>()

    /**
     * media data source
     */
    var mediaSource = mutableListOf<LocalMedia>()


    /**
     * select result
     */
    var selectResult = mutableListOf<LocalMedia>()

    /**
     * Current apply permission
     */
    var currentRequestPermission = arrayOf<String>()

    fun reset() {
        if (mediaSource.isNotEmpty()) {
            mediaSource.clear()
        }
        if (albumSource.isNotEmpty()) {
            albumSource.clear()
        }
        if (selectResult.isNotEmpty()) {
            selectResult.clear()
        }
        previewWrap.reset()
        currentMediaAlbum = LocalMediaAlbum.ofDefault()
        if (currentRequestPermission.isNotEmpty()) {
            currentRequestPermission = arrayOf()
        }
    }

    companion object {
        fun getInstance() = InstanceHelper.instance
    }

    object InstanceHelper {
        val instance = TempDataProvider()
    }
}