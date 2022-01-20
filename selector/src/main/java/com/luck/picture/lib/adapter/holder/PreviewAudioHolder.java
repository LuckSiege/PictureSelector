package com.luck.picture.lib.adapter.holder;

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

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.PictureFileUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

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
    public PlayerView mPlayerView;
    public ImageView ivPlayButton;
    public TextView tvAudioName;
    public TextView tvTotalDuration;
    public TextView tvCurrentTime;
    public SeekBar seekBar;
    public ImageView ivPlayBack, ivPlayFast;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    /**
     * 播放计时器
     */
    public Runnable mTickerRunnable = new Runnable() {
        @Override
        public void run() {
            Player player = mPlayerView.getPlayer();
            if (player != null) {
                long currentPosition = player.getCurrentPosition();
                String time = DateUtils.getStringForTime(formatBuilder, formatter, currentPosition);
                if (!TextUtils.equals(time, tvCurrentTime.getText())) {
                    tvCurrentTime.setText(time);
                    if (player.getContentDuration() - currentPosition > MIN_CURRENT_POSITION) {
                        seekBar.setProgress((int) currentPosition);
                    } else {
                        seekBar.setProgress((int) player.getContentDuration());
                    }
                }
                long nextSecondMs = MAX_UPDATE_INTERVAL_MS - currentPosition % MAX_UPDATE_INTERVAL_MS;
                mHandler.postDelayed(this, nextSecondMs);
            } else {
                mHandler.removeCallbacks(this);
            }
        }
    };

    public PreviewAudioHolder(@NonNull View itemView) {
        super(itemView);
        mPlayerView = itemView.findViewById(R.id.playerView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        tvAudioName = itemView.findViewById(R.id.tv_audio_name);
        tvCurrentTime = itemView.findViewById(R.id.tv_current_time);
        tvTotalDuration = itemView.findViewById(R.id.tv_total_duration);
        seekBar = itemView.findViewById(R.id.music_seek_bar);
        ivPlayBack = itemView.findViewById(R.id.iv_play_back);
        ivPlayFast = itemView.findViewById(R.id.iv_play_fast);
        mPlayerView.setUseController(false);
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        String path = media.getAvailablePath();
        String dataFormat = DateUtils.getYearDataFormat(media.getDateAddedTime());
        String fileSize = PictureFileUtils.formatFileSize(media.getSize(), 2);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(media.getFileName()).append("\n").append(dataFormat).append(" - ").append(fileSize);
        SpannableStringBuilder builder = new SpannableStringBuilder(stringBuilder.toString());
        String indexOfStr = dataFormat + " - " + fileSize;
        int startIndex = stringBuilder.indexOf(indexOfStr);
        int endOf = startIndex + indexOfStr.length();
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(itemView.getContext(), 12)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF656565), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAudioName.setText(builder);
        seekBar.setMax((int) media.getDuration());
        tvTotalDuration.setText(DateUtils.formatDurationTime(media.getDuration()));
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
                    Player player = mPlayerView.getPlayer();
                    if (player != null && player.getPlaybackState() == Player.STATE_READY) {
                        player.seekTo(seekBar.getProgress());
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
                Player player = mPlayerView.getPlayer();
                if (player != null) {
                    stopUpdateProgress();
                    mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                    if (player.getPlaybackState() == Player.STATE_READY) {
                        if (player.isPlaying()) {
                            player.pause();
                        } else {
                            player.play();
                        }
                    } else {
                        MediaItem mediaItem = PictureMimeType.isContent(path)
                                ? MediaItem.fromUri(Uri.parse(path)) : MediaItem.fromUri(Uri.fromFile(new File(path)));
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        player.seekTo(seekBar.getProgress());
                        player.play();
                    }
                }
            }
        });

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

    /**
     * 设置当前播放进度
     *
     * @param progress
     */
    private void setCurrentPlayTime(int progress) {
        String time = DateUtils.getStringForTime(formatBuilder, formatter, progress);
        tvCurrentTime.setText(time);
    }

    /**
     * 快进
     */
    private void fastAudioPlay() {
        Player player = mPlayerView.getPlayer();
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            if (seekBar.getProgress() > MAX_BACK_FAST_MS) {
                seekBar.setProgress(seekBar.getMax());
            } else {
                seekBar.setProgress((int) (seekBar.getProgress() + MAX_BACK_FAST_MS));
            }
            setCurrentPlayTime(seekBar.getProgress());
            player.seekTo(seekBar.getProgress());
        }
    }

    /**
     * 回退
     */
    private void slowAudioPlay() {
        Player player = mPlayerView.getPlayer();
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            if (seekBar.getProgress() < MAX_BACK_FAST_MS) {
                seekBar.setProgress(0);
            } else {
                seekBar.setProgress((int) (seekBar.getProgress() - MAX_BACK_FAST_MS));
            }
            setCurrentPlayTime(seekBar.getProgress());
            player.seekTo(seekBar.getProgress());
        }
    }

    private final Player.Listener mPlayerListener = new Player.Listener() {
        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            playerDefaultUI(true);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY) {
                playerIngUI();
            } else if (playbackState == Player.STATE_ENDED) {
                setBackFastUI(false);
                playerDefaultUI(true);
            }
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            if (playWhenReady) {
                playerIngUI();
            } else {
                playerDefaultUI(false);
            }
        }
    };


    /**
     * 开始更新播放进度
     */
    private void startUpdateProgress() {
        mHandler.postDelayed(mTickerRunnable, MAX_UPDATE_INTERVAL_MS);
    }

    /**
     * 停止更新播放进度
     */
    private void stopUpdateProgress() {
        mHandler.removeCallbacks(mTickerRunnable);
    }

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

    private void playerIngUI() {
        startUpdateProgress();
        setBackFastUI(true);
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_stop);
    }

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
        Player player;
        if (mPlayerView.getPlayer() == null) {
            player = new ExoPlayer.Builder(itemView.getContext().getApplicationContext()).build();
            mPlayerView.setPlayer(player);
        } else {
            player = mPlayerView.getPlayer();
        }
        player.addListener(mPlayerListener);
        playerDefaultUI(true);
    }

    @Override
    public void onViewDetachedFromWindow() {
        mHandler.removeCallbacks(mTickerRunnable);
        Player player = mPlayerView.getPlayer();
        if (player != null) {
            player.stop();
            player.removeListener(mPlayerListener);
            playerDefaultUI(true);
        }
    }

    /**
     * 释放PlayerView
     */
    public void releaseAudio() {
        mHandler.removeCallbacks(mTickerRunnable);
        Player player = mPlayerView.getPlayer();
        if (player != null) {
            player.removeListener(mPlayerListener);
            player.release();
        }
    }
}
