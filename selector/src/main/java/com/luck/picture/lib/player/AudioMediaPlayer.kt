package com.luck.picture.lib.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Audio MediaPlayer
 */
class AudioMediaPlayer : IMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null

    override fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
    }

    override fun setDataSource(context: Context, path: String, isLoopAutoPlay: Boolean) {
        if (MediaUtils.isContent(path)) {
            mediaPlayer?.setDataSource(context, Uri.parse(path))
        } else {
            mediaPlayer?.setDataSource(path)
        }
        mediaPlayer?.isLooping = isLoopAutoPlay
        mediaPlayer?.prepareAsync()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    override fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    override fun seekTo(speed: Int) {
        mediaPlayer?.seekTo(speed)
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun resume() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun reset() {
        mediaPlayer?.reset()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener?) {
        if (listener != null) {
            mediaPlayer?.setOnInfoListener { mp, what, extra ->
                listener.onInfo(this, what, extra)
            }
        } else {
            mediaPlayer?.setOnInfoListener(null)
        }
    }

    override fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener?) {
        if (listener != null) {
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                listener.onError(this, what, extra)
            }
        } else {
            mediaPlayer?.setOnErrorListener(null)
        }
    }

    override fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener?) {
        if (listener != null) {
            mediaPlayer?.setOnPreparedListener {
                listener.onPrepared(this)
            }
        } else {
            mediaPlayer?.setOnPreparedListener(null)
        }
    }

    override fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener?) {
        if (listener != null) {
            mediaPlayer?.setOnCompletionListener {
                listener.onCompletion(this)
            }
        } else {
            mediaPlayer?.setOnCompletionListener(null)
        }
    }

    override fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener?) {
    }
}