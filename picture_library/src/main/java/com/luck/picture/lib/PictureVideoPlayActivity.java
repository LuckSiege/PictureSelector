package com.luck.picture.lib;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SdkVersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2017/8/28 下午11:00
 * @描述: 视频播放类
 */
public class PictureVideoPlayActivity extends PictureBaseActivity implements
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, View.OnClickListener {
    private String videoPath;
    private ImageButton ibLeftBack;
    private MediaController mMediaController;
    private VideoView mVideoView;
    private TextView tvConfirm;
    private ImageView iv_play;
    private int mPositionWhenPaused = -1;

    @Override
    public boolean isImmersive() {
        return false;
    }

    @Override
    public boolean isRequestedOrientation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceId() {
        return R.layout.picture_activity_video_play;
    }

    @Override
    protected void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureLeftBackIcon != 0) {
                ibLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
        }
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        videoPath = getIntent().getStringExtra(PictureConfig.EXTRA_VIDEO_PATH);
        boolean isExternalPreview = getIntent().getBooleanExtra
                (PictureConfig.EXTRA_PREVIEW_VIDEO, false);
        if (TextUtils.isEmpty(videoPath)) {
            LocalMedia media = getIntent().getParcelableExtra(PictureConfig.EXTRA_MEDIA_KEY);
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                finish();
                return;
            }
            videoPath = media.getPath();
        }
        if (TextUtils.isEmpty(videoPath)) {
            closeActivity();
            return;
        }
        ibLeftBack = findViewById(R.id.pictureLeftBack);
        mVideoView = findViewById(R.id.video_view);
        tvConfirm = findViewById(R.id.tv_confirm);
        mVideoView.setBackgroundColor(Color.BLACK);
        iv_play = findViewById(R.id.iv_play);
        mMediaController = new MediaController(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setMediaController(mMediaController);
        ibLeftBack.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        tvConfirm.setVisibility(config.selectionMode
                == PictureConfig.SINGLE
                && config.enPreviewVideo && !isExternalPreview ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        // Play Video
        if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(videoPath)) {
            mVideoView.setVideoURI(Uri.parse(videoPath));
        } else {
            mVideoView.setVideoPath(videoPath);
        }
        mVideoView.start();
        super.onStart();
    }

    @Override
    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMediaController = null;
        mVideoView = null;
        iv_play = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null != iv_play) {
            iv_play.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pictureLeftBack) {
            onBackPressed();
        } else if (id == R.id.iv_play) {
            mVideoView.start();
            iv_play.setVisibility(View.INVISIBLE);
        } else if (id == R.id.tv_confirm) {
            List<LocalMedia> result = new ArrayList<>();
            result.add(getIntent().getParcelableExtra(PictureConfig.EXTRA_MEDIA_KEY));
            setResult(RESULT_OK, new Intent()
                    .putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                            (ArrayList<? extends Parcelable>) result));
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (config.windowAnimationStyle != null
                && config.windowAnimationStyle.activityPreviewExitAnimation != 0) {
            finish();
            overridePendingTransition(0, config.windowAnimationStyle != null
                    && config.windowAnimationStyle.activityPreviewExitAnimation != 0 ?
                    config.windowAnimationStyle.activityPreviewExitAnimation : R.anim.picture_anim_exit);
        } else {
            closeActivity();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)) {
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnInfoListener((mp1, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // video started
                mVideoView.setBackgroundColor(Color.TRANSPARENT);
                return true;
            }
            return false;
        });
    }
}
