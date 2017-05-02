package com.luck.picture.lib.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.luck.picture.lib.R;

public class PictureVideoPlayActivity extends PictureBaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private String video_path = "";
    private ImageView picture_left_back;
    private MediaController mMediaController;
    private VideoView mVideoView;
    private ImageView iv_play;
    private int mPositionWhenPaused = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_video_play);
        video_path = getIntent().getStringExtra("video_path");
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        mMediaController = new MediaController(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setMediaController(mMediaController);
        picture_left_back.setOnClickListener(this);
        iv_play.setOnClickListener(this);
    }


    public void onStart() {
        // Play Video
        mVideoView.setVideoPath(video_path);
        mVideoView.start();
        super.onStart();
    }

    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaController = null;
        mVideoView = null;
    }

    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    public void onCompletion(MediaPlayer mp) {
        iv_play.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back) {
            finish();
        } else if (id == R.id.iv_play) {
            mVideoView.start();
            iv_play.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }
}
