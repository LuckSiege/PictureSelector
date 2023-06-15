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
    var currentMediaAlbum: LocalMediaAlbum? = null

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
        mediaSource.clear()
        albumSource.clear()
        selectResult.clear()
        previewWrap.reset()
        currentMediaAlbum = null
    }

    companion object {
        fun getInstance() = InstanceHelper.sSingle
    }

    object InstanceHelper {
        val sSingle = TempDataProvider()
    }
}