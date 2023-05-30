package com.luck.pictureselector.custom

import android.view.LayoutInflater
import android.view.ViewGroup
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.pictureselector.R

/**
 * @author：luck
 * @date：2023/1/4 4:58 下午
 * @describe：Custom MediaPreviewAdapter
 */
class CustomMediaPreviewAdapter : MediaPreviewAdapter() {

    override fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BasePreviewMediaHolder {
        val itemView = inflater.inflate(R.layout.ps_custom_preview_image, parent, false)
        return CustomPreviewImageHolder(itemView)
    }

    override fun onCreateVideoViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BasePreviewMediaHolder {
        val itemView = inflater.inflate(R.layout.ps_custom_preview_video, parent, false)
        return CustomPreviewExoVideoHolder(itemView)
    }
}