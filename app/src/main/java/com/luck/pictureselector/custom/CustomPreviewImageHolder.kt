package com.luck.pictureselector.custom

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.luck.picture.lib.adapter.PreviewImageHolder
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import com.luck.pictureselector.R

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewImageHolder
 */
class CustomPreviewImageHolder(itemView: View) : PreviewImageHolder(itemView) {
    private val longImageView: SubsamplingScaleImageView =
        itemView.findViewById(R.id.preview_long_image)

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        longImageView.setOnClickListener {
            setClickEvent(media)
        }
        longImageView.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    override fun loadCover(media: LocalMedia) {
        if (MediaUtils.isLongImage(media.width, media.height)) {
            imageCover.alpha = 0F
            longImageView.visibility = View.VISIBLE
            Glide.with(imageCover.context).asBitmap().load(media.getAvailablePath()!!)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val scale =
                            (screenWidth / resource.width.toFloat()).coerceAtLeast(screenHeight / resource.height.toFloat())
                        longImageView.setImage(
                            ImageSource.cachedBitmap(resource),
                            ImageViewState(scale, PointF(0F, 0F), 0)
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        imageCover.alpha = 1F
                        longImageView.visibility = View.GONE
                    }
                })
        } else {
            imageCover.alpha = 1F
            longImageView.visibility = View.GONE
            super.loadCover(media)
        }
    }
}