package com.luck.picture.lib.adapter.holder;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;


/**
 * @author：luck
 * @date：2021/12/15 5:12 下午
 * @describe：PreviewVideoHolder
 */
public class PreviewVideoHolder extends BasePreviewHolder {
    public ImageView ivPlayButton;
    public VideoView videoView;

    public PreviewVideoHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        videoView = itemView.findViewById(R.id.video_view);
        ivPlayButton.setVisibility(PictureSelectionConfig.getInstance().isPreviewScaleMode ? View.GONE : View.VISIBLE);
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        String path = media.getAvailablePath();
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivPlayButton.getVisibility() == View.VISIBLE) {
                    ivPlayButton.setVisibility(View.GONE);
                }
                if (coverImageView.getVisibility() == View.VISIBLE) {
                    coverImageView.setVisibility(View.GONE);
                }
                if (videoView.getVisibility() == View.GONE) {
                    videoView.setVisibility(View.VISIBLE);
                }
                if (PictureMimeType.isContent(path)) {
                    videoView.setVideoURI(Uri.parse(path));
                } else {
                    if (TextUtils.isEmpty(media.getSandboxPath())) {
                        videoView.setVideoPath(media.getPath());
                    } else {
                        videoView.setVideoPath(media.getSandboxPath());
                    }
                }
                videoView.start();
                mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onBackPressed();
                        }
                    }
                });
            }
        });
    }


    /**
     * 给 VideoView绑定监听器
     */
    public void addVideoListener() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ivPlayButton.setVisibility(View.VISIBLE);
                coverImageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onPreviewVideoTitle(null);
                }
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (ivPlayButton.getVisibility() == View.GONE) {
                    ivPlayButton.setVisibility(View.VISIBLE);
                }
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onPreviewVideoTitle(null);
                }
                if (videoView.getVisibility() == View.VISIBLE) {
                    videoView.setVisibility(View.GONE);
                }
                if (coverImageView.getVisibility() == View.GONE) {
                    coverImageView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            videoView.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    /**
     * 释放VideoView
     */
    public void releaseVideo() {
        videoView.stopPlayback();
        coverImageView.setVisibility(View.VISIBLE);
        ivPlayButton.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        videoView.setOnErrorListener(null);
        videoView.setOnCompletionListener(null);
        videoView.setOnPreparedListener(null);
    }
}
