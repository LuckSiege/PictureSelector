package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/3/12 9:00 下午
 * @describe：OnConfirmListener
 */
interface OnConfirmListener {
    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param context
     * @param result select result
     * @return the boolean result
     */
    fun onConfirm(context: Context, result: MutableList<LocalMedia>): Boolean
}