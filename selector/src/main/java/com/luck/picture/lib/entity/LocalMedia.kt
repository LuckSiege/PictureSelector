package com.luck.picture.lib.entity

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

/**
 * @author：luck
 * @date：2017-5-24 16:21
 * @describe：Media Entity
 */
class LocalMedia() : Parcelable {
    var id: Long = 0
    var bucketId: Long = 0
    var displayName: String? = null
    var bucketDisplayName: String? = null
    var path: String? = null
    var absolutePath: String? = null
    var mimeType: String? = null
    var width: Int = 0
    var height: Int = 0
    var cropPath: String? = null
    var editorPath: String? = null
    var cropWidth: Int = 0
    var cropHeight: Int = 0
    var cropOffsetX: Int = 0
    var cropOffsetY: Int = 0
    var cropAspectRatio: Float = 0F
    var duration: Long = 0
    var size: Long = 0
    var dateAdded: Long = 0
    var orientation: Int = 0
    var editorData: String? = null
    var sandboxPath: String? = null
    var originalPath: String? = null
    var compressPath: String? = null
    var watermarkPath: String? = null
    var customizeExtra: String? = null
    var videoThumbnailPath: String? = null
    var isEnabledMask: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        bucketId = parcel.readLong()
        displayName = parcel.readString()
        bucketDisplayName = parcel.readString()
        path = parcel.readString()
        absolutePath = parcel.readString()
        mimeType = parcel.readString()
        width = parcel.readInt()
        height = parcel.readInt()
        cropPath = parcel.readString()
        editorPath = parcel.readString()
        cropWidth = parcel.readInt()
        cropHeight = parcel.readInt()
        cropOffsetX = parcel.readInt()
        cropOffsetY = parcel.readInt()
        cropAspectRatio = parcel.readFloat()
        duration = parcel.readLong()
        size = parcel.readLong()
        dateAdded = parcel.readLong()
        orientation = parcel.readInt()
        editorData = parcel.readString()
        sandboxPath = parcel.readString()
        originalPath = parcel.readString()
        compressPath = parcel.readString()
        watermarkPath = parcel.readString()
        customizeExtra = parcel.readString()
        videoThumbnailPath = parcel.readString()
        isEnabledMask = parcel.readByte() != 0.toByte()
    }

    fun isCrop(): Boolean {
        return !TextUtils.isEmpty(cropPath)
    }

    fun isEditor(): Boolean {
        return !TextUtils.isEmpty(editorPath)
    }

    fun isCompress(): Boolean {
        return !TextUtils.isEmpty(compressPath)
    }

    fun isOriginal(): Boolean {
        return !TextUtils.isEmpty(originalPath)
    }

    fun isWatermark(): Boolean {
        return !TextUtils.isEmpty(watermarkPath)
    }

    fun isCopySandbox(): Boolean {
        return !TextUtils.isEmpty(sandboxPath)
    }

    fun getAvailablePath(): String? {
        return when {
            isCrop() -> {
                cropPath
            }
            isEditor() -> {
                editorPath
            }
            isCompress() -> {
                compressPath
            }
            isCopySandbox() -> {
                sandboxPath
            }
            isOriginal() -> {
                originalPath
            }
            isWatermark() -> {
                watermarkPath
            }
            else -> path
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalMedia) return false
        return (id == other.id || TextUtils.equals(path, other.path)
                || TextUtils.equals(absolutePath, other.absolutePath))
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + bucketId.hashCode()
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (bucketDisplayName?.hashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + (absolutePath?.hashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + (cropPath?.hashCode() ?: 0)
        result = 31 * result + (editorPath?.hashCode() ?: 0)
        result = 31 * result + cropWidth
        result = 31 * result + cropHeight
        result = 31 * result + cropOffsetX
        result = 31 * result + cropOffsetY
        result = 31 * result + cropAspectRatio.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + orientation
        result = 31 * result + (editorData?.hashCode() ?: 0)
        result = 31 * result + (sandboxPath?.hashCode() ?: 0)
        result = 31 * result + (originalPath?.hashCode() ?: 0)
        result = 31 * result + (compressPath?.hashCode() ?: 0)
        result = 31 * result + (watermarkPath?.hashCode() ?: 0)
        result = 31 * result + (customizeExtra?.hashCode() ?: 0)
        result = 31 * result + (videoThumbnailPath?.hashCode() ?: 0)
        result = 31 * result + isEnabledMask.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(bucketId)
        parcel.writeString(displayName)
        parcel.writeString(bucketDisplayName)
        parcel.writeString(path)
        parcel.writeString(absolutePath)
        parcel.writeString(mimeType)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(cropPath)
        parcel.writeString(editorPath)
        parcel.writeInt(cropWidth)
        parcel.writeInt(cropHeight)
        parcel.writeInt(cropOffsetX)
        parcel.writeInt(cropOffsetY)
        parcel.writeFloat(cropAspectRatio)
        parcel.writeLong(duration)
        parcel.writeLong(size)
        parcel.writeLong(dateAdded)
        parcel.writeInt(orientation)
        parcel.writeString(editorData)
        parcel.writeString(sandboxPath)
        parcel.writeString(originalPath)
        parcel.writeString(compressPath)
        parcel.writeString(watermarkPath)
        parcel.writeString(customizeExtra)
        parcel.writeString(videoThumbnailPath)
        parcel.writeByte(if (isEnabledMask) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalMedia> {
        override fun createFromParcel(parcel: Parcel): LocalMedia {
            return LocalMedia(parcel)
        }

        override fun newArray(size: Int): Array<LocalMedia?> {
            return arrayOfNulls(size)
        }
    }


}