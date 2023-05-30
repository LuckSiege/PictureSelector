package com.luck.picture.lib.adapter

import android.media.MediaPlayer
import android.net.Uri
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
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.DateUtils.formatDurationTime
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.DoubleUtils.isFastDoubleClick
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.utils.MediaUtils
import java.io.IOException

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewAudioHolder
 */
open class PreviewAudioHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    private val maxBackFastMs: Long = 3 * 1000L
    private val maxUpdateIntervalMs: Long = 1000L
    private val minCurrentPosition: Long = 1000L
    private var ivPlayButton: ImageView = itemView.findViewById(R.id.iv_play_video)
    private var tvAudioName: TextView = itemView.findViewById(R.id.tv_audio_name)
    private var tvTotalDuration: TextView = itemView.findViewById(R.id.tv_total_duration)
    private var tvCurrentTime: TextView = itemView.findViewById(R.id.tv_current_time)
    private var seekBar: SeekBar = itemView.findViewById(R.id.music_seek_bar)
    private var ivPlayBack: ImageView = itemView.findViewById(R.id.iv_play_back)
    private var ivPlayFast: ImageView = itemView.findViewById(R.id.iv_play_fast)
    private var mPlayer: MediaPlayer = MediaPlayer()
    private var isPausePlayer = false
    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * 播放计时器
     */
    private var mTickerRunnable: Runnable = object : Runnable {
        override fun run() {
            val currentPosition = mPlayer.currentPosition.toLong()
            val time = formatDurationTime(currentPosition)
            if (!TextUtils.equals(time, tvCurrentTime.text)) {
                tvCurrentTime.text = time
                if (mPlayer.duration - currentPosition > minCurrentPosition) {
                    seekBar.progress = currentPosition.toInt()
                } else {
                    seekBar.progress = mPlayer.duration
                }
            }
            val nextSecondMs: Long = maxUpdateIntervalMs - currentPosition % maxUpdateIntervalMs
            mHandler.postDelayed(this, nextSecondMs)
        }
    }

    override fun bindData(media: LocalMedia, position: Int) {
        val dataFormat: String = DateUtils.getYearDataFormat(media.dateAdded)
        val fileSize: String = FileUtils.formatAccurateUnitFileSize(media.size)
        val stringBuilder = StringBuilder()
        stringBuilder.append(media.displayName).append("\n").append(dataFormat).append(" - ")
            .append(fileSize)
        val builder = SpannableStringBuilder(stringBuilder.toString())
        val indexOfStr = "$dataFormat - $fileSize"
        val startIndex = stringBuilder.indexOf(indexOfStr)
        val endOf = startIndex + indexOfStr.length
        builder.setSpan(
            AbsoluteSizeSpan(dip2px(itemView.context, 12f)),
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
        tvTotalDuration.text = formatDurationTime(media.duration)
        seekBar.max = media.duration.toInt()
        setBackFastUI(false)
        loadCover(media)

        ivPlayBack.setOnClickListener { slowAudioPlay() }

        ivPlayFast.setOnClickListener { fastAudioPlay() }

        ivPlayButton.setOnClickListener(View.OnClickListener {
            try {
                if (isFastDoubleClick()) {
                    return@OnClickListener
                }
                setPreviewVideoTitle(media.displayName)
                if (isPlaying()) {
                    pausePlayer()
                } else {
                    if (isPausePlayer) {
                        resumePlayer()
                    } else {
                        startPlayer(media.path!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar.progress = progress
                    setCurrentPlayTime(progress)
                    if (isPlaying()) {
                        mPlayer.seekTo(seekBar.progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        itemView.setOnClickListener {
            setClickEvent(media)
        }
        itemView.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            false
        }
    }

    override fun loadCover(media: LocalMedia) {
        tvAudioName.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            R.drawable.ps_ic_audio_play_cover,
            0,
            0
        )
    }

    private fun startPlayer(path: String) {
        try {
            if (MediaUtils.isContent(path)) {
                mPlayer.setDataSource(itemView.context, Uri.parse(path))
            } else {
                mPlayer.setDataSource(path)
            }
            mPlayer.prepare()
            mPlayer.seekTo(seekBar.progress)
            mPlayer.start()
            isPausePlayer = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    open fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    private fun pausePlayer() {
        mPlayer.pause()
        isPausePlayer = true
        playerDefaultUI(false)
        stopUpdateProgress()
    }

    private fun resumePlayer() {
        mPlayer.seekTo(seekBar.progress)
        mPlayer.start()
        startUpdateProgress()
        playerIngUI()
    }

    private fun resetMediaPlayer() {
        isPausePlayer = false
        mPlayer.stop()
        mPlayer.reset()
    }

    private fun setCurrentPlayTime(progress: Int) {
        val time = formatDurationTime(progress.toLong())
        tvCurrentTime.text = time
    }


    private fun fastAudioPlay() {
        val progress: Long = seekBar.progress + maxBackFastMs
        if (progress >= seekBar.max) {
            seekBar.progress = seekBar.max
        } else {
            seekBar.progress = progress.toInt()
        }
        setCurrentPlayTime(seekBar.progress)
        mPlayer.seekTo(seekBar.progress)
    }

    private fun slowAudioPlay() {
        val progress: Long = seekBar.progress - maxBackFastMs
        if (progress <= 0) {
            seekBar.progress = 0
        } else {
            seekBar.progress = progress.toInt()
        }
        setCurrentPlayTime(seekBar.progress)
        mPlayer.seekTo(seekBar.progress)
    }


    private val mPlayCompletionListener = MediaPlayer.OnCompletionListener {
        stopUpdateProgress()
        resetMediaPlayer()
        playerDefaultUI(true)
    }


    private val mPlayErrorListener = MediaPlayer.OnErrorListener { _, _, _ ->
        resetMediaPlayer()
        playerDefaultUI(true)
        false
    }


    private val mPlayPreparedListener = MediaPlayer.OnPreparedListener { mp ->
        if (mp.isPlaying) {
            seekBar.max = mp.duration
            startUpdateProgress()
            playerIngUI()
        } else {
            stopUpdateProgress()
            resetMediaPlayer()
            playerDefaultUI(true)
        }
    }

    /**
     * 开始更新播放进度
     */
    private fun startUpdateProgress() {
        mHandler.post(mTickerRunnable)
    }

    /**
     * 停止更新播放进度
     */
    private fun stopUpdateProgress() {
        mHandler.removeCallbacks(mTickerRunnable)
    }

    /**
     * 默认UI样式
     *
     * @param isResetProgress 是否重置进度条
     */
    private fun playerDefaultUI(isResetProgress: Boolean) {
        stopUpdateProgress()
        if (isResetProgress) {
            seekBar.progress = 0
            tvCurrentTime.text = String.format("00:00")
        }
        setBackFastUI(false)
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_play)
        setPreviewVideoTitle(null)
    }

    /**
     * 播放中UI样式
     */
    private fun playerIngUI() {
        startUpdateProgress()
        setBackFastUI(true)
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_stop)
    }


    private fun setBackFastUI(isEnabled: Boolean) {
        ivPlayBack.isEnabled = isEnabled
        ivPlayFast.isEnabled = isEnabled
        if (isEnabled) {
            ivPlayBack.alpha = 1.0f
            ivPlayFast.alpha = 1.0f
        } else {
            ivPlayBack.alpha = 0.5f
            ivPlayFast.alpha = 0.5f
        }
    }


    override fun onViewAttachedToWindow() {
        isPausePlayer = false
        setMediaPlayerListener()
        playerDefaultUI(true)
    }

    override fun onViewDetachedFromWindow() {
        isPausePlayer = false
        mHandler.removeCallbacks(mTickerRunnable)
        setNullMediaPlayerListener()
        resetMediaPlayer()
        playerDefaultUI(true)
    }

    /**
     * resume and pause play
     */
    open fun resumePausePlay() {
        if (isPlaying()) {
            pausePlayer()
        } else {
            resumePlayer()
        }
    }

    private fun setMediaPlayerListener() {
        mPlayer.setOnCompletionListener(mPlayCompletionListener)
        mPlayer.setOnErrorListener(mPlayErrorListener)
        mPlayer.setOnPreparedListener(mPlayPreparedListener)
    }

    private fun setNullMediaPlayerListener() {
        mPlayer.setOnCompletionListener(null)
        mPlayer.setOnErrorListener(null)
        mPlayer.setOnPreparedListener(null)
    }

    override fun release() {
        mHandler.removeCallbacks(mTickerRunnable)
        setNullMediaPlayerListener()
        mPlayer.release()
    }
}