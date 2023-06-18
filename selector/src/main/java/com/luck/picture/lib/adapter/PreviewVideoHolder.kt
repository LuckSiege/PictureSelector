package com.luck.picture.lib.adapter

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.player.AbsController
import com.luck.picture.lib.player.DefaultMediaPlayer
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.player.VideoController
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewVideoHolder
 */
open class PreviewVideoHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    var pbLoading: ProgressBar = itemView.findViewById(R.id.pb_loading)
    var ivPlay: ImageView = itemView.findViewById(R.id.iv_play)
    var mediaPlayer = this.onCreateVideoComponent()
    var controller = this.onCreateVideoController()
    private val handler = Handler(Looper.getMainLooper())
    var isPlayed = false

    /**
     * Create custom player components
     */
    open fun onCreateVideoComponent(): IMediaPlayer {
        return DefaultMediaPlayer(itemView.context)
    }

    /**
     * Create custom player controller
     */
    open fun onCreateVideoController(): AbsController? {
        return VideoController(itemView.context).apply {
            this.setBackgroundResource(R.drawable.ps_video_controller_bg)
            this.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                this.bottomMargin = DensityUtil.dip2px(itemView.context, 48f)
                this.leftMargin = DensityUtil.dip2px(itemView.context, 15f)
                this.rightMargin = DensityUtil.dip2px(itemView.context, 15f)
                this.gravity = Gravity.BOTTOM
            }
        }
    }

    init {
        this.attachComponent(itemView as ViewGroup)
    }

    open fun attachComponent(group: ViewGroup) {
        group.addView(mediaPlayer as View, 0)
        controller?.let {
            group.addView(controller as View)
        }
    }

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        controller?.let { controller ->
            (controller as View).alpha = 0F
            controller.setDataSource(media)
            controller.setIMediaPlayer(mediaPlayer)
            controller.setOnPlayStateListener(playStateListener)
            controller.setOnSeekBarChangeListener(seekBarChangeListener)
        }
        ivPlay.visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
        ivPlay.setOnClickListener {
            dispatchPlay(media.getAvailablePath()!!, media.displayName)
            showVideoController()
        }
        itemView.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay(media.getAvailablePath()!!, media.displayName)
                showVideoController()
            } else {
                if (controller != null && (controller as View).alpha == 0F) {
                    showVideoController()
                } else {
                    setClickEvent(media)
                }
            }
        }
    }

    open fun dispatchPlay(path: String, displayName: String?) {
        if (isPlayed) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                controller?.stop(false)
                ivPlay.visibility = View.VISIBLE
                setPreviewVideoTitle(null)
            } else {
                mediaPlayer.resume()
                controller?.start()
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
        showVideoController()
        controller?.start()
    }

    open fun onDefaultVideoState() {
        setPreviewVideoTitle(null)
        imageCover.visibility = View.VISIBLE
        ivPlay.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
        hideVideoController()
        controller?.stop(true)
        isPlayed = false
    }

    open fun showVideoController() {
        controller?.let { controller ->
            if (mediaPlayer.isPlaying() && (controller as View).alpha == 0F) {
                (controller as View).animate().alpha(1F).setDuration(300).start()
                startControllerHandler()
            }
        }
    }

    open fun startControllerHandler() {
        stopControllerHandler()
        if (mediaPlayer.getDuration() > disappearControllerDuration()) {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    handler.removeCallbacks(this)
                    (controller as View).animate().alpha(0F).setDuration(220).start()
                }
            }, disappearControllerDuration())
        }
    }

    open fun stopControllerHandler() {
        handler.removeCallbacksAndMessages(null)
    }

    open fun hideVideoController() {
        controller?.let { controller ->
            (controller as View).animate().alpha(0F).setDuration(80).start()
        }
    }

    open fun disappearControllerDuration(): Long {
        return 3000L
    }

    private val playStateListener = object : AbsController.OnPlayStateListener {
        override fun onPlayState(isPlaying: Boolean) {
            if (isPlaying) {
                ivPlay.visibility = View.GONE
            } else {
                ivPlay.visibility = View.VISIBLE
            }
        }
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            stopControllerHandler()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            controller?.let { controller ->
                if ((controller as View).alpha == 1F) {
                    startControllerHandler()
                }
            }
        }
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
        controller?.setOnPlayStateListener(null)
        controller?.setOnSeekBarChangeListener(null)
        stopControllerHandler()
        onDefaultVideoState()
    }
}