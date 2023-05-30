package com.luck.picture.lib.entity

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：MediaInfo
 */
class MediaInfo {
    var id: Long = 0
    var bucketId: Long = 0
    var path: String? = null
    var absolutePath: String? = null
    var mimeType: String? = null
    var width: Int = 0
    var height: Int = 0
    var duration: Long = 0L
    var orientation: Int = 0
    var size: Long = 0
    var dateAdded: Long = 0
}