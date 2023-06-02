package com.luck.pictureselector.custom

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.luck.picture.lib.component.IMediaPlayer
import com.luck.picture.lib.component.IPlayerController
import com.luck.picture.lib.component.VideoControllerImpl
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils
import com.luck.pictureselector.R
import java.io.File

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Google ExoPlayer Component
 */
class ExoPlayerPreviewImpl : FrameLayout, IMediaPlayer {
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenAppInHeight = 0

    private lateinit var ivCover: ImageView
    private lateinit var playerView: StyledPlayerView
    private lateinit var videoController: VideoControllerImpl
    private var player: ExoPlayer? = null
    private var isPlayed = false

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
        inflate(context, R.layout.ps_preview_video_component, this)
        ivCover = findViewById(R.id.iv_preview_cover)
        videoController = VideoControllerImpl(context)
        addView(videoController)
        playerView = StyledPlayerView(context)
        playerView.useController = false
        addView(playerView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private val exoPlayerListener: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            onDefaultVideoState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    onPlayingVideoState()
                }
                Player.STATE_BUFFERING -> {
                    onPlayingLoading()
                }
                Player.STATE_ENDED -> {
                    onDefaultVideoState()
                }
                else -> {
                }
            }
        }
    }


    private fun onPlayingLoading() {
        videoController.getViewLoading().visibility = View.VISIBLE
        videoController.getViewPlay().visibility = View.GONE
    }

    private fun onPlayingVideoState() {
        ivCover.visibility = View.GONE
        videoController.getViewPlay().visibility = View.GONE
        videoController.getViewLoading().visibility = View.GONE
    }

    private fun onDefaultVideoState() {
        ivCover.visibility = View.VISIBLE
        videoController.getViewPlay().visibility = View.VISIBLE
        videoController.getViewLoading().visibility = View.GONE
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {
        videoController.getViewPlay().visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
        val size = getRealSizeFromMedia(media)
        val mediaComputeSize = BitmapUtils.getComputeImageSize(size[0], size[1])
        val width = mediaComputeSize[0]
        val height = mediaComputeSize[1]
        if (width > 0 && height > 0) {
            config.imageEngine?.loadImage(context, media.getAvailablePath(), width, height, ivCover)
        } else {
            config.imageEngine?.loadImage(context, media.getAvailablePath(), ivCover)
        }
        if (MediaUtils.isLongImage(media.width, media.height)) {
            ivCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            ivCover.scaleType = ImageView.ScaleType.FIT_CENTER
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
        videoController.getViewPlay().setOnClickListener {
            if (config.isPauseResumePlay) {
                if (isPlayed) {
                    if (isPlaying()) {
                        onPause()
                    } else {
                        onResume()
                    }
                } else {
                    onStart(media.getAvailablePath()!!, config.isLoopAutoPlay)
                }
            } else {
                onStart(media.getAvailablePath()!!, config.isLoopAutoPlay)
            }
        }
    }

    private fun getRealSizeFromMedia(media: LocalMedia): IntArray {
        return if (media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0) {
            intArrayOf(media.cropWidth, media.cropHeight)
        } else {
            intArrayOf(media.width, media.height)
        }
    }

    override fun getController(): IPlayerController {
        return videoController
    }

    override fun onViewAttachedToWindow() {
        player = ExoPlayer.Builder(context).build()
        player?.addListener(exoPlayerListener)
        playerView.player = player
    }

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        onDefaultVideoState()
        player?.release()
        player?.removeListener(exoPlayerListener)
        playerView.player = null
        player = null
    }

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        val mediaItem: MediaItem = when {
            MediaUtils.isContent(path) -> {
                MediaItem.fromUri(Uri.parse(path))
            }
            MediaUtils.isHasHttp(path) -> {
                MediaItem.fromUri(path)
            }
            else -> {
                MediaItem.fromUri(Uri.fromFile(File(path)))
            }
        }
        player?.repeatMode =
            if (isLoopAutoPlay) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        isPlayed = true
    }

    override fun onResume() {
        player?.play()
    }

    override fun onPause() {
        player?.pause()
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    override fun getImageCover(): ImageView {
        return ivCover
    }

    override fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return player?.duration ?: 0L
    }
}