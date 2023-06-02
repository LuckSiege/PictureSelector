package com.luck.picture.lib.component

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils.formatDurationTime
import com.luck.picture.lib.utils.DateUtils.getYearDataFormat
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Audio MediaPlayer Component
 */
class MediaAudioPlayerPreviewImpl : RelativeLayout, IMediaPlayer {
    private val maxBackFastMs = 3 * 1000L
    private val maxUpdateIntervalMs = 1000L
    private val minCurrentPosition = 1000L
    private lateinit var tvAudioName: TextView
    private lateinit var ivCover: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var ivBack: View
    private lateinit var ivFast: View
    private lateinit var ivPlay: ImageView
    private lateinit var tvDuration: TextView
    private lateinit var tvCurrentDuration: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioController: AudioControllerImpl
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
        inflate(context, R.layout.ps_preview_audio_component, this)
        tvAudioName = findViewById(R.id.tv_audio_name)
        ivCover = findViewById(R.id.iv_preview_cover)
        audioController = findViewById(R.id.audio_controller)
    }

    override fun bindData(config: SelectorConfig, media: LocalMedia) {
        val dataFormat = getYearDataFormat(media.dateAdded)
        val fileSize = FileUtils.formatAccurateUnitFileSize(media.size)
        val stringBuilder = StringBuilder()
        stringBuilder.append(media.displayName).append("\n").append(dataFormat).append(" - ")
            .append(fileSize)
        val builder = SpannableStringBuilder(stringBuilder.toString())
        val indexOfStr = "$dataFormat - $fileSize"
        val startIndex = stringBuilder.indexOf(indexOfStr)
        val endOf = startIndex + indexOfStr.length
        builder.setSpan(
            AbsoluteSizeSpan(dip2px(context, 12f)),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            ForegroundColorSpan(-0x9a9a9b),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tvAudioName.text = builder
        seekBar = audioController.getSeekBar()
        ivBack = audioController.getViewBack()
        ivFast = audioController.getViewFast()
        ivPlay = audioController.getViewPlay()
        tvDuration = audioController.getViewDuration()
        tvCurrentDuration = audioController.getViewCurrentDuration()
        tvDuration.text = formatDurationTime(media.duration)
        seekBar.max = media.duration.toInt()
        setBackFastUI(false)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar?.progress = progress
                    tvCurrentDuration.text = formatDurationTime(progress.toLong())
                    if (isPlaying()) {
                        mediaPlayer?.seekTo(progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        ivBack.setOnClickListener {
            val progress = seekBar.progress - maxBackFastMs
            if (progress <= 0) {
                seekBar.progress = 0
            } else {
                seekBar.progress = progress.toInt()
            }
            tvCurrentDuration.text = formatDurationTime(seekBar.progress.toLong())
            mediaPlayer?.seekTo(seekBar.progress)
        }
        ivFast.setOnClickListener {
            val progress = seekBar.progress + maxBackFastMs
            if (progress >= seekBar.max) {
                seekBar.progress = seekBar.max
            } else {
                seekBar.progress = progress.toInt()
            }
            tvCurrentDuration.text = formatDurationTime(seekBar.progress.toLong())
            mediaPlayer?.seekTo(seekBar.progress)
        }
        ivPlay.setOnClickListener {
            if (isPlaying()) {
                onPause()
            } else {
                if (isPlayed) {
                    onResume()
                } else {
                    onStart(media.getAvailablePath()!!, config.isLoopAutoPlay)
                }
            }
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val mTickerRunnable = object : Runnable {
        override fun run() {
            val duration = getDuration()
            val currentPosition = getCurrentPosition()
            val time = formatDurationTime(currentPosition)
            if (TextUtils.equals(time, tvCurrentDuration.text)) {
                // Same progress ignored
            } else {
                tvCurrentDuration.text = time
                if (duration - currentPosition > minCurrentPosition) {
                    seekBar.progress = currentPosition.toInt()
                } else {
                    seekBar.progress = duration.toInt()
                }
            }
            mHandler.postDelayed(this, maxUpdateIntervalMs - currentPosition % maxUpdateIntervalMs)
        }
    }

    private fun setBackFastUI(isEnabled: Boolean) {
        audioController.getViewBack().isEnabled = isEnabled
        audioController.getViewFast().isEnabled = isEnabled
        if (isEnabled) {
            audioController.getViewBack().alpha = 1.0F
            audioController.getViewFast().alpha = 1.0F
        } else {
            audioController.getViewBack().alpha = 0.5F
            audioController.getViewFast().alpha = 0.5F
        }
    }

    override fun getCurrentPosition(): Long {
        return (mediaPlayer?.currentPosition ?: 0L).toLong()
    }

    override fun getDuration(): Long {
        return (mediaPlayer?.duration ?: 0L).toLong()
    }

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        mediaPlayer?.isLooping = isLoopAutoPlay
        if (MediaUtils.isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.setDataSource(path)
        }
        mediaPlayer?.prepareAsync()
        isPlayed = true
    }

    override fun onResume() {
        mediaPlayer?.start()
        ivPlay.setImageResource(R.drawable.ps_ic_audio_stop);
    }

    override fun onPause() {
        mediaPlayer?.pause()
        ivPlay.setImageResource(R.drawable.ps_ic_audio_play);
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun getController(): IPlayerController {
        return audioController
    }

    override fun getImageCover(): ImageView {
        return ivCover
    }

    private fun onPlayingAudioState() {
        ivPlay.setImageResource(R.drawable.ps_ic_audio_stop);
        mHandler.post(mTickerRunnable)
        setBackFastUI(true)
    }

    private fun onDefaultAudioState() {
        ivPlay.setImageResource(R.drawable.ps_ic_audio_play);
        mHandler.removeCallbacks(mTickerRunnable)
        tvCurrentDuration.text = String.format("00:00")
        seekBar.progress = 0
        setBackFastUI(false)
        isPlayed = false
    }

    override fun onViewAttachedToWindow() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer?.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            onPlayingAudioState()
        }
        mediaPlayer?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.stop()
            mediaPlayer.reset()
            onDefaultAudioState()
        }
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            onDefaultAudioState()
            false
        }
    }

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        onDefaultAudioState()
        mediaPlayer?.release()
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer = null
        isPlayed = false
    }
}