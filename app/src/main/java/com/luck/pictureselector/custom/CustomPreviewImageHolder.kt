package com.luck.pictureselector.custom

import android.view.View
import com.luck.picture.lib.adapter.PreviewImageHolder
import com.luck.picture.lib.component.IBasePreviewComponent

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewImageHolder
 */
class CustomPreviewImageHolder(itemView: View) : PreviewImageHolder(itemView) {

    override fun createPreviewComponent(): IBasePreviewComponent {
        return PreviewLongImagePreviewComponent(itemView.context)
    }
}