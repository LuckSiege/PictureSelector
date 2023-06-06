package com.luck.picture.lib.player

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.FrameLayout
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.widget.VideoSurfaceView

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：default Video MediaPlayer
 */
class DefaultMediaPlayer : FrameLayout, SurfaceHolder.Callback, IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surfaceView: VideoSurfaceView

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
        surfaceView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        ).apply {
            this.gravity = Gravity.CENTER
        }
        surfaceView.holder.setKeepScreenOn(true)
        addView(surfaceView)
    }

    override fun setDataSource(context: Context, path: String, isLoopAutoPlay: Boolean) {
        if (MediaUtils.isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.setDataSource(path)
        }
        surfaceView.setZOrderOnTop(MediaUtils.isHasHttp(path))
        mediaPlayer?.isLooping = isLoopAutoPlay
        mediaPlayer?.prepareAsync()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    override fun seekTo(speed: Int) {
        mediaPlayer?.seekTo(speed)
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun resume() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun reset() {
        mediaPlayer?.reset()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        surfaceView.holder.removeCallback(this)
    }

    override fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener?) {
        if (listener != null) {
            mediaPlayer?.setOnInfoListener { mp, what, extra ->
                listener.onInfo(this, what, extra)
            }
        } else {
            mediaPlayer?.setOnInfoListener(null)
        }
    }

    override fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener?) {
        if (listener != null) {
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                listener.onError(this, what, extra)
            }
        } else {
            mediaPlayer?.setOnErrorListener(null)
        }
    }

    override fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener?) {
        if (listener != null) {
            mediaPlayer?.setOnPreparedListener {
                listener.onPrepared(this)
            }
        } else {
            mediaPlayer?.setOnPreparedListener(null)
        }
    }

    override fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener?) {
        if (listener != null) {
            mediaPlayer?.setOnCompletionListener {
                listener.onCompletion(this)
                clearCanvas()
            }
        } else {
            mediaPlayer?.setOnCompletionListener(null)
        }
    }

    override fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener?) {
        if (listener != null) {
            mediaPlayer?.setOnVideoSizeChangedListener { mp, width, height ->
                surfaceView.adjustVideoSize(width, height)
                listener.onVideoSizeChanged(this, width, height)
            }
        } else {
            mediaPlayer?.setOnVideoSizeChangedListener(null)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        surfaceView.holder.addCallback(this)
    }

    private fun clearCanvas() {
        surfaceView.holder.setFormat(PixelFormat.OPAQUE)
        surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
    }
}