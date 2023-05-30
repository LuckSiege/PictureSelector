package com.luck.picture.lib.adapter.base

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnLongClickListener
import com.luck.picture.lib.photoview.PhotoView
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil.getRealScreenHeight
import com.luck.picture.lib.utils.DensityUtil.getRealScreenWidth
import com.luck.picture.lib.utils.DensityUtil.getScreenHeight
import com.luck.picture.lib.utils.MediaUtils.isLongImage

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：BasePreviewMediaHolder
 */
open class BasePreviewMediaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val config: SelectorConfig = SelectorProviders.getInstance().getSelectorConfig()
    val screenWidth = getRealScreenWidth(itemView.context)
    val screenHeight = getScreenHeight(itemView.context)
    val screenAppInHeight = getRealScreenHeight(itemView.context)
    var ivCover: PhotoView = itemView.findViewById(R.id.iv_preview_cover)
    lateinit var media: LocalMedia

    /**
     * bind data
     */
    open fun bindData(media: LocalMedia, position: Int) {
        this.media = media
        loadCover(media)
        setCoverScaleType(media)
        setScaleDisplaySize(media)
        ivCover.setOnViewTapListener { view, x, y -> setClickEvent(media) }
        ivCover.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            false
        }
    }

    open fun loadCover(media: LocalMedia) {
        val realSizeFromMedia = getRealSizeFromMedia(media)
        val mediaSize = BitmapUtils.getComputeImageSize(realSizeFromMedia[0], realSizeFromMedia[1])
        val width = mediaSize[0]
        val height = mediaSize[1]
        if (width > 0 && height > 0) {
            config.imageEngine?.loadImage(
                itemView.context, media.getAvailablePath(), width, height, ivCover
            )
        } else {
            config.imageEngine?.loadImage(itemView.context, media.getAvailablePath(), ivCover)
        }
    }

    open fun setCoverScaleType(media: LocalMedia) {
        if (isLongImage(media.width, media.height)) {
            ivCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            ivCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    open fun setScaleDisplaySize(media: LocalMedia) {
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.width > 0 && media.height > 0) {
                ivCover.layoutParams?.apply {
                    width = screenWidth
                    height = screenAppInHeight
                    when (this) {
                        is FrameLayout.LayoutParams -> {
                            gravity = Gravity.CENTER
                        }
                        is RelativeLayout.LayoutParams -> {
                            addRule(RelativeLayout.CENTER_IN_PARENT)
                        }
                        is ConstraintLayout.LayoutParams -> {
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                    }
                }
            }
        }
    }

    open fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if (media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

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

    /**
     * onViewAttachedToWindow
     */
    open fun onViewAttachedToWindow() {}

    /**
     * onViewDetachedFromWindow
     */
    open fun onViewDetachedFromWindow() {}

    /**
     * release
     */
    open fun release() {

    }
}