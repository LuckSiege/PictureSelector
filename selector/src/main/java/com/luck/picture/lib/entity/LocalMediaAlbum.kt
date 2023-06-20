package com.luck.picture.lib.entity

import com.luck.picture.lib.constant.SelectorConstant

/**
 * @author：luck
 * @date：2017-5-24 16:21
 * @describe：Media Album Entity
 */
class LocalMediaAlbum {
    var bucketId: Long = 0L
    var bucketDisplayName: String? = null
    var bucketDisplayCover: String? = null
    var bucketDisplayMimeType: String? = null
    var totalCount: Int = 0
    var cachePage: Int = 0
    var source = mutableListOf<LocalMedia>()
    var isSelectedTag: Boolean = false
    var isSelected: Boolean = false

    fun isAllAlbum(): Boolean {
        return bucketId == SelectorConstant.DEFAULT_ALL_BUCKET_ID
    }

    fun isSandboxAlbum(): Boolean {
        return bucketId == SelectorConstant.DEFAULT_DIR_BUCKET_ID
    }

    fun isEqualAlbum(bucketId: Long): Boolean {
        return this.bucketId == bucketId
    }

    companion object {
        fun ofDefault(): LocalMediaAlbum {
            return LocalMediaAlbum().apply {
                this.bucketId = SelectorConstant.DEFAULT_ALL_BUCKET_ID
                this.cachePage = 0
                this.totalCount = 0
                this.isSelected = true
            }
        }
    }
}