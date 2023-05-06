package com.luck.picture.lib.adapter.holder;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.utils.PictureFileUtils;

import java.io.IOException;

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewAudioHolder
 */
public class PreviewAudioHolder extends BasePreviewHolder {
    private static final long MAX_BACK_FAST_MS = 3 * 1000;
    private static final long MAX_UPDATE_INTERVAL_MS = 1000;
    private static final long MIN_CURRENT_POSITION = 1000;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public ImageView ivPlayButton;
    public TextView tvAudioName;
    public TextView tvTotalDuration;
    public TextView tvCurrentTime;
    public SeekBar seekBar;
    public ImageView ivPlayBack, ivPlayFast;
    private MediaPlayer mPlayer = new MediaPlayer();
    private boolean isPausePlayer = false;

    /**
     * 播放计时器
     */
    public Runnable mTickerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentPosition = mPlayer.getCurrentPosition();
            String time = DateUtils.formatDurationTime(currentPosition);
            if (!TextUtils.equals(time, tvCurrentTime.getText())) {
                tvCurrentTime.setText(time);
                if (mPlayer.getDuration() - currentPosition > MIN_CURRENT_POSITION) {
                    seekBar.setProgress((int) currentPosition);
                } else {
                    seekBar.setProgress(mPlayer.getDuration());
                }
            }
            long nextSecondMs = MAX_UPDATE_INTERVAL_MS - currentPosition % MAX_UPDATE_INTERVAL_MS;
            mHandler.postDelayed(this, nextSecondMs);
        }
    };

    public PreviewAudioHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        tvAudioName = itemView.findViewById(R.id.tv_audio_name);
        tvCurrentTime = itemView.findViewById(R.id.tv_current_time);
        tvTotalDuration = itemView.findViewById(R.id.tv_total_duration);
        seekBar = itemView.findViewById(R.id.music_seek_bar);
        ivPlayBack = itemView.findViewById(R.id.iv_play_back);
        ivPlayFast = itemView.findViewById(R.id.iv_play_fast);
    }

    @Override
    protected void findViews(View itemView) {
    }

    @Override
    protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
        tvAudioName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ps_ic_audio_play_cover, 0, 0);
    }

    @Override
    protected void onClickBackPressed() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onLongPressDownload(LocalMedia media) {
        coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        String path = media.getAvailablePath();
        String dataFormat = DateUtils.getYearDataFormat(media.getDateAddedTime());
        String fileSize = PictureFileUtils.formatAccurateUnitFileSize(media.getSize());
        loadImage(media, PictureConfig.UNSET, PictureConfig.UNSET);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(media.getFileName()).append("\n").append(dataFormat).append(" - ").append(fileSize);
        SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());
        String indexOfStr = dataFormat + " - " + fileSize;
        int startIndex = stringBuilder.indexOf(indexOfStr);
        int endOf = startIndex + indexOfStr.length();
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(itemView.getContext(), 12)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF656565), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAudioName.setText(builder);
        tvTotalDuration.setText(DateUtils.formatDurationTime(media.getDuration()));
        seekBar.setMax((int) media.getDuration());
        setBackFastUI(false);
        ivPlayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slowAudioPlay();
            }
        });

        ivPlayFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastAudioPlay();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    setCurrentPlayTime(progress);
                    if (isPlaying()) {
                        mPlayer.seekTo(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (DoubleUtils.isFastDoubleClick()) {
                        return;
                    }
                    mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                    if (isPlaying()) {
                        pausePlayer();
                    } else {
                        if (isPausePlayer) {
                            resumePlayer();
                        } else {
                            startPlayer(path);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    /**
     * 重新开始播放
     *
     * @param path
     */
    private void startPlayer(String path) {
        try {
            if (PictureMimeType.isContent(path)) {
                mPlayer.setDataSource(itemView.getContext(), Uri.parse(path));
            } else {
                mPlayer.setDataSource(path);
            }
            mPlayer.prepare();
            mPlayer.seekTo(seekBar.getProgress());
            mPlayer.start();
            isPausePlayer = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    /**
     * 暂停播放
     */
    private void pausePlayer() {
        mPlayer.pause();
        isPausePlayer = true;
        playerDefaultUI(false);
        stopUpdateProgress();
    }

    /**
     * 恢复播放
     */
    private void resumePlayer() {
        mPlayer.seekTo(seekBar.getProgress());
        mPlayer.start();
        startUpdateProgress();
        playerIngUI();
    }

    /**
     * 重置播放器
     */
    private void resetMediaPlayer() {
        isPausePlayer = false;
        mPlayer.stop();
        mPlayer.reset();
    }

    /**
     * 设置当前播放进度
     *
     * @param progress
     */
    private void setCurrentPlayTime(int progress) {
        String time = DateUtils.formatDurationTime( progress);
        tvCurrentTime.setText(time);
    }

    /**
     * 快进
     */
    private void fastAudioPlay() {
        long progress = seekBar.getProgress() + MAX_BACK_FAST_MS;
        if (progress >= seekBar.getMax()) {
            seekBar.setProgress(seekBar.getMax());
        } else {
            seekBar.setProgress((int) progress);
        }
        setCurrentPlayTime(seekBar.getProgress());
        mPlayer.seekTo(seekBar.getProgress());
    }

    /**
     * 回退
     */
    private void slowAudioPlay() {
        long progress = seekBar.getProgress() - MAX_BACK_FAST_MS;
        if (progress <= 0) {
            seekBar.setProgress(0);
        } else {
            seekBar.setProgress((int) progress);
        }
        setCurrentPlayTime(seekBar.getProgress());
        mPlayer.seekTo(seekBar.getProgress());
    }

    /**
     * 播放完成监听
     */
    private final MediaPlayer.OnCompletionListener mPlayCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopUpdateProgress();
            resetMediaPlayer();
            playerDefaultUI(true);
        }
    };

    /**
     * 播放失败监听
     */
    private final MediaPlayer.OnErrorListener mPlayErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            resetMediaPlayer();
            playerDefaultUI(true);
            return false;
        }
    };

    /**
     * 资源装载完成
     */
    private final MediaPlayer.OnPreparedListener mPlayPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mp.isPlaying()) {
                seekBar.setMax(mp.getDuration());
                startUpdateProgress();
                playerIngUI();
            } else {
                stopUpdateProgress();
                resetMediaPlayer();
                playerDefaultUI(true);
            }
        }
    };

    /**
     * 开始更新播放进度
     */
    private void startUpdateProgress() {
        mHandler.post(mTickerRunnable);
    }

    /**
     * 停止更新播放进度
     */
    private void stopUpdateProgress() {
        mHandler.removeCallbacks(mTickerRunnable);
    }

    /**
     * 默认UI样式
     *
     * @param isResetProgress 是否重置进度条
     */
    private void playerDefaultUI(boolean isResetProgress) {
        stopUpdateProgress();
        if (isResetProgress) {
            seekBar.setProgress(0);
            tvCurrentTime.setText("00:00");
        }
        setBackFastUI(false);
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_play);
        if (mPreviewEventListener != null) {
            mPreviewEventListener.onPreviewVideoTitle(null);
        }
    }

    /**
     * 播放中UI样式
     */
    private void playerIngUI() {
        startUpdateProgress();
        setBackFastUI(true);
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_stop);
    }

    /**
     * 设置快进和回退UI样式
     *
     * @param isEnabled
     */
    private void setBackFastUI(boolean isEnabled) {
        ivPlayBack.setEnabled(isEnabled);
        ivPlayFast.setEnabled(isEnabled);
        if (isEnabled) {
            ivPlayBack.setAlpha(1.0F);
            ivPlayFast.setAlpha(1.0F);
        } else {
            ivPlayBack.setAlpha(0.5F);
            ivPlayFast.setAlpha(0.5F);
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        isPausePlayer = false;
        setMediaPlayerListener();
        playerDefaultUI(true);
    }

    @Override
    public void onViewDetachedFromWindow() {
        isPausePlayer = false;
        mHandler.removeCallbacks(mTickerRunnable);
        setNullMediaPlayerListener();
        resetMediaPlayer();
        playerDefaultUI(true);
    }

    /**
     * resume and pause play
     */
    @Override
    public void resumePausePlay() {
        if (isPlaying()) {
            pausePlayer();
        } else {
            resumePlayer();
        }
    }

    @Override
    public void release() {
        mHandler.removeCallbacks(mTickerRunnable);
        if (mPlayer != null) {
            setNullMediaPlayerListener();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 设置监听器
     */
    private void setMediaPlayerListener() {
        mPlayer.setOnCompletionListener(mPlayCompletionListener);
        mPlayer.setOnErrorListener(mPlayErrorListener);
        mPlayer.setOnPreparedListener(mPlayPreparedListener);
    }

    /**
     * 置空监听器
     */
    private void setNullMediaPlayerListener() {
        mPlayer.setOnCompletionListener(null);
        mPlayer.setOnErrorListener(null);
        mPlayer.setOnPreparedListener(null);
    }
}
