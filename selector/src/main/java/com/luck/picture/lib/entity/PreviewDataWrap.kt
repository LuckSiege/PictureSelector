package com.luck.picture.lib.entity

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：Preview data wrap
 */
class PreviewDataWrap {
    var page: Int = 1
    var position: Int = 0
    var bucketId: Long = 0
    var totalCount: Int = 0
    var isDownload: Boolean = false
    var isDisplayCamera: Boolean = false
    var isBottomPreview: Boolean = false
    var isDisplayDelete: Boolean = false
    var isExternalPreview: Boolean = false
    var source = mutableListOf<LocalMedia>()

    fun copy(): PreviewDataWrap {
        val wrap = PreviewDataWrap()
        wrap.page = page
        wrap.position = position
        wrap.bucketId = bucketId
        wrap.totalCount = totalCount
        wrap.isDownload = isDownload
        wrap.isBottomPreview = isBottomPreview
        wrap.isDisplayCamera = isDisplayCamera
        wrap.isDisplayDelete = isDisplayDelete
        wrap.isExternalPreview = isExternalPreview
        wrap.source = source.toMutableList()
        return wrap
    }

    fun reset() {
        position = 0
        bucketId = 0
        totalCount = 0
        isDownload = false
        isDisplayCamera = false
        isBottomPreview = false
        isDisplayDelete = false
        if (source.isNotEmpty()) {
            source.clear()
        }
    }
}