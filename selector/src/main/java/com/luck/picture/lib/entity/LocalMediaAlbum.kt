package com.luck.picture.lib.entity

import android.os.Parcel
import android.os.Parcelable
import com.luck.picture.lib.constant.SelectorConstant

/**
 * @author：luck
 * @date：2017-5-24 16:21
 * @describe：Media Album Entity
 */
class LocalMediaAlbum() : Parcelable {
    var bucketId: Long = 0L
    var bucketDisplayName: String? = null
    var bucketDisplayCover: String? = null
    var bucketDisplayMimeType: String? = null
    var totalCount: Int = 0
    var cachePage: Int = 0
    var source = arrayListOf<LocalMedia>()
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

    constructor(parcel: Parcel) : this() {
        bucketId = parcel.readLong()
        bucketDisplayName = parcel.readString()
        bucketDisplayCover = parcel.readString()
        bucketDisplayMimeType = parcel.readString()
        totalCount = parcel.readInt()
        cachePage = parcel.readInt()
        isSelectedTag = parcel.readByte() != 0.toByte()
        isSelected = parcel.readByte() != 0.toByte()
        source = parcel.createTypedArrayList(LocalMedia.CREATOR) as ArrayList<LocalMedia>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(bucketId)
        parcel.writeString(bucketDisplayName)
        parcel.writeString(bucketDisplayCover)
        parcel.writeString(bucketDisplayMimeType)
        parcel.writeInt(totalCount)
        parcel.writeInt(cachePage)
        parcel.writeByte(if (isSelectedTag) 1 else 0)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeTypedList(source)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalMediaAlbum> {
        override fun createFromParcel(parcel: Parcel): LocalMediaAlbum {
            return LocalMediaAlbum(parcel)
        }

        override fun newArray(size: Int): Array<LocalMediaAlbum?> {
            return arrayOfNulls(size)
        }
    }
}