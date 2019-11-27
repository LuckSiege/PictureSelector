package com.luck.picture.lib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.JumpUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.widget.FolderPopWindow;
import com.luck.picture.lib.widget.PhotoPopupWindow;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2018/1/27 19:12
 * @描述: Media 选择页面
 */
public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener,
        PictureAlbumDirectoryAdapter.OnItemClickListener,
        PictureImageGridAdapter.OnPhotoSelectChangedListener, PhotoPopupWindow.OnItemClickListener {
    private static final int SHOW_DIALOG = 0;
    private static final int DISMISS_DIALOG = 1;
    private ImageView mIvPictureLeftBack;
    private ImageView mIvArrow;
    private View titleViewBg;
    private TextView mTvPictureTitle, mTvPictureRight, mTvPictureOk, mTvEmpty,
            mTvPictureImgNum, mTvPicturePreview, mTvPlayPause, mTvStop, mTvQuit,
            mTvMusicStatus, mTvMusicTotal, mTvMusicTime;
    private RecyclerView mPictureRecycler;
    private RelativeLayout mBottomLayout;
    private PictureImageGridAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private FolderPopWindow folderWindow;
    private Animation animation = null;
    private boolean anim = false;
    private PhotoPopupWindow popupWindow;
    private LocalMediaLoader mediaLoader;
    private MediaPlayer mediaPlayer;
    private SeekBar musicSeekBar;
    private boolean isPlayAudio = false;
    private PictureCustomDialog audioDialog;
    private int audioH;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG:
                    showPleaseDialog();
                    break;
                case DISMISS_DIALOG:
                    dismissDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_selector);
        BroadcastManager.getInstance(this)
                .registerReceiver(commonBroadcastReceiver, BroadcastAction.ACTION_SELECTED_DATA,
                        BroadcastAction.ACTION_PREVIEW_COMPRESSION);
        initView(savedInstanceState);
        initPictureSelectorStyle();
    }

    /**
     * init views
     */
    private void initView(Bundle savedInstanceState) {
        titleViewBg = findViewById(R.id.titleViewBg);
        mIvPictureLeftBack = findViewById(R.id.picture_left_back);
        mTvPictureTitle = findViewById(R.id.picture_title);
        mTvPictureRight = findViewById(R.id.picture_right);
        mTvPictureOk = findViewById(R.id.picture_tv_ok);
        mIvArrow = findViewById(R.id.ivArrow);
        mTvPicturePreview = findViewById(R.id.picture_id_preview);
        mTvPictureImgNum = findViewById(R.id.picture_tv_img_num);
        mPictureRecycler = findViewById(R.id.picture_recycler);
        mBottomLayout = findViewById(R.id.rl_bottom);
        mTvEmpty = findViewById(R.id.tv_empty);
        isNumComplete(numComplete);
        if (config.chooseMode == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }
        mTvPicturePreview.setOnClickListener(this);
        if (config.chooseMode == PictureMimeType.ofAudio()) {
            mTvPicturePreview.setVisibility(View.GONE);
            audioH = ScreenUtils.getScreenHeight(mContext)
                    + ScreenUtils.getStatusBarHeight(mContext);
        } else {
            mTvPicturePreview.setVisibility(config.chooseMode == PictureMimeType.ofVideo()
                    ? View.GONE : View.VISIBLE);
        }
        mBottomLayout.setVisibility(config.selectionMode == PictureConfig.SINGLE
                && config.isSingleDirectReturn ? View.GONE : View.VISIBLE);
        mIvPictureLeftBack.setOnClickListener(this);
        mTvPictureRight.setOnClickListener(this);
        mTvPictureOk.setOnClickListener(this);
        mTvPictureImgNum.setOnClickListener(this);
        mTvPictureTitle.setOnClickListener(this);
        String title = config.chooseMode == PictureMimeType.ofAudio() ?
                getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
        mTvPictureTitle.setText(title);
        folderWindow = new FolderPopWindow(this, config);
        folderWindow.setArrowImageView(mIvArrow);
        folderWindow.setOnItemClickListener(this);
        mPictureRecycler.setHasFixedSize(true);
        mPictureRecycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        mPictureRecycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) mPictureRecycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        mediaLoader = new LocalMediaLoader(this, config);

        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mHandler.sendEmptyMessage(SHOW_DIALOG);
            readLocalMedia();
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }

        mTvEmpty.setText(config.chooseMode == PictureMimeType.ofAudio() ?
                getString(R.string.picture_audio_empty)
                : getString(R.string.picture_empty));
        StringUtils.tempTextFont(mTvEmpty, config.chooseMode);
        if (savedInstanceState != null) {
            // 防止拍照内存不足时activity被回收，导致拍照后的图片未选中
            selectionMedias = PictureSelector.obtainSelectorList(savedInstanceState);
        }
        adapter = new PictureImageGridAdapter(mContext, config);
        adapter.setOnPhotoSelectChangedListener(PictureSelectorActivity.this);
        adapter.bindSelectImages(selectionMedias);
        mPictureRecycler.setAdapter(adapter);
    }

    /**
     * 动态设置相册主题
     */
    private void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleDownResId != 0) {
                Drawable drawable = ContextCompat.getDrawable(this, config.style.pictureTitleDownResId);
                mIvArrow.setImageDrawable(drawable);
            }
            if (config.style.pictureTitleTextColor != 0) {
                mTvPictureTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureCancelTextColor != 0) {
                mTvPictureRight.setTextColor(config.style.pictureCancelTextColor);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                mIvPictureLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.pictureUnPreviewTextColor != 0) {
                mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
            }
            if (config.style.pictureCheckNumBgStyle != 0) {
                mTvPictureImgNum.setBackgroundResource(config.style.pictureCheckNumBgStyle);
            }
            if (config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            if (config.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(config.style.pictureBottomBgColor);
            }
        } else {
            if (config.downResId != 0) {
                Drawable drawable = ContextCompat.getDrawable(this, config.downResId);
                mIvArrow.setImageDrawable(drawable);
            }
        }
        titleViewBg.setBackgroundColor(colorPrimary);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            PictureSelector.saveSelectorList(outState, selectedImages);
        }
    }

    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        mTvPictureOk.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_please_select));
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.picture_anim_modal_in);
        }
        animation = numComplete ? null : AnimationUtils.loadAnimation(this, R.anim.picture_anim_modal_in);
    }

    /**
     * get LocalMedia s
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia();
        mediaLoader.setCompleteListener(folders -> {
            if (folders.size() > 0) {
                foldersList = folders;
                LocalMediaFolder folder = folders.get(0);
                folder.setChecked(true);
                List<LocalMedia> localImg = folder.getImages();
                // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                // 因为onActivityResult里手动添加拍照后的照片，
                // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                int size = images.size();
                if (localImg.size() >= size) {
                    images = localImg;
                    folderWindow.bindFolder(folders);
                }
            }
            if (adapter != null && images != null) {
                adapter.bindImagesData(images);
                mTvEmpty.setVisibility(images.size() > 0 ? View.INVISIBLE : View.VISIBLE);
            }
            mHandler.sendEmptyMessage(DISMISS_DIALOG);
        });
    }

    /**
     * open camera
     */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick()) {
            switch (config.chooseMode) {
                case PictureConfig.TYPE_ALL:
                    // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAsDropDown(mTvPictureTitle);
                    } else {
                        startOpenCamera();
                    }
                    break;
                case PictureConfig.TYPE_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_VIDEO:
                    // 录视频
                    startOpenCameraVideo();
                    break;
                case PictureConfig.TYPE_AUDIO:
                    // 录音
                    startOpenCameraAudio();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back || id == R.id.picture_right) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                closeActivity();
            }
        }
        if (id == R.id.picture_title) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    folderWindow.showAsDropDown(mTvPictureTitle);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    folderWindow.notifyDataCheckedStatus(selectedImages);
                }
            }
        }

        if (id == R.id.picture_id_preview) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            List<LocalMedia> medias = new ArrayList<>();
            int size = selectedImages.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectedImages.get(i);
                medias.add(media);
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (ArrayList<? extends Parcelable>) medias);
            bundle.putParcelableArrayList(PictureConfig.EXTRA_SELECT_LIST, (ArrayList<? extends Parcelable>) selectedImages);
            bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
            JumpUtils.startPicturePreviewActivity(mContext, bundle,
                    config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);

            overridePendingTransition(config.windowAnimationStyle != null
                            && config.windowAnimationStyle.activityPreviewEnterAnimation != 0
                            ? config.windowAnimationStyle.activityPreviewEnterAnimation : R.anim.picture_anim_enter,
                    R.anim.picture_anim_fade_in);
        }

        if (id == R.id.picture_tv_ok || id == R.id.picture_tv_img_num) {
            List<LocalMedia> images = adapter.getSelectedImages();
            LocalMedia image = images.size() > 0 ? images.get(0) : null;
            String mimeType = image != null ? image.getMimeType() : "";
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = images.size();
            boolean eqImg = PictureMimeType.eqImage(mimeType);
            if (config.minSelectNum > 0 && config.selectionMode == PictureConfig.MULTIPLE) {
                if (size < config.minSelectNum) {
                    String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastUtils.s(mContext, str);
                    return;
                }
            }
            if (config.enableCrop && eqImg) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getPath();
                    startCrop(originalPath);
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<CutInfo> cuts = new ArrayList<>();
                    int count = images.size();
                    for (int i = 0; i < count; i++) {
                        LocalMedia media = images.get(i);
                        CutInfo cutInfo = new CutInfo();
                        cutInfo.setPath(media.getPath());
                        cutInfo.setImageWidth(media.getWidth());
                        cutInfo.setImageHeight(media.getHeight());
                        cutInfo.setMimeType(media.getMimeType());
                        if (SdkVersionUtils.checkedAndroid_Q()) {
                            cutInfo.setAndroidQToPath(media.getAndroidQToPath());
                        }
                        cuts.add(cutInfo);
                    }
                    startCrop(cuts);
                }
            } else if (config.isCompress && eqImg) {
                // 图片才压缩，视频不管
                compressImage(images);
            } else {
                onResult(images);
            }
        }
    }

    /**
     * 播放音频
     *
     * @param path
     */
    private void audioDialog(final String path) {
        audioDialog = new PictureCustomDialog(mContext,
                LinearLayout.LayoutParams.MATCH_PARENT, audioH,
                R.layout.picture_audio_dialog, R.style.Picture_Theme_Dialog);
        audioDialog.getWindow().setWindowAnimations(R.style.Picture_Theme_Dialog_AudioStyle);
        mTvMusicStatus = audioDialog.findViewById(R.id.tv_musicStatus);
        mTvMusicTime = audioDialog.findViewById(R.id.tv_musicTime);
        musicSeekBar = audioDialog.findViewById(R.id.musicSeekBar);
        mTvMusicTotal = audioDialog.findViewById(R.id.tv_musicTotal);
        mTvPlayPause = audioDialog.findViewById(R.id.tv_PlayPause);
        mTvStop = audioDialog.findViewById(R.id.tv_Stop);
        mTvQuit = audioDialog.findViewById(R.id.tv_Quit);
        if (handler != null) {
            handler.postDelayed(() -> initPlayer(path), 30);
        }
        mTvPlayPause.setOnClickListener(new audioOnClick(path));
        mTvStop.setOnClickListener(new audioOnClick(path));
        mTvQuit.setOnClickListener(new audioOnClick(path));
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        audioDialog.setOnDismissListener(dialog -> {
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
            new Handler().postDelayed(() -> stop(path), 30);
            try {
                if (audioDialog != null
                        && audioDialog.isShowing()) {
                    audioDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (handler != null) {
            handler.post(runnable);
        }
        audioDialog.show();
    }

    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    mTvMusicTime.setText(DateUtils.formatDurationTime(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    mTvMusicTotal.setText(DateUtils.formatDurationTime(mediaPlayer.getDuration()));
                    if (handler != null) {
                        handler.postDelayed(runnable, 200);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 初始化音频播放组件
     *
     * @param path
     */
    private void initPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频点击事件
     */
    public class audioOnClick implements View.OnClickListener {
        private String path;

        public audioOnClick(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_PlayPause) {
                playAudio();
            }
            if (id == R.id.tv_Stop) {
                mTvMusicStatus.setText(getString(R.string.picture_stop_audio));
                mTvPlayPause.setText(getString(R.string.picture_play_audio));
                stop(path);
            }
            if (id == R.id.tv_Quit) {
                if (handler != null) {
                    handler.postDelayed(() -> stop(path), 30);
                    try {
                        if (audioDialog != null
                                && audioDialog.isShowing()) {
                            audioDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(runnable);
                }
            }
        }
    }

    /**
     * 播放音频
     */
    private void playAudio() {
        if (mediaPlayer != null) {
            musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            musicSeekBar.setMax(mediaPlayer.getDuration());
        }
        String ppStr = mTvPlayPause.getText().toString();
        if (ppStr.equals(getString(R.string.picture_play_audio))) {
            mTvPlayPause.setText(getString(R.string.picture_pause_audio));
            mTvMusicStatus.setText(getString(R.string.picture_play_audio));
            playOrPause();
        } else {
            mTvPlayPause.setText(getString(R.string.picture_play_audio));
            mTvMusicStatus.setText(getString(R.string.picture_pause_audio));
            playOrPause();
        }
        if (isPlayAudio == false) {
            if (handler != null) {
                handler.post(runnable);
            }
            isPlayAudio = true;
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    public void stop(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(boolean isCameraFolder, String folderName, List<LocalMedia> images) {
        boolean camera = config.isCamera ? isCameraFolder : false;
        adapter.setShowCamera(camera);
        mTvPictureTitle.setText(folderName);
        folderWindow.dismiss();
        adapter.bindImagesData(images);
        mPictureRecycler.smoothScrollToPosition(0);
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            PermissionChecker
                    .requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        changeImageNumber(selectImages);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        if (config.selectionMode == PictureConfig.SINGLE && config.isSingleDirectReturn) {
            List<LocalMedia> list = new ArrayList<>();
            list.add(media);
            if (config.enableCrop) {
                adapter.bindSelectImages(list);
                startCrop(media.getPath());
            } else {
                handlerResult(list);
            }
        } else {
            List<LocalMedia> images = adapter.getImages();
            startPreview(images, position);
        }
    }

    /**
     * preview image and video
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        LocalMedia media = previewImages.get(position);
        String mimeType = media.getMimeType();
        Bundle bundle = new Bundle();
        List<LocalMedia> result = new ArrayList<>();
        if (PictureMimeType.eqVideo(mimeType)) {
            // video
            if (config.selectionMode == PictureConfig.SINGLE) {
                result.add(media);
                onResult(result);
            } else {
                bundle.putString("video_path", media.getPath());
                JumpUtils.startPictureVideoPlayActivity(mContext, bundle);
            }
        } else if (PictureMimeType.eqAudio(mimeType)) {
            // audio
            if (config.selectionMode == PictureConfig.SINGLE) {
                result.add(media);
                onResult(result);
            } else {
                audioDialog(media.getPath());
            }
        } else {
            // image
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            ImagesObservable.getInstance().savePreviewMediaData(new ArrayList<>(previewImages));
            bundle.putParcelableArrayList(PictureConfig.EXTRA_SELECT_LIST, (ArrayList<? extends Parcelable>) selectedImages);
            bundle.putInt(PictureConfig.EXTRA_POSITION, position);
            JumpUtils.startPicturePreviewActivity(mContext, bundle,
                    config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
            overridePendingTransition(config.windowAnimationStyle != null
                    && config.windowAnimationStyle.activityPreviewEnterAnimation != 0
                    ? config.windowAnimationStyle.activityPreviewEnterAnimation : R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
        }
    }


    /**
     * change image selector state
     *
     * @param selectImages
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        String mimeType = selectImages.size() > 0
                ? selectImages.get(0).getMimeType() : "";
        if (config.chooseMode == PictureMimeType.ofAudio()) {
            mTvPicturePreview.setVisibility(View.GONE);
        } else {
            boolean isVideo = PictureMimeType.eqVideo(mimeType);
            boolean eqVideo = config.chooseMode == PictureConfig.TYPE_VIDEO;
            mTvPicturePreview.setVisibility(isVideo || eqVideo ? View.GONE : View.VISIBLE);
        }
        boolean enable = selectImages.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            if (config.style != null && config.style.pictureCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureCompleteTextColor);
            }
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            if (config.style != null && config.style.picturePreviewTextColor != 0) {
                mTvPicturePreview.setTextColor(config.style.picturePreviewTextColor);
            }
            if (numComplete) {
                mTvPictureOk.setText(getString
                        (R.string.picture_done_front_num, selectImages.size(),
                                config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (!anim) {
                    mTvPictureImgNum.startAnimation(animation);
                }
                mTvPictureImgNum.setVisibility(View.VISIBLE);
                mTvPictureImgNum.setText(String.valueOf(selectImages.size()));
                mTvPictureOk.setText(getString(R.string.picture_completed));
                anim = false;
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            if (config.style != null && config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (config.style != null && config.style.pictureUnPreviewTextColor != 0) {
                mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
            }
            if (numComplete) {
                mTvPictureOk.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                mTvPictureImgNum.setVisibility(View.INVISIBLE);
                mTvPictureOk.setText(getString(R.string.picture_please_select));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    singleCropHandleResult(data);
                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    multiCropHandleResult(data);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    requestCamera(data);
                    break;
                default:
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastUtils.s(mContext, throwable.getMessage());
        }
    }

    /**
     * 摄像头后处理方式
     *
     * @param media
     * @param mimeType
     */
    private void cameraHandleResult(LocalMedia media, String mimeType) {
        // 如果是单选 拍照后直接返回
        boolean eqImg = PictureMimeType.eqImage(mimeType);
        if (config.enableCrop && eqImg) {
            // 去裁剪
            originalPath = cameraPath;
            startCrop(cameraPath);
        } else if (config.isCompress && eqImg) {
            // 去压缩
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            compressImage(result);
            images.add(0, media);
            adapter.bindSelectImages(result);
            adapter.notifyDataSetChanged();
        } else {
            // 不裁剪 不压缩 直接返回结果
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            onResult(result);
        }
    }

    /**
     * 拍照后处理结果
     *
     * @param data
     */
    private void requestCamera(Intent data) {
        // on take photo success
        String mimeType = null;
        long duration = 0;
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        if (config.chooseMode == PictureMimeType.ofAudio()) {
            // 音频处理规则
            cameraPath = getAudioPath(data);
            if (TextUtils.isEmpty(cameraPath)) {
                return;
            }
            mimeType = PictureMimeType.MIME_TYPE_AUDIO;
            if (isAndroidQ) {
                duration = MediaUtils.extractDuration(mContext, true, cameraPath);
            } else {
                duration = MediaUtils.extractDuration(mContext, false, cameraPath);
            }
        }
        if (TextUtils.isEmpty(cameraPath) || new File(cameraPath) == null) {
            return;
        }
        long size = 0;
        int[] newSize = new int[2];
        final File file = new File(cameraPath);
        if (!isAndroidQ) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
        LocalMedia media = new LocalMedia();
        if (config.chooseMode != PictureMimeType.ofAudio()) {
            // 图片视频处理规则
            if (isAndroidQ) {
                String path = PictureFileUtils.getPath(getApplicationContext(), Uri.parse(cameraPath));
                File f = new File(path);
                size = f.length();
                mimeType = PictureMimeType.fileToType(f);
                if (PictureMimeType.eqImage(mimeType)) {
                    int degree = PictureFileUtils.readPictureDegree(this, cameraPath);
                    String rotateImagePath = PictureFileUtils.rotateImageToAndroidQ(this,
                            degree, cameraPath);
                    media.setAndroidQToPath(rotateImagePath);
                    newSize = MediaUtils.getLocalImageSizeToAndroidQ(this, cameraPath);
                } else {
                    newSize = MediaUtils.getLocalVideoSize(this, Uri.parse(cameraPath));
                    duration = MediaUtils.extractDuration(mContext, true, cameraPath);
                }
            } else {
                mimeType = PictureMimeType.fileToType(file);
                size = new File(cameraPath).length();
                if (PictureMimeType.eqImage(mimeType)) {
                    int degree = PictureFileUtils.readPictureDegree(this, cameraPath);
                    PictureFileUtils.rotateImage(degree, cameraPath);
                    newSize = MediaUtils.getLocalImageWidthOrHeight(cameraPath);
                } else {
                    newSize = MediaUtils.getLocalVideoSize(cameraPath);
                    duration = MediaUtils.extractDuration(mContext, false, cameraPath);
                }
            }
            boolean isMimeType = PictureMimeType.eqImage(mimeType);
            int lastImageId = MediaUtils.getLastImageId(this, isMimeType);
            if (lastImageId != -1) {
                removeImage(lastImageId, isMimeType);
            }
        }
        media.setDuration(duration);
        media.setWidth(newSize[0]);
        media.setHeight(newSize[1]);
        media.setPath(cameraPath);
        media.setMimeType(mimeType);
        media.setSize(size);
        media.setChooseModel(config.chooseMode);
        if (adapter != null) {
            if (config.selectionMode == PictureConfig.SINGLE) {
                // 单选模式
                if (config.isSingleDirectReturn) {
                    cameraHandleResult(media, mimeType);
                } else {
                    // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                    images.add(0, media);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    mimeType = selectedImages.size() > 0 ? selectedImages.get(0).getMimeType() : "";
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, media.getMimeType());
                    // 类型相同或还没有选中才加进选中集合中
                    if (mimeTypeSame || selectedImages.size() == 0) {
                        singleRadioMediaImage();
                        selectedImages.add(media);
                        adapter.bindSelectImages(selectedImages);
                    }
                }
            } else {
                // 多选模式
                images.add(0, media);
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                // 没有到最大选择量 才做默认选中刚拍好的
                if (selectedImages.size() < config.maxSelectNum) {
                    mimeType = selectedImages.size() > 0 ? selectedImages.get(0).getMimeType() : "";
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, media.getMimeType());
                    // 类型相同或还没有选中才加进选中集合中
                    if (mimeTypeSame || selectedImages.size() == 0) {
                        if (selectedImages.size() < config.maxSelectNum) {
                            selectedImages.add(media);
                            adapter.bindSelectImages(selectedImages);
                        }
                    }
                } else {
                    ToastUtils.s(this, StringUtils.getToastMsg(this, mimeType,
                            config.maxSelectNum));
                }
            }
            adapter.notifyDataSetChanged();
            // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE，不及时刷新问题手动添加
            manualSaveFolder(media);
            mTvEmpty.setVisibility(images.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        }
    }

    /**
     * 单张图片裁剪
     *
     * @param data
     */
    private void singleCropHandleResult(Intent data) {
        List<LocalMedia> medias = new ArrayList<>();
        Uri resultUri = UCrop.getOutput(data);
        String cutPath = resultUri.getPath();
        String mimeType;
        if (adapter != null) {
            // 取单张裁剪已选中图片的path作为原图
            List<LocalMedia> mediaList = adapter.getSelectedImages();
            LocalMedia media = mediaList != null && mediaList.size() > 0 ? mediaList.get(0) : null;
            if (media != null) {
                originalPath = media.getPath();
                media.setCutPath(cutPath);
                media.setSize(new File(cutPath).length());
                media.setChooseModel(config.chooseMode);
                media.setCut(true);
                mimeType = PictureMimeType.getImageMimeType(cutPath);
                media.setMimeType(mimeType);
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    media.setAndroidQToPath(TextUtils.isEmpty(media.getAndroidQToPath())
                            ? cutPath : media.getAndroidQToPath());
                }
                medias.add(media);
                handlerResult(medias);
            }
        }
    }

    /**
     * 单选图片
     */
    private void singleRadioMediaImage() {
        List<LocalMedia> selectImages = adapter.getSelectedImages();
        if (selectImages != null
                && selectImages.size() > 0) {
            selectImages.clear();
        }
    }

    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            createNewFolder(foldersList);
            LocalMediaFolder folder = getImageFolder(media.getPath(), foldersList);
            LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getPath());
                cameraFolder.setImages(images);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getImages().add(0, media);
                folder.setFirstImagePath(cameraPath);
                folderWindow.bindFolder(foldersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commonBroadcastReceiver != null) {
            BroadcastManager.getInstance(this)
                    .unregisterReceiver(commonBroadcastReceiver,
                            BroadcastAction.ACTION_SELECTED_DATA,
                            BroadcastAction.ACTION_PREVIEW_COMPRESSION);
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                startOpenCamera();
                break;
            case 1:
                // 录视频
                startOpenCameraVideo();
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver commonBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras;
            switch (action) {
                case BroadcastAction.ACTION_SELECTED_DATA:
                    // 预览时勾选图片更新回调
                    extras = intent.getExtras();
                    if (extras != null) {
                        List<LocalMedia> selectImages = extras.
                                getParcelableArrayList("selectImages");
                        int position = extras.getInt("position");
                        anim = true;
                        adapter.bindSelectImages(selectImages);
                        adapter.notifyItemChanged(position);
                    }
                    break;
                case BroadcastAction.ACTION_PREVIEW_COMPRESSION:
                    extras = intent.getExtras();
                    if (extras != null) {
                        List<LocalMedia> selectImages = extras.getParcelableArrayList("selectImages");
                        if (selectImages.size() > 0) {
                            // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                            String mimeType = selectImages.get(0).getMimeType();
                            if (config.isCompress && PictureMimeType.eqImage(mimeType)) {
                                compressImage(selectImages);
                            } else {
                                onResult(selectImages);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // 存储权限
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        mHandler.sendEmptyMessage(SHOW_DIALOG);
                        readLocalMedia();
                    } else {
                        ToastUtils.s(mContext, getString(R.string.picture_jurisdiction));
                        onBackPressed();
                    }
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // 相机权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    ToastUtils.s(mContext, getString(R.string.picture_camera));
                }
                break;
        }
    }
}
