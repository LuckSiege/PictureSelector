package com.luck.picture.lib.constant


object CropWrap {
    /**
     * Compatible with uCrop
     */
    private const val UCROP_PREFIX = "com.yalantis.ucrop"
    const val CROP_OUTPUT_URI = "${UCROP_PREFIX}.OutputUri"
    const val CROP_IMAGE_WIDTH: String = "$UCROP_PREFIX.ImageWidth"
    const val CROP_IMAGE_HEIGHT: String = "$UCROP_PREFIX.ImageHeight"
    const val CROP_OFFSET_X: String = "$UCROP_PREFIX.OffsetX"
    const val CROP_OFFSET_Y: String = "$UCROP_PREFIX.OffsetY"
    const val CROP_ASPECT_RATIO: String = "$UCROP_PREFIX.CropAspectRatio"

    /**
     * Use PictureSelector default
     */
    const val DEFAULT_CROP_OUTPUT_PATH = "outPutPath"
    const val DEFAULT_CROP_IMAGE_WIDTH = "imageWidth"
    const val DEFAULT_CROP_IMAGE_HEIGHT = "imageHeight"
    const val DEFAULT_CROP_OFFSET_X = "offsetX"
    const val DEFAULT_CROP_OFFSET_Y = "offsetY"
    const val DEFAULT_CROP_ASPECT_RATIO = "aspectRatio"
    const val DEFAULT_EXTRA_DATA = "extraData"
}