package com.luck.pictureselector.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils.formatDurationTime
import com.luck.picture.lib.utils.MediaUtils.hasMimeTypeOfAudio
import com.luck.picture.lib.utils.MediaUtils.hasMimeTypeOfVideo
import com.luck.picture.lib.utils.MediaUtils.isContent
import com.luck.pictureselector.R
import com.luck.pictureselector.listener.OnItemLongClickListener

/**
 * @author：luck
 * @date：2016-7-27 23:02
 * @describe：GridImageAdapter
 */
class GridImageAdapter(context: Context, result: MutableList<LocalMedia>) :
    RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mData = mutableListOf<LocalMedia>()
    var selectMax = 9

    fun getData(): MutableList<LocalMedia> {
        return mData
    }

    /**
     * 删除
     */
    fun delete(position: Int) {
        try {
            if (position != RecyclerView.NO_POSITION && mData.size > position) {
                mData.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, mData.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun remove(position: Int) {
        if (position < mData.size) {
            mData.removeAt(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mImg: ImageView = view.findViewById(R.id.fiv)
        var mIvDel: ImageView = view.findViewById(R.id.iv_del)
        var tvDuration: TextView = view.findViewById(R.id.tv_duration)

    }

    override fun getItemCount(): Int {
        return if (mData.size < selectMax) {
            mData.size + 1
        } else {
            mData.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem(position)) {
            TYPE_CAMERA
        } else {
            TYPE_PICTURE
        }
    }

    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.gv_filter_image, viewGroup, false)
        return ViewHolder(view)
    }

    private fun isShowAddItem(position: Int): Boolean {
        return position == mData.size
    }

    /**
     * 设置值
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //少于MaxSize张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.drawable.ic_add_image)
            viewHolder.mImg.setOnClickListener {
                if (mItemClickListener != null) {
                    mItemClickListener!!.openPicture()
                }
            }
            viewHolder.mIvDel.visibility = View.INVISIBLE
        } else {
            viewHolder.mIvDel.visibility = View.VISIBLE
            viewHolder.mIvDel.setOnClickListener {
                val index = viewHolder.absoluteAdapterPosition
                if (index != RecyclerView.NO_POSITION && mData.size > index) {
                    mData.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, mData.size)
                }
            }
            val media = mData[position]
            val path = media.getAvailablePath()
            val duration = media.duration
            viewHolder.tvDuration.visibility =
                if (hasMimeTypeOfVideo(media.mimeType)) View.VISIBLE else View.GONE
            if (hasMimeTypeOfAudio(media.mimeType)) {
                viewHolder.tvDuration.visibility = View.VISIBLE
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ps_ic_audio,
                    0,
                    0,
                    0
                )
                viewHolder.mImg.setImageResource(R.drawable.ps_audio_placeholder)
            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ps_ic_video,
                    0,
                    0,
                    0
                )
                Glide.with(viewHolder.itemView.context)
                    .load(
                        if (isContent(path!!) && !media.isCrop() && !media.isCompress()) Uri.parse(
                            path
                        ) else path
                    )
                    .centerCrop()
                    .placeholder(R.color.app_color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg)
            }
            viewHolder.tvDuration.text = formatDurationTime(duration)
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener { v: View? ->
                    val adapterPosition = viewHolder.absoluteAdapterPosition
                    mItemClickListener!!.onItemClick(v, adapterPosition)
                }
            }
            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener { v: View? ->
                    val adapterPosition = viewHolder.absoluteAdapterPosition
                    mItemLongClickListener!!.onItemLongClick(viewHolder, adapterPosition, v)
                    true
                }
            }
        }
    }

    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    interface OnItemClickListener {
        /**
         * Item click event
         *
         * @param v
         * @param position
         */
        fun onItemClick(v: View?, position: Int)

        /**
         * Open PictureSelector
         */
        fun openPicture()
    }

    private var mItemLongClickListener: OnItemLongClickListener? = null

    fun setItemLongClickListener(l: OnItemLongClickListener?) {
        mItemLongClickListener = l
    }

    companion object {
        const val TAG = "PictureSelector"
        const val TYPE_CAMERA = 1
        const val TYPE_PICTURE = 2
    }

    init {
        mData.addAll(result)
    }
}