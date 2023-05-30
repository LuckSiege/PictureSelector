package com.luck.picture.lib.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：MediaStoreUtils
 */
object MediaStoreUtils {

    fun insertImage(context: Context, fileName: String): Uri? {
        val values = ContentValues(3)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_DCIM}/Camera"
            )
        }
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            context.contentResolver
                .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values)
        }
    }

    fun insertVideo(context: Context, fileName: String): Uri? {
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            context.contentResolver
                .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            context.contentResolver
                .insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, values)
        }
    }
}