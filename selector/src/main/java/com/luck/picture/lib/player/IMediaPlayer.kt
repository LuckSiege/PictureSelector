package com.luck.picture.lib.player

import android.content.Context

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player General Function Behavior
 */
interface IMediaPlayer {
    fun initMediaPlayer()
    fun setDataSource(context: Context, path: String, isLoopAutoPlay: Boolean)
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun seekTo(speed: Int)
    fun start()
    fun resume()
    fun pause()
    fun isPlaying(): Boolean
    fun stop()
    fun reset()
    fun release()
    fun setOnInfoListener(listener: OnInfoListener?)
    fun setOnErrorListener(listener: OnErrorListener?)
    fun setOnPreparedListener(listener: OnPreparedListener?)
    fun setOnCompletionListener(listener: OnCompletionListener?)
    fun setOnVideoSizeChangedListener(listener: OnVideoSizeChangedListener?)

    interface OnInfoListener {
        fun onInfo(mp: IMediaPlayer?, what: Int, extra: Int): Boolean
    }

    interface OnVideoSizeChangedListener {
        fun onVideoSizeChanged(mp: IMediaPlayer?, width: Int, height: Int)
    }

    interface OnPreparedListener {
        fun onPrepared(mp: IMediaPlayer?)
    }

    interface OnCompletionListener {
        fun onCompletion(mp: IMediaPlayer?)
    }

    interface OnErrorListener {
        fun onError(mp: IMediaPlayer?, what: Int, extra: Int): Boolean
    }
}