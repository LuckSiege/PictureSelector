package com.luck.picture.lib.player

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.FrameLayout
import com.luck.picture.lib.interfaces.OnPlayerListener
import com.luck.picture.lib.utils.MediaUtils.isContent

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayer Component
 */
class MediaPlayerComponent : FrameLayout, SurfaceHolder.Callback, IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surfaceView: VideoSurfaceView
    private var playerListener: OnPlayerListener? = null

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
        surfaceView = VideoSurfaceView(context)
        surfaceView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER
            }
        addView(surfaceView)
        surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
        surfaceView.holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    private fun clearCanvas() {
        surfaceView.holder.setFormat(PixelFormat.OPAQUE)
        surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
    }

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        playerListener?.onPlayerLoading()
        mediaPlayer?.isLooping = isLoopAutoPlay
        if (isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.setDataSource(path)
        }
        mediaPlayer?.prepareAsync()
    }

    override fun onResume() {
        mediaPlayer?.start()
    }

    override fun onPause() {
        mediaPlayer?.pause()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    override fun initMediaPlayer(l: OnPlayerListener?) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        playerListener = l
        surfaceView.holder.addCallback(this)
        mediaPlayer?.setOnVideoSizeChangedListener { mediaPlayer, width, height ->
            surfaceView.adjustVideoSize(mediaPlayer.videoWidth, mediaPlayer.videoHeight)
        }
        mediaPlayer?.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            playerListener?.onPlayerReady()
        }
        mediaPlayer?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.reset()
            playerListener?.onPlayerComplete()
            clearCanvas()
        }
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            playerListener?.onPlayerError()
            false
        }
    }

    override fun destroy() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer?.setOnErrorListener(null)
            mediaPlayer?.setOnPreparedListener(null)
            mediaPlayer?.setOnCompletionListener(null)
            mediaPlayer?.setOnVideoSizeChangedListener(null)
            surfaceView.holder.removeCallback(this)
            mediaPlayer = null
            playerListener = null
        }
    }
}