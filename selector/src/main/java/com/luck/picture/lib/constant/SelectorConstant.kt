package com.luck.picture.lib.constant

/**
 * @author：luck
 * @date：2023-4-17 22:30
 * @describe：SelectorConstant
 */
object SelectorConstant {
    const val UNSET = -1
    const val UNKNOWN = -Int.MAX_VALUE
    const val DEFAULT_ALL_BUCKET_ID = -1L
    const val DEFAULT_DIR_BUCKET_ID = -2L
    const val DEFAULT_MAX_PAGE_SIZE = 60
    const val DEFAULT_GRID_ITEM_COUNT = 4

    const val DEFAULT_MAX_SELECT_NUM = 9
    const val CHOOSE_REQUEST = 188
    const val REQUEST_CAMERA = 10000
    const val REQUEST_CROP = 10001
    const val REQUEST_EDITOR_CROP = 10002
    const val REQUEST_GO_SETTING = 10003
    const val INVALID_DATA = -10000L

    const val KEY_EXTRA_RESULT = "extra_result_media"
    const val QUICK_CAPTURE = "android.intent.extra.quickCapture"
}