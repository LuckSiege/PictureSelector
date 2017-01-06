package com.yalantis.ucrop.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yalantis.ucrop.R;

public class VideoPlayActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private String video_path = "";
    private ImageView left_back;
    private MediaController mMediaController;
    private VideoView mVideoView;
    private int mPositionWhenPaused = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        video_path = getIntent().getStringExtra("video_path");
        left_back = (ImageView) findViewById(R.id.left_back);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        //Video file
        //Create media controller，组件可以控制视频的播放，暂停，回复，seek等操作，不需要你实现
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);

        left_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void onStart() {
        // Play Video
        new Thread(new Runnable() {
            @Override
            public void run() {
                mVideoView.setVideoPath(video_path);
                mVideoView.start();
            }
        }).start();
        super.onStart();
    }

    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();

        super.onPause();
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
        this.finish();
    }
}
