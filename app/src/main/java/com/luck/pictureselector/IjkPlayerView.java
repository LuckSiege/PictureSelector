package com.luck.pictureselector;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
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

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author：luck
 * @date：2022/7/1 23:57 上午
 * @describe：IjkPlayerView
 */
public class IjkPlayerView extends FrameLayout implements SurfaceHolder.Callback {
    private IjkVideoSurfaceView surfaceView;
    private IjkMediaPlayer mediaPlayer;

    public IjkPlayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public IjkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IjkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceView = new IjkVideoSurfaceView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        surfaceView.setLayoutParams(layoutParams);
        addView(surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }

    public IjkMediaPlayer initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new IjkMediaPlayer();
        }
        mediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mediaPlayer, int width, int height, int sar_num, int sar_den) {
                surfaceView.adjustVideoSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
            }
        });
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        return mediaPlayer;
    }

    public IjkMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void start(String path) {
        try {
            if (PictureMimeType.isContent(path)) {
                mediaPlayer.setDataSource(getContext(), Uri.parse(path));
            } else {
                mediaPlayer.setDataSource(path);
            }
            mediaPlayer.setDisplay(surfaceView.getHolder());
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

    public static class IjkVideoSurfaceView extends SurfaceView {
        /**
         * 视频宽度
         */
        private int videoWidth;
        /**
         * 视频高度
         */
        private int videoHeight;

        public IjkVideoSurfaceView(Context context) {
            this(context, null);
        }

        public IjkVideoSurfaceView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public IjkVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void clearCanvas() {
        surfaceView.getHolder().setFormat(PixelFormat.OPAQUE);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }
}
