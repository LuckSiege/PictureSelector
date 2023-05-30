package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/12/2 6:02 下午
 * @describe：OnCustomPreviewListener
 */
interface OnCustomPreviewListener {
    fun onPreview(
        context: Context,
        page: Int,
        position: Int,
        totalCount: Int,
        list: MutableList<LocalMedia>,
        isBottomPreview: Boolean,
    )
}