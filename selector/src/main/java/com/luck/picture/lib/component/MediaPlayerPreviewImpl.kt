package com.luck.picture.lib.component

import android.content.Context
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
import com.luck.picture.lib.utils.BitmapUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.MediaUtils.isContent
import com.luck.picture.lib.widget.VideoSurfaceView

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayer Component
 */
class MediaPlayerPreviewImpl : FrameLayout, SurfaceHolder.Callback, IMediaPlayer {
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenAppInHeight = 0
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surfaceView: VideoSurfaceView
    private lateinit var ivCover: ImageView
    private lateinit var videoController: VideoControllerImpl
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
        surfaceView = VideoSurfaceView(context)
        addView(surfaceView, 0)
        surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
        surfaceView.holder.addCallback(this)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {
        videoController.getViewPlay().visibility =
            if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
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
        isPlayed = true
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
        isPlayed = false
    }

}