package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2022/8/12 7:19 下午
 * @describe：Use custom file name
 */
interface OnReplaceFileNameListener {
    /**
     * Users can customize file names
     */
    fun apply(fileName: String): String?
}