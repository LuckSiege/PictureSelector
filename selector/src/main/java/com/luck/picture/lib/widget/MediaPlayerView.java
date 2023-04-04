package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureMimeType;

import java.io.IOException;

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayerView
 */
public class MediaPlayerView extends FrameLayout implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private VideoSurfaceView surfaceView;

    public MediaPlayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceView = new VideoSurfaceView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        surfaceView.setLayoutParams(layoutParams);

        addView(surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }

    public MediaPlayer initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                surfaceView.adjustVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
            }
        });
        return mediaPlayer;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public VideoSurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void start(String path) {
        try {
            if (PictureMimeType.isContent(path)) {
                mediaPlayer.setDataSource(getContext(), Uri.parse(path));
            } else {
                mediaPlayer.setDataSource(path);
            }
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }


    public void clearCanvas() {
        surfaceView.getHolder().setFormat(PixelFormat.OPAQUE);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public static class VideoSurfaceView extends SurfaceView {
        /**
         * 视频宽度
         */
        private int videoWidth;
        /**
         * 视频高度
         */
        private int videoHeight;

        public VideoSurfaceView(Context context) {
            this(context, null);
        }

        public VideoSurfaceView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void adjustVideoSize(int videoWidth, int videoHeight) {
            if (videoWidth == 0 || videoHeight == 0) {
                return;
            }
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            getHolder().setFixedSize(videoWidth, videoHeight);
            requestLayout();
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = getDefaultSize(videoWidth, widthMeasureSpec);
            int height = getDefaultSize(videoHeight, heightMeasureSpec);
            if (videoWidth > 0 && videoHeight > 0) {
                int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
                int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
                int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
                if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize;
                    height = heightSpecSize;
                    if (videoWidth * height < width * videoHeight) {
                        width = height * videoWidth / videoHeight;
                    } else if (videoWidth * height > width * videoHeight) {
                        height = width * videoHeight / videoWidth;
                    }
                } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize;
                    }
                } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize;
                    }
                } else {
                    width = videoWidth;
                    height = videoHeight;
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize;
                        width = height * videoWidth / videoHeight;
                    }
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize;
                        height = width * videoHeight / videoWidth;
                    }
                }
            }
            setMeasuredDimension(width, height);
        }
    }


    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer = null;
        }
    }
}
