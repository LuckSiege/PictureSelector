package com.luck.picture.lib.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnPlayerListener
import com.luck.picture.lib.player.Controller
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.player.MediaPlayerComponent
import com.luck.picture.lib.player.VideoController

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewVideoHolder
 */
open class PreviewVideoHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    val mediaPlayer: IMediaPlayer = this.onCreatePlayerComponent()
    val videoController: Controller = this.onCreatePlayerController()
    private var isPlayed = false

    init {
        if (itemView is ViewGroup) {
            itemView.addView(mediaPlayer as View, 0)
            itemView.addView(videoController as View)
        }
    }

    /**
     * Creating Player Components，User can override to achieve customization
     */
    open fun onCreatePlayerComponent(): IMediaPlayer {
        return MediaPlayerComponent(itemView.context).apply {
            this.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    /**
     * Creating a player controller，User can override to achieve customization
     */
    open fun onCreatePlayerController(): Controller {
        return VideoController(itemView.context)
    }

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        val videoPlay = videoController.getVideoPlay()
        videoPlay.visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
        videoPlay.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay()
            } else {
                startPlay()
            }
        }
        itemView.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay()
            } else {
                setClickEvent(media)
            }
        }
    }

    private fun dispatchPlay() {
        if (isPlayed) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.onPause()
            } else {
                mediaPlayer.onResume()
            }
        } else {
            startPlay()
        }
    }

    private fun startPlay() {
        isPlayed = true
        mediaPlayer.onStart(media.getAvailablePath()!!, config.isLoopAutoPlay)
    }

    private val playerListener: OnPlayerListener = object : OnPlayerListener {

        override fun onPlayerError() {
            onDefaultVideoState()
        }

        override fun onPlayerReady() {
            onPlayingVideoState()
        }

        override fun onPlayerLoading() {
            videoController.getVideoLoading().visibility = View.VISIBLE
            videoController.getVideoPlay().visibility = View.GONE
        }

        override fun onPlayerComplete() {
            onDefaultVideoState()
        }
    }

    override fun onViewAttachedToWindow() {
        mediaPlayer.initMediaPlayer(playerListener)
    }

    override fun onViewDetachedFromWindow() {
        release()
        onDefaultVideoState()
    }

    open fun onPlayingVideoState() {
        ivCover.visibility = View.GONE
        videoController.getVideoPlay().visibility = View.GONE
        videoController.getVideoLoading().visibility = View.GONE
        setPreviewVideoTitle(media.displayName)
    }

    open fun onDefaultVideoState() {
        ivCover.visibility = View.VISIBLE
        videoController.getVideoPlay().visibility = View.VISIBLE
        videoController.getVideoLoading().visibility = View.GONE
        setPreviewVideoTitle(null)
    }

    override fun setScaleDisplaySize(media: LocalMedia) {
        super.setScaleDisplaySize(media)
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            ((mediaPlayer as View).layoutParams as FrameLayout.LayoutParams).apply {
                this.width = screenWidth
                this.height = screenAppInHeight
                this.gravity = Gravity.CENTER
            }
        }
    }

    override fun release() {
        mediaPlayer.destroy()
    }
}