package com.luck.picture.lib.adapter.holder;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;


/**
 * @author：luck
 * @date：2021/12/15 5:12 下午
 * @describe：PreviewVideoHolder
 */
public class PreviewVideoHolder extends BasePreviewHolder {
    public ImageView ivPlayButton;
    public PlayerView mPlayerView;
    public ProgressBar progress;

    public PreviewVideoHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        mPlayerView = itemView.findViewById(R.id.playerView);
        progress = itemView.findViewById(R.id.progress);
        ivPlayButton.setVisibility(PictureSelectionConfig.getInstance().isPreviewZoomEffect ? View.GONE : View.VISIBLE);
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        String path = media.getAvailablePath();
        ExoPlayer player = new ExoPlayer.Builder(itemView.getContext()).build();
        mPlayerView.setPlayer(player);
        mPlayerView.setUseController(false);
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaItem mediaItem = PictureMimeType.isContent(path) ? MediaItem.fromUri(Uri.parse(path))
                        : MediaItem.fromUri(Uri.fromFile(new File(path)));
                progress.setVisibility(View.VISIBLE);
                ivPlayButton.setVisibility(View.GONE);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
                mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            if (progress.getVisibility() == View.VISIBLE) {
                                progress.setVisibility(View.GONE);
                            }
                            if (ivPlayButton.getVisibility() == View.VISIBLE) {
                                ivPlayButton.setVisibility(View.GONE);
                            }
                            if (coverImageView.getVisibility() == View.VISIBLE) {
                                coverImageView.setVisibility(View.GONE);
                            }
                            if (mPlayerView.getVisibility() == View.GONE) {
                                mPlayerView.setVisibility(View.VISIBLE);
                            }
                        } else if (playbackState == Player.STATE_ENDED) {
                            ivPlayButton.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                            coverImageView.setVisibility(View.VISIBLE);
                            mPlayerView.setVisibility(View.GONE);
                            if (mPreviewEventListener != null) {
                                mPreviewEventListener.onPreviewVideoTitle(null);
                            }
                        }
                    }
                });
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    /**
     * 释放VideoView
     */
    public void releaseVideo() {
        if (mPlayerView.getPlayer() != null) {
            mPlayerView.getPlayer().release();
        }
    }
}
