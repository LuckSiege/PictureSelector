package com.luck.picture.lib.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.luck.picture.lib.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2022/11/30 3:33 下午
 * @describe：ImageViewHolder
 */
open class ImageViewHolder(itemView: View) : ListMediaViewHolder(itemView) {
    private var tvMediaTag: TextView = itemView.findViewById(R.id.tv_media_tag)
    private var ivEditor: ImageView = itemView.findViewById(R.id.iv_editor)

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        onMergeEditor(media)
        if (MediaUtils.isHasGif(media.mimeType) || MediaUtils.isUrlHasGif(media.getAvailablePath())) {
            tvMediaTag.text = itemView.context.getString(R.string.ps_gif_tag)
            tvMediaTag.visibility = View.VISIBLE
        } else if (MediaUtils.isLongImage(media.width, media.height)) {
            tvMediaTag.text = itemView.context.getString(R.string.ps_long_chart)
            tvMediaTag.visibility = View.VISIBLE
        } else if (MediaUtils.isHasWebp(media.mimeType) || MediaUtils.isUrlHasWebp(media.getAvailablePath())) {
            tvMediaTag.text = itemView.context.getString(R.string.ps_webp_tag)
            tvMediaTag.visibility = View.VISIBLE
        } else {
            tvMediaTag.visibility = View.GONE
        }
    }

    open fun onMergeEditor(media: LocalMedia) {
        val selectResult = mGetSelectResultListener?.onSelectResult()
        if (selectResult != null && selectResult.isNotEmpty()) {
            if (!media.isEditor()) {
                val position = selectResult.indexOf(media)
                if (position >= 0) {
                    val existsMedia = selectResult[position]
                    if (existsMedia.isEditor()) {
                        media.editorPath = existsMedia.editorPath
                        media.editorData = existsMedia.editorData
                    }
                }
            }
        }
        ivEditor.visibility = if (media.isEditor()) View.VISIBLE else View.GONE
    }
}