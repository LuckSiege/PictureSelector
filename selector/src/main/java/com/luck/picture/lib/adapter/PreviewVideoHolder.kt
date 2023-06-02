package com.luck.picture.lib.adapter

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.component.IBasePreviewComponent
import com.luck.picture.lib.component.IMediaPlayer
import com.luck.picture.lib.component.IPlayerController
import com.luck.picture.lib.component.MediaPlayerPreviewImpl
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewVideoHolder
 */
open class PreviewVideoHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    private var isPlayed = false
    private lateinit var controller: IPlayerController
    override fun createPreviewComponent(): IBasePreviewComponent {
        return MediaPlayerPreviewImpl(itemView.context)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        component.bindData(config, media)
        controller = (component as IMediaPlayer).getController()
        val viewPlay = controller.getViewPlay()
        val imageCover = component.getImageCover()
        viewPlay.visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
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
        viewPlay.setOnClickListener {
            dispatchPlay(media.getAvailablePath()!!)
        }
        itemView.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay(media.getAvailablePath()!!)
            } else {
                setClickEvent(media)
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

    private fun dispatchPlay(path: String) {
        val mediaPlayer = component as IMediaPlayer
        if (isPlayed && config.isPauseResumePlay) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.onPause()
            } else {
                mediaPlayer.onResume()
            }
        } else {
            mediaPlayer.onStart(path, config.isLoopAutoPlay)
            isPlayed = true
        }
    }

    override fun onViewAttachedToWindow() {
        component.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow() {
        isPlayed = false
        component.onViewDetachedFromWindow()
    }

    override fun release() {
        component.release()
    }
}