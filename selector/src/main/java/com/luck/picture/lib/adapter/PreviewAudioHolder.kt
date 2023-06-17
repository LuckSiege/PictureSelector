package com.luck.picture.lib.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.player.AbsController
import com.luck.picture.lib.player.AudioController
import com.luck.picture.lib.player.AudioMediaPlayer
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.FileUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：PreviewAudioHolder
 */
open class PreviewAudioHolder(itemView: View) : BasePreviewMediaHolder(itemView) {
    private var tvAudioName: TextView = itemView.findViewById(R.id.tv_audio_name)
    var mediaPlayer = AudioMediaPlayer()
    var controller = this.onCreateAudioController()

    init {
        this.attachComponent(itemView as ViewGroup)
    }

    open fun attachComponent(group: ViewGroup) {
        group.addView(controller as View)
    }

    /**
     * Create custom player audio controller
     */
    open fun onCreateAudioController(): AbsController {
        return AudioController(itemView.context).apply {
            this.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                this.addRule(RelativeLayout.BELOW, R.id.tv_audio_name)
            }
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
        controller.setDataSource(media)
        controller.setIMediaPlayer(mediaPlayer)
        controller.setOnPlayStateListener(playStateListener)
        controller.setOnSeekBarChangeListener(seekBarChangeListener)
        itemView.setOnClickListener {
            setClickEvent(media)
        }
        itemView.setOnLongClickListener {
            setLongClickEvent(this, position, media)
            return@setOnLongClickListener false
        }
    }

    private val playStateListener = object : AbsController.OnPlayStateListener {
        override fun onPlayState(isPlaying: Boolean) {

        }
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    open fun onPlayingAudioState() {
        controller.start()
    }

    open fun onDefaultAudioState() {
        controller.stop(true)
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
        controller.setOnPlayStateListener(null)
        controller.setOnSeekBarChangeListener(null)
        onDefaultAudioState()
    }
}