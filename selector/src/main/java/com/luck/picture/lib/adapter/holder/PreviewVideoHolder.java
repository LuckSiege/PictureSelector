package com.luck.picture.lib.adapter.holder;

import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
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
    public StyledPlayerView mPlayerView;
    public ProgressBar progress;

    public PreviewVideoHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        mPlayerView = itemView.findViewById(R.id.playerView);
        progress = itemView.findViewById(R.id.progress);
        mPlayerView.setUseController(false);
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        ivPlayButton.setVisibility(config.isPreviewZoomEffect ? View.GONE : View.VISIBLE);
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        String path = media.getAvailablePath();
        setScaleDisplaySize(media);
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player player = mPlayerView.getPlayer();
                if (player != null) {
                    progress.setVisibility(View.VISIBLE);
                    ivPlayButton.setVisibility(View.GONE);
                    mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                    MediaItem mediaItem;
                    if (PictureMimeType.isContent(path)) {
                        mediaItem = MediaItem.fromUri(Uri.parse(path));
                    } else if (PictureMimeType.isHasHttp(path)) {
                        mediaItem = MediaItem.fromUri(path);
                    } else {
                        mediaItem = MediaItem.fromUri(Uri.fromFile(new File(path)));
                    }
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                }
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

    @Override
    protected void setScaleDisplaySize(LocalMedia media) {
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            float ratio;
            if (media.getWidth() > media.getHeight()) {
                ratio = (float) media.getHeight() / (float) media.getWidth();
            } else {
                ratio = (float) media.getWidth() / (float) media.getHeight();
            }
            int displayHeight = (int) (screenWidth / ratio);
            FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) mPlayerView.getLayoutParams();
            playerLayoutParams.width = screenWidth;
            playerLayoutParams.height = displayHeight > screenHeight ? screenAppInHeight : screenHeight;
            playerLayoutParams.gravity = Gravity.CENTER;

            FrameLayout.LayoutParams coverLayoutParams = (FrameLayout.LayoutParams) coverImageView.getLayoutParams();
            coverLayoutParams.width = screenWidth;
            coverLayoutParams.height = displayHeight > screenHeight ? screenAppInHeight : screenHeight;
            coverLayoutParams.gravity = Gravity.CENTER;
        }
    }

    private final Player.Listener mPlayerListener = new Player.Listener() {
        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            playerDefaultUI();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY) {
                playerIngUI();
            } else if (playbackState == Player.STATE_BUFFERING) {
                progress.setVisibility(View.VISIBLE);
            } else if (playbackState == Player.STATE_ENDED) {
                playerDefaultUI();
            }
        }
    };

    private void playerDefaultUI() {
        ivPlayButton.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        coverImageView.setVisibility(View.VISIBLE);
        mPlayerView.setVisibility(View.GONE);
        if (mPreviewEventListener != null) {
            mPreviewEventListener.onPreviewVideoTitle(null);
        }
    }

    private void playerIngUI() {
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
    }

    @Override
    public void onViewAttachedToWindow() {
        Player player = new ExoPlayer.Builder(itemView.getContext()).build();
        mPlayerView.setPlayer(player);
        player.addListener(mPlayerListener);
    }

    @Override
    public void onViewDetachedFromWindow() {
        Player player = mPlayerView.getPlayer();
        if (player != null) {
            player.removeListener(mPlayerListener);
            player.release();
            mPlayerView.setPlayer(null);
            playerDefaultUI();
        }
    }

    /**
     * 释放VideoView
     */
    public void releaseVideo() {
        Player player = mPlayerView.getPlayer();
        if (player != null) {
            player.removeListener(mPlayerListener);
            player.release();
        }
    }
}
