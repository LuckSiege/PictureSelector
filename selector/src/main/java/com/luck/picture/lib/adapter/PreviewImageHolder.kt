package com.luck.picture.lib.adapter

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.component.IBasePreviewComponent
import com.luck.picture.lib.component.PreviewImagePreviewImpl
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewImageHolder
 */
open class PreviewImageHolder(itemView: View) : BasePreviewMediaHolder(itemView) {

    override fun createPreviewComponent(): IBasePreviewComponent {
        return PreviewImagePreviewImpl(itemView.context)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        component.bindData(config, media)
        val imageCover = component.getImageCover()
        val size = getRealSizeFromMedia(media)
        val mediaComputeSize = BitmapUtils.getComputeImageSize(size[0], size[1])
        val width = mediaComputeSize[0]
        val height = mediaComputeSize[1]
        if (width > 0 && height > 0) {
            config.imageEngine?.loadImage(
                itemView.context,
                media.getAvailablePath(),
                width,
                height,
                imageCover
            )
        } else {
            config.imageEngine?.loadImage(itemView.context, media.getAvailablePath(), imageCover)
        }
        if (MediaUtils.isLongImage(media.width, media.height)) {
            imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.width > 0 && media.height > 0) {
                (imageCover.layoutParams as FrameLayout.LayoutParams).apply {
                    this.width = screenWidth
                    this.height = screenAppInHeight
                    this.gravity = Gravity.CENTER
                }
            }
        }
        imageCover.setOnClickListener {
            setClickEvent(media)
        }
        imageCover.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    override fun onViewAttachedToWindow() {
        component.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow() {
        component.onViewDetachedFromWindow()
    }

    override fun release() {
        component.release()
    }
}