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
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import com.luck.pictureselector.R
import kotlin.math.max

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewImageHolder
 */
class CustomPreviewImageHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    private var longImage: SubsamplingScaleImageView =
        itemView.findViewById(R.id.preview_long_image)

    override fun loadCover(media: LocalMedia) {
        if (MediaUtils.isLongImage(media.width, media.height)) {
            Glide.with(itemView.context).asBitmap().load(media.getAvailablePath())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        longImage.visibility = View.VISIBLE
                        val scale = max(
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
            super.loadCover(media)
        }
    }
}