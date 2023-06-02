package com.luck.picture.lib.adapter

import android.view.View
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.component.IBasePreviewComponent
import com.luck.picture.lib.component.IPreviewCoverComponent
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.component.MediaPlayerPreviewImpl

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewVideoHolder
 */
open class PreviewVideoHolder(itemView: View) : BasePreviewMediaHolder(itemView) {

    override fun createPreviewComponent(): IBasePreviewComponent {
        return MediaPlayerPreviewImpl(itemView.context)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        component.bindData(config, media)
        if (component is IPreviewCoverComponent) {
            val imageCover = component.getImageCover()
            imageCover.setOnClickListener {
                setClickEvent(media)
            }
            imageCover.setOnLongClickListener {
                setLongClickEvent(this, position, media)
                return@setOnLongClickListener false
            }
        }
    }

    override fun onViewAttachedToWindow() {
        component.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow() {
        component.onViewDetachedFromWindow()
    }

    override fun release() {
        component.release()
    }
}