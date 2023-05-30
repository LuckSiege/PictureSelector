package com.luck.picture.lib.engine

import android.content.Context
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：Media Data Converter
 */
interface MediaConverterEngine {
    /**
     * Media Data Converter
     * 1、Android 10 platform, sandbox processing of external directory files [LocalMedia.sandboxPath]
     * 2、Android 10 platform, original image processing [LocalMedia.originalPath]
     * 3、Video thumbnail [LocalMedia.videoThumbnailPath]
     * 4、Image watermark [LocalMedia.watermarkPath]
     * 5、Image or video compression [LocalMedia.compressPath]
     * ...
     * Customize Other Actions [LocalMedia.customizeExtra]
     */
    suspend fun converter(context: Context, media: LocalMedia): LocalMedia
}