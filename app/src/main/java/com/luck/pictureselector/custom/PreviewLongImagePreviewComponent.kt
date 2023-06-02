package com.luck.pictureselector.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.luck.picture.lib.component.IBasePreviewComponent
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils
import com.luck.pictureselector.R

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：PreviewLongImageComponent
 */
class PreviewLongImagePreviewComponent : FrameLayout, IBasePreviewComponent {
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenAppInHeight = 0
    private lateinit var ivCover: ImageView
    private lateinit var longImage: SubsamplingScaleImageView

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
        inflate(context, R.layout.ps_custom_preview_image, this)
        ivCover = findViewById(R.id.iv_preview_cover)
        longImage = findViewById(R.id.preview_long_image)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {
        if (MediaUtils.isLongImage(media.width, media.height)) {
            ivCover.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).asBitmap().load(media.getAvailablePath())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        longImage.visibility = View.VISIBLE
                        val scale = kotlin.math.max(
                            screenWidth / resource.width.toFloat(),
                            screenHeight / resource.height.toFloat()
                        )
                        longImage.setImage(
                            ImageSource.cachedBitmap(resource),
                            ImageViewState(scale, PointF(0F, 0F), 0)
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }
                })

        } else {
            val size = getRealSizeFromMedia(media)
            val mediaComputeSize = BitmapUtils.getComputeImageSize(size[0], size[1])
            val width = mediaComputeSize[0]
            val height = mediaComputeSize[1]
            ivCover.scaleType = ImageView.ScaleType.FIT_CENTER
            if (width > 0 && height > 0) {
                config.imageEngine?.loadImage(
                    context,
                    media.getAvailablePath(),
                    width,
                    height,
                    ivCover
                )
            } else {
                config.imageEngine?.loadImage(context, media.getAvailablePath(), ivCover)
            }
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

    override fun getImageCover(): ImageView {
        return ivCover
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