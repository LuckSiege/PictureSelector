package com.luck.pictureselector.custom

import android.view.View
import android.widget.FrameLayout
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.player.IMediaPlayer

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：CustomPreviewIjkVideoHolder
 */
class CustomPreviewIjkVideoHolder(itemView: View) : PreviewVideoHolder(itemView) {

    override fun onCreatePlayerComponent(): IMediaPlayer {
        return IjkPlayerComponent(itemView.context).apply {
            this.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }
}