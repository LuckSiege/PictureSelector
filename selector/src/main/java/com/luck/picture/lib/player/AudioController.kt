package com.luck.picture.lib.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.luck.picture.lib.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.DateUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Audio Player Controller
 */
open class AudioController : ConstraintLayout, AbsController {
    private val config = SelectorProviders.getInstance().getConfig()
    private lateinit var seekBar: SeekBar
    private lateinit var ivBack: ImageView
    private lateinit var ivFast: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var tvDuration: TextView
    private lateinit var tvCurrentDuration: TextView
    private lateinit var mediaPlayer: IMediaPlayer
    private var isPlayed = false
    private var playStateListener: AbsController.OnPlayStateListener? = null
    private var seekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val mTickerRunnable = object : Runnable {
        override fun run() {
            val duration = mediaPlayer.getDuration()
            val currentPosition = mediaPlayer.getCurrentPosition()
            val time = DateUtils.formatDurationTime(currentPosition, false)
            if (TextUtils.equals(time, tvCurrentDuration.text)) {
                // Same progress ignored
            } else {
                tvCurrentDuration.text = time
                if (duration - currentPosition > getMinCurrentPosition()) {
                    seekBar.progress = currentPosition.toInt()
                } else {
                    seekBar.progress = duration.toInt()
                }
            }
            mHandler.postDelayed(
                this,
                getMaxUpdateIntervalDuration() - currentPosition % getMaxUpdateIntervalDuration()
            )
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.ps_audio_controller, this)
        seekBar = findViewById(R.id.seek_bar)
        ivBack = findViewById(R.id.iv_play_back)
        ivFast = findViewById(R.id.iv_play_fast)
        ivPlay = findViewById(R.id.iv_play_audio)
        tvDuration = findViewById(R.id.tv_total_duration)
        tvCurrentDuration = findViewById(R.id.tv_current_time)
        tvDuration.text = String.format("00:00")
        tvCurrentDuration.text = String.format("00:00")
    }

    override fun getViewPlay(): ImageView? {
        return ivPlay
    }

    override fun getSeekBar(): SeekBar? {
        return seekBar
    }

    override fun getFast(): ImageView? {
        return ivFast
    }

    override fun getBack(): ImageView? {
        return ivBack
    }

    override fun getTvDuration(): TextView? {
        return tvDuration
    }

    override fun getTvCurrentDuration(): TextView? {
        return tvCurrentDuration
    }


    override fun setDataSource(media: LocalMedia) {
        tvDuration.text = DateUtils.formatDurationTime(media.duration, false)
        seekBar.max = media.duration.toInt()
        setBackFastUI(false)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar?.progress = progress
                    tvCurrentDuration.text = DateUtils.formatDurationTime(progress.toLong(), false)
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(progress)
                    }
                }
                seekBarChangeListener?.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarChangeListener?.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBarChangeListener?.onStopTrackingTouch(seekBar)
            }
        })

        ivBack.setOnClickListener {
            onBackAudioPlay()
        }
        ivFast.setOnClickListener {
            onFastAudioPlay()
        }
        ivPlay.setOnClickListener {
            dispatchPlay(media.getAvailablePath()!!)
        }
    }

    open fun dispatchPlay(path: String) {
        if (mediaPlayer.isPlaying()) {
            playStateListener?.onPlayState(false)
            mediaPlayer.pause()
            ivPlay.setImageResource(R.drawable.ps_ic_audio_play)
        } else {
            playStateListener?.onPlayState(true)
            if (isPlayed) {
                mediaPlayer.resume()
                ivPlay.setImageResource(R.drawable.ps_ic_audio_stop)
            } else {
                mediaPlayer.setDataSource(context, path, config.isLoopAutoPlay)
                isPlayed = true
            }
        }
    }

    override fun start() {
        mHandler.post(mTickerRunnable)
        setBackFastUI(true)
        ivPlay.setImageResource(R.drawable.ps_ic_audio_stop)
    }

    override fun stop(isReset: Boolean) {
        mHandler.removeCallbacks(mTickerRunnable)
        setBackFastUI(false)
        ivPlay.setImageResource(R.drawable.ps_ic_audio_play)
        if (isReset) {
            tvCurrentDuration.text = String.format("00:00")
            seekBar.progress = 0
        }
        isPlayed = false
    }

    override fun setOnPlayStateListener(l: AbsController.OnPlayStateListener?) {
        this.playStateListener = l;
    }

    override fun setOnSeekBarChangeListener(l: SeekBar.OnSeekBarChangeListener?) {
        this.seekBarChangeListener = l
    }

    open fun onBackAudioPlay() {
        val progress = seekBar.progress - getBackFastDuration()
        if (progress <= 0) {
            seekBar.progress = 0
        } else {
            seekBar.progress = progress.toInt()
        }
        tvCurrentDuration.text = DateUtils.formatDurationTime(seekBar.progress.toLong(),false)
        mediaPlayer.seekTo(seekBar.progress)
    }

    open fun onFastAudioPlay() {
        val progress = seekBar.progress + getBackFastDuration()
        if (progress >= seekBar.max) {
            seekBar.progress = seekBar.max
        } else {
            seekBar.progress = progress.toInt()
        }
        tvCurrentDuration.text = DateUtils.formatDurationTime(seekBar.progress.toLong(),false)
        mediaPlayer.seekTo(seekBar.progress)
    }

    open fun setBackFastUI(isEnabled: Boolean) {
        ivBack.isEnabled = isEnabled
        ivFast.isEnabled = isEnabled
        if (isEnabled) {
            ivBack.alpha = 1.0F
            ivFast.alpha = 1.0F
        } else {
            ivBack.alpha = 0.5F
            ivFast.alpha = 0.5F
        }
    }

    override fun setIMediaPlayer(mediaPlayer: IMediaPlayer) {
        this.mediaPlayer = mediaPlayer
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacks(mTickerRunnable)
    }

    open fun getBackFastDuration(): Long {
        return 3 * 1000L
    }

    open fun getMaxUpdateIntervalDuration(): Long {
        return 1000L
    }

    open fun getMinCurrentPosition(): Long {
        return 1000L
    }
}