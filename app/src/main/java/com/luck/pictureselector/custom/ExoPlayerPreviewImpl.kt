package com.luck.pictureselector.custom

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
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
import com.luck.picture.lib.utils.MediaUtils
import com.luck.pictureselector.R
import java.io.File

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Google ExoPlayer Component
 */
class ExoPlayerPreviewImpl : FrameLayout, IMediaPlayer {
    private lateinit var ivCover: ImageView
    private lateinit var playerView: StyledPlayerView
    private lateinit var videoController: VideoControllerImpl
    private var player: ExoPlayer? = null

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
        inflate(context, R.layout.ps_preview_video_component, this)
        ivCover = findViewById(R.id.iv_preview_cover)
        videoController = VideoControllerImpl(context)
        addView(videoController)
        playerView = StyledPlayerView(context)
        playerView.useController = false
        addView(playerView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {

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