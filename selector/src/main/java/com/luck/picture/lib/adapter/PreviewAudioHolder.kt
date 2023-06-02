package com.luck.picture.lib.adapter

import android.view.View
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.component.IBasePreviewComponent
import com.luck.picture.lib.component.MediaAudioPlayerPreviewImpl
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewAudioHolder
 */
open class PreviewAudioHolder(itemView: View) : BasePreviewMediaHolder(itemView) {

    override fun onViewAttachedToWindow() {
        component.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow() {
        component.onViewDetachedFromWindow()
    }

    override fun createPreviewComponent(): IBasePreviewComponent {
        return MediaAudioPlayerPreviewImpl(itemView.context)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        component.bindData(config, media)
        itemView.setOnClickListener {
            setClickEvent(media)
        }
        itemView.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    override fun release() {
        component.release()
    }

}