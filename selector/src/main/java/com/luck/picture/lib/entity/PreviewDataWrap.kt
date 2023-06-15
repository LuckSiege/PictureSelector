package com.luck.picture.lib.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：Preview data wrap
 */
class PreviewDataWrap() : Parcelable {
    var page: Int = 1
    var position: Int = 0
    var bucketId: Long = 0
    var totalCount: Int = 0
    var isDownload: Boolean = false
    var isDisplayCamera: Boolean = false
    var isBottomPreview: Boolean = false
    var isDisplayDelete: Boolean = false
    var isExternalPreview: Boolean = false
    var source = arrayListOf<LocalMedia>()

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
        wrap.source = source.toList() as ArrayList<LocalMedia>
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
        source.clear()
    }

    constructor(parcel: Parcel) : this() {
        page = parcel.readInt()
        position = parcel.readInt()
        bucketId = parcel.readLong()
        totalCount = parcel.readInt()
        isDownload = parcel.readByte() != 0.toByte()
        isDisplayCamera = parcel.readByte() != 0.toByte()
        isBottomPreview = parcel.readByte() != 0.toByte()
        isDisplayDelete = parcel.readByte() != 0.toByte()
        isExternalPreview = parcel.readByte() != 0.toByte()
        source = parcel.createTypedArrayList(LocalMedia.CREATOR) as ArrayList<LocalMedia>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(page)
        parcel.writeInt(position)
        parcel.writeLong(bucketId)
        parcel.writeInt(totalCount)
        parcel.writeByte(if (isDownload) 1 else 0)
        parcel.writeByte(if (isDisplayCamera) 1 else 0)
        parcel.writeByte(if (isBottomPreview) 1 else 0)
        parcel.writeByte(if (isDisplayDelete) 1 else 0)
        parcel.writeByte(if (isExternalPreview) 1 else 0)
        parcel.writeTypedList(source)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PreviewDataWrap> {
        override fun createFromParcel(parcel: Parcel): PreviewDataWrap {
            return PreviewDataWrap(parcel)
        }

        override fun newArray(size: Int): Array<PreviewDataWrap?> {
            return arrayOfNulls(size)
        }
    }
}