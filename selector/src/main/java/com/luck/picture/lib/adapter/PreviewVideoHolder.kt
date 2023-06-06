package com.luck.picture.lib.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.player.DefaultMediaPlayer
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewVideoHolder
 */
open class PreviewVideoHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    var pbLoading: ProgressBar = itemView.findViewById(R.id.pb_loading)
    var ivPlay: ImageView = itemView.findViewById(R.id.iv_play)
    var mediaPlayer: IMediaPlayer = this.onCreateVideoComponent()
    var isPlayed = false

    /**
     * Create custom player components
     */
    open fun onCreateVideoComponent(): IMediaPlayer {
        return DefaultMediaPlayer(itemView.context)
    }

    init {
        (itemView as ViewGroup).addView(mediaPlayer as View, 0)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        ivPlay.visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
        ivPlay.setOnClickListener {
            dispatchPlay(media.getAvailablePath()!!, media.displayName)
        }
        itemView.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay(media.getAvailablePath()!!, media.displayName)
            } else {
                setClickEvent(media)
            }
        }
    }

    open fun dispatchPlay(path: String, displayName: String?) {
        if (isPlayed && config.isPauseResumePlay) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                ivPlay.visibility = View.VISIBLE
                setPreviewVideoTitle(null)
            } else {
                mediaPlayer.resume()
                ivPlay.visibility = View.GONE
                setPreviewVideoTitle(displayName)
            }
        } else {
            onPlayingLoading()
            setPreviewVideoTitle(displayName)
            mediaPlayer.setDataSource(itemView.context, path, config.isLoopAutoPlay)
            isPlayed = true
        }
    }

    override fun loadCover(media: LocalMedia) {
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
    }

    override fun coverScaleType(media: LocalMedia) {
        if (MediaUtils.isLongImage(media.width, media.height)) {
            imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    override fun coverLayoutParams(media: LocalMedia) {
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.width > 0 && media.height > 0) {
                (imageCover.layoutParams as FrameLayout.LayoutParams).apply {
                    this.width = screenWidth
                    this.height = screenAppInHeight
                    this.gravity = Gravity.CENTER
                }
            }
        }
    }

    open fun onPlayingLoading() {
        pbLoading.visibility = View.VISIBLE
        ivPlay.visibility = View.GONE
    }

    open fun onPlayingVideoState() {
        imageCover.visibility = View.GONE
        ivPlay.visibility = View.GONE
        pbLoading.visibility = View.GONE
    }

    open fun onDefaultVideoState() {
        setPreviewVideoTitle(null)
        imageCover.visibility = View.VISIBLE
        ivPlay.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
        isPlayed = false
    }

    override fun onViewAttachedToWindow() {
        mediaPlayer.initMediaPlayer()
        mediaPlayer.setOnVideoSizeChangedListener(object : IMediaPlayer.OnVideoSizeChangedListener {
            override fun onVideoSizeChanged(mp: IMediaPlayer?, width: Int, height: Int) {

            }
        })
        mediaPlayer.setOnInfoListener(object : IMediaPlayer.OnInfoListener {
            override fun onInfo(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
                return false
            }

        })
        mediaPlayer.setOnPreparedListener(object : IMediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: IMediaPlayer?) {
                mp?.start()
                onPlayingVideoState()
            }
        })
        mediaPlayer.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: IMediaPlayer?) {
                mp?.stop()
                mp?.reset()
                onDefaultVideoState()
            }
        })
        mediaPlayer.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
                onDefaultVideoState()
                return false
            }
        })
    }

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        mediaPlayer.release()
        mediaPlayer.setOnInfoListener(null)
        mediaPlayer.setOnErrorListener(null)
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.setOnVideoSizeChangedListener(null)
        onDefaultVideoState()
    }
}