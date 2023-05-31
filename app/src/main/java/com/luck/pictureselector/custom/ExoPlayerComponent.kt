package com.luck.pictureselector.custom

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.luck.picture.lib.interfaces.OnPlayerListener
import com.luck.picture.lib.player.IMediaPlayer
import com.luck.picture.lib.utils.MediaUtils
import java.io.File

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Google ExoPlayer Component
 */
class ExoPlayerComponent(context: Context) : StyledPlayerView(context), IMediaPlayer {
    private var playerListener: OnPlayerListener? = null

    private val exoPlayerListener: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            playerListener?.onPlayerError()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    playerListener?.onPlayerReady()
                }
                Player.STATE_BUFFERING -> {
                    playerListener?.onPlayerLoading()
                }
                Player.STATE_ENDED -> {
                    playerListener?.onPlayerComplete()
                }
                else -> {
                }
            }
        }
    }

    override fun initMediaPlayer(l: OnPlayerListener?) {
        playerListener = l
        player = ExoPlayer.Builder(context).build()
        player?.addListener(exoPlayerListener)
        useController = false
    }

    override fun onStart(path: String, isLoopAutoPlay: Boolean) {
        val mediaItem: MediaItem = when {
            MediaUtils.isContent(path) -> {
                MediaItem.fromUri(Uri.parse(path))
            }
            MediaUtils.isHasHttp(path) -> {
                MediaItem.fromUri(path)
            }
            else -> {
                MediaItem.fromUri(Uri.fromFile(File(path)))
            }
        }
        player?.repeatMode =
            if (isLoopAutoPlay) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onResume() {
        player?.play()
    }

    override fun onPause() {
        player?.pause()
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    override fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return player?.duration ?: 0L
    }

    override fun destroy() {
        player?.release()
        player?.removeListener(exoPlayerListener)
        player = null
        playerListener = null
    }
}