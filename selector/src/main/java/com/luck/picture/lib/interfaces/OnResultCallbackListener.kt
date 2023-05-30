package com.luck.picture.lib.interfaces

import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：onResult Callback Listener
 */
interface OnResultCallbackListener {
    /**
     * return LocalMedia result
     *
     * @param result
     */
    fun onResult(result: List<LocalMedia>)

    /**
     * Cancel
     */
    fun onCancel()
}