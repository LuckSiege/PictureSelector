package com.luck.picture.lib.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.MediaInfo
import com.luck.picture.lib.loader.BUCKET_DISPLAY_NAME
import com.luck.picture.lib.loader.BUCKET_ID
import com.luck.picture.lib.loader.DURATION
import com.luck.picture.lib.loader.ORIENTATION
import com.luck.picture.lib.utils.SdkVersionUtils.isO
import com.luck.picture.lib.utils.SdkVersionUtils.isR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.URLConnection
import java.util.*
import kotlin.coroutines.resume

/**
 * @author：luck
 * @date：2022/11/22 5:51 下午
 * @describe：MediaUtils
 */
object MediaUtils {

    fun hasMimeTypeOfImage(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("image")
    }

    fun hasMimeTypeOfVideo(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("video")
    }

    fun hasUrlOfVideo(url: String?): Boolean {
        return url != null && url.lowercase(Locale.getDefault()).endsWith(".mp4")
    }

    fun hasMimeTypeOfAudio(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("audio")
    }

    fun hasMimeTypeOfUnknown(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("image/*")
    }

    fun isLongImage(width: Int, height: Int): Boolean {
        return if (width <= 0 || height <= 0) {
            false
        } else height > width * 3
    }

    fun isHasGif(mimeType: String?): Boolean {
        return mimeType != null && (mimeType == "image/gif" || mimeType == "image/GIF")
    }

    fun isUrlHasGif(url: String?): Boolean {
        return url != null && url.lowercase(Locale.getDefault()).endsWith(".gif")
    }

    fun isHasWebp(mimeType: String?): Boolean {
        return mimeType != null && mimeType == "image/webp"
    }

    fun isHasBMP(mimeType: String?): Boolean {
        return mimeType != null && mimeType == "image/bmp"
    }

    fun isHasHeic(mimeType: String?): Boolean {
        return mimeType != null && mimeType == "image/heic"
    }

    fun isUrlHasWebp(url: String?): Boolean {
        return url != null && url.lowercase(Locale.getDefault()).endsWith(".webp")
    }

    fun isHasHttp(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) {
            false
        } else path.startsWith("http") || path.startsWith("https")
    }

    fun isContent(url: String): Boolean {
        return if (TextUtils.isEmpty(url)) {
            false
        } else url.startsWith("content://")
    }

    fun ofGIF(): String {
        return "image/gif"
    }

    fun ofBMP(): String {
        return "image/bmp"
    }

    fun ofXMSBMP(): String {
        return "image/x-ms-bmp"
    }

    fun ofVNDBMP(): String {
        return "image/vnd.wap.wbmp"
    }

    fun ofHeic(): String {
        return "image/heic"
    }

    fun ofWebp(): String {
        return "image/webp"
    }

    fun ofJPEG(): String {
        return "image/jpeg"
    }

    fun ofJPG(): String {
        return "image/jpg"
    }

    fun ofPNG(): String {
        return "image/png"
    }

    fun of3GP(): String {
        return "video/3gp"
    }

    fun ofMP4(): String {
        return "video/mp4"
    }

    fun ofWebm(): String {
        return "video/webm"
    }

    fun ofAVI(): String {
        return "video/avi"
    }

    fun ofMPEG(): String {
        return "video/mpeg"
    }

    fun getUrlMimeType(url: String?): String? {
        if (url == null || TextUtils.isEmpty(url)) {
            return null
        }
        val postfix = url.substring(url.lastIndexOf(".") + 1)
        val lowercase = postfix.lowercase(Locale.getDefault())
        if (lowercase == "jpg" || lowercase == "jpeg" || lowercase == "png" || lowercase == "gif"
            || lowercase == "webp" || lowercase == "bmp"
        ) {
            return "image/$postfix"
        } else if (lowercase == "mp4" || lowercase == "avi") {
            return "video/$postfix"
        } else if (lowercase == "mp3" || lowercase == "amr" || postfix == "m4a") {
            return "audio/$postfix"
        }
        return null
    }

    fun getMimeType(path: String?): String? {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(path)
        var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
        if (TextUtils.isEmpty(mimeType)) {
            val fileNameMap = URLConnection.getFileNameMap()
            mimeType = fileNameMap.getContentTypeFor(path?.let { File(it).name })
        }
        return mimeType
    }

    fun getRealPathUri(id: Long, mimeType: String?): String {
        val contentUri: Uri = when {
            hasMimeTypeOfImage(mimeType) -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            hasMimeTypeOfVideo(mimeType) -> {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            hasMimeTypeOfAudio(mimeType) -> {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            else -> {
                MediaStore.Files.getContentUri("external")
            }
        }
        return ContentUris.withAppendedId(contentUri, id).toString()
    }

    fun getPostfix(context: Context, path: String, defPostfix: String): String {
        return if (isContent(path)) {
            val realPath = getPath(context, Uri.parse(path))
            if (realPath == null || TextUtils.isEmpty(realPath)) {
                defPostfix
            } else {
                realPath.substring(realPath.lastIndexOf(".") + 1)
            }
        } else {
            path.substring(path.lastIndexOf(".") + 1)
        }
    }

    fun createQueryArgsBundle(
        selection: String,
        selectionArgs: Array<String>,
        limitCount: Int,
        offset: Int,
        orderBy: String?
    ): Bundle {
        val queryArgs = Bundle()
        if (isO()) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, orderBy)
        }
        if (isR()) {
            queryArgs.putString(
                ContentResolver.QUERY_ARG_SQL_LIMIT, "$limitCount offset $offset"
            )
        }
        return queryArgs
    }


    suspend fun getMediaInfo(context: Context, path: String): MediaInfo {
        return getMediaInfo(context, getMimeType(path), path)
    }

    suspend fun getMediaInfo(context: Context, mimeType: String?, path: String): MediaInfo {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                val mediaInfo = MediaInfo()
                mediaInfo.mimeType = mimeType
                if (hasMimeTypeOfImage(mimeType)) {
                    var exif: ExifInterface? = null
                    var inputStream: InputStream? = null
                    if (isContent(path)) {
                        inputStream = context.contentResolver.openInputStream(Uri.parse(path))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && inputStream != null) {
                            exif = ExifInterface(inputStream)
                        }
                    } else {
                        exif = ExifInterface(path)
                    }
                    exif?.apply {
                        val orientation = this.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                        val width = this.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
                        val height = this.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                            || orientation == ExifInterface.ORIENTATION_ROTATE_180
                            || orientation == ExifInterface.ORIENTATION_ROTATE_270
                            || orientation == ExifInterface.ORIENTATION_TRANSVERSE
                        ) {
                            mediaInfo.width = height
                            mediaInfo.height = width
                        } else {
                            mediaInfo.width = width
                            mediaInfo.height = height
                        }
                    }
                    inputStream?.apply {
                        FileUtils.close(this)
                    }
                } else if (hasMimeTypeOfVideo(mimeType)) {
                    val retriever = MediaMetadataRetriever()
                    if (isContent(path)) {
                        retriever.setDataSource(context, Uri.parse(path))
                    } else {
                        retriever.setDataSource(path)
                    }
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong()?.let { duration ->
                            mediaInfo.duration = duration
                        }
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                        ?.toInt()?.let { orientation ->
                            mediaInfo.orientation = orientation
                        }
                    if (mediaInfo.orientation == 90 || mediaInfo.orientation == 270) {
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                        )?.toInt()?.let { width ->
                            mediaInfo.height = width
                        }
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                        )?.toInt()?.let { height ->
                            mediaInfo.width = height
                        }
                    } else {
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                        )?.toInt()?.let { width ->
                            mediaInfo.width = width
                        }
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                        )?.toInt()?.let { height ->
                            mediaInfo.height = height
                        }
                    }

                } else if (hasMimeTypeOfAudio(mimeType)) {
                    val retriever = MediaMetadataRetriever()
                    if (isContent(path)) {
                        retriever.setDataSource(context, Uri.parse(path))
                    } else {
                        retriever.setDataSource(path)
                    }
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong()?.let { duration ->
                            mediaInfo.duration = duration
                        }
                }
                it.resume(mediaInfo)
            }
        }
    }

    suspend fun getAssignFileMedia(context: Context, absolutePath: String): LocalMedia {
        return withContext(Dispatchers.IO) {
            val media = LocalMedia()
            val file = File(absolutePath)
            media.id = file.hashCode().toLong()
            media.path = absolutePath
            media.absolutePath = absolutePath
            media.displayName = file.name
            media.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
            media.bucketDisplayName = file.parentFile?.name
            media.mimeType = getMimeType(media.absolutePath)
            val mediaInfo = getMediaInfo(context, media.mimeType, absolutePath)
            media.orientation = mediaInfo.orientation
            media.duration = mediaInfo.duration
            media.size = file.length()
            media.dateAdded = file.lastModified()
            if (media.orientation == 90 || media.orientation == 270) {
                media.width = mediaInfo.height
                media.height = mediaInfo.width
            } else {
                media.width = mediaInfo.width
                media.height = mediaInfo.height
            }
            return@withContext media
        }
    }

    suspend fun getAssignPathMedia(context: Context, absolutePath: String): LocalMedia? {
        return withContext(Dispatchers.IO) {
            val selection = MediaStore.Files.FileColumns.DATA + " like ?"
            val selectionArgs = arrayOf("%$absolutePath%")
            val cursor: Cursor?
            if (isR()) {
                val queryArgs = createQueryArgsBundle(
                    selection,
                    selectionArgs,
                    1,
                    0,
                    MediaStore.Files.FileColumns._ID + " DESC"
                )
                cursor = context.contentResolver
                    .query(
                        MediaStore.Files.getContentUri("external"), getProjection(), queryArgs, null
                    )
            } else {
                cursor = context.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    getProjection(),
                    selection,
                    selectionArgs,
                    MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
                )
            }
            cursor?.use { data ->
                if (data.count > 0 && data.moveToFirst()) {
                    val media = LocalMedia()
                    media.id =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    media.bucketId = data.getLong(data.getColumnIndexOrThrow(BUCKET_ID))
                    media.displayName =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                    media.bucketDisplayName =
                        data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME))
                    media.absolutePath =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                    media.mimeType =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                    if (hasMimeTypeOfUnknown(media.mimeType)) {
                        val mimeType = getMimeType(media.absolutePath)
                        media.mimeType =
                            if (TextUtils.isEmpty(mimeType)) media.mimeType else mimeType
                    }
                    media.path =
                        if (SdkVersionUtils.isQ()) getRealPathUri(
                            media.id,
                            media.mimeType
                        ) else media.absolutePath
                    media.orientation = data.getInt(data.getColumnIndexOrThrow(ORIENTATION))
                    media.duration = data.getLong(data.getColumnIndexOrThrow(DURATION))
                    media.size =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                    media.dateAdded =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                    if (media.orientation == 90 || media.orientation == 270) {
                        media.width =
                            data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
                        media.height =
                            data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
                    } else {
                        media.width =
                            data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
                        media.height =
                            data.getInt(data.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
                    }
                    return@withContext media
                }
                data.close()
            }
            return@withContext null
        }
    }

    suspend fun getDCIMLastId(context: Context, absoluteDir: String): Long {
        withContext(Dispatchers.IO) {
            val selection = MediaStore.Images.Media.DATA + " like ?"
            val selectionArgs = arrayOf("%$absoluteDir%")
            val contentResolver = context.contentResolver
            if (isR()) {
                val queryArgs = createQueryArgsBundle(
                    selection,
                    selectionArgs,
                    1,
                    0,
                    MediaStore.Files.FileColumns._ID + " DESC"
                )
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, queryArgs, null
                )
            } else {
                val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    orderBy
                )
            }?.use { data ->
                if (data.count > 0 && data.moveToFirst()) {
                    val id = data.getLong(data.getColumnIndex(MediaStore.Images.Media._ID))
                    val date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    return@withContext if (DateUtils.dateDiffer(date)) id else -1L
                }
                data.close()
            }
        }
        return -1L
    }

    private fun getProjection(): Array<String> {
        return arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            DURATION,
            MediaStore.MediaColumns.SIZE,
            BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            ORIENTATION
        )
    }


    fun parUri(context: Context, cameraFile: File): Uri {
        val authority = context.packageName + ".luckProvider"
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(context, authority, cameraFile)
        } else {
            Uri.fromFile(cameraFile)
        }
    }


    fun getPath(ctx: Context, uri: Uri): String? {
        val context = ctx.applicationContext
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return if (SdkVersionUtils.isQ()) {
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            .toString() + "/" + split[1]
                    } else {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), ValueOf.toLong(id)
                )
                return getDataColumn(
                    context,
                    contentUri,
                    null,
                    null
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(
                    context,
                    contentUri!!,
                    selection,
                    selectionArgs
                )
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return ""
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(column))
            }
        } catch (ex: IllegalArgumentException) {
            SelectorLogUtils.info(
                String.format(
                    Locale.getDefault(),
                    "getDataColumn: _data - [%s]",
                    ex.message
                )
            )
        } finally {
            cursor?.close()
        }
        return ""
    }

    fun deleteUri(context: Context, uri: Uri) {
        try {
            context.contentResolver.delete(uri, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun remove(context: Context, id: Long) {
        try {
            val contentResolver = context.contentResolver
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Images.Media._ID + "=?"
            contentResolver.delete(uri, selection, arrayOf(id.toString()))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}