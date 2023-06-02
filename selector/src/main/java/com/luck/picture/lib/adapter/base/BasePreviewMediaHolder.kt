package com.luck.picture.lib.adapter.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.component.IBasePreviewComponent
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
    val config = SelectorProviders.getInstance().getSelectorConfig()
    var screenWidth = DensityUtil.getRealScreenWidth(itemView.context)
    var screenHeight = DensityUtil.getScreenHeight(itemView.context)
    var screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.context)
    val component = this.createPreviewComponent()

    init {
        (itemView as ViewGroup).addView(component as View)
    }

    open fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if (media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

    /**
     * onViewAttachedToWindow
     */
    abstract fun onViewAttachedToWindow()

    /**
     * onViewDetachedFromWindow
     */
    abstract fun onViewDetachedFromWindow()

    /**
     * Create preview component，Can be Used to implement a custom player or long image viewer
     */
    abstract fun createPreviewComponent(): IBasePreviewComponent

    /**
     * bind data
     */
    abstract fun bindData(media: LocalMedia, position: Int)

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
}