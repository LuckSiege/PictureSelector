package com.luck.picture.lib.adapter.base

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnLongClickListener
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.DensityUtil

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：BasePreviewMediaHolder
 */
abstract class BasePreviewMediaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val config = SelectorProviders.getInstance().getConfig()
    var screenWidth = DensityUtil.getRealScreenWidth(itemView.context)
    var screenHeight = DensityUtil.getScreenHeight(itemView.context)
    var screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.context)
    val imageCover: ImageView = itemView.findViewById(R.id.iv_preview_cover)

    open fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if ((media.isCrop() || media.isEditor()) && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

    abstract fun loadCover(media: LocalMedia)

    abstract fun coverScaleType(media: LocalMedia)

    abstract fun coverLayoutParams(media: LocalMedia)

    /**
     * onViewAttachedToWindow
     */
    abstract fun onViewAttachedToWindow()

    /**
     * onViewDetachedFromWindow
     */
    abstract fun onViewDetachedFromWindow()

    /**
     * bind data
     */
    open fun bindData(media: LocalMedia, position: Int) {
        loadCover(media)
        coverScaleType(media)
        coverLayoutParams(media)
        imageCover.setOnClickListener {
            setClickEvent(media)
        }
        imageCover.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    /**
     * release
     */
    abstract fun release()

    /**
     * Item click
     */
    private var onClickListener: MediaPreviewAdapter.OnClickListener? = null
    fun setOnClickListener(l: MediaPreviewAdapter.OnClickListener?) {
        this.onClickListener = l
    }

    open fun setClickEvent(media: LocalMedia) {
        onClickListener?.onClick(media)
    }

    /**
     * Item Long press click
     */
    private var onLongClickListener: OnLongClickListener<LocalMedia>? = null
    fun setOnLongClickListener(l: OnLongClickListener<LocalMedia>?) {
        this.onLongClickListener = l
    }

    open fun setLongClickEvent(holder: RecyclerView.ViewHolder, position: Int, media: LocalMedia) {
        onLongClickListener?.onLongClick(holder, position, media)
    }

    /**
     * Item Title Content Switching
     */
    private var onTitleChangeListener: MediaPreviewAdapter.OnTitleChangeListener? = null

    fun setOnTitleChangeListener(l: MediaPreviewAdapter.OnTitleChangeListener?) {
        this.onTitleChangeListener = l
    }

    open fun setPreviewVideoTitle(title: String?) {
        onTitleChangeListener?.onTitle(title)
    }
}