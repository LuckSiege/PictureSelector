package com.luck.picture.lib.component

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils.isContent
import com.luck.picture.lib.widget.VideoSurfaceView

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayer Component
 */
class MediaPlayerPreviewImpl : FrameLayout, SurfaceHolder.Callback, IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surfaceView: VideoSurfaceView
    private lateinit var ivCover: ImageView
    private lateinit var videoController: VideoControllerImpl

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
        surfaceView = VideoSurfaceView(context)
        surfaceView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                this.gravity = Gravity.CENTER
            }
        addView(surfaceView, 0)
        surfaceView.holder.addCallback(this)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {

    }

    override fun onViewAttachedToWindow() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        surfaceView.holder.addCallback(this)
        mediaPlayer?.setOnVideoSizeChangedListener { mediaPlayer, width, height ->
            surfaceView.adjustVideoSize(mediaPlayer.videoWidth, mediaPlayer.videoHeight)
        }
        mediaPlayer?.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            onPlayingVideoState()
        }
        mediaPlayer?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.reset()
            onDefaultVideoState()
            clearCanvas()
        }
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            onDefaultVideoState()
            false
        }
    }

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        onDefaultVideoState()
        mediaPlayer?.release()
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnVideoSizeChangedListener(null)
        surfaceView.holder.removeCallback(this)
        mediaPlayer = null
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
        onPlayingLoading()
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

    override fun getController(): IPlayerController {
        return videoController
    }

    override fun getImageCover(): ImageView {
        return ivCover
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
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

}