package com.luck.picture.lib.adapter

import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.component.AudioMediaPlayer
import com.luck.picture.lib.component.IMediaPlayer
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.FileUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewAudioHolder
 */
open class PreviewAudioHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    private val maxBackFastMs = 3 * 1000L
    private val maxUpdateIntervalMs = 1000L
    private val minCurrentPosition = 1000L
    private var tvAudioName: TextView = itemView.findViewById(R.id.tv_audio_name)
    private var seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)
    private var ivBack: View = itemView.findViewById(R.id.iv_play_back)
    private var ivFast: View = itemView.findViewById(R.id.iv_play_fast)
    private var ivPlay: ImageView = itemView.findViewById(R.id.iv_play_audio)
    private var tvDuration: TextView = itemView.findViewById(R.id.tv_total_duration)
    private var tvCurrentDuration: TextView = itemView.findViewById(R.id.tv_current_time)
    private var isPlayed = false
    var mediaPlayer: AudioMediaPlayer = AudioMediaPlayer()
    private val mHandler = Handler(Looper.getMainLooper())
    private val mTickerRunnable = object : Runnable {
        override fun run() {
            val duration = mediaPlayer.getDuration()
            val currentPosition = mediaPlayer.getCurrentPosition()
            val time = DateUtils.formatDurationTime(currentPosition)
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

    override fun bindData(media: LocalMedia, position: Int) {
        loadCover(media)
        val dataFormat = DateUtils.getYearDataFormat(media.dateAdded)
        val fileSize = FileUtils.formatAccurateUnitFileSize(media.size)
        val stringBuilder = StringBuilder()
        stringBuilder.append(media.displayName).append("\n").append(dataFormat).append(" - ")
            .append(fileSize)
        val builder = SpannableStringBuilder(stringBuilder.toString())
        val indexOfStr = "$dataFormat - $fileSize"
        val startIndex = stringBuilder.indexOf(indexOfStr)
        val endOf = startIndex + indexOfStr.length
        builder.setSpan(
            AbsoluteSizeSpan(DensityUtil.dip2px(itemView.context, 12f)),
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
        tvDuration.text = DateUtils.formatDurationTime(media.duration)
        seekBar.max = media.duration.toInt()
        setBackFastUI(false)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar?.progress = progress
                    tvCurrentDuration.text = DateUtils.formatDurationTime(progress.toLong())
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
        itemView.setOnClickListener {
            setClickEvent(media)
        }
        itemView.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    open fun dispatchPlay(path: String) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause()
            ivPlay.setImageResource(R.drawable.ps_ic_audio_play)
        } else {
            if (isPlayed) {
                mediaPlayer.resume()
                ivPlay.setImageResource(R.drawable.ps_ic_audio_stop)
            } else {
                mediaPlayer.setDataSource(itemView.context, path, config.isLoopAutoPlay)
                isPlayed = true
            }
        }
    }

    open fun onBackAudioPlay() {
        val progress = seekBar.progress - maxBackFastMs
        if (progress <= 0) {
            seekBar.progress = 0
        } else {
            seekBar.progress = progress.toInt()
        }
        tvCurrentDuration.text = DateUtils.formatDurationTime(seekBar.progress.toLong())
        mediaPlayer.seekTo(seekBar.progress)
    }

    open fun onFastAudioPlay() {
        val progress = seekBar.progress + maxBackFastMs
        if (progress >= seekBar.max) {
            seekBar.progress = seekBar.max
        } else {
            seekBar.progress = progress.toInt()
        }
        tvCurrentDuration.text = DateUtils.formatDurationTime(seekBar.progress.toLong())
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


    open fun onPlayingAudioState() {
        ivPlay.setImageResource(R.drawable.ps_ic_audio_stop);
        mHandler.post(mTickerRunnable)
        setBackFastUI(true)
    }

    open fun onDefaultAudioState() {
        ivPlay.setImageResource(R.drawable.ps_ic_audio_play);
        mHandler.removeCallbacks(mTickerRunnable)
        tvCurrentDuration.text = String.format("00:00")
        seekBar.progress = 0
        setBackFastUI(false)
        isPlayed = false
    }

    override fun loadCover(media: LocalMedia) {
        imageCover.setImageResource(R.drawable.ps_ic_audio_play_cover)
    }

    override fun coverScaleType(media: LocalMedia) {
    }

    override fun coverLayoutParams(media: LocalMedia) {
    }

    override fun onViewAttachedToWindow() {
        mediaPlayer.initMediaPlayer()
        mediaPlayer.setOnPreparedListener(object : IMediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: IMediaPlayer?) {
                mediaPlayer.start()
                onPlayingAudioState()
            }
        })
        mediaPlayer.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: IMediaPlayer?) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                onDefaultAudioState()
            }
        })
        mediaPlayer.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
                onDefaultAudioState()
                return false
            }
        })
    }

    override fun onViewDetachedFromWindow() {
        release()
    }

    override fun release() {
        mediaPlayer.release()
        mediaPlayer.setOnErrorListener(null)
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.setOnPreparedListener(null)
        onDefaultAudioState()
    }
}