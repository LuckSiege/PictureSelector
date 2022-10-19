package com.luck.pictureselector;

import android.content.Context;
import android.view.View;

import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnPlayerListener;

import java.util.concurrent.CopyOnWriteArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author：luck
 * @date：2022/7/1 21:52 上午
 * @describe：IjkPlayerEngine
 */
public class IjkPlayerEngine implements VideoPlayerEngine<IjkPlayerView> {
    /**
     * 播放状态监听器集
     */
    private final CopyOnWriteArrayList<OnPlayerListener> listeners = new CopyOnWriteArrayList<>();


    @Override
    public View onCreateVideoPlayer(Context context) {
        return new IjkPlayerView(context);
    }

    @Override
    public void onStarPlayer(IjkPlayerView player, LocalMedia media) {
        IjkMediaPlayer mediaPlayer = player.getMediaPlayer();
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        mediaPlayer.setLooping(config.isLoopAutoPlay);
        player.start(media.getAvailablePath());
    }

    @Override
    public void onResume(IjkPlayerView player) {
        IjkMediaPlayer mediaPlayer = player.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onPause(IjkPlayerView player) {
        IjkMediaPlayer mediaPlayer = player.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public boolean isPlaying(IjkPlayerView player) {
        IjkMediaPlayer mediaPlayer = player.getMediaPlayer();
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public void addPlayListener(OnPlayerListener playerListener) {
        if (!listeners.contains(playerListener)) {
            listeners.add(playerListener);
        }
    }

    @Override
    public void removePlayListener(OnPlayerListener playerListener) {
        if (playerListener != null) {
            listeners.remove(playerListener);
        } else {
            listeners.clear();
        }
    }

    @Override
    public void onPlayerAttachedToWindow(IjkPlayerView player) {
        IjkMediaPlayer mediaPlayer = player.initMediaPlayer();
        mediaPlayer.setOnPreparedListener(new IjkMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(IMediaPlayer mediaPlayer) {
                mediaPlayer.start();
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerReady();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new IjkMediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(IMediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerEnd();
                }
                player.clearCanvas();
            }
        });

        mediaPlayer.setOnErrorListener(new IjkMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(IMediaPlayer mediaPlayer, int what, int extra) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerError();
                }
                return false;
            }
        });
    }

    @Override
    public void onPlayerDetachedFromWindow(IjkPlayerView player) {
        player.release();
    }

    @Override
    public void destroy(IjkPlayerView player) {
        player.release();
    }
}
