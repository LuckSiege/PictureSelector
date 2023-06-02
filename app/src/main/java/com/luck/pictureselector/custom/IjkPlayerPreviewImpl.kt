package com.luck.pictureselector.custom

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.component.IMediaPlayer
import com.luck.picture.lib.component.IPlayerController
import com.luck.picture.lib.component.VideoControllerImpl
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：IjkPlayer Component
 */
class IjkPlayerPreviewImpl : FrameLayout, TextureView.SurfaceTextureListener, IMediaPlayer {
    private lateinit var textureView: IjkVideoTextureView
    private var mediaPlayer: IjkMediaPlayer? = null
    private lateinit var ivCover: ImageView
    private lateinit var videoController: VideoControllerImpl
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
        inflate(context, R.layout.ps_preview_video_component, this)
        ivCover = findViewById(R.id.iv_preview_cover)
        videoController = VideoControllerImpl(context)
        addView(videoController)
        textureView = IjkVideoTextureView(context)
        textureView.surfaceTextureListener = this
        textureView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER
            }
        addView(textureView, 0)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {

    }

    override fun onViewAttachedToWindow() {
        if (mediaPlayer == null) {
            mediaPlayer = IjkMediaPlayer()
        }
        mediaPlayer?.setOnVideoSizeChangedListener { mediaPlayer, width, height, sar_num, sar_den ->
            textureView.adjustVideoSize(width, height, videoRotation)
        }
        mediaPlayer?.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            onPlayingVideoState()
        }
        mediaPlayer?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.reset()
            onDefaultVideoState()
        }
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            onDefaultVideoState()
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

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        onDefaultVideoState()
        mediaPlayer?.release()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.setOnInfoListener(null)
        mediaPlayer = null
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

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        onPlayingLoading()
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

    override fun getImageCover(): ImageView {
        return ivCover
    }

    override fun getController(): IPlayerController {
        return videoController
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration ?: 0L
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