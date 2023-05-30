package com.luck.picture.lib.interfaces

import android.view.View
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2023/1/13 11:40 上午
 * @describe：OnMediaItemClickListener
 */
interface OnMediaItemClickListener {
    fun openCamera()

    fun onItemClick(selectedView: View, position: Int, media: LocalMedia)

    fun onItemLongClick(itemView: View, position: Int, media: LocalMedia)

    fun onSelected(isSelected: Boolean, position: Int, media: LocalMedia): Int

    fun onComplete(isSelected: Boolean, position: Int, media: LocalMedia)
}