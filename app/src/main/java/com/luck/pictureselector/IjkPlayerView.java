package com.luck.pictureselector;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
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
public class IjkPlayerView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private IjkVideoTextureView textureView;
    private IjkMediaPlayer mediaPlayer;
    private int mVideoRotation;

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
        textureView = new IjkVideoTextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        textureView.setLayoutParams(layoutParams);
        addView(textureView);
    }

    public IjkMediaPlayer initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new IjkMediaPlayer();
        }
        mediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mediaPlayer, int width, int height, int sar_num, int sar_den) {
                textureView.adjustVideoSize(width, height, mVideoRotation);
            }
        });
        mediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == 10001) {
                    mVideoRotation = extra;
                }
                return false;
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
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture != null) {
                mediaPlayer.setSurface(new Surface(surfaceTexture));
            }
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mediaPlayer.setSurface(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    public static class IjkVideoTextureView extends TextureView {
        /**
         * 视频宽度
         */
        private int mVideoWidth;
        /**
         * 视频高度
         */
        private int mVideoHeight;

        /**
         * 视频旋转角度
         */
        private int mVideoRotation;

        public IjkVideoTextureView(@NonNull Context context) {
            super(context);
        }

        public void adjustVideoSize(int videoWidth, int videoHeight, int videoRotation) {
            this.mVideoWidth = videoWidth;
            this.mVideoHeight = videoHeight;
            this.mVideoRotation = videoRotation;
            this.setRotation(mVideoRotation);
            this.requestLayout();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
            int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize;
                    height = heightSpecSize;

                    if (mVideoWidth * height < width * mVideoHeight) {
                        width = height * mVideoWidth / mVideoHeight;
                    } else if (mVideoWidth * height > width * mVideoHeight) {
                        height = width * mVideoHeight / mVideoWidth;
                    }
                } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize;
                    }
                } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize;
                    }
                } else {
                    width = mVideoWidth;
                    height = mVideoHeight;
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize;
                        width = height * mVideoWidth / mVideoHeight;
                    }
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize;
                        height = width * mVideoHeight / mVideoWidth;
                    }
                }
            }
            setMeasuredDimension(width, height);
            if ((mVideoRotation + 180) % 180 != 0) {
                int[] size = scaleSize(widthSpecSize, heightSpecSize, height, width);
                setScaleX(size[0] / ((float) height));
                setScaleY(size[1] / ((float) width));
            }
        }
    }

    public static int[] scaleSize(int textureWidth, int textureHeight, int realWidth, int realHeight) {
        float deviceRate = (float) textureWidth / (float) textureHeight;
        float rate = (float) realWidth / (float) realHeight;
        int width;
        int height;
        if (rate < deviceRate) {
            height = textureHeight;
            width = (int) (textureHeight * rate);
        } else {
            width = textureWidth;
            height = (int) (textureWidth / rate);
        }
        return new int[]{width, height};
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer = null;
        }
    }

    public void clearCanvas() {
    }
}
