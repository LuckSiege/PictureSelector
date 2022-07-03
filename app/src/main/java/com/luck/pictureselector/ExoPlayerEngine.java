package com.luck.pictureselector;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnPlayerListener;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author：luck
 * @date：2022/7/1 22:26 下午
 * @describe：ExoPlayerEngine
 */
public class ExoPlayerEngine implements VideoPlayerEngine<StyledPlayerView> {
    /**
     * 播放状态监听器集
     */
    private final CopyOnWriteArrayList<OnPlayerListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public View onCreateVideoPlayer(Context context) {
        StyledPlayerView exoPlayer = new StyledPlayerView(context);
        exoPlayer.setUseController(false);
        return exoPlayer;
    }

    @Override
    public void onStarPlayer(StyledPlayerView exoPlayer, LocalMedia media) {
        Player player = exoPlayer.getPlayer();
        if (player != null) {
            MediaItem mediaItem;
            String path = media.getAvailablePath();
            if (PictureMimeType.isContent(path)) {
                mediaItem = MediaItem.fromUri(Uri.parse(path));
            } else if (PictureMimeType.isHasHttp(path)) {
                mediaItem = MediaItem.fromUri(path);
            } else {
                mediaItem = MediaItem.fromUri(Uri.fromFile(new File(path)));
            }
            PictureSelectionConfig config = PictureSelectionConfig.getInstance();
            player.setRepeatMode(config.isLoopAutoPlay ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    @Override
    public void onResume(StyledPlayerView exoPlayer) {
        Player player = exoPlayer.getPlayer();
        if (player != null) {
            player.play();
        }
    }

    @Override
    public void onPause(StyledPlayerView exoPlayer) {
        Player player = exoPlayer.getPlayer();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public boolean isPlaying(StyledPlayerView exoPlayer) {
        Player player = exoPlayer.getPlayer();
        return player != null && player.isPlaying();
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
    public void onPlayerAttachedToWindow(StyledPlayerView exoPlayer) {
        Player player = new ExoPlayer.Builder(exoPlayer.getContext()).build();
        exoPlayer.setPlayer(player);
        player.addListener(mPlayerListener);
    }

    @Override
    public void onPlayerDetachedFromWindow(StyledPlayerView exoPlayer) {
        Player player = exoPlayer.getPlayer();
        if (player != null) {
            player.removeListener(mPlayerListener);
            player.release();
            exoPlayer.setPlayer(null);
        }
    }

    @Override
    public void destroy(StyledPlayerView exoPlayer) {
        Player player = exoPlayer.getPlayer();
        if (player != null) {
            player.removeListener(mPlayerListener);
            player.release();
        }
    }

    /**
     * ExoPlayer播放状态回调
     */
    private final Player.Listener mPlayerListener = new Player.Listener() {
        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            for (int i = 0; i < listeners.size(); i++) {
                OnPlayerListener playerListener = listeners.get(i);
                playerListener.onPlayerError();
            }
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerReady();
                }
            } else if (playbackState == Player.STATE_BUFFERING) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerLoading();
                }
            } else if (playbackState == Player.STATE_ENDED) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerEnd();
                }
            }
        }
    };
}
