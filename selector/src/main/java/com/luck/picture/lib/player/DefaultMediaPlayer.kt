package com.luck.picture.lib.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：default Video MediaPlayer
 */
class DefaultMediaPlayer : FrameLayout, TextureView.SurfaceTextureListener, IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var textureView: VideoTextureView
    private var mVideoRotation = 0

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
        textureView = VideoTextureView(context)
        textureView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER
            }
        addView(textureView)
    }

    override fun setDataSource(context: Context, path: String, isLoopAutoPlay: Boolean) {
        if (MediaUtils.isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.setDataSource(path)
        }
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
        textureView.surfaceTextureListener = null
    }

    override fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener?) {
        if (listener != null) {
            mediaPlayer?.setOnInfoListener { mp, what, extra ->
                listener.onInfo(this, what, extra)
                if (what == 10001) {
                    mVideoRotation = extra
                }
                return@setOnInfoListener false
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
            }
        } else {
            mediaPlayer?.setOnCompletionListener(null)
        }
    }

    override fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener?) {
        if (listener != null) {
            mediaPlayer?.setOnVideoSizeChangedListener { mp, width, height ->
                textureView.adjustVideoSize(width, height, mVideoRotation)
                listener.onVideoSizeChanged(this, width, height)
            }
        } else {
            mediaPlayer?.setOnVideoSizeChangedListener(null)
        }
    }

    override fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        textureView.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mediaPlayer?.setSurface(Surface(surface))
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

}