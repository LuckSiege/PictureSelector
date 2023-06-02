package com.luck.pictureselector.custom

import android.view.View
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.component.IBasePreviewComponent

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：CustomPreviewIjkVideoHolder
 */
class CustomPreviewIjkVideoHolder(itemView: View) : PreviewVideoHolder(itemView) {

    override fun createPreviewComponent(): IBasePreviewComponent {
        return IjkPlayerPreviewImpl(itemView.context)
    }
}