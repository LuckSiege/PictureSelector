package com.luck.picture.lib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BaseListViewHolder
import com.luck.picture.lib.adapter.base.BaseMediaListAdapter
import com.luck.picture.lib.config.LayoutSource

/**
 * @author：luck
 * @date：2022/12/1 1:18 下午
 * @describe：MediaListAdapter
 */
open class MediaListAdapter : BaseMediaListAdapter() {

    override fun onCreateCameraViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BaseListViewHolder {
        val resource = config.layoutSource[LayoutSource.ADAPTER_ITEM_CAMERA]
            ?: R.layout.ps_item_grid_camera
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(CameraViewHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    override fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder {
        val resource =
            config.layoutSource[LayoutSource.ADAPTER_ITEM_IMAGE] ?: R.layout.ps_item_grid_image
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(ImageViewHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    override fun onCreateVideoViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder {
        val resource =
            config.layoutSource[LayoutSource.ADAPTER_ITEM_VIDEO] ?: R.layout.ps_item_grid_video
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(VideoViewHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    override fun onCreateAudioViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListMediaViewHolder {
        val resource =
            config.layoutSource[LayoutSource.ADAPTER_ITEM_AUDIO] ?: R.layout.ps_item_grid_audio
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(AudioViewHolder::class.java)
        return holderFactory.create(clz, itemView)
    }
}