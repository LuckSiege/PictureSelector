package com.luck.picture.lib.loader

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.IntRange
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import org.jetbrains.annotations.NotNull

const val DURATION: String = "duration"
const val BUCKET_DISPLAY_NAME = "bucket_display_name"
const val BUCKET_ID = "bucket_id"
const val ORIENTATION = "orientation"
const val NOT_GIF = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/gif')"
const val NOT_WEBP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/webp')"
const val NOT_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/bmp')"
const val NOT_XMS_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/x-ms-bmp')"
const val NOT_VND_WAP_BMP = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/vnd.wap.wbmp')"
const val NOT_HEIC = " AND (${MediaStore.MediaColumns.MIME_TYPE}!='image/heic')"
const val MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE

val PROJECTION = arrayOf(
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

abstract class MediaLoader {

    /**
     * Query the given URI, returning a Cursor over the result set with support for cancellation.
     */
    protected abstract fun getQueryUri(): Uri

    /**
     * Query the given column
     */
    protected abstract fun getProjection(): Array<String>?

    /**
     * A filter declaring which rows to return,
     * formatted as an SQL WHERE clause (excluding the WHERE itself).
     * Passing null will return all rows for the given URI.
     */
    protected abstract fun getAlbumSelection(): String?

    /**
     * A filter declaring which rows to return,
     * formatted as an SQL WHERE clause (excluding the WHERE itself).
     * Passing null will return all rows for the given URI.
     * @param bucketId Album ID
     */
    protected abstract fun getSelection(bucketId: Long): String?

    /**
     * You may include =? s in selection, which will be replaced by the values from selectionArgs,
     * in the order that they appear in the selection. The values will be bound as Strings.
     */
    protected abstract fun getSelectionArgs(): Array<String>?

    /**
     * How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     * Passing null will use the default sort order, which may be unordered.
     */
    protected abstract fun getSortOrder(): String?

    /**
     * Parsing data from Cursor and generating [LocalMedia] objects
     */
    protected abstract fun parse(media: LocalMedia, data: Cursor): LocalMedia?

    /**
     * Load all media album list
     */
    abstract suspend fun loadMediaAlbum(): MutableList<LocalMediaAlbum>

    /**
     * Load all media list
     * @param pageSize number page item count
     */
    abstract suspend fun loadMedia(
        @IntRange(
            from = 1,
            to = Long.MAX_VALUE
        ) pageSize: Int
    ): MutableList<LocalMedia>

    /**
     * Load all media list
     * @param bucketId media resources ID
     * @param pageSize number page item count
     */
    abstract suspend fun loadMedia(
        bucketId: Long,
        @IntRange(
            from = 1,
            to = Long.MAX_VALUE
        ) pageSize: Int
    ): MutableList<LocalMedia>

    /**
     * Query more multimedia data according to bucketId Use [MediaStore.MediaColumns.BUCKET_ID]
     * @param bucketId media resources ID
     * @param page number page
     * @param pageSize number page item count
     */
    abstract suspend fun loadMediaMore(
        bucketId: Long,
        @IntRange(from = 1, to = Long.MAX_VALUE) page: Int,
        @IntRange(from = 1, to = Long.MAX_VALUE) pageSize: Int
    ): MutableList<LocalMedia>

    /**
     * Load the specified folder resources in the app
     * @param sandboxDir Root directory where resources are located
     */
    abstract suspend fun loadAppInternalDir(@NotNull sandboxDir: String): MutableList<LocalMedia>
}