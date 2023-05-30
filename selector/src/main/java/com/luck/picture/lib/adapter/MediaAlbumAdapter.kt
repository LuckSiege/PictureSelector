package com.luck.picture.lib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2022/12/13 11:35 上午
 * @describe：MediaAlbumAdapter
 */
open class MediaAlbumAdapter(var config: SelectorConfig) :
    RecyclerView.Adapter<MediaAlbumAdapter.ViewHolder>() {
    private var albumList = mutableListOf<LocalMediaAlbum>()
    private var albumMap = mutableMapOf<Long, LocalMediaAlbum>()
    private var lastSelectPosition = 0

    fun setAlbumList(albumList: MutableList<LocalMediaAlbum>) {
        this.albumList.addAll(albumList)
        this.albumList.forEach { mediaAlbum ->
            this.albumMap[mediaAlbum.bucketId] = mediaAlbum
        }
        this.notifyItemRangeChanged(0, this.albumList.size)
    }

    fun getAlbumList(): MutableList<LocalMediaAlbum> {
        return albumList
    }

    fun getAlbum(bucketId: Long): LocalMediaAlbum? {
        return albumMap[bucketId]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val resource = config.layoutSource[LayoutSource.ALBUM_WINDOW_ITEM] ?: R.layout.ps_album_item
        val itemView =
            LayoutInflater.from(parent.context).inflate(resource, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaAlbum = albumList[position]
        holder.itemView.isSelected = mediaAlbum.isSelected
        holder.tvAlbumName.text = holder.itemView.context.getString(
            R.string.ps_camera_roll_num,
            mediaAlbum.bucketDisplayName,
            mediaAlbum.totalCount
        )
        if (MediaUtils.hasMimeTypeOfAudio(mediaAlbum.bucketDisplayMimeType)) {
            holder.ivFirstCover.setImageResource(R.drawable.ps_audio_placeholder)
        } else {
            config.imageEngine?.loadAlbumCover(
                holder.itemView.context,
                mediaAlbum.bucketDisplayCover,
                holder.ivFirstCover
            )
        }
        holder.tvSelectTag.visibility = if (mediaAlbum.isSelectedTag) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (itemCount > lastSelectPosition) {
                albumList[lastSelectPosition].isSelected = false
            }
            notifyItemChanged(lastSelectPosition)
            mediaAlbum.isSelected = true
            lastSelectPosition = position
            notifyItemChanged(lastSelectPosition)
            mItemClickListener?.onItemClick(position, mediaAlbum)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFirstCover: ImageView = itemView.findViewById(R.id.ps_iv_first_cover)
        var tvSelectTag: TextView = itemView.findViewById(R.id.ps_tv_select_tag)
        var tvAlbumName: TextView = itemView.findViewById(R.id.ps_tv_album_name)
    }

    private var mItemClickListener: OnItemClickListener<LocalMediaAlbum>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<LocalMediaAlbum>?) {
        this.mItemClickListener = listener
    }

}