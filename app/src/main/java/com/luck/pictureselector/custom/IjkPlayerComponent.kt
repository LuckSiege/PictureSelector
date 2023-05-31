package com.luck.pictureselector.custom

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import com.luck.picture.lib.interfaces.OnPlayerListener
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.utils.MediaUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：IjkPlayer Component
 */
class IjkPlayerComponent : FrameLayout, TextureView.SurfaceTextureListener, IMediaPlayer {
    private lateinit var textureView: IjkVideoTextureView
    private var playerListener: OnPlayerListener? = null
    private var mediaPlayer: IjkMediaPlayer? = null
    private var videoRotation = 0

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
        textureView = IjkVideoTextureView(context)
        textureView.surfaceTextureListener = this
        textureView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER
            }
        addView(textureView)
    }

    override fun initMediaPlayer(l: OnPlayerListener?) {
        playerListener = l
        if (mediaPlayer == null) {
            mediaPlayer = IjkMediaPlayer()
        }
        mediaPlayer?.setOnVideoSizeChangedListener { mediaPlayer, width, height, sar_num, sar_den ->
            textureView.adjustVideoSize(width, height, videoRotation)
        }
        mediaPlayer?.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            playerListener?.onPlayerReady()
        }
        mediaPlayer?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.reset()
            playerListener?.onPlayerComplete()
        }
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            playerListener?.onPlayerError()
            return@setOnErrorListener false
        }
        mediaPlayer?.setOnInfoListener { mp, what, extra ->
            if (what == 10001) {
                videoRotation = extra
            }
            return@setOnInfoListener false
        }
        mediaPlayer?.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
    }

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        mediaPlayer?.isLooping = isLoopAutoPlay
        if (MediaUtils.isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.dataSource = path
        }
        val surfaceTexture = textureView.surfaceTexture
        if (surfaceTexture != null) {
            mediaPlayer?.setSurface(Surface(surfaceTexture))
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
        return mediaPlayer?.isPlaying == true
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration ?: 0L
    }

    override fun destroy() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer?.setOnPreparedListener(null)
            mediaPlayer?.setOnCompletionListener(null)
            mediaPlayer?.setOnErrorListener(null)
            mediaPlayer?.setOnInfoListener(null)
            mediaPlayer = null
            playerListener = null
        }
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