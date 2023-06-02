package com.luck.picture.lib.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DensityUtil

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：PreviewImagePreviewImpl
 */
class PreviewImagePreviewImpl : FrameLayout, IBasePreviewComponent {

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
        inflate(context, R.layout.ps_preview_image_component, this)
        ivCover = findViewById(R.id.iv_preview_cover)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {

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