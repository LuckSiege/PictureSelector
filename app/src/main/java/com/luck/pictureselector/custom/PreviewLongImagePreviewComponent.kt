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
            ivCover.visibility = View.GONE
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
            ivCover.visibility = View.VISIBLE
        }
    }

    override fun getImageCover(): ImageView {
        return ivCover
    }

    override fun onViewAttachedToWindow() {

    }

    override fun onViewDetachedFromWindow() {

    }

    override fun release() {

    }
}