package com.luck.picture.lib.component

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：PreviewImagePreviewImpl
 */
class PreviewImagePreviewImpl : FrameLayout, IBasePreviewComponent {
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenAppInHeight = 0
    private lateinit var ivCover: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        screenWidth = DensityUtil.getRealScreenWidth(context)
        screenHeight = DensityUtil.getScreenHeight(context)
        screenAppInHeight = DensityUtil.getRealScreenHeight(context)
        inflate(context, R.layout.ps_preview_image_component, this)
        ivCover = findViewById(R.id.iv_preview_cover)
    }

    override fun getImageCover(): ImageView {
        return ivCover
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {
        val size = getRealSizeFromMedia(media)
        val mediaComputeSize = BitmapUtils.getComputeImageSize(size[0], size[1])
        val width = mediaComputeSize[0]
        val height = mediaComputeSize[1]
        if (width > 0 && height > 0) {
            config.imageEngine?.loadImage(context, media.getAvailablePath(), width, height, ivCover)
        } else {
            config.imageEngine?.loadImage(context, media.getAvailablePath(), ivCover)
        }
        if (MediaUtils.isLongImage(media.width, media.height)) {
            ivCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            ivCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.width > 0 && media.height > 0) {
                (ivCover.layoutParams as LayoutParams).apply {
                    this.width = screenWidth
                    this.height = screenAppInHeight
                    this.gravity = Gravity.CENTER
                }
            }
        }
    }

    private fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if (media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

    override fun onViewAttachedToWindow() {

    }

    override fun onViewDetachedFromWindow() {

    }

    override fun release() {

    }
}