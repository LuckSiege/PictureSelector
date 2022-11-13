package com.luck.pictureselector;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.lib.camerax.CameraImageEngine;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener;
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.FragmentInjectManager;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.IBridgeViewLifecycle;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelectionSystemModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ExtendLoaderEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnGridItemSelectAnimListener;
import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPreviewInterceptListener;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnQueryFilterListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectAnimListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.loader.SandboxFileLoader;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.permissions.PermissionUtil;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.luck.picture.lib.utils.ValueOf;
import com.luck.picture.lib.widget.MediumBoldTextView;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.luck.pictureselector.listener.DragListener;
import com.luck.pictureselector.listener.OnItemLongClickListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;
import com.yalantis.ucrop.model.AspectRatio;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * @author：luck
 * @data：2019/12/20 晚上 23:12
 * @描述: Demo
 */

public class MainActivity extends AppCompatActivity implements IBridgePictureBehavior, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "PictureSelectorTag";
    private final static String TAG_EXPLAIN_VIEW = "TAG_EXPLAIN_VIEW";
    private final static int ACTIVITY_RESULT = 1;
    private final static int CALLBACK_RESULT = 2;
    private final static int LAUNCHER_RESULT = 3;
    private GridImageAdapter mAdapter;
    private int maxSelectNum = 9;
    private int maxSelectVideoNum = 1;
    private TextView tv_select_num;
    private TextView tv_select_video_num;
    private TextView tv_original_tips;
    private TextView tvDeleteText;
    private RadioGroup rgb_crop;
    private LinearLayout llSelectVideoSize;
    private int aspect_ratio_x = -1, aspect_ratio_y = -1;
    private CheckBox cb_voice, cb_choose_mode, cb_isCamera, cb_isGif,
            cb_preview_img, cb_preview_video, cb_crop, cb_compress,
            cb_mode, cb_hide, cb_crop_circular, cb_styleCrop, cb_showCropGrid,
            cb_showCropFrame, cb_preview_audio, cb_original, cb_single_back,
            cb_custom_camera, cbPage, cbEnabledMask, cbEditor, cb_custom_sandbox, cb_only_dir,
            cb_preview_full, cb_preview_scale, cb_inject_layout, cb_time_axis, cb_WithImageVideo,
            cb_system_album, cb_fast_select, cb_skip_not_gif, cb_not_gif, cb_attach_camera_mode,
            cb_attach_system_mode, cb_camera_zoom, cb_camera_focus, cb_query_sort_order, cb_watermark,
            cb_custom_preview, cb_permission_desc,cb_video_thumbnails, cb_auto_video, cb_selected_anim,
            cb_video_resume, cb_custom_loading;
    private int chooseMode = SelectMimeType.ofAll();
    private boolean isHasLiftDelete;
    private boolean needScaleBig = true;
    private boolean needScaleSmall = false;
    private int language = LanguageConfig.UNKNOWN_LANGUAGE;
    private int x = 0, y = 0;
    private int animationMode = AnimationType.DEFAULT_ANIMATION;
    private PictureSelectorStyle selectorStyle;
    private final List<LocalMedia> mData = new ArrayList<>();
    private ActivityResultLauncher<Intent> launcherResult;
    private int resultMode = LAUNCHER_RESULT;
    private ImageEngine imageEngine;
    private VideoPlayerEngine videoPlayerEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectorStyle = new PictureSelectorStyle();
        ImageView minus = findViewById(R.id.minus);
        ImageView plus = findViewById(R.id.plus);
        tv_select_num = findViewById(R.id.tv_select_num);

        ImageView videoMinus = findViewById(R.id.video_minus);
        ImageView videoPlus = findViewById(R.id.video_plus);
        tv_select_video_num = findViewById(R.id.tv_select_video_num);
        llSelectVideoSize = findViewById(R.id.ll_select_video_size);
        tvDeleteText = findViewById(R.id.tv_delete_text);
        tv_original_tips = findViewById(R.id.tv_original_tips);
        rgb_crop = findViewById(R.id.rgb_crop);
        cb_video_thumbnails = findViewById(R.id.cb_video_thumbnails);
        RadioGroup rgb_video_player = findViewById(R.id.rgb_video_player);
        RadioGroup rgb_result = findViewById(R.id.rgb_result);
        RadioGroup rgb_style = findViewById(R.id.rgb_style);
        RadioGroup rgb_animation = findViewById(R.id.rgb_animation);
        RadioGroup rgb_list_anim = findViewById(R.id.rgb_list_anim);
        RadioGroup rgb_photo_mode = findViewById(R.id.rgb_photo_mode);
        RadioGroup rgb_language = findViewById(R.id.rgb_language);
        RadioGroup rgb_engine = findViewById(R.id.rgb_engine);
        cb_voice = findViewById(R.id.cb_voice);
        cb_choose_mode = findViewById(R.id.cb_choose_mode);
        cb_video_resume = findViewById(R.id.cb_video_resume);
        cb_isCamera = findViewById(R.id.cb_isCamera);
        cb_isGif = findViewById(R.id.cb_isGif);
        cb_watermark = findViewById(R.id.cb_watermark);
        cb_WithImageVideo = findViewById(R.id.cbWithImageVideo);
        cb_system_album = findViewById(R.id.cb_system_album);
        cb_fast_select = findViewById(R.id.cb_fast_select);
        cb_preview_full = findViewById(R.id.cb_preview_full);
        cb_preview_scale = findViewById(R.id.cb_preview_scale);
        cb_inject_layout = findViewById(R.id.cb_inject_layout);
        cb_preview_img = findViewById(R.id.cb_preview_img);
        cb_camera_zoom = findViewById(R.id.cb_camera_zoom);
        cb_camera_focus = findViewById(R.id.cb_camera_focus);
        cb_query_sort_order = findViewById(R.id.cb_query_sort_order);
        cb_custom_preview = findViewById(R.id.cb_custom_preview);
        cb_permission_desc = findViewById(R.id.cb_permission_desc);
        cb_preview_video = findViewById(R.id.cb_preview_video);
        cb_auto_video = findViewById(R.id.cb_auto_video);
        cb_selected_anim = findViewById(R.id.cb_selected_anim);
        cb_time_axis = findViewById(R.id.cb_time_axis);
        cb_custom_loading = findViewById(R.id.cb_custom_loading);
        cb_crop = findViewById(R.id.cb_crop);
        cbPage = findViewById(R.id.cbPage);
        cbEditor = findViewById(R.id.cb_editor);
        cbEnabledMask = findViewById(R.id.cbEnabledMask);
        cb_styleCrop = findViewById(R.id.cb_styleCrop);
        cb_compress = findViewById(R.id.cb_compress);
        cb_mode = findViewById(R.id.cb_mode);
        cb_custom_sandbox = findViewById(R.id.cb_custom_sandbox);
        cb_only_dir = findViewById(R.id.cb_only_dir);
        cb_showCropGrid = findViewById(R.id.cb_showCropGrid);
        cb_showCropFrame = findViewById(R.id.cb_showCropFrame);
        cb_preview_audio = findViewById(R.id.cb_preview_audio);
        cb_original = findViewById(R.id.cb_original);
        cb_single_back = findViewById(R.id.cb_single_back);
        cb_custom_camera = findViewById(R.id.cb_custom_camera);
        cb_hide = findViewById(R.id.cb_hide);
        cb_not_gif = findViewById(R.id.cb_not_gif);
        cb_skip_not_gif = findViewById(R.id.cb_skip_not_gif);
        cb_crop_circular = findViewById(R.id.cb_crop_circular);
        cb_attach_camera_mode = findViewById(R.id.cb_attach_camera_mode);
        cb_attach_system_mode = findViewById(R.id.cb_attach_system_mode);
        cb_mode.setOnCheckedChangeListener(this);
        rgb_crop.setOnCheckedChangeListener(this);
        cb_custom_camera.setOnCheckedChangeListener(this);
        rgb_result.setOnCheckedChangeListener(this);
        rgb_style.setOnCheckedChangeListener(this);
        rgb_animation.setOnCheckedChangeListener(this);
        rgb_list_anim.setOnCheckedChangeListener(this);
        rgb_photo_mode.setOnCheckedChangeListener(this);
        rgb_language.setOnCheckedChangeListener(this);
        rgb_video_player.setOnCheckedChangeListener(this);
        rgb_engine.setOnCheckedChangeListener(this);
        RecyclerView mRecyclerView = findViewById(R.id.recycler);
        ImageView left_back = findViewById(R.id.left_back);
        left_back.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);
        videoMinus.setOnClickListener(this);
        videoPlus.setOnClickListener(this);
        cb_crop.setOnCheckedChangeListener(this);
        cb_only_dir.setOnCheckedChangeListener(this);
        cb_custom_sandbox.setOnCheckedChangeListener(this);
        cb_crop_circular.setOnCheckedChangeListener(this);
        cb_attach_camera_mode.setOnCheckedChangeListener(this);
        cb_attach_system_mode.setOnCheckedChangeListener(this);
        cb_system_album.setOnCheckedChangeListener(this);
        cb_compress.setOnCheckedChangeListener(this);
        cb_not_gif.setOnCheckedChangeListener(this);
        cb_skip_not_gif.setOnCheckedChangeListener(this);
        tv_select_num.setText(ValueOf.toString(maxSelectNum));
        tv_select_video_num.setText(ValueOf.toString(maxSelectVideoNum));
        // 注册需要写在onCreate或Fragment onAttach里，否则会报java.lang.IllegalStateException异常
        launcherResult = createActivityResultLauncher();

//        List<LocalMedia> list = new ArrayList<>();
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://fdfs.test-kepu.weiyilewen.com/group1/M00/00/01/wKhkY2Iv936EMKWzAAAAAHuLNY8762.mp4"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx1.sinaimg.cn/mw2000/0073ozWdly1h0afogn4vij30u05keb29.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx3.sinaimg.cn/mw2000/0073ozWdly1h0afohdkygj30u05791kx.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx2.sinaimg.cn/mw2000/0073ozWdly1h0afoi70m2j30u05fq1kx.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx2.sinaimg.cn/mw2000/0073ozWdly1h0afoipj8xj30kw3kmwru.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx4.sinaimg.cn/mw2000/0073ozWdly1h0afoj5q8ij30u04gqkb1.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://ww1.sinaimg.cn/bmiddle/bcd10523ly1g96mg4sfhag20c806wu0x.gif"));
//        mData.addAll(list);

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                DensityUtil.dip2px(this, 8), false));
        mAdapter = new GridImageAdapter(getContext(), mData);
        mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mData.clear();
            mData.addAll(savedInstanceState.getParcelableArrayList("selectorList"));
        }
        String systemHigh = " (仅支持部分api)";
        String systemTips = "使用系统图库" + systemHigh;
        int startIndex = systemTips.indexOf(systemHigh);
        int endOf = startIndex + systemHigh.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(systemTips);
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(getContext(), 12)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFFCC0000), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        cb_system_album.setText(builder);

        String cameraHigh = " (默认fragment)";
        String cameraTips = "使用Activity承载Camera相机" + cameraHigh;
        int startIndex2 = cameraTips.indexOf(cameraHigh);
        int endOf2 = startIndex2 + cameraHigh.length();
        SpannableStringBuilder builder2 = new SpannableStringBuilder(cameraTips);
        builder2.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(getContext(), 12)), startIndex2, endOf2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder2.setSpan(new ForegroundColorSpan(0xFFCC0000), startIndex2, endOf2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        cb_attach_camera_mode.setText(builder2);


        String systemAlbumHigh = " (默认fragment)";
        String systemAlbumTips = "使用Activity承载系统相册" + systemAlbumHigh;
        int startIndex3 = systemAlbumTips.indexOf(systemAlbumHigh);
        int endOf3 = startIndex3 + systemAlbumHigh.length();
        SpannableStringBuilder builder3 = new SpannableStringBuilder(systemAlbumTips);
        builder3.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(getContext(), 12)), startIndex3, endOf3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder3.setSpan(new ForegroundColorSpan(0xFFCC0000), startIndex3, endOf3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        cb_attach_system_mode.setText(builder3);

        cb_original.setOnCheckedChangeListener((buttonView, isChecked) ->
                tv_original_tips.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        cb_choose_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cb_single_back.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            cb_single_back.setChecked(!isChecked && cb_single_back.isChecked());
        });

        imageEngine = GlideEngine.createGlideEngine();

        mAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 预览图片、视频、音频
                PictureSelector.create(MainActivity.this)
                        .openPreview()
                        .setImageEngine(imageEngine)
                        .setVideoPlayerEngine(videoPlayerEngine)
                        .setSelectorUIStyle(selectorStyle)
                        .setLanguage(language)
                        .isAutoVideoPlay(cb_auto_video.isChecked())
                        .isLoopAutoVideoPlay(cb_auto_video.isChecked())
                        .isPreviewFullScreenMode(cb_preview_full.isChecked())
                        .isVideoPauseResumePlay(cb_video_resume.isChecked())
                        .setCustomLoadingListener(getCustomLoadingListener())
                        .isPreviewZoomEffect(chooseMode != SelectMimeType.ofAudio() && cb_preview_scale.isChecked(), mRecyclerView)
                        .setAttachViewLifecycle(new IBridgeViewLifecycle() {
                            @Override
                            public void onViewCreated(Fragment fragment, View view, Bundle savedInstanceState) {
//                                PictureSelectorPreviewFragment previewFragment = (PictureSelectorPreviewFragment) fragment;
//                                MediumBoldTextView tvShare = view.findViewById(R.id.tv_share);
//                                tvShare.setVisibility(View.VISIBLE)
//                                previewFragment.addAminViews(tvShare);
//                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvShare.getLayoutParams();
//                                layoutParams.topMargin = cb_preview_full.isChecked() ? DensityUtil.getStatusBarHeight(getContext()) : 0;
//                                tvShare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        PicturePreviewAdapter previewAdapter = previewFragment.getAdapter();
//                                        ViewPager2 viewPager2 = previewFragment.getViewPager2();
//                                        LocalMedia media = previewAdapter.getItem(viewPager2.getCurrentItem());
//                                        ToastUtils.showToast(fragment.getContext(), "自定义分享事件:" + viewPager2.getCurrentItem());
//                                    }
//                                });
                            }

                            @Override
                            public void onDestroy(Fragment fragment) {
//                                if (cb_preview_full.isChecked()) {
//                                    // 如果是全屏预览模式且是startFragmentPreview预览，回到自己的界面时需要恢复一下自己的沉浸式状态
//                                    // 以下提供2种解决方案:
//                                    // 1.通过ImmersiveManager.immersiveAboveAPI23重新设置一下沉浸式
//                                    int statusBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    int navigationBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    ImmersiveManager.immersiveAboveAPI23(MainActivity.this,
//                                            true, true,
//                                            statusBarColor, navigationBarColor, false);
//                                    // 2.让自己的titleBar的高度加上一个状态栏高度且内容PaddingTop下沉一个状态栏的高度
//                                }
                            }
                        })
                        .setInjectLayoutResourceListener(new OnInjectLayoutResourceListener() {
                            @Override
                            public int getLayoutResourceId(Context context, int resourceSource) {
                                return resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE
                                        ? R.layout.ps_custom_fragment_preview
                                        : InjectResourceSource.DEFAULT_LAYOUT_RESOURCE;
                            }
                        })
                        .setExternalPreviewEventListener(new MyExternalPreviewEventListener())
                        .setInjectActivityPreviewFragment(new OnInjectActivityPreviewListener() {
                            @Override
                            public PictureSelectorPreviewFragment onInjectPreviewFragment() {
                                return cb_custom_preview.isChecked() ? CustomPreviewFragment.newInstance() : null;
                            }
                        })
                        .startActivityPreview(position, true, mAdapter.getData());
            }

            @Override
            public void openPicture() {
                boolean mode = cb_mode.isChecked();
                if (mode) {
                    // 进入系统相册
                    if (cb_system_album.isChecked()) {
                        PictureSelectionSystemModel systemGalleryMode = PictureSelector.create(getContext())
                                .openSystemGallery(chooseMode)
                                .setSelectionMode(cb_choose_mode.isChecked() ? SelectModeConfig.MULTIPLE : SelectModeConfig.SINGLE)
                                .setCompressEngine(getCompressFileEngine())
                                .setCropEngine(getCropFileEngine())
                                .setSkipCropMimeType(getNotSupportCrop())
                                .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                                .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
                                .setVideoThumbnailListener(getVideoThumbnailEventListener())
                                .setCustomLoadingListener(getCustomLoadingListener())
                                .isOriginalControl(cb_original.isChecked())
                                .setPermissionDescriptionListener(getPermissionDescriptionListener())
                                .setSandboxFileEngine(new MeSandboxFileEngine());
                        forSystemResult(systemGalleryMode);
                    } else {
                        // 进入相册
                        PictureSelectionModel selectionModel = PictureSelector.create(getContext())
                                .openGallery(chooseMode)
                                .setSelectorUIStyle(selectorStyle)
                                .setImageEngine(imageEngine)
                                .setVideoPlayerEngine(videoPlayerEngine)
                                .setCropEngine(getCropFileEngine())
                                .setCompressEngine(getCompressFileEngine())
                                .setSandboxFileEngine(new MeSandboxFileEngine())
                                .setCameraInterceptListener(getCustomCameraEvent())
                                .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
                                .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                                .setEditMediaInterceptListener(getCustomEditMediaEvent())
                                .setPermissionDescriptionListener(getPermissionDescriptionListener())
                                .setPreviewInterceptListener(getPreviewInterceptListener())
                                .setPermissionDeniedListener(getPermissionDeniedListener())
                                .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
                                .setVideoThumbnailListener(getVideoThumbnailEventListener())
                                .isAutoVideoPlay(cb_auto_video.isChecked())
                                .isLoopAutoVideoPlay(cb_auto_video.isChecked())
                                .isPageSyncAlbumCount(true)
                                .setCustomLoadingListener(getCustomLoadingListener())
                                .setQueryFilterListener(new OnQueryFilterListener() {
                                    @Override
                                    public boolean onFilter(LocalMedia media) {
                                        return false;
                                    }
                                })
                                //.setExtendLoaderEngine(getExtendLoaderEngine())
                                .setInjectLayoutResourceListener(getInjectLayoutResource())
                                .setSelectionMode(cb_choose_mode.isChecked() ? SelectModeConfig.MULTIPLE : SelectModeConfig.SINGLE)
                                .setLanguage(language)
                                .setQuerySortOrder(cb_query_sort_order.isChecked() ? MediaStore.MediaColumns.DATE_MODIFIED + " ASC" : "")
                                .setOutputCameraDir(chooseMode == SelectMimeType.ofAudio()
                                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                                .setOutputAudioDir(chooseMode == SelectMimeType.ofAudio()
                                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                                .setQuerySandboxDir(chooseMode == SelectMimeType.ofAudio()
                                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                                .isDisplayTimeAxis(cb_time_axis.isChecked())
                                .isOnlyObtainSandboxDir(cb_only_dir.isChecked())
                                .isPageStrategy(cbPage.isChecked())
                                .isOriginalControl(cb_original.isChecked())
                                .isDisplayCamera(cb_isCamera.isChecked())
                                .isOpenClickSound(cb_voice.isChecked())
                                .setSkipCropMimeType(getNotSupportCrop())
                                .isFastSlidingSelect(cb_fast_select.isChecked())
                                //.setOutputCameraImageFileName("luck.jpeg")
                                //.setOutputCameraVideoFileName("luck.mp4")
                                .isWithSelectVideoImage(cb_WithImageVideo.isChecked())
                                .isPreviewFullScreenMode(cb_preview_full.isChecked())
                                .isVideoPauseResumePlay(cb_video_resume.isChecked())
                                .isPreviewZoomEffect(cb_preview_scale.isChecked())
                                .isPreviewImage(cb_preview_img.isChecked())
                                .isPreviewVideo(cb_preview_video.isChecked())
                                .isPreviewAudio(cb_preview_audio.isChecked())
                                .setGridItemSelectAnimListener(cb_selected_anim.isChecked() ? new OnGridItemSelectAnimListener() {

                                    @Override
                                    public void onSelectItemAnim(View view, boolean isSelected) {
                                        AnimatorSet set = new AnimatorSet();
                                        set.playTogether(
                                                ObjectAnimator.ofFloat(view, "scaleX", isSelected ? 1F : 1.12F, isSelected ? 1.12f : 1.0F),
                                                ObjectAnimator.ofFloat(view, "scaleY", isSelected ? 1F : 1.12F, isSelected ? 1.12f : 1.0F)
                                        );
                                        set.setDuration(350);
                                        set.start();
                                    }
                                } : null)
                                .setSelectAnimListener(cb_selected_anim.isChecked() ? new OnSelectAnimListener() {

                                    @Override
                                    public long onSelectAnim(View view) {
                                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
                                        view.startAnimation(animation);
                                        return animation.getDuration();
                                    }
                                } : null)
                                //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
                                .isMaxSelectEnabledMask(cbEnabledMask.isChecked())
                                .isDirectReturnSingle(cb_single_back.isChecked())
                                .setMaxSelectNum(maxSelectNum)
                                .setMaxVideoSelectNum(maxSelectVideoNum)
                                .setRecyclerAnimationMode(animationMode)
                                .isGif(cb_isGif.isChecked())
                                .setSelectedData(mAdapter.getData());
                        forSelectResult(selectionModel);
                    }
                } else {
                    // 单独拍照
                    PictureSelectionCameraModel cameraModel = PictureSelector.create(MainActivity.this)
                            .openCamera(chooseMode)
                            .setCameraInterceptListener(getCustomCameraEvent())
                            .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
                            .setCropEngine(getCropFileEngine())
                            .setCompressEngine(getCompressFileEngine())
                            .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                            .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
                            .setVideoThumbnailListener(getVideoThumbnailEventListener())
                            .setCustomLoadingListener(getCustomLoadingListener())
                            .setLanguage(language)
                            .setSandboxFileEngine(new MeSandboxFileEngine())
                            .isOriginalControl(cb_original.isChecked())
                            .setPermissionDescriptionListener(getPermissionDescriptionListener())
                            .setOutputAudioDir(getSandboxAudioOutputPath())
                            .setSelectedData(mAdapter.getData());
                    forOnlyCameraResult(cameraModel);
                }
            }
        });

        mAdapter.setItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v) {
                int itemViewType = holder.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    mItemTouchHelper.startDrag(holder);
                }
            }
        });
        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        // 清除缓存
//        clearCache();
    }

    private String[] getNotSupportCrop() {
        if (cb_skip_not_gif.isChecked()) {
            return new String[]{PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()};
        }
        return null;
    }

    private final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(0.7f);
            }
            return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            try {
                //得到item原来的position
                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                //得到目标position
                int toPosition = target.getAbsoluteAdapterPosition();
                int itemViewType = target.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(mAdapter.getData(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(mAdapter.getData(), i, i - 1);
                        }
                    }
                    mAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dx, float dy, int actionState, boolean isCurrentlyActive) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (needScaleBig) {
                    needScaleBig = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0F, 1.1F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0F, 1.1F));
                    animatorSet.setDuration(50);
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleSmall = true;
                        }
                    });
                }
                int targetDy = tvDeleteText.getTop() - viewHolder.itemView.getBottom();
                if (dy >= targetDy) {
                    //拖到删除处
                    mDragListener.deleteState(true);
                    if (isHasLiftDelete) {
                        //在删除处放手，则删除item
                        viewHolder.itemView.setVisibility(View.INVISIBLE);
                        mAdapter.delete(viewHolder.getAbsoluteAdapterPosition());
                        resetState();
                        return;
                    }
                } else {
                    //没有到删除处
                    if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {
                        //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                        mDragListener.dragState(false);
                    }
                    mDragListener.deleteState(false);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : GridImageAdapter.TYPE_CAMERA;
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                    mDragListener.dragState(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            isHasLiftDelete = true;
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(1.0F);
                if (needScaleSmall) {
                    needScaleSmall = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1F, 1.0F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1F, 1.0F));
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.setDuration(50);
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleBig = true;
                        }
                    });
                }
                super.clearView(recyclerView, viewHolder);
                mAdapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                resetState();
            }
        }
    });

    private final DragListener mDragListener = new DragListener() {
        @Override
        public void deleteState(boolean isDelete) {
            if (isDelete) {
                if (!TextUtils.equals(getString(R.string.app_let_go_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_let_go_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_dump_delete, 0, 0);
                }
            } else {
                if (!TextUtils.equals(getString(R.string.app_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_normal_delete, 0, 0);
                }
            }

        }

        @Override
        public void dragState(boolean isStart) {
            if (isStart) {
                if (tvDeleteText.getAlpha() == 0F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 0F, 1F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            } else {
                if (tvDeleteText.getAlpha() == 1F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 1F, 0F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            }
        }
    };

    private void forSystemResult(PictureSelectionSystemModel model) {
        if (cb_attach_system_mode.isChecked()) {
            switch (resultMode) {
                case ACTIVITY_RESULT:
                    model.forSystemResultActivity(PictureConfig.REQUEST_CAMERA);
                    break;
                case CALLBACK_RESULT:
                    model.forSystemResultActivity(new MeOnResultCallbackListener());
                    break;
                default:
                    model.forSystemResultActivity(launcherResult);
                    break;
            }
        } else {
            if (resultMode == CALLBACK_RESULT) {
                model.forSystemResult(new MeOnResultCallbackListener());
            } else {
                model.forSystemResult();
            }
        }
    }

    private void forSelectResult(PictureSelectionModel model) {
        switch (resultMode) {
            case ACTIVITY_RESULT:
                model.forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case CALLBACK_RESULT:
                model.forResult(new MeOnResultCallbackListener());
                break;
            default:
                model.forResult(launcherResult);
                break;
        }
    }

    private void forOnlyCameraResult(PictureSelectionCameraModel model) {
        if (cb_attach_camera_mode.isChecked()) {
            switch (resultMode) {
                case ACTIVITY_RESULT:
                    model.forResultActivity(PictureConfig.REQUEST_CAMERA);
                    break;
                case CALLBACK_RESULT:
                    model.forResultActivity(new MeOnResultCallbackListener());
                    break;
                default:
                    model.forResultActivity(launcherResult);
                    break;
            }
        } else {
            if (resultMode == CALLBACK_RESULT) {
                model.forResult(new MeOnResultCallbackListener());
            } else {
                model.forResult();
            }
        }
    }

    /**
     * 重置
     */
    private void resetState() {
        isHasLiftDelete = false;
        mDragListener.deleteState(false);
        mDragListener.dragState(false);
    }

    /**
     * 外部预览监听事件
     */
    private class MyExternalPreviewEventListener implements OnExternalPreviewEventListener {

        @Override
        public void onPreviewDelete(int position) {
            mAdapter.remove(position);
            mAdapter.notifyItemRemoved(position);
        }

        @Override
        public boolean onLongPressDownload(LocalMedia media) {
            return false;
        }
    }

    /**
     * 选择结果
     */
    private class MeOnResultCallbackListener implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {
            analyticalSelectResults(result);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "PictureSelector Cancel");
        }
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    private ImageFileCompressEngine getCompressFileEngine() {
        return cb_compress.isChecked() ? new ImageFileCompressEngine() : null;
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    @Deprecated
    private ImageCompressEngine getCompressEngine() {
        return cb_compress.isChecked() ? new ImageCompressEngine() : null;
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageFileCropEngine getCropFileEngine() {
        return cb_crop.isChecked() ? new ImageFileCropEngine() : null;
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageCropEngine getCropEngine() {
        return cb_crop.isChecked() ? new ImageCropEngine() : null;
    }

    /**
     * 自定义相机事件
     *
     * @return
     */
    private OnCameraInterceptListener getCustomCameraEvent() {
        return cb_custom_camera.isChecked() ? new MeOnCameraInterceptListener() : null;
    }


    /**
     * 自定义数据加载器
     *
     * @return
     */
    private ExtendLoaderEngine getExtendLoaderEngine() {
        return new MeExtendLoaderEngine();
    }


    /**
     * 注入自定义布局
     *
     * @return
     */
    private OnInjectLayoutResourceListener getInjectLayoutResource() {
        return cb_inject_layout.isChecked() ? new MeOnInjectLayoutResourceListener() : null;
    }


    /**
     * 处理视频缩略图
     */
    private OnVideoThumbnailEventListener getVideoThumbnailEventListener() {
        return cb_video_thumbnails.isChecked() ? new MeOnVideoThumbnailEventListener(getVideoThumbnailDir()) : null;
    }

    /**
     * 处理视频缩略图
     */
    private static class MeOnVideoThumbnailEventListener implements OnVideoThumbnailEventListener {
        private final String targetPath;

        public MeOnVideoThumbnailEventListener(String targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public void onVideoThumbnail(Context context, String videoPath, OnKeyValueResultCallbackListener call) {
            Glide.with(context).asBitmap().sizeMultiplier(0.6F).load(videoPath).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    FileOutputStream fos = null;
                    String result = null;
                    try {
                        File targetFile = new File(targetPath, "thumbnails_" + System.currentTimeMillis() + ".jpg");
                        fos = new FileOutputStream(targetFile);
                        fos.write(stream.toByteArray());
                        fos.flush();
                        result = targetFile.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        PictureFileUtils.close(fos);
                        PictureFileUtils.close(stream);
                    }
                    if (call != null) {
                        call.onCallback(videoPath, result);
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    if (call != null) {
                        call.onCallback(videoPath, "");
                    }
                }
            });
        }
    }

    /**
     * 自定义loading
     *
     * @return
     */
    private OnCustomLoadingListener getCustomLoadingListener() {
        if (cb_custom_loading.isChecked()) {
            return new OnCustomLoadingListener() {
                @Override
                public Dialog create(Context context) {
                    return new CustomLoadingDialog(context);
                }
            };
        }
        return null;
    }

    /**
     * 给图片添加水印
     */
    private OnBitmapWatermarkEventListener getAddBitmapWatermarkListener() {
        return cb_watermark.isChecked() ? new MeBitmapWatermarkEventListener(getSandboxMarkDir()) : null;
    }

    /**
     * 给图片添加水印
     */
    private static class MeBitmapWatermarkEventListener implements OnBitmapWatermarkEventListener {
        private final String targetPath;

        public MeBitmapWatermarkEventListener(String targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public void onAddBitmapWatermark(Context context, String srcPath, String mimeType, OnKeyValueResultCallbackListener call) {
            if (PictureMimeType.isHasHttp(srcPath) || PictureMimeType.isHasVideo(mimeType)) {
                // 网络图片和视频忽略，有需求的可自行扩展
                call.onCallback(srcPath, "");
            } else {
                // 暂时只以图片为例
                Glide.with(context).asBitmap().sizeMultiplier(0.6F).load(srcPath).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mark_win);
                        Bitmap watermarkBitmap = ImageUtil.createWaterMaskRightTop(context, resource, watermark, 15, 15);
                        watermarkBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                        watermarkBitmap.recycle();
                        FileOutputStream fos = null;
                        String result = null;
                        try {
                            File targetFile = new File(targetPath, DateUtils.getCreateFileName("Mark_") + ".jpg");
                            fos = new FileOutputStream(targetFile);
                            fos.write(stream.toByteArray());
                            fos.flush();
                            result = targetFile.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            PictureFileUtils.close(fos);
                            PictureFileUtils.close(stream);
                        }
                        if (call != null) {
                            call.onCallback(srcPath, result);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (call != null) {
                            call.onCallback(srcPath, "");
                        }
                    }
                });
            }
        }
    }


    /**
     * 权限拒绝后回调
     *
     * @return
     */
    private OnPermissionDeniedListener getPermissionDeniedListener() {
        return cb_permission_desc.isChecked() ? new MeOnPermissionDeniedListener() : null;
    }


    /**
     * 权限拒绝后回调
     */
    private static class MeOnPermissionDeniedListener implements OnPermissionDeniedListener {

        @Override
        public void onDenied(Fragment fragment, String[] permissionArray,
                             int requestCode, OnCallbackListener<Boolean> call) {
            String tips;
            if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少录音权限\n访问您设备上的音频、媒体内容和文件";
            } else {
                tips = "缺少存储权限\n访问您设备上的照片、媒体内容和文件";
            }
            RemindDialog dialog = RemindDialog.buildDialog(fragment.getContext(), tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionUtil.goIntentSetting(fragment, requestCode);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * SimpleCameraX权限拒绝后回调
     *
     * @return
     */
    private OnSimpleXPermissionDeniedListener getSimpleXPermissionDeniedListener() {
        return cb_permission_desc.isChecked() ? new MeOnSimpleXPermissionDeniedListener() : null;
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private static class MeOnSimpleXPermissionDeniedListener implements OnSimpleXPermissionDeniedListener {

        @Override
        public void onDenied(Context context, String permission, int requestCode) {
            String tips;
            if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少麦克风权限\n可能会导致录视频无法采集声音";
            } else {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            }
            RemindDialog dialog = RemindDialog.buildDialog(context, tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleXPermissionUtil.goIntentSetting((Activity) context, requestCode);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * SimpleCameraX权限说明
     *
     * @return
     */
    private OnSimpleXPermissionDescriptionListener getSimpleXPermissionDescriptionListener() {
        return cb_permission_desc.isChecked() ? new MeOnSimpleXPermissionDescriptionListener() : null;
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private static class MeOnSimpleXPermissionDescriptionListener implements OnSimpleXPermissionDescriptionListener {

        @Override
        public void onPermissionDescription(Context context, ViewGroup viewGroup, String permission) {
            addPermissionDescription(true, viewGroup, new String[]{permission});
        }

        @Override
        public void onDismiss(ViewGroup viewGroup) {
            removePermissionDescription(viewGroup);
        }
    }


    /**
     * 权限说明
     *
     * @return
     */
    private OnPermissionDescriptionListener getPermissionDescriptionListener() {
        return cb_permission_desc.isChecked() ? new MeOnPermissionDescriptionListener() : null;
    }

    /**
     * 添加权限说明
     */
    private static class MeOnPermissionDescriptionListener implements OnPermissionDescriptionListener {

        @Override
        public void onPermissionDescription(Fragment fragment, String[] permissionArray) {
            View rootView = fragment.requireView();
            if (rootView instanceof ViewGroup) {
                addPermissionDescription(false, (ViewGroup) rootView, permissionArray);
            }
        }

        @Override
        public void onDismiss(Fragment fragment) {
            removePermissionDescription((ViewGroup) fragment.requireView());
        }
    }

    /**
     * 添加权限说明
     *
     * @param viewGroup
     * @param permissionArray
     */
    private static void addPermissionDescription(boolean isHasSimpleXCamera, ViewGroup viewGroup, String[] permissionArray) {
        int dp10 = DensityUtil.dip2px(viewGroup.getContext(), 10);
        int dp15 = DensityUtil.dip2px(viewGroup.getContext(), 15);
        MediumBoldTextView view = new MediumBoldTextView(viewGroup.getContext());
        view.setTag(TAG_EXPLAIN_VIEW);
        view.setTextSize(14);
        view.setTextColor(Color.parseColor("#333333"));
        view.setPadding(dp10, dp15, dp10, dp15);

        String title;
        String explain;

        if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
            title = "相机权限使用说明";
            explain = "相机权限使用说明\n用户app用于拍照/录视频";
        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
            if (isHasSimpleXCamera) {
                title = "麦克风权限使用说明";
                explain = "麦克风权限使用说明\n用户app用于录视频时采集声音";
            } else {
                title = "录音权限使用说明";
                explain = "录音权限使用说明\n用户app用于采集声音";
            }
        } else {
            title = "存储权限使用说明";
            explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息";
        }
        int startIndex = 0;
        int endOf = startIndex + title.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(explain);
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.getContext(), 16)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF333333), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(builder);
        view.setBackground(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.ps_demo_permission_desc_bg));

        if (isHasSimpleXCamera) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.getContext());
            layoutParams.leftMargin = dp10;
            layoutParams.rightMargin = dp10;
            viewGroup.addView(view, layoutParams);
        } else {
            ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topToBottom = R.id.title_bar;
            layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
            layoutParams.leftMargin = dp10;
            layoutParams.rightMargin = dp10;
            viewGroup.addView(view, layoutParams);
        }
    }

    /**
     * 移除权限说明
     *
     * @param viewGroup
     */
    private static void removePermissionDescription(ViewGroup viewGroup) {
        View tagExplainView = viewGroup.findViewWithTag(TAG_EXPLAIN_VIEW);
        viewGroup.removeView(tagExplainView);
    }



    /**
     * 自定义预览
     *
     * @return
     */
    private OnPreviewInterceptListener getPreviewInterceptListener() {
        return cb_custom_preview.isChecked() ? new MeOnPreviewInterceptListener() : null;
    }

    /**
     * 自定义预览
     *
     * @return
     */
    private static class MeOnPreviewInterceptListener implements OnPreviewInterceptListener {

        @Override
        public void onPreview(Context context, int position, int totalNum, int page, long currentBucketId, String currentAlbumName, boolean isShowCamera, ArrayList<LocalMedia> data, boolean isBottomPreview) {
            CustomPreviewFragment previewFragment = CustomPreviewFragment.newInstance();
            previewFragment.setInternalPreviewData(isBottomPreview, currentAlbumName, isShowCamera,
                    position, totalNum, page, currentBucketId, data);
            FragmentInjectManager.injectFragment((FragmentActivity) context, CustomPreviewFragment.TAG, previewFragment);
        }
    }

    /**
     * 拦截自定义提示
     */
    private static class MeOnSelectLimitTipsListener implements OnSelectLimitTipsListener {

        @Override
        public boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, PictureSelectionConfig config, int limitType) {
            if (limitType == SelectLimitType.SELECT_MIN_SELECT_LIMIT) {
                ToastUtils.showToast(context, "图片最少不能低于" + config.minSelectNum + "张");
                return true;
            } else if (limitType == SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "视频最少不能低于" + config.minVideoSelectNum + "个");
                return true;
            } else if (limitType == SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "音频最少不能低于" + config.minAudioSelectNum + "个");
                return true;
            }
            return false;
        }
    }

    /**
     * 注入自定义布局UI，前提是布局View id 和 根目录Layout必须一致
     */
    private static class MeOnInjectLayoutResourceListener implements OnInjectLayoutResourceListener {

        @Override
        public int getLayoutResourceId(Context context, int resourceSource) {
            switch (resourceSource) {
                case InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_fragment_selector;
                case InjectResourceSource.PREVIEW_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_fragment_preview;
                case InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_image;
                case InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_video;
                case InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_audio;
                case InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_album_folder_item;
                case InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_image;
                case InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_video;
                case InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_gallery_item;
                default:
                    return 0;
            }
        }
    }

    /**
     * 自定义数据加载器
     */
    private class MeExtendLoaderEngine implements ExtendLoaderEngine {

        @Override
        public void loadAllAlbumData(Context context,
                                     OnQueryAllAlbumListener<LocalMediaFolder> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            List<LocalMediaFolder> folders = new ArrayList<>();
            folders.add(folder);
            query.onComplete(folders);
        }

        @Override
        public void loadOnlyInAppDirAllMediaData(Context context,
                                                 OnQueryAlbumListener<LocalMediaFolder> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            query.onComplete(folder);
        }

        @Override
        public void loadFirstPageMediaData(Context context, long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            query.onComplete(folder.getData(), false);
        }

        @Override
        public void loadMoreMediaData(Context context, long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

        }
    }

    /**
     * 自定义编辑事件
     *
     * @return
     */
    private OnMediaEditInterceptListener getCustomEditMediaEvent() {
        return cbEditor.isChecked() ? new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()) : null;
    }


    /**
     * 自定义编辑
     */
    private static class MeOnMediaEditInterceptListener implements OnMediaEditInterceptListener {
        private final String outputCropPath;
        private final UCrop.Options options;

        public MeOnMediaEditInterceptListener(String outputCropPath, UCrop.Options options) {
            this.outputCropPath = outputCropPath;
            this.options = options;
        }

        @Override
        public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
            String currentEditPath = currentLocalMedia.getAvailablePath();
            Uri inputUri = PictureMimeType.isContent(currentEditPath)
                    ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
            Uri destinationUri = Uri.fromFile(
                    new File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg"));
            UCrop uCrop = UCrop.of(inputUri, destinationUri);
            options.setHideBottomControls(false);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.startEdit(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /**
     * 录音回调事件
     */
    private static class MeOnRecordAudioInterceptListener implements OnRecordAudioInterceptListener {

        @Override
        public void onRecordAudio(Fragment fragment, int requestCode) {
            String[] recordAudio = {Manifest.permission.RECORD_AUDIO};
            if (PermissionChecker.isCheckSelfPermission(fragment.getContext(), recordAudio)) {
                startRecordSoundAction(fragment, requestCode);
            } else {
                addPermissionDescription(false, (ViewGroup) fragment.requireView(), recordAudio);
                PermissionChecker.getInstance().requestPermissions(fragment,
                        new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionResultCallback() {
                            @Override
                            public void onGranted() {
                                removePermissionDescription((ViewGroup) fragment.requireView());
                                startRecordSoundAction(fragment, requestCode);
                            }

                            @Override
                            public void onDenied() {
                                removePermissionDescription((ViewGroup) fragment.requireView());
                            }
                        });
            }
        }
    }

    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    private static void startRecordSoundAction(Fragment fragment, int requestCode) {
        Intent recordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (recordAudioIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode);
        } else {
            ToastUtils.showToast(fragment.getContext(), "The system is missing a recording component");
        }
    }

    /**
     * 自定义拍照
     */
    private class MeOnCameraInterceptListener implements OnCameraInterceptListener {

        @Override
        public void openCamera(Fragment fragment, int cameraMode, int requestCode) {
            SimpleCameraX camera = SimpleCameraX.of();
            camera.isAutoRotation(true);
            camera.setCameraMode(cameraMode);
            camera.setVideoFrameRate(25);
            camera.setVideoBitRate(3 * 1024 * 1024);
            camera.isDisplayRecordChangeTime(true);
            camera.isManualFocusCameraPreview(cb_camera_focus.isChecked());
            camera.isZoomCameraPreview(cb_camera_zoom.isChecked());
            camera.setOutputPathDir(getSandboxCameraOutputPath());
            camera.setPermissionDeniedListener(getSimpleXPermissionDeniedListener());
            camera.setPermissionDescriptionListener(getSimpleXPermissionDescriptionListener());
            camera.setImageEngine(new CameraImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    Glide.with(context).load(url).into(imageView);
                }
            });
            camera.start(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /**
     * 自定义沙盒文件处理
     */
    private static class MeSandboxFileEngine implements UriToFileTransformEngine {

        @Override
        public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
            if (call != null) {
                call.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType));
            }
        }
    }

    /**
     * 自定义裁剪
     */
    private class ImageFileCropEngine implements CropFileEngine {

        @Override
        public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
            UCrop.Options options = buildOptions();
            UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /**
     * 自定义裁剪
     */
    private class ImageCropEngine implements CropEngine {

        @Override
        public void onStartCrop(Fragment fragment, LocalMedia currentLocalMedia,
                                ArrayList<LocalMedia> dataSource, int requestCode) {
            String currentCropPath = currentLocalMedia.getAvailablePath();
            Uri inputUri;
            if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
                inputUri = Uri.parse(currentCropPath);
            } else {
                inputUri = Uri.fromFile(new File(currentCropPath));
            }
            String fileName = DateUtils.getCreateFileName("CROP_") + ".jpg";
            Uri destinationUri = Uri.fromFile(new File(getSandboxPath(), fileName));
            UCrop.Options options = buildOptions();
            ArrayList<String> dataCropSource = new ArrayList<>();
            for (int i = 0; i < dataSource.size(); i++) {
                LocalMedia media = dataSource.get(i);
                dataCropSource.add(media.getAvailablePath());
            }
            UCrop uCrop = UCrop.of(inputUri, destinationUri, dataCropSource);
            //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                }
            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }




    /**
     * 多图裁剪时每张对应的裁剪比例
     *
     * @param dataSourceCount
     * @return
     */
    private AspectRatio[] buildAspectRatios(int dataSourceCount) {
        AspectRatio[] aspectRatios = new AspectRatio[dataSourceCount];
        for (int i = 0; i < dataSourceCount; i++) {
            if (i == 0) {
                aspectRatios[i] = new AspectRatio("16:9", 16, 9);
            } else if (i == 1) {
                aspectRatios[i] = new AspectRatio("3:2", 3, 2);
            } else {
                aspectRatios[i] = new AspectRatio("原始比例", 0, 0);
            }
        }
        return aspectRatios;
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(!cb_hide.isChecked());
        options.setFreeStyleCropEnabled(cb_styleCrop.isChecked());
        options.setShowCropFrame(cb_showCropFrame.isChecked());
        options.setShowCropGrid(cb_showCropGrid.isChecked());
        options.setCircleDimmedLayer(cb_crop_circular.isChecked());
        options.withAspectRatio(aspect_ratio_x, aspect_ratio_y);
        options.setCropOutputPathDir(getSandboxPath());
        options.isCropDragSmoothToCenter(false);
        options.setSkipCropMimeType(getNotSupportCrop());
        options.isForbidCropGifWebp(cb_not_gif.isChecked());
        options.isForbidSkipMultipleCrop(true);
        options.setMaxScaleMultiplier(100);
        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
            int statusBarColor = mainStyle.getStatusBarColor();
            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor);
                options.setToolbarColor(statusBarColor);
            } else {
                options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        }
        return options;
    }

    /**
     * 自定义压缩
     */
    private static class ImageFileCompressEngine implements CompressFileEngine {

        @Override
        public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener(new OnRenameListener() {
                @Override
                public String rename(String filePath) {
                    int indexOf = filePath.lastIndexOf(".");
                    String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                    return DateUtils.getCreateFileName("CMP_") + postfix;
                }
            }).filter(new CompressionPredicate() {
                @Override
                public boolean apply(String path) {
                    if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                        return true;
                    }
                    return !PictureMimeType.isUrlHasGif(path);
                }
            }).setCompressListener(new OnNewCompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(String source, File compressFile) {
                    if (call != null) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }
                }

                @Override
                public void onError(String source, Throwable e) {
                    if (call != null) {
                        call.onCallback(source, null);
                    }
                }
            }).launch();
        }
    }


    /**
     * 自定义压缩
     */
    @Deprecated
    private static class ImageCompressEngine implements CompressEngine {

        @Override
        public void onStartCompress(Context context, ArrayList<LocalMedia> list,
                                    OnCallbackListener<ArrayList<LocalMedia>> listener) {
            // 自定义压缩
            List<Uri> compress = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                LocalMedia media = list.get(i);
                String availablePath = media.getAvailablePath();
                Uri uri = PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)
                        ? Uri.parse(availablePath)
                        : Uri.fromFile(new File(availablePath));
                compress.add(uri);
            }
            if (compress.size() == 0) {
                listener.onCall(list);
                return;
            }
            Luban.with(context)
                    .load(compress)
                    .ignoreBy(100)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path);
                        }
                    })
                    .setRenameListener(new OnRenameListener() {
                        @Override
                        public String rename(String filePath) {
                            int indexOf = filePath.lastIndexOf(".");
                            String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                            return DateUtils.getCreateFileName("CMP_") + postfix;
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int index, File compressFile) {
                            LocalMedia media = list.get(index);
                            if (compressFile.exists() && !TextUtils.isEmpty(compressFile.getAbsolutePath())) {
                                media.setCompressed(true);
                                media.setCompressPath(compressFile.getAbsolutePath());
                                media.setSandboxPath(SdkVersionUtils.isQ() ? media.getCompressPath() : null);
                            }
                            if (index == list.size() - 1) {
                                listener.onCall(list);
                            }
                        }

                        @Override
                        public void onError(int index, Throwable e) {
                            if (index != -1) {
                                LocalMedia media = list.get(index);
                                media.setCompressed(false);
                                media.setCompressPath(null);
                                media.setSandboxPath(null);
                                if (index == list.size() - 1) {
                                    listener.onCall(list);
                                }
                            }
                        }
                    }).launch();
        }

    }

    /**
     * 创建相机自定义输出目录
     *
     * @return
     */
    private String getSandboxCameraOutputPath() {
        if (cb_custom_sandbox.isChecked()) {
            File externalFilesDir = getContext().getExternalFilesDir("");
            File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
            return customFile.getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 创建音频自定义输出目录
     *
     * @return
     */
    private String getSandboxAudioOutputPath() {
        if (cb_custom_sandbox.isChecked()) {
            File externalFilesDir = getContext().getExternalFilesDir("");
            File customFile = new File(externalFilesDir.getAbsolutePath(), "Sound");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
            return customFile.getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxMarkDir() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Mark");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getVideoThumbnailDir() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Thumbnail");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.minus:
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                tv_select_num.setText(String.valueOf(maxSelectNum));
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
                break;
            case R.id.plus:
                maxSelectNum++;
                tv_select_num.setText(String.valueOf(maxSelectNum));
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
                break;

            case R.id.video_minus:
                if (maxSelectVideoNum > 1) {
                    maxSelectVideoNum--;
                }
                tv_select_video_num.setText(String.valueOf(maxSelectVideoNum));
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum);
                break;
            case R.id.video_plus:
                maxSelectVideoNum++;
                tv_select_video_num.setText(String.valueOf(maxSelectVideoNum));
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_all:
                chooseMode = SelectMimeType.ofAll();
                cb_preview_img.setChecked(true);
                cb_preview_video.setChecked(true);
                cb_isGif.setChecked(false);
                cb_preview_video.setChecked(true);
                cb_preview_img.setChecked(true);
                cb_preview_video.setVisibility(View.VISIBLE);
                cb_preview_img.setVisibility(View.VISIBLE);
                llSelectVideoSize.setVisibility(View.VISIBLE);
                cb_compress.setVisibility(View.VISIBLE);
                cb_crop.setVisibility(View.VISIBLE);
                cb_isGif.setVisibility(View.VISIBLE);
                cb_preview_audio.setVisibility(View.GONE);
                break;
            case R.id.rb_image:
                llSelectVideoSize.setVisibility(View.GONE);
                chooseMode = SelectMimeType.ofImage();
                cb_preview_img.setChecked(true);
                cb_preview_video.setChecked(false);
                cb_isGif.setChecked(false);
                cb_preview_video.setChecked(false);
                cb_preview_video.setVisibility(View.GONE);
                cb_preview_img.setChecked(true);
                cb_preview_audio.setVisibility(View.GONE);
                cb_preview_img.setVisibility(View.VISIBLE);
                cb_compress.setVisibility(View.VISIBLE);
                cb_crop.setVisibility(View.VISIBLE);
                cb_isGif.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_video:
                llSelectVideoSize.setVisibility(View.GONE);
                chooseMode = SelectMimeType.ofVideo();
                cb_preview_img.setChecked(false);
                cb_preview_video.setChecked(true);
                cb_isGif.setChecked(false);
                cb_isGif.setVisibility(View.GONE);
                cb_preview_video.setChecked(true);
                cb_preview_video.setVisibility(View.VISIBLE);
                cb_preview_img.setVisibility(View.GONE);
                cb_preview_img.setChecked(false);
                cb_compress.setVisibility(View.GONE);
                cb_preview_audio.setVisibility(View.GONE);
                cb_crop.setVisibility(View.GONE);
                break;
            case R.id.rb_audio:
                chooseMode = SelectMimeType.ofAudio();
                cb_preview_audio.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_glide:
                imageEngine = GlideEngine.createGlideEngine();
                break;
            case R.id.rb_picasso:
                imageEngine = PicassoEngine.createPicassoEngine();
                break;
            case R.id.rb_coil:
                imageEngine = new CoilEngine();
                break;
            case R.id.rb_media_player:
                videoPlayerEngine = null;
                break;
            case R.id.rb_exo_player:
                videoPlayerEngine = new ExoPlayerEngine();
                break;
            case R.id.rb_ijk_player:
                videoPlayerEngine = new IjkPlayerEngine();
                break;
            case R.id.rb_system:
                language = LanguageConfig.SYSTEM_LANGUAGE;
                break;
            case R.id.rb_jpan:
                language = LanguageConfig.JAPAN;
                break;
            case R.id.rb_tw:
                language = LanguageConfig.TRADITIONAL_CHINESE;
                break;
            case R.id.rb_us:
                language = LanguageConfig.ENGLISH;
                break;
            case R.id.rb_ka:
                language = LanguageConfig.KOREA;
                break;
            case R.id.rb_de:
                language = LanguageConfig.GERMANY;
                break;
            case R.id.rb_fr:
                language = LanguageConfig.FRANCE;
                break;
            case R.id.rb_spanish:
                language = LanguageConfig.SPANISH;
                break;
            case R.id.rb_portugal:
                language = LanguageConfig.PORTUGAL;
                break;
            case R.id.rb_ar:
                language = LanguageConfig.AR;
            case R.id.rb_ru:
                language = LanguageConfig.RU;
                break;
            case R.id.rb_crop_default:
                aspect_ratio_x = -1;
                aspect_ratio_y = -1;
                break;
            case R.id.rb_crop_1to1:
                aspect_ratio_x = 1;
                aspect_ratio_y = 1;
                break;
            case R.id.rb_crop_3to4:
                aspect_ratio_x = 3;
                aspect_ratio_y = 4;
                break;
            case R.id.rb_crop_3to2:
                aspect_ratio_x = 3;
                aspect_ratio_y = 2;
                break;
            case R.id.rb_crop_16to9:
                aspect_ratio_x = 16;
                aspect_ratio_y = 9;
                break;
            case R.id.rb_launcher_result:
                resultMode = 0;
                break;
            case R.id.rb_activity_result:
                resultMode = 1;
                break;
            case R.id.rb_callback_result:
                resultMode = 2;
                break;
            case R.id.rb_photo_default_animation:
                PictureWindowAnimationStyle defaultAnimationStyle = new PictureWindowAnimationStyle();
                defaultAnimationStyle.setActivityEnterAnimation(R.anim.ps_anim_enter);
                defaultAnimationStyle.setActivityExitAnimation(R.anim.ps_anim_exit);
                selectorStyle.setWindowAnimationStyle(defaultAnimationStyle);
                break;
            case R.id.rb_photo_up_animation:
                PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in);
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out);
                selectorStyle.setWindowAnimationStyle(animationStyle);
                break;
            case R.id.rb_default_style:
                selectorStyle = new PictureSelectorStyle();

                break;
            case R.id.rb_white_style:
                TitleBarStyle whiteTitleBarStyle = new TitleBarStyle();
                whiteTitleBarStyle.setTitleBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                whiteTitleBarStyle.setTitleDrawableRightResource(R.drawable.ic_orange_arrow_down);
                whiteTitleBarStyle.setTitleLeftBackResource(R.drawable.ps_ic_black_back);
                whiteTitleBarStyle.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
                whiteTitleBarStyle.setTitleCancelTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
                whiteTitleBarStyle.setDisplayTitleBarLine(true);

                BottomNavBarStyle whiteBottomNavBarStyle = new BottomNavBarStyle();
                whiteBottomNavBarStyle.setBottomNarBarBackgroundColor(Color.parseColor("#EEEEEE"));
                whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));

                whiteBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_fa632d));
                whiteBottomNavBarStyle.setCompleteCountTips(false);
                whiteBottomNavBarStyle.setBottomEditorTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
                whiteBottomNavBarStyle.setBottomOriginalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));

                SelectMainStyle selectMainStyle = new SelectMainStyle();
                selectMainStyle.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                selectMainStyle.setDarkStatusBarBlack(true);
                selectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                selectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_fa632d));
                selectMainStyle.setPreviewSelectBackground(R.drawable.ps_demo_white_preview_selector);
                selectMainStyle.setSelectBackground(R.drawable.ps_checkbox_selector);
                selectMainStyle.setSelectText(getString(R.string.ps_done_front_num));
                selectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));

                selectorStyle.setTitleBarStyle(whiteTitleBarStyle);
                selectorStyle.setBottomBarStyle(whiteBottomNavBarStyle);
                selectorStyle.setSelectMainStyle(selectMainStyle);
                break;
            case R.id.rb_num_style:
                TitleBarStyle blueTitleBarStyle = new TitleBarStyle();
                blueTitleBarStyle.setTitleBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_blue));

                BottomNavBarStyle numberBlueBottomNavBarStyle = new BottomNavBarStyle();
                numberBlueBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                numberBlueBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_blue));
                numberBlueBottomNavBarStyle.setBottomNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                numberBlueBottomNavBarStyle.setBottomSelectNumResources(R.drawable.ps_demo_blue_num_selected);
                numberBlueBottomNavBarStyle.setBottomEditorTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
                numberBlueBottomNavBarStyle.setBottomOriginalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));


                SelectMainStyle numberBlueSelectMainStyle = new SelectMainStyle();
                numberBlueSelectMainStyle.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_blue));
                numberBlueSelectMainStyle.setSelectNumberStyle(true);
                numberBlueSelectMainStyle.setPreviewSelectNumberStyle(true);
                numberBlueSelectMainStyle.setSelectBackground(R.drawable.ps_demo_blue_num_selector);
                numberBlueSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                numberBlueSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_demo_preview_blue_num_selector);

                numberBlueSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                numberBlueSelectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_blue));
                numberBlueSelectMainStyle.setSelectText(getString(R.string.ps_completed));

                selectorStyle.setTitleBarStyle(blueTitleBarStyle);
                selectorStyle.setBottomBarStyle(numberBlueBottomNavBarStyle);
                selectorStyle.setSelectMainStyle(numberBlueSelectMainStyle);
                break;
            case R.id.rb_we_chat_style:
                // 主体风格
                SelectMainStyle numberSelectMainStyle = new SelectMainStyle();
                numberSelectMainStyle.setSelectNumberStyle(true);
                numberSelectMainStyle.setPreviewSelectNumberStyle(false);
                numberSelectMainStyle.setPreviewDisplaySelectGallery(true);
                numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector);
                numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector);
                numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg);
                numberSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
                numberSelectMainStyle.setSelectNormalText(getString(R.string.ps_send));
                numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg);
                numberSelectMainStyle.setAdapterPreviewGalleryItemSize(DensityUtil.dip2px(getContext(), 52));
                numberSelectMainStyle.setPreviewSelectText(getString(R.string.ps_select));
                numberSelectMainStyle.setPreviewSelectTextSize(14);
                numberSelectMainStyle.setPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(getContext(), 6));
                numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg);
                numberSelectMainStyle.setSelectText(getString(R.string.ps_send_num));
                numberSelectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
                numberSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
                numberSelectMainStyle.setCompleteSelectRelativeTop(true);
                numberSelectMainStyle.setPreviewSelectRelativeBottom(true);
                numberSelectMainStyle.setAdapterItemIncludeEdge(false);

                // 头部TitleBar 风格
                TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
                numberTitleBarStyle.setHideCancelButton(true);
                numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
                if (cb_only_dir.isChecked()) {
                    numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_demo_only_album_bg);
                } else {
                    numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
                }
                numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
                numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

                // 底部NavBar 风格
                BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
                numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_half_grey));
                numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview));
                numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
                numberBottomNavBarStyle.setCompleteCountTips(false);
                numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num));
                numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));


                selectorStyle.setTitleBarStyle(numberTitleBarStyle);
                selectorStyle.setBottomBarStyle(numberBottomNavBarStyle);
                selectorStyle.setSelectMainStyle(numberSelectMainStyle);

                break;
            case R.id.rb_default:
                animationMode = AnimationType.DEFAULT_ANIMATION;
                break;
            case R.id.rb_alpha:
                animationMode = AnimationType.ALPHA_IN_ANIMATION;
                break;
            case R.id.rb_slide_in:
                animationMode = AnimationType.SLIDE_IN_BOTTOM_ANIMATION;
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_crop:
                rgb_crop.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_hide.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_crop_circular.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_styleCrop.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_showCropFrame.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_showCropGrid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_skip_not_gif.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_not_gif.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
            case R.id.cb_custom_sandbox:
                cb_only_dir.setChecked(isChecked);
                break;
            case R.id.cb_only_dir:
                cb_custom_sandbox.setChecked(isChecked);
                break;
            case R.id.cb_skip_not_gif:
                cb_not_gif.setChecked(false);
                cb_skip_not_gif.setChecked(isChecked);
                break;
            case R.id.cb_not_gif:
                cb_skip_not_gif.setChecked(false);
                cb_not_gif.setChecked(isChecked);
                break;
            case R.id.cb_mode:
                cb_attach_camera_mode.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                break;
            case R.id.cb_system_album:
                cb_attach_system_mode.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
            case R.id.cb_custom_camera:
                cb_camera_zoom.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cb_camera_focus.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (isChecked) {
                } else {
                    cb_camera_zoom.setChecked(false);
                    cb_camera_focus.setChecked(false);
                }
                break;
            case R.id.cb_crop_circular:
                if (isChecked) {
                    x = aspect_ratio_x;
                    y = aspect_ratio_y;
                    aspect_ratio_x = 1;
                    aspect_ratio_y = 1;
                } else {
                    aspect_ratio_x = x;
                    aspect_ratio_y = y;
                }
                rgb_crop.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (isChecked) {
                    cb_showCropFrame.setChecked(false);
                    cb_showCropGrid.setChecked(false);
                } else {
                    cb_showCropFrame.setChecked(true);
                    cb_showCropGrid.setChecked(true);
                }
                break;
        }
    }


    @Override
    public void onSelectFinish(@Nullable PictureCommonFragment.SelectorResult result) {
        if (result == null) {
            return;
        }
        if (result.mResultCode == RESULT_OK) {
            ArrayList<LocalMedia> selectorResult = PictureSelector.obtainSelectorList(result.mResultData);
            analyticalSelectResults(selectorResult);
        } else if (result.mResultCode == RESULT_CANCELED) {
            Log.i(TAG, "onSelectFinish PictureSelector Cancel");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
                ArrayList<LocalMedia> result = PictureSelector.obtainSelectorList(data);
                analyticalSelectResults(result);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult PictureSelector Cancel");
        }
    }

    /**
     * 创建一个ActivityResultLauncher
     *
     * @return
     */
    private ActivityResultLauncher<Intent> createActivityResultLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK) {
                            ArrayList<LocalMedia> selectList = PictureSelector.obtainSelectorList(result.getData());
                            analyticalSelectResults(selectList);
                        } else if (resultCode == RESULT_CANCELED) {
                            Log.i(TAG, "onActivityResult PictureSelector Cancel");
                        }
                    }
                });
    }


    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(getContext(), media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(getContext(), media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
            Log.i(TAG, "文件名: " + media.getFileName());
            Log.i(TAG, "是否压缩:" + media.isCompressed());
            Log.i(TAG, "压缩:" + media.getCompressPath());
            Log.i(TAG, "初始路径:" + media.getPath());
            Log.i(TAG, "绝对路径:" + media.getRealPath());
            Log.i(TAG, "是否裁剪:" + media.isCut());
            Log.i(TAG, "裁剪路径:" + media.getCutPath());
            Log.i(TAG, "是否开启原图:" + media.isOriginal());
            Log.i(TAG, "原图路径:" + media.getOriginalPath());
            Log.i(TAG, "沙盒路径:" + media.getSandboxPath());
            Log.i(TAG, "水印路径:" + media.getWatermarkPath());
            Log.i(TAG, "视频缩略图:" + media.getVideoThumbnailPath());
            Log.i(TAG, "原始宽高: " + media.getWidth() + "x" + media.getHeight());
            Log.i(TAG, "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
            Log.i(TAG, "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()));
            Log.i(TAG, "文件时长: " + media.getDuration());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isMaxSize = result.size() == mAdapter.getSelectMax();
                int oldSize = mAdapter.getData().size();
                mAdapter.notifyItemRangeRemoved(0, isMaxSize ? oldSize + 1 : oldSize);
                mAdapter.getData().clear();

                mAdapter.getData().addAll(result);
                mAdapter.notifyItemRangeInserted(0, result.size());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            outState.putParcelableArrayList("selectorList",
                    mAdapter.getData());
        }
    }

    public Context getContext() {
        return this;
    }
}
