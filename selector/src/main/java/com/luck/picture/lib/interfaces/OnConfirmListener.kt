package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig
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
     * @param result select result
     * @return the boolean result
     */
    fun onConfirm(config: SelectorConfig, result: MutableList<LocalMedia>): Boolean
}