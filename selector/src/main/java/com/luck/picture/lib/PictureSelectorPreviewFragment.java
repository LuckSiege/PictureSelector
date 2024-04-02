package com.luck.picture.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.adapter.holder.BasePreviewHolder;
import com.luck.picture.lib.adapter.holder.PreviewGalleryAdapter;
import com.luck.picture.lib.adapter.holder.PreviewVideoHolder;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureMediaScannerConnection;
import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.HorizontalItemDecoration;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.dialog.PictureCommonDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.loader.IBridgeMediaLoader;
import com.luck.picture.lib.loader.LocalMediaLoader;
import com.luck.picture.lib.loader.LocalMediaPageLoader;
import com.luck.picture.lib.magical.BuildRecycleItemViewParams;
import com.luck.picture.lib.magical.MagicalView;
import com.luck.picture.lib.magical.OnMagicalViewCallback;
import com.luck.picture.lib.magical.ViewParams;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DownloadFileUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.luck.picture.lib.utils.ValueOf;
import com.luck.picture.lib.widget.BottomNavBar;
import com.luck.picture.lib.widget.CompleteSelectView;
import com.luck.picture.lib.widget.PreviewBottomNavBar;
import com.luck.picture.lib.widget.PreviewTitleBar;
import com.luck.picture.lib.widget.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/18 10:13 下午
 * @describe：PictureSelectorPreviewFragment
 */
public class PictureSelectorPreviewFragment extends PictureCommonFragment {
    public static final String TAG = PictureSelectorPreviewFragment.class.getSimpleName();

    protected ArrayList<LocalMedia> mData = new ArrayList<>();

    protected MagicalView magicalView;

    protected ViewPager2 viewPager;

    protected PicturePreviewAdapter viewPageAdapter;

    protected PreviewBottomNavBar bottomNarBar;

    protected PreviewTitleBar titleBar;

    /**
     * if there more
     */
    protected boolean isHasMore = true;

    protected int curPosition;

    protected boolean isInternalBottomPreview;

    protected boolean isSaveInstanceState;

    /**
     * 当前相册
     */
    protected String currentAlbum;

    /**
     * 是否显示了拍照入口
     */
    protected boolean isShowCamera;

    /**
     * 是否外部预览进来
     */
    protected boolean isExternalPreview;

    /**
     * 外部预览是否支持删除
     */
    protected boolean isDisplayDelete;

    protected boolean isAnimationStart;

    protected int totalNum;

    protected int screenWidth, screenHeight;

    protected long mBucketId = -1;

    protected TextView tvSelected;

    protected TextView tvSelectedWord;

    protected View selectClickArea;

    protected CompleteSelectView completeSelectView;

    protected boolean needScaleBig = true;

    protected boolean needScaleSmall = false;

    protected RecyclerView mGalleryRecycle;

    protected PreviewGalleryAdapter mGalleryAdapter;

    protected List<View> mAnimViews = new ArrayList<>();

    private boolean isPause = false;
    private boolean isEnterWindowFullScreen = false;

    public static PictureSelectorPreviewFragment newInstance() {
        PictureSelectorPreviewFragment fragment = new PictureSelectorPreviewFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }


    /**
     * 内部预览
     *
     * @param isBottomPreview 是否顶部预览进来的
     * @param currentAlbum    当前预览的目录
     * @param isShowCamera    是否有显示拍照图标
     * @param position        预览下标
     * @param totalNum        当前预览总数
     * @param page            当前页码
     * @param currentBucketId 当前相册目录id
     * @param data            预览数据源
     */
    public void setInternalPreviewData(boolean isBottomPreview, String currentAlbumName, boolean isShowCamera,
                                       int position, int totalNum, int page, long currentBucketId,
                                       ArrayList<LocalMedia> data) {
        this.mPage = page;
        this.mBucketId = currentBucketId;
        this.mData = data;
        this.totalNum = totalNum;
        this.curPosition = position;
        this.currentAlbum = currentAlbumName;
        this.isShowCamera = isShowCamera;
        this.isInternalBottomPreview = isBottomPreview;
    }

    /**
     * 外部预览
     *
     * @param position        预览下标
     * @param totalNum        当前预览总数
     * @param data            预览数据源
     * @param isDisplayDelete 是否显示删除按钮
     */
    public void setExternalPreviewData(int position, int totalNum, ArrayList<LocalMedia> data, boolean isDisplayDelete) {
        this.mData = data;
        this.totalNum = totalNum;
        this.curPosition = position;
        this.isDisplayDelete = isDisplayDelete;
        this.isExternalPreview = true;
    }

    @Override
    public int getResourceId() {
        int layoutResourceId = InjectResourceSource.getLayoutResource(getContext(), InjectResourceSource.PREVIEW_LAYOUT_RESOURCE, selectorConfig);
        if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            return layoutResourceId;
        }
        return R.layout.ps_fragment_preview;
    }

    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {
        // 更新TitleBar和BottomNarBar选择态
        tvSelected.setSelected(selectorConfig.getSelectedResult().contains(currentMedia));
        bottomNarBar.setSelectedChange();
        completeSelectView.setSelectedChange(true);
        notifySelectNumberStyle(currentMedia);
        notifyPreviewGalleryData(isAddRemove, currentMedia);
    }

    @Override
    public void onCheckOriginalChange() {
        bottomNarBar.setOriginalCheck();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reStartSavedInstance(savedInstanceState);
        isSaveInstanceState = savedInstanceState != null;
        screenWidth = DensityUtil.getRealScreenWidth(getContext());
        screenHeight = DensityUtil.getScreenHeight(getContext());
        titleBar = view.findViewById(R.id.title_bar);
        tvSelected = view.findViewById(R.id.ps_tv_selected);
        tvSelectedWord = view.findViewById(R.id.ps_tv_selected_word);
        selectClickArea = view.findViewById(R.id.select_click_area);
        completeSelectView = view.findViewById(R.id.ps_complete_select);
        magicalView = view.findViewById(R.id.magical);
        viewPager = new ViewPager2(getContext());
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar);
        magicalView.setMagicalContent(viewPager);
        setMagicalViewBackgroundColor();
        setMagicalViewAction();
        addAminViews(titleBar, tvSelected, tvSelectedWord, selectClickArea, completeSelectView, bottomNarBar);
        onCreateLoader();
        initTitleBar();
        initViewPagerData(mData);
        if (isExternalPreview) {
            externalPreviewStyle();
        } else {
            initBottomNavBar();
            initPreviewSelectGallery((ViewGroup) view);
            initComplete();
        }
        iniMagicalView();
    }

    /**
     * addAminViews
     *
     * @param views
     */
    public void addAminViews(View... views) {
        Collections.addAll(mAnimViews, views);
    }

    private void setMagicalViewBackgroundColor() {
        SelectMainStyle mainStyle = selectorConfig.selectorStyle.getSelectMainStyle();
        if (StyleUtils.checkStyleValidity(mainStyle.getPreviewBackgroundColor())) {
            magicalView.setBackgroundColor(mainStyle.getPreviewBackgroundColor());
        } else {
            if (selectorConfig.chooseMode == SelectMimeType.ofAudio()
                    || (mData != null && mData.size() > 0
                    && PictureMimeType.isHasAudio(mData.get(0).getMimeType()))) {
                magicalView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
            } else {
                magicalView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
            }
        }
    }

    @Override
    public void reStartSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPage = savedInstanceState.getInt(PictureConfig.EXTRA_CURRENT_PAGE, 1);
            mBucketId = savedInstanceState.getLong(PictureConfig.EXTRA_CURRENT_BUCKET_ID, -1);
            curPosition = savedInstanceState.getInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, curPosition);
            isShowCamera = savedInstanceState.getBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, isShowCamera);
            totalNum = savedInstanceState.getInt(PictureConfig.EXTRA_PREVIEW_CURRENT_ALBUM_TOTAL, totalNum);
            isExternalPreview = savedInstanceState.getBoolean(PictureConfig.EXTRA_EXTERNAL_PREVIEW, isExternalPreview);
            isDisplayDelete = savedInstanceState.getBoolean(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, isDisplayDelete);
            isInternalBottomPreview = savedInstanceState.getBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, isInternalBottomPreview);
            currentAlbum = savedInstanceState.getString(PictureConfig.EXTRA_CURRENT_ALBUM_NAME, "");
            if (mData.size() == 0) {
                mData.addAll(new ArrayList<>(selectorConfig.selectedPreviewResult));
            }
        }
    }

    @Override
    public void onKeyBackFragmentFinish() {
        onKeyDownBackToMin();
    }

    /**
     * 设置MagicalView
     */
    private void iniMagicalView() {
        if (isHasMagicalEffect()) {
            float alpha = isSaveInstanceState ? 1.0F : 0.0F;
            magicalView.setBackgroundAlpha(alpha);
            for (int i = 0; i < mAnimViews.size(); i++) {
                if (mAnimViews.get(i) instanceof TitleBar) {
                    continue;
                }
                mAnimViews.get(i).setAlpha(alpha);
            }
        } else {
            magicalView.setBackgroundAlpha(1.0F);
        }
    }

    private boolean isHasMagicalEffect() {
        return !isInternalBottomPreview && selectorConfig.isPreviewZoomEffect;
    }

    /**
     * 设置MagicalView监听器
     */
    protected void setMagicalViewAction() {
        if (isHasMagicalEffect()) {
            magicalView.setOnMojitoViewCallback(new OnMagicalViewCallback() {

                @Override
                public void onBeginBackMinAnim() {
                    onMojitoBeginBackMinAnim();
                }

                @Override
                public void onBeginMagicalAnimComplete(MagicalView mojitoView, boolean showImmediately) {
                    onMojitoBeginAnimComplete(mojitoView, showImmediately);
                }

                @Override
                public void onBackgroundAlpha(float alpha) {
                    onMojitoBackgroundAlpha(alpha);
                }

                @Override
                public void onMagicalViewFinish() {
                    onMojitoMagicalViewFinish();
                }

                @Override
                public void onBeginBackMinMagicalFinish(boolean isResetSize) {
                    onMojitoBeginBackMinFinish(isResetSize);
                }
            });
        }
    }

    /**
     * 开始准备执行缩放动画
     */
    protected void onMojitoBeginBackMinAnim() {
        BasePreviewHolder currentHolder = viewPageAdapter.getCurrentHolder(viewPager.getCurrentItem());
        if (currentHolder == null) {
            return;
        }
        if (currentHolder.coverImageView.getVisibility() == View.GONE) {
            currentHolder.coverImageView.setVisibility(View.VISIBLE);
        }
        if (currentHolder instanceof PreviewVideoHolder) {
            PreviewVideoHolder videoHolder = (PreviewVideoHolder) currentHolder;
            if (videoHolder.ivPlayButton.getVisibility() == View.VISIBLE) {
                videoHolder.ivPlayButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 关闭缩放动画执行完成后关闭页面
     */
    protected void onMojitoMagicalViewFinish() {
        if (isExternalPreview && isNormalDefaultEnter() && isHasMagicalEffect()) {
            onExitPictureSelector();
        } else {
            onBackCurrentFragment();
        }
    }

    /**
     * 缩放动画执行时透明度跟随变化
     *
     * @param alpha
     */
    protected void onMojitoBackgroundAlpha(float alpha) {
        for (int i = 0; i < mAnimViews.size(); i++) {
            if (mAnimViews.get(i) instanceof TitleBar) {
                continue;
            }
            mAnimViews.get(i).setAlpha(alpha);
        }
    }

    /**
     * 关闭缩放动画执行完成
     *
     * @param isResetSize
     */
    protected void onMojitoBeginBackMinFinish(boolean isResetSize) {
        ViewParams itemViewParams = BuildRecycleItemViewParams.getItemViewParams(isShowCamera ? curPosition + 1 : curPosition);
        if (itemViewParams == null) {
            return;
        }
        BasePreviewHolder currentHolder = viewPageAdapter.getCurrentHolder(viewPager.getCurrentItem());
        if (currentHolder == null) {
            return;
        }
        currentHolder.coverImageView.getLayoutParams().width = itemViewParams.width;
        currentHolder.coverImageView.getLayoutParams().height = itemViewParams.height;
        currentHolder.coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * 缩放动画执行完成
     *
     * @param mojitoView
     * @param showImmediately
     */
    protected void onMojitoBeginAnimComplete(MagicalView mojitoView, boolean showImmediately) {
        BasePreviewHolder currentHolder = viewPageAdapter.getCurrentHolder(viewPager.getCurrentItem());
        if (currentHolder == null) {
            return;
        }
        LocalMedia media = mData.get(viewPager.getCurrentItem());
        int realWidth, realHeight;
        if (media.isCut() && media.getCropImageWidth() > 0 && media.getCropImageHeight() > 0) {
            realWidth = media.getCropImageWidth();
            realHeight = media.getCropImageHeight();
        } else {
            realWidth = media.getWidth();
            realHeight = media.getHeight();
        }
        if (MediaUtils.isLongImage(realWidth, realHeight)) {
            currentHolder.coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            currentHolder.coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (currentHolder instanceof PreviewVideoHolder) {
            PreviewVideoHolder videoHolder = (PreviewVideoHolder) currentHolder;
            if (selectorConfig.isAutoVideoPlay) {
                startAutoVideoPlay(viewPager.getCurrentItem());
            } else {
                if (videoHolder.ivPlayButton.getVisibility() == View.GONE) {
                    if (!isPlaying()) {
                        videoHolder.ivPlayButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
        outState.putLong(PictureConfig.EXTRA_CURRENT_BUCKET_ID, mBucketId);
        outState.putInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, curPosition);
        outState.putInt(PictureConfig.EXTRA_PREVIEW_CURRENT_ALBUM_TOTAL, totalNum);
        outState.putBoolean(PictureConfig.EXTRA_EXTERNAL_PREVIEW, isExternalPreview);
        outState.putBoolean(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, isDisplayDelete);
        outState.putBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, isShowCamera);
        outState.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, isInternalBottomPreview);
        outState.putString(PictureConfig.EXTRA_CURRENT_ALBUM_NAME, currentAlbum);
        selectorConfig.addSelectedPreviewResult(mData);
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (isHasMagicalEffect()) {
            // config.isPreviewZoomEffect模式下使用缩放动画
            return null;
        }
        PictureWindowAnimationStyle windowAnimationStyle = selectorConfig.selectorStyle.getWindowAnimationStyle();
        if (windowAnimationStyle.activityPreviewEnterAnimation != 0 && windowAnimationStyle.activityPreviewExitAnimation != 0) {
            Animation loadAnimation = AnimationUtils.loadAnimation(getActivity(),
                    enter ? windowAnimationStyle.activityPreviewEnterAnimation : windowAnimationStyle.activityPreviewExitAnimation);
            if (enter) {
                onEnterFragment();
            } else {
                onExitFragment();
            }
            return loadAnimation;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @Override
    public void sendChangeSubSelectPositionEvent(boolean adapterChange) {
        if (selectorConfig.selectorStyle.getSelectMainStyle().isPreviewSelectNumberStyle()) {
            if (selectorConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
                for (int index = 0; index < selectorConfig.getSelectCount(); index++) {
                    LocalMedia media = selectorConfig.getSelectedResult().get(index);
                    media.setNum(index + 1);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isHasMagicalEffect() && mData.size() > curPosition) {
            LocalMedia media = mData.get(curPosition);
            if (PictureMimeType.isHasVideo(media.getMimeType())) {
                getVideoRealSizeFromMedia(media, false, new OnCallbackListener<int[]>() {
                    @Override
                    public void onCall(int[] size) {
                        changeViewParams(size);
                    }
                });
            } else {
                getImageRealSizeFromMedia(media, false, new OnCallbackListener<int[]>() {
                    @Override
                    public void onCall(int[] size) {
                        changeViewParams(size);
                    }
                });
            }
        }
    }

    private void changeViewParams(int[] size) {
        ViewParams viewParams = BuildRecycleItemViewParams.getItemViewParams(isShowCamera ? curPosition + 1 : curPosition);
        if (viewParams == null || size[0] == 0 || size[1] == 0) {
            magicalView.setViewParams(0, 0, 0, 0, size[0], size[1]);
            magicalView.resetStartNormal(size[0], size[1], false);
        } else {
            magicalView.setViewParams(viewParams.left, viewParams.top, viewParams.width, viewParams.height, size[0], size[1]);
            magicalView.resetStart();
        }
    }

    @Override
    public void onCreateLoader() {
        if (isExternalPreview) {
            return;
        }
        if (selectorConfig.loaderFactory != null) {
            mLoader = selectorConfig.loaderFactory.onCreateLoader();
            if (mLoader == null) {
                throw new NullPointerException("No available " + IBridgeMediaLoader.class + " loader found");
            }
        } else {
            mLoader = selectorConfig.isPageStrategy
                    ? new LocalMediaPageLoader(getAppContext(), selectorConfig)
                    : new LocalMediaLoader(getAppContext(), selectorConfig);
        }
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        mPage++;
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadMoreMediaData(getContext(), mBucketId, mPage,
                    selectorConfig.pageSize, selectorConfig.pageSize, new OnQueryDataResultListener<LocalMedia>() {
                        @Override
                        public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                            handleMoreData(result, isHasMore);
                        }
                    });
        } else {
            mLoader.loadPageMediaData(mBucketId, mPage, selectorConfig.pageSize, new OnQueryDataResultListener<LocalMedia>() {
                @Override
                public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                    handleMoreData(result, isHasMore);
                }
            });
        }
    }

    private void handleMoreData(List<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        PictureSelectorPreviewFragment.this.isHasMore = isHasMore;
        if (isHasMore) {
            if (result.size() > 0) {
                int oldStartPosition = mData.size();
                mData.addAll(result);
                int itemCount = mData.size();
                viewPageAdapter.notifyItemRangeChanged(oldStartPosition, itemCount);
            } else {
                loadMoreData();
            }
        }
    }


    private void initComplete() {
        SelectMainStyle selectMainStyle = selectorConfig.selectorStyle.getSelectMainStyle();

        if (StyleUtils.checkStyleValidity(selectMainStyle.getPreviewSelectBackground())) {
            tvSelected.setBackgroundResource(selectMainStyle.getPreviewSelectBackground());
        } else if (StyleUtils.checkStyleValidity(selectMainStyle.getSelectBackground())) {
            tvSelected.setBackgroundResource(selectMainStyle.getSelectBackground());
        }
        if (StyleUtils.checkStyleValidity(selectMainStyle.getPreviewSelectTextResId())) {
            tvSelectedWord.setText(getString(selectMainStyle.getPreviewSelectTextResId()));
        } else if (StyleUtils.checkTextValidity(selectMainStyle.getPreviewSelectText())) {
            tvSelectedWord.setText(selectMainStyle.getPreviewSelectText());
        } else {
            tvSelectedWord.setText("");
        }
        if (StyleUtils.checkSizeValidity(selectMainStyle.getPreviewSelectTextSize())) {
            tvSelectedWord.setTextSize(selectMainStyle.getPreviewSelectTextSize());
        }

        if (StyleUtils.checkStyleValidity(selectMainStyle.getPreviewSelectTextColor())) {
            tvSelectedWord.setTextColor(selectMainStyle.getPreviewSelectTextColor());
        }

        if (StyleUtils.checkSizeValidity(selectMainStyle.getPreviewSelectMarginRight())) {
            if (tvSelected.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                if (tvSelected.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tvSelected.getLayoutParams();
                    layoutParams.rightMargin = selectMainStyle.getPreviewSelectMarginRight();
                }
            } else if (tvSelected.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvSelected.getLayoutParams();
                layoutParams.rightMargin = selectMainStyle.getPreviewSelectMarginRight();
            }
        }
        completeSelectView.setCompleteSelectViewStyle();
        completeSelectView.setSelectedChange(true);
        if (selectMainStyle.isCompleteSelectRelativeTop()) {
            if (completeSelectView.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) completeSelectView
                        .getLayoutParams()).topToTop = R.id.title_bar;
                ((ConstraintLayout.LayoutParams) completeSelectView
                        .getLayoutParams()).bottomToBottom = R.id.title_bar;
                if (selectorConfig.isPreviewFullScreenMode) {
                    ((ConstraintLayout.LayoutParams) completeSelectView
                            .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                }
            } else if (completeSelectView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                if (selectorConfig.isPreviewFullScreenMode) {
                    ((RelativeLayout.LayoutParams) completeSelectView
                            .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                }
            }
        }

        if (selectMainStyle.isPreviewSelectRelativeBottom()) {
            if (tvSelected.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) tvSelected
                        .getLayoutParams()).topToTop = R.id.bottom_nar_bar;
                ((ConstraintLayout.LayoutParams) tvSelected
                        .getLayoutParams()).bottomToBottom = R.id.bottom_nar_bar;

                ((ConstraintLayout.LayoutParams) tvSelectedWord
                        .getLayoutParams()).topToTop = R.id.bottom_nar_bar;
                ((ConstraintLayout.LayoutParams) tvSelectedWord
                        .getLayoutParams()).bottomToBottom = R.id.bottom_nar_bar;

                ((ConstraintLayout.LayoutParams) selectClickArea
                        .getLayoutParams()).topToTop = R.id.bottom_nar_bar;
                ((ConstraintLayout.LayoutParams) selectClickArea
                        .getLayoutParams()).bottomToBottom = R.id.bottom_nar_bar;
            }
        } else {
            if (selectorConfig.isPreviewFullScreenMode) {
                if (tvSelectedWord.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                    ((ConstraintLayout.LayoutParams) tvSelectedWord
                            .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                } else if (tvSelectedWord.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) tvSelectedWord
                            .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                }
            }
        }
        completeSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isComplete;
                if (selectMainStyle.isCompleteSelectRelativeTop() && selectorConfig.getSelectCount() == 0) {
                    isComplete = confirmSelect(mData.get(viewPager.getCurrentItem()), false)
                            == SelectedManager.ADD_SUCCESS;
                } else {
                    isComplete = selectorConfig.getSelectCount() > 0;
                }
                if (selectorConfig.isEmptyResultReturn && selectorConfig.getSelectCount() == 0) {
                    onExitPictureSelector();
                } else {
                    if (isComplete) {
                        dispatchTransformResult();
                    }
                }
            }
        });
    }


    private void initTitleBar() {
        if (selectorConfig.selectorStyle.getTitleBarStyle().isHideTitleBar()) {
            titleBar.setVisibility(View.GONE);
        }
        titleBar.setTitleBarStyle();
        titleBar.setOnTitleBarListener(new TitleBar.OnTitleBarListener() {
            @Override
            public void onBackPressed() {
                if (isExternalPreview) {
                    if (selectorConfig.isPreviewZoomEffect) {
                        magicalView.backToMin();
                    } else {
                        handleExternalPreviewBack();
                    }
                } else {
                    if (!isInternalBottomPreview && selectorConfig.isPreviewZoomEffect) {
                        magicalView.backToMin();
                    } else {
                        onBackCurrentFragment();
                    }
                }
                exitWindowFullScreen();
            }
        });
        titleBar.setTitle((curPosition + 1) + "/" + totalNum);
        titleBar.getImageDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePreview();
            }
        });

        selectClickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExternalPreview) {
                    deletePreview();
                } else {
                    LocalMedia currentMedia = mData.get(viewPager.getCurrentItem());
                    int selectResultCode = confirmSelect(currentMedia, tvSelected.isSelected());
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        if (selectorConfig.onSelectAnimListener != null) {
                            selectorConfig.onSelectAnimListener.onSelectAnim(tvSelected);
                        } else {
                            tvSelected.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in));
                        }
                    }
                }
            }
        });
        tvSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectClickArea.performClick();
            }
        });
    }

    protected void initPreviewSelectGallery(ViewGroup group) {
        SelectMainStyle selectMainStyle = selectorConfig.selectorStyle.getSelectMainStyle();
        if (selectMainStyle.isPreviewDisplaySelectGallery()) {
            mGalleryRecycle = new RecyclerView(getContext());
            if (StyleUtils.checkStyleValidity(selectMainStyle.getAdapterPreviewGalleryBackgroundResource())) {
                mGalleryRecycle.setBackgroundResource(selectMainStyle.getAdapterPreviewGalleryBackgroundResource());
            } else {
                mGalleryRecycle.setBackgroundResource(R.drawable.ps_preview_gallery_bg);
            }
            group.addView(mGalleryRecycle);

            ViewGroup.LayoutParams layoutParams = mGalleryRecycle.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layoutParams;
                params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                params.bottomToTop = R.id.bottom_nar_bar;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            }
            WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext()) {
                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    super.smoothScrollToPosition(recyclerView, state, position);
                    LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                            return 300F / displayMetrics.densityDpi;
                        }
                    };
                    smoothScroller.setTargetPosition(position);
                    startSmoothScroll(smoothScroller);
                }
            };
            RecyclerView.ItemAnimator itemAnimator = mGalleryRecycle.getItemAnimator();
            if (itemAnimator != null) {
                ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            }
            if (mGalleryRecycle.getItemDecorationCount() == 0) {
                mGalleryRecycle.addItemDecoration(new HorizontalItemDecoration(Integer.MAX_VALUE,
                        DensityUtil.dip2px(getContext(), 6)));
            }
            layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
            mGalleryRecycle.setLayoutManager(layoutManager);
            if (selectorConfig.getSelectCount() > 0) {
                mGalleryRecycle.setLayoutAnimation(AnimationUtils
                        .loadLayoutAnimation(getContext(), R.anim.ps_anim_layout_fall_enter));
            }
            mGalleryAdapter = new PreviewGalleryAdapter(selectorConfig, isInternalBottomPreview);
            notifyGallerySelectMedia(mData.get(curPosition));
            mGalleryRecycle.setAdapter(mGalleryAdapter);
            mGalleryAdapter.setItemClickListener(new PreviewGalleryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, LocalMedia media, View v) {
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    String albumName = TextUtils.isEmpty(selectorConfig.defaultAlbumName) ? getString(R.string.ps_camera_roll) : selectorConfig.defaultAlbumName;
                    if (isInternalBottomPreview || TextUtils.equals(currentAlbum, albumName)
                            || TextUtils.equals(media.getParentFolderName(), currentAlbum)) {
                        int newPosition = isInternalBottomPreview ? position : isShowCamera ? media.position - 1 : media.position;
                        if (newPosition == viewPager.getCurrentItem() && media.isChecked()) {
                            return;
                        }
                        LocalMedia item = viewPageAdapter.getItem(newPosition);
                        if (item != null && (!TextUtils.equals(media.getPath(), item.getPath()) || media.getId() != item.getId())) {
                            return;
                        }
                        if (viewPager.getAdapter() != null) {
                            // 这里清空一下重新设置，发现频繁调用setCurrentItem会出现页面闪现之前图片
                            viewPager.setAdapter(null);
                            viewPager.setAdapter(viewPageAdapter);
                        }
                        viewPager.setCurrentItem(newPosition, false);
                        notifyGallerySelectMedia(media);
                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                if (selectorConfig.isPreviewZoomEffect) {
                                    viewPageAdapter.setVideoPlayButtonUI(newPosition);
                                }
                            }
                        });
                    }
                }
            });
            if (selectorConfig.getSelectCount() > 0) {
                mGalleryRecycle.setVisibility(View.VISIBLE);
            } else {
                mGalleryRecycle.setVisibility(View.INVISIBLE);
            }
            addAminViews(mGalleryRecycle);
            ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                @Override
                public boolean isLongPressDragEnabled() {
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                }

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    viewHolder.itemView.setAlpha(0.7F);
                    return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    try {
                        //得到item原来的position
                        int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                        //得到目标position
                        int toPosition = target.getAbsoluteAdapterPosition();
                        if (fromPosition < toPosition) {
                            for (int i = fromPosition; i < toPosition; i++) {
                                Collections.swap(mGalleryAdapter.getData(), i, i + 1);
                                Collections.swap(selectorConfig.getSelectedResult(), i, i + 1);
                                if (isInternalBottomPreview) {
                                    Collections.swap(mData, i, i + 1);
                                }
                            }
                        } else {
                            for (int i = fromPosition; i > toPosition; i--) {
                                Collections.swap(mGalleryAdapter.getData(), i, i - 1);
                                Collections.swap(selectorConfig.getSelectedResult(), i, i - 1);
                                if (isInternalBottomPreview) {
                                    Collections.swap(mData, i, i - 1);
                                }
                            }
                        }
                        mGalleryAdapter.notifyItemMoved(fromPosition, toPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
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
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                    super.onSelectedChanged(viewHolder, actionState);
                }

                @Override
                public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                    return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
                }

                @Override
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
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
                    mGalleryAdapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                    if (isInternalBottomPreview) {
                        int position = mGalleryAdapter.getLastCheckPosition();
                        if (viewPager.getCurrentItem() != position && position != RecyclerView.NO_POSITION) {
                            if (viewPager.getAdapter() != null) {
                                viewPager.setAdapter(null);
                                viewPager.setAdapter(viewPageAdapter);
                            }
                            viewPager.setCurrentItem(position, false);
                        }
                    }
                    if (selectorConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
                        if (!ActivityCompatHelper.isDestroy(getActivity())) {
                            List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                            for (int i = 0; i < fragments.size(); i++) {
                                Fragment fragment = fragments.get(i);
                                if (fragment instanceof PictureCommonFragment) {
                                    ((PictureCommonFragment) fragment).sendChangeSubSelectPositionEvent(true);
                                }
                            }
                        }
                    }
                }
            });
            mItemTouchHelper.attachToRecyclerView(mGalleryRecycle);
            mGalleryAdapter.setItemLongClickListener(new PreviewGalleryAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                    if (mGalleryAdapter.getItemCount() != selectorConfig.maxSelectNum) {
                        mItemTouchHelper.startDrag(holder);
                        return;
                    }
                    if (holder.getLayoutPosition() != mGalleryAdapter.getItemCount() - 1) {
                        mItemTouchHelper.startDrag(holder);
                    }
                }
            });
        }
    }

    /**
     * 刷新画廊数据选中状态
     *
     * @param currentMedia
     */
    private void notifyGallerySelectMedia(LocalMedia currentMedia) {
        if (mGalleryAdapter != null && selectorConfig.selectorStyle
                .getSelectMainStyle().isPreviewDisplaySelectGallery()) {
            mGalleryAdapter.isSelectMedia(currentMedia);
        }
    }

    /**
     * 刷新画廊数据
     */
    private void notifyPreviewGalleryData(boolean isAddRemove, LocalMedia currentMedia) {
        if (mGalleryAdapter != null && selectorConfig.selectorStyle
                .getSelectMainStyle().isPreviewDisplaySelectGallery()) {
            if (mGalleryRecycle.getVisibility() == View.INVISIBLE) {
                mGalleryRecycle.setVisibility(View.VISIBLE);
            }
            if (isAddRemove) {
                if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
                    mGalleryAdapter.clear();
                }
                mGalleryAdapter.addGalleryData(currentMedia);
                mGalleryRecycle.smoothScrollToPosition(mGalleryAdapter.getItemCount() - 1);
            } else {
                mGalleryAdapter.removeGalleryData(currentMedia);
                if (selectorConfig.getSelectCount() == 0) {
                    mGalleryRecycle.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 调用了startPreview预览逻辑
     */
    @SuppressLint("NotifyDataSetChanged")
    private void deletePreview() {
        if (isDisplayDelete) {
            if (selectorConfig.onExternalPreviewEventListener != null) {
                selectorConfig.onExternalPreviewEventListener.onPreviewDelete(viewPager.getCurrentItem());
                int currentItem = viewPager.getCurrentItem();
                mData.remove(currentItem);
                if (mData.size() == 0) {
                    handleExternalPreviewBack();
                    return;
                }
                titleBar.setTitle(getString(R.string.ps_preview_image_num,
                        curPosition + 1, mData.size()));
                totalNum = mData.size();
                curPosition = currentItem;
                if (viewPager.getAdapter() != null) {
                    viewPager.setAdapter(null);
                    viewPager.setAdapter(viewPageAdapter);
                }
                viewPager.setCurrentItem(curPosition, false);
            }
        }
    }

    /**
     * 处理外部预览返回处理
     */
    private void handleExternalPreviewBack() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (selectorConfig.isPreviewFullScreenMode) {
                hideFullScreenStatusBar();
            }
            onExitPictureSelector();
        }
    }

    @Override
    public void onExitFragment() {
        if (selectorConfig.isPreviewFullScreenMode) {
            hideFullScreenStatusBar();
        }
    }

    private void initBottomNavBar() {
        bottomNarBar.setBottomNavBarStyle();
        bottomNarBar.setSelectedChange();
        bottomNarBar.setOnBottomNavBarListener(new BottomNavBar.OnBottomNavBarListener() {

            @Override
            public void onEditImage() {
                if (selectorConfig.onEditMediaEventListener != null) {
                    LocalMedia media = mData.get(viewPager.getCurrentItem());
                    selectorConfig.onEditMediaEventListener
                            .onStartMediaEdit(PictureSelectorPreviewFragment.this, media,
                                    Crop.REQUEST_EDIT_CROP);
                }
            }

            @Override
            public void onCheckOriginalChange() {
                sendSelectedOriginalChangeEvent();
            }

            @Override
            public void onFirstCheckOriginalSelectedChange() {
                int currentItem = viewPager.getCurrentItem();
                if (mData.size() > currentItem) {
                    LocalMedia media = mData.get(currentItem);
                    confirmSelect(media, false);
                }
            }
        });
    }

    /**
     * 外部预览的样式
     */
    private void externalPreviewStyle() {
        titleBar.getImageDelete().setVisibility(isDisplayDelete ? View.VISIBLE : View.GONE);
        tvSelected.setVisibility(View.GONE);
        bottomNarBar.setVisibility(View.GONE);
        completeSelectView.setVisibility(View.GONE);
    }

    protected PicturePreviewAdapter createAdapter() {
        return new PicturePreviewAdapter(selectorConfig);
    }

    public PicturePreviewAdapter getAdapter() {
        return viewPageAdapter;
    }

    public ViewPager2 getViewPager2() {
        return viewPager;
    }

    private void initViewPagerData(ArrayList<LocalMedia> data) {
        viewPageAdapter = createAdapter();
        viewPageAdapter.setData(data);
        viewPageAdapter.setOnPreviewEventListener(new MyOnPreviewEventListener());
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(viewPageAdapter);
        selectorConfig.selectedPreviewResult.clear();
        if (data.size() == 0 || curPosition >= data.size() || curPosition < 0) {
            onKeyBackFragmentFinish();
            return;
        }
        LocalMedia media = data.get(curPosition);
        bottomNarBar.isDisplayEditor(PictureMimeType.isHasVideo(media.getMimeType())
                || PictureMimeType.isHasAudio(media.getMimeType()));
        tvSelected.setSelected(selectorConfig.getSelectedResult().contains(data.get(viewPager.getCurrentItem())));
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
        viewPager.setPageTransformer(new MarginPageTransformer(DensityUtil.dip2px(getAppContext(), 3)));
        viewPager.setCurrentItem(curPosition, false);
        sendChangeSubSelectPositionEvent(false);
        notifySelectNumberStyle(data.get(curPosition));
        startZoomEffect(media);
    }

    /**
     * 启动预览缩放特效
     */
    protected void startZoomEffect(LocalMedia media) {
        if (isSaveInstanceState || isInternalBottomPreview) {
            return;
        }
        if (selectorConfig.isPreviewZoomEffect) {
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    viewPageAdapter.setCoverScaleType(curPosition);
                }
            });
            if (PictureMimeType.isHasVideo(media.getMimeType())) {
                getVideoRealSizeFromMedia(media, !PictureMimeType.isHasHttp(media.getAvailablePath()), new OnCallbackListener<int[]>() {
                    @Override
                    public void onCall(int[] size) {
                        start(size);
                    }
                });
            } else {
                getImageRealSizeFromMedia(media, !PictureMimeType.isHasHttp(media.getAvailablePath()), new OnCallbackListener<int[]>() {
                    @Override
                    public void onCall(int[] size) {
                        start(size);
                    }
                });
            }
        }
    }

    /**
     * start magical
     *
     * @param size
     */
    private void start(int[] size) {
        magicalView.changeRealScreenHeight(size[0], size[1], false);
        ViewParams viewParams = BuildRecycleItemViewParams.getItemViewParams(isShowCamera ? curPosition + 1 : curPosition);
        if (viewParams == null || (size[0] == 0 && size[1] == 0)) {
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    magicalView.startNormal(size[0], size[1], false);
                }
            });
            magicalView.setBackgroundAlpha(1.0F);
            for (int i = 0; i < mAnimViews.size(); i++) {
                mAnimViews.get(i).setAlpha(1.0F);
            }
        } else {
            magicalView.setViewParams(viewParams.left, viewParams.top, viewParams.width, viewParams.height, size[0], size[1]);
            magicalView.start(false);
        }
        ObjectAnimator.ofFloat(viewPager, "alpha", 0.0F, 1.0F).setDuration(50).start();
    }

    /**
     * ViewPageAdapter回调事件处理
     */
    private class MyOnPreviewEventListener implements BasePreviewHolder.OnPreviewEventListener {

        @Override
        public void onBackPressed() {
            if (selectorConfig.isPreviewFullScreenMode) {
                previewFullScreenMode();
            } else {
                if (isExternalPreview) {
                    if (selectorConfig.isPreviewZoomEffect) {
                        magicalView.backToMin();
                    } else {
                        handleExternalPreviewBack();
                    }
                } else {
                    if (!isInternalBottomPreview && selectorConfig.isPreviewZoomEffect) {
                        magicalView.backToMin();
                    } else {
                        onBackCurrentFragment();
                    }
                }
            }
        }

        @Override
        public void onPreviewVideoTitle(String videoName) {
            if (TextUtils.isEmpty(videoName)) {
                titleBar.setTitle((curPosition + 1) + "/" + totalNum);
            } else {
                titleBar.setTitle(videoName);
            }
        }

        @Override
        public void onLongPressDownload(LocalMedia media) {
            if (selectorConfig.isHidePreviewDownload) {
                return;
            }
            if (isExternalPreview) {
                onExternalLongPressDownload(media);
            }
        }
    }

    /**
     * 回到初始位置
     */
    private void onKeyDownBackToMin() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (isExternalPreview) {
                if (selectorConfig.isPreviewZoomEffect) {
                    magicalView.backToMin();
                } else {
                    onExitPictureSelector();
                }
            } else if (isInternalBottomPreview) {
                onBackCurrentFragment();
            } else if (selectorConfig.isPreviewZoomEffect) {
                magicalView.backToMin();
            } else {
                onBackCurrentFragment();
            }
            exitWindowFullScreen();
        }
    }

    /**
     * 预览全屏模式
     */
    private void previewFullScreenMode() {
        if (isAnimationStart) {
            return;
        }
        boolean isAnimInit = titleBar.getTranslationY() == 0.0F;
        AnimatorSet set = new AnimatorSet();
        float titleBarForm = isAnimInit ? 0 : -titleBar.getHeight();
        float titleBarTo = isAnimInit ? -titleBar.getHeight() : 0;
        float alphaForm = isAnimInit ? 1.0F : 0.0F;
        float alphaTo = isAnimInit ? 0.0F : 1.0F;
        for (int i = 0; i < mAnimViews.size(); i++) {
            View view = mAnimViews.get(i);
            set.playTogether(ObjectAnimator.ofFloat(view, "alpha", alphaForm, alphaTo));
            if (view instanceof TitleBar) {
                set.playTogether(ObjectAnimator.ofFloat(view, "translationY", titleBarForm, titleBarTo));
            }
        }
        set.setDuration(350);
        set.start();
        isAnimationStart = true;
        set.addListener(new AnimatorListenerAdapter() {
            @SuppressLint("WrongConstant")
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationStart = false;
                if (isAnimInit) {
                    enterWindowFullScreen();
                } else {
                    exitWindowFullScreen();
                }
            }
        });

        if (isAnimInit) {
            showFullScreenStatusBar();
        } else {
            hideFullScreenStatusBar();
        }
    }

    private void enterWindowFullScreen() {
        if (SdkVersionUtils.isP() && isAdded() && !isEnterWindowFullScreen) {
            isEnterWindowFullScreen = true;
            Window window = requireActivity().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void exitWindowFullScreen() {
        if (SdkVersionUtils.isP() && isAdded() && isEnterWindowFullScreen) {
            isEnterWindowFullScreen = false;
            Window window = requireActivity().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 全屏模式
     */
    private void showFullScreenStatusBar() {
        for (int i = 0; i < mAnimViews.size(); i++) {
            mAnimViews.get(i).setEnabled(false);
        }
        bottomNarBar.getEditor().setEnabled(false);
    }

    /**
     * 隐藏全屏模式
     */
    private void hideFullScreenStatusBar() {
        for (int i = 0; i < mAnimViews.size(); i++) {
            mAnimViews.get(i).setEnabled(true);
        }
        bottomNarBar.getEditor().setEnabled(true);
    }

    /**
     * 外部预览长按下载
     *
     * @param media
     */
    private void onExternalLongPressDownload(LocalMedia media) {
        if (selectorConfig.onExternalPreviewEventListener != null) {
            if (!selectorConfig.onExternalPreviewEventListener.onLongPressDownload(getContext(), media)) {
                String content;
                if (PictureMimeType.isHasAudio(media.getMimeType())
                        || PictureMimeType.isUrlHasAudio(media.getAvailablePath())) {
                    content = getString(R.string.ps_prompt_audio_content);
                } else if (PictureMimeType.isHasVideo(media.getMimeType())
                        || PictureMimeType.isUrlHasVideo(media.getAvailablePath())) {
                    content = getString(R.string.ps_prompt_video_content);
                } else {
                    content = getString(R.string.ps_prompt_image_content);
                }
                PictureCommonDialog dialog = PictureCommonDialog.showDialog(getContext(), getString(R.string.ps_prompt), content);
                dialog.setOnDialogEventListener(new PictureCommonDialog.OnDialogEventListener() {
                    @Override
                    public void onConfirm() {
                        String path = media.getAvailablePath();
                        if (PictureMimeType.isHasHttp(path)) {
                            showLoading();
                        }
                        DownloadFileUtils.saveLocalFile(getContext(), path, media.getMimeType(), new OnCallbackListener<String>() {
                            @Override
                            public void onCall(String realPath) {
                                dismissLoading();
                                if (TextUtils.isEmpty(realPath)) {
                                    String errorMsg;
                                    if (PictureMimeType.isHasAudio(media.getMimeType())) {
                                        errorMsg = getString(R.string.ps_save_audio_error);
                                    } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                        errorMsg = getString(R.string.ps_save_video_error);
                                    } else {
                                        errorMsg = getString(R.string.ps_save_image_error);
                                    }
                                    ToastUtils.showToast(getContext(), errorMsg);
                                } else {
                                    new PictureMediaScannerConnection(getActivity(), realPath);
                                    ToastUtils.showToast(getContext(), getString(R.string.ps_save_success) + "\n" + realPath);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mData.size() > position) {
                LocalMedia currentMedia = positionOffsetPixels < screenWidth / 2 ? mData.get(position) : mData.get(position + 1);
                tvSelected.setSelected(isSelected(currentMedia));
                notifyGallerySelectMedia(currentMedia);
                notifySelectNumberStyle(currentMedia);
            }
        }

        @Override
        public void onPageSelected(int position) {
            curPosition = position;
            titleBar.setTitle((curPosition + 1) + "/" + totalNum);
            if (mData.size() > position) {
                LocalMedia currentMedia = mData.get(position);
                notifySelectNumberStyle(currentMedia);
                if (isHasMagicalEffect()) {
                    changeMagicalViewParams(position);
                }
                if (selectorConfig.isPreviewZoomEffect) {
                    if (isInternalBottomPreview && selectorConfig.isAutoVideoPlay) {
                        startAutoVideoPlay(position);
                    } else {
                        viewPageAdapter.setVideoPlayButtonUI(position);
                    }
                } else {
                    if (selectorConfig.isAutoVideoPlay) {
                        startAutoVideoPlay(position);
                    }
                }
                notifyGallerySelectMedia(currentMedia);
                bottomNarBar.isDisplayEditor(PictureMimeType.isHasVideo(currentMedia.getMimeType())
                        || PictureMimeType.isHasAudio(currentMedia.getMimeType()));
                if (!isExternalPreview && !isInternalBottomPreview && !selectorConfig.isOnlySandboxDir) {
                    if (selectorConfig.isPageStrategy) {
                        if (isHasMore) {
                            if (position == (viewPageAdapter.getItemCount() - 1) - PictureConfig.MIN_PAGE_SIZE
                                    || position == viewPageAdapter.getItemCount() - 1) {
                                loadMoreData();
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 自动播放视频
     *
     * @param position
     */
    private void startAutoVideoPlay(int position) {
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.startAutoVideoPlay(position);
            }
        });
    }

    /**
     * 更新MagicalView ViewParams 参数
     *
     * @param position
     */
    private void changeMagicalViewParams(int position) {
        LocalMedia media = mData.get(position);
        if (PictureMimeType.isHasVideo(media.getMimeType())) {
            getVideoRealSizeFromMedia(media, false, new OnCallbackListener<int[]>() {
                @Override
                public void onCall(int[] size) {
                    setMagicalViewParams(size[0], size[1], position);
                }
            });
        } else {
            getImageRealSizeFromMedia(media, false, new OnCallbackListener<int[]>() {
                @Override
                public void onCall(int[] size) {
                    setMagicalViewParams(size[0], size[1], position);
                }
            });
        }
    }

    /**
     * setMagicalViewParams
     *
     * @param imageWidth
     * @param imageHeight
     * @param position
     */
    private void setMagicalViewParams(int imageWidth, int imageHeight, int position) {
        magicalView.changeRealScreenHeight(imageWidth, imageHeight, true);
        ViewParams viewParams = BuildRecycleItemViewParams.getItemViewParams(isShowCamera ? position + 1 : position);
        if (viewParams == null || imageWidth == 0 || imageHeight == 0) {
            magicalView.setViewParams(0, 0, 0, 0, imageWidth, imageHeight);
        } else {
            magicalView.setViewParams(viewParams.left, viewParams.top, viewParams.width, viewParams.height, imageWidth, imageHeight);
        }
    }

    /**
     * 获取图片Media的真实大小
     *
     * @param media
     * @param resize
     */
    private void getImageRealSizeFromMedia(LocalMedia media, boolean resize, OnCallbackListener<int[]> call) {
        int realWidth;
        int realHeight;
        boolean isReturnNow = true;
        if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
            realWidth = screenWidth;
            realHeight = screenHeight;
        } else {
            realWidth = media.getWidth();
            realHeight = media.getHeight();
            if (resize) {
                if ((realWidth <= 0 || realHeight <= 0) || (realWidth > realHeight)) {
                    if (selectorConfig.isSyncWidthAndHeight) {
                        isReturnNow = false;
                        // 先不展现内容，异步获取可能耗时会导致界面先出现图片而后在放大出现
                        viewPager.setAlpha(0F);
                        MediaUtils.getImageSize(getContext(), media.getAvailablePath(), new OnCallbackListener<MediaExtraInfo>() {
                            @Override
                            public void onCall(MediaExtraInfo extraInfo) {
                                if (extraInfo.getWidth() > 0) {
                                    media.setWidth(extraInfo.getWidth());
                                }
                                if (extraInfo.getHeight() > 0) {
                                    media.setHeight(extraInfo.getHeight());
                                }
                                if (call != null) {
                                    call.onCall(new int[]{media.getWidth(), media.getHeight()});
                                }
                            }
                        });
                    }
                }
            }
        }
        if (media.isCut() && media.getCropImageWidth() > 0 && media.getCropImageHeight() > 0) {
            realWidth = media.getCropImageWidth();
            realHeight = media.getCropImageHeight();
        }
        if (isReturnNow) {
            call.onCall(new int[]{realWidth, realHeight});
        }
    }


    /**
     * 获取视频Media的真实大小
     *
     * @param media
     * @param resize
     */
    private void getVideoRealSizeFromMedia(LocalMedia media, boolean resize, OnCallbackListener<int[]> call) {
        boolean isReturnNow = true;
        if (resize) {
            if ((media.getWidth() <= 0 || media.getHeight() <= 0) || (media.getWidth() > media.getHeight())) {
                if (selectorConfig.isSyncWidthAndHeight) {
                    isReturnNow = false;
                    // 先不展现内容，异步获取可能耗时会导致界面先出现图片而后在放大出现
                    viewPager.setAlpha(0F);
                    MediaUtils.getVideoSize(getContext(), media.getAvailablePath(), new OnCallbackListener<MediaExtraInfo>() {
                        @Override
                        public void onCall(MediaExtraInfo extraInfo) {
                            if (extraInfo.getWidth() > 0) {
                                media.setWidth(extraInfo.getWidth());
                            }
                            if (extraInfo.getHeight() > 0) {
                                media.setHeight(extraInfo.getHeight());
                            }
                            if (call != null) {
                                call.onCall(new int[]{media.getWidth(), media.getHeight()});
                            }
                        }
                    });
                }
            }
        }
        if (isReturnNow) {
            call.onCall(new int[]{media.getWidth(), media.getHeight()});
        }
    }

    /**
     * 对选择数量进行编号排序
     */
    public void notifySelectNumberStyle(LocalMedia currentMedia) {
        if (selectorConfig.selectorStyle.getSelectMainStyle().isPreviewSelectNumberStyle()) {
            if (selectorConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
                tvSelected.setText("");
                for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                    LocalMedia media = selectorConfig.getSelectedResult().get(i);
                    if (TextUtils.equals(media.getPath(), currentMedia.getPath())
                            || media.getId() == currentMedia.getId()) {
                        currentMedia.setNum(media.getNum());
                        media.setPosition(currentMedia.getPosition());
                        tvSelected.setText(ValueOf.toString(currentMedia.getNum()));
                    }
                }
            }
        }
    }

    /**
     * 当前图片是否选中
     *
     * @param media
     * @return
     */
    protected boolean isSelected(LocalMedia media) {
        return selectorConfig.getSelectedResult().contains(media);
    }

    @Override
    public void onEditMedia(Intent data) {
        if (mData.size() > viewPager.getCurrentItem()) {
            LocalMedia currentMedia = mData.get(viewPager.getCurrentItem());
            Uri output = Crop.getOutput(data);
            currentMedia.setCutPath(output != null ? output.getPath() : "");
            currentMedia.setCropImageWidth(Crop.getOutputImageWidth(data));
            currentMedia.setCropImageHeight(Crop.getOutputImageHeight(data));
            currentMedia.setCropOffsetX(Crop.getOutputImageOffsetX(data));
            currentMedia.setCropOffsetY(Crop.getOutputImageOffsetY(data));
            currentMedia.setCropResultAspectRatio(Crop.getOutputCropAspectRatio(data));
            currentMedia.setCut(!TextUtils.isEmpty(currentMedia.getCutPath()));
            currentMedia.setCustomData(Crop.getOutputCustomExtraData(data));
            currentMedia.setEditorImage(currentMedia.isCut());
            currentMedia.setSandboxPath(currentMedia.getCutPath());
            if (selectorConfig.getSelectedResult().contains(currentMedia)) {
                LocalMedia exitsMedia = currentMedia.getCompareLocalMedia();
                if (exitsMedia != null) {
                    exitsMedia.setCutPath(currentMedia.getCutPath());
                    exitsMedia.setCut(currentMedia.isCut());
                    exitsMedia.setEditorImage(currentMedia.isEditorImage());
                    exitsMedia.setCustomData(currentMedia.getCustomData());
                    exitsMedia.setSandboxPath(currentMedia.getCutPath());
                    exitsMedia.setCropImageWidth(Crop.getOutputImageWidth(data));
                    exitsMedia.setCropImageHeight(Crop.getOutputImageHeight(data));
                    exitsMedia.setCropOffsetX(Crop.getOutputImageOffsetX(data));
                    exitsMedia.setCropOffsetY(Crop.getOutputImageOffsetY(data));
                    exitsMedia.setCropResultAspectRatio(Crop.getOutputCropAspectRatio(data));
                }
                sendFixedSelectedChangeEvent(currentMedia);
            } else {
                confirmSelect(currentMedia, false);
            }
            viewPageAdapter.notifyItemChanged(viewPager.getCurrentItem());
            notifyGallerySelectMedia(currentMedia);
        }
    }

    @Override
    protected void onExitPictureSelector() {
        if (viewPageAdapter != null) {
            viewPageAdapter.destroy();
        }
        super.onExitPictureSelector();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            resumePausePlay();
            isPause = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isPlaying()) {
            resumePausePlay();
            isPause = true;
        }
    }

    private void resumePausePlay() {
        if (viewPageAdapter != null) {
            BasePreviewHolder holder = viewPageAdapter.getCurrentHolder(viewPager.getCurrentItem());
            if (holder != null) {
                holder.resumePausePlay();
            }
        }
    }

    private boolean isPlaying() {
        return viewPageAdapter != null && viewPageAdapter.isPlaying(viewPager.getCurrentItem());
    }

    @Override
    public void onDestroy() {
        if (viewPageAdapter != null) {
            viewPageAdapter.destroy();
        }
        if (viewPager != null) {
            viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        super.onDestroy();
    }


}
