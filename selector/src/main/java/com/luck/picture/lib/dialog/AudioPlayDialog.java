package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.utils.DateUtils;

/**
 * @author：luck
 * @date：2021/11/21 2:31 下午
 * @describe：AudioPlayDialog
 */
public class AudioPlayDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private final MediaPlayer mediaPlayer;
    private final String audioPath;
    private final TextView tvMusicStatus;
    private final TextView tvMusicTime;
    private final TextView tvMusicTotal;
    private final TextView tvPlayPause;
    private final SeekBar musicSeekBar;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public AudioPlayDialog(@NonNull Context context, String audioPath) {
        super(context, R.style.Picture_Theme_Dialog);
        this.audioPath = audioPath;
        this.mediaPlayer = new MediaPlayer();
        setContentView(R.layout.ps_audio_play_dialog);
        getWindow().setWindowAnimations(R.style.Picture_Theme_Dialog_AudioStyle);
        tvMusicStatus = findViewById(R.id.tv_musicStatus);
        tvMusicTime = findViewById(R.id.tv_musicTime);
        musicSeekBar = findViewById(R.id.music_seek_bar);
        tvMusicTotal = findViewById(R.id.tv_music_total);
        tvPlayPause = findViewById(R.id.tv_play_pause);
        TextView tvStop = findViewById(R.id.tv_stop);
        TextView tvQuit = findViewById(R.id.tv_quit);
        musicSeekBar.setOnSeekBarChangeListener(this);
        tvPlayPause.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvQuit.setOnClickListener(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.removeCallbacks(mRunnable);
                mediaPlayer.release();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (PictureMimeType.isContent(audioPath)) {
                mediaPlayer.setDataSource(getContext(), Uri.parse(audioPath));
            } else {
                mediaPlayer.setDataSource(audioPath);
            }
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_play_pause) {
            playAudio();
        } else if (id == R.id.tv_stop) {
            stop(audioPath);
        } else if (id == R.id.tv_quit) {
            mHandler.removeCallbacks(mRunnable);
            stop(audioPath);
            dismiss();
        }
    }

    /**
     * 开始播放
     */
    private void playAudio() {
        musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        musicSeekBar.setMax(mediaPlayer.getDuration());
        if (mediaPlayer.isPlaying()) {
            tvPlayPause.setText(tvPlayPause.getContext().getString(R.string.ps_play_audio));
            tvMusicStatus.setText(tvPlayPause.getContext().getString(R.string.ps_pause_audio));
            mediaPlayer.pause();
        } else {
            tvPlayPause.setText(tvPlayPause.getContext().getString(R.string.ps_pause_audio));
            tvMusicStatus.setText(tvPlayPause.getContext().getString(R.string.ps_play_audio));
            mediaPlayer.start();
            mHandler.post(mRunnable);
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    private void stop(String path) {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            if (PictureMimeType.isContent(path)) {
                mediaPlayer.setDataSource(getContext(), Uri.parse(path));
            } else {
                mediaPlayer.setDataSource(path);
            }
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            tvMusicStatus.setText(tvMusicStatus.getContext().getString(R.string.ps_stop_audio));
            tvPlayPause.setText(tvMusicStatus.getContext().getString(R.string.ps_play_audio));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                tvMusicTime.setText(DateUtils.formatDurationTime(mediaPlayer.getCurrentPosition()));
                musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                musicSeekBar.setMax(mediaPlayer.getDuration());
                tvMusicTotal.setText(DateUtils.formatDurationTime(mediaPlayer.getDuration()));
                mHandler.postDelayed(mRunnable, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static void showPlayAudioDialog(Context context, String path) {
        new AudioPlayDialog(context, path).show();
    }
}
