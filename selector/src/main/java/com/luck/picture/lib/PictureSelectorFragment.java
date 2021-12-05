package com.luck.picture.lib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.animators.AlphaInAnimationAdapter;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.animators.SlideInBottomAnimationAdapter;
import com.luck.picture.lib.basic.IPictureSelectorEvent;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.AlbumListPopWindow;
import com.luck.picture.lib.dialog.AudioPlayDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener;
import com.luck.picture.lib.loader.LocalMediaLoader;
import com.luck.picture.lib.loader.LocalMediaPageLoader;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.AnimUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.widget.BottomNavBar;
import com.luck.picture.lib.widget.CompleteSelectView;
import com.luck.picture.lib.widget.RecyclerPreloadView;
import com.luck.picture.lib.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelectorFragment
 */
public class PictureSelectorFragment extends PictureCommonFragment
        implements OnRecyclerViewPreloadMoreListener, IPictureSelectorEvent {
    public static final String TAG = PictureSelectorFragment.class.getSimpleName();

    private RecyclerPreloadView mRecycler;

    private TextView tvDataEmpty;

    private TitleBar titleBar;

    private BottomNavBar bottomNarBar;

    private CompleteSelectView completeSelectView;

    /**
     * open camera number
     */
    private int openCameraNumber;

    private int allFolderSize;

    private int currentPosition = -1;

    private boolean isDisplayCamera;

    private PictureImageGridAdapter mAdapter;

    private AlbumListPopWindow albumListPopWindow;

    public static PictureSelectorFragment newInstance() {
        PictureSelectorFragment fragment = new PictureSelectorFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_fragment_selector;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {
        bottomNarBar.setSelectedChange();
        completeSelectView.setSelectedChange(false);
        // 刷新列表数据
        if (checkNotifyStrategy(isAddRemove)) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.notifyItemChanged(currentMedia.position);
        }
        if (!isAddRemove) {
            subSelectPosition(true);
        }
    }

    @Override
    public void onLastSingleSelectedChange(LocalMedia oldLocalMedia) {
        mAdapter.notifyItemChanged(oldLocalMedia.position);
    }

    @Override
    public void subSelectPosition(boolean isRefreshAdapter) {
        if (PictureSelectionConfig.selectorStyle.getSelectMainStyle()
                .isSelectNumberStyle()) {
            for (int index = 0; index < SelectedManager.getCount(); index++) {
                LocalMedia media = SelectedManager.getSelectedResult().get(index);
                media.setNum(index + 1);
                if (isRefreshAdapter) {
                    mAdapter.notifyItemChanged(media.position);
                }
            }
        }
    }

    @Override
    public void onCheckOriginalChange() {
        bottomNarBar.setOriginalCheck();
    }

    /**
     * 刷新列表策略
     *
     * @param isAddRemove
     * @return
     */
    private boolean checkNotifyStrategy(boolean isAddRemove) {
        boolean isNotifyAll = false;
        if (config.isMaxSelectEnabledMask) {
            if (config.isWithVideoImage) {
                isNotifyAll = SelectedManager.getCount() == config.maxSelectNum
                        || (!isAddRemove && SelectedManager.getCount() == config.maxSelectNum - 1);
            } else {
                if (SelectedManager.getCount() == 0 || (isAddRemove && SelectedManager.getCount() == 1)) {
                    // 首次添加或者选择数量变为0了，都notifyDataSetChanged
                    isNotifyAll = true;
                } else {
                    if (PictureMimeType.isHasVideo(SelectedManager.getTopResultMimeType())) {
                        isNotifyAll = SelectedManager.getCount() == config.maxVideoSelectNum
                                || (!isAddRemove && SelectedManager.getCount() == config.maxVideoSelectNum - 1);
                    } else {
                        isNotifyAll = SelectedManager.getCount() == config.maxSelectNum
                                || (!isAddRemove && SelectedManager.getCount() == config.maxSelectNum - 1);
                    }
                }
            }
        }
        return isNotifyAll;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE, allFolderSize);
        outState.putInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
        outState.putInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, mRecycler.getLastVisiblePosition());
        outState.putBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, mAdapter.isDisplayCamera());
        outState.putString(PictureConfig.EXTRA_CURRENT_FIRST_PATH, getFirstImagePath());
        Log.i("YYY", "onSaveInstanceState: " + getFirstImagePath());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE);
            mPage = savedInstanceState.getInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
            currentPosition = savedInstanceState.getInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition);
            isDisplayCamera = savedInstanceState.getBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, config.isDisplayCamera);
        } else {
            isDisplayCamera = config.isDisplayCamera;
        }
        tvDataEmpty = view.findViewById(R.id.tv_data_empty);
        completeSelectView = view.findViewById(R.id.ps_complete_select);
        titleBar = view.findViewById(R.id.title_bar);
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar);
        initLoader();
        initAlbumListPopWindow();
        initTitleBar();
        initComplete();
        initRecycler(view);
        initBottomNavBar();
        requestLoadData();
    }

    /**
     * 完成按钮
     */
    private void initComplete() {
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            PictureSelectionConfig.selectorStyle.getTitleBarStyle().setHideCancelButton(false);
            titleBar.getTitleCancelView().setVisibility(View.VISIBLE);
        } else {
            completeSelectView.setCompleteSelectViewStyle();
            completeSelectView.setSelectedChange(false);
            SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
            if (selectMainStyle.isCompleteSelectRelativeTop()) {
                ((ConstraintLayout.LayoutParams)
                        completeSelectView.getLayoutParams()).topToTop = R.id.title_bar;
                ((ConstraintLayout.LayoutParams)
                        completeSelectView.getLayoutParams()).bottomToBottom = R.id.title_bar;
            }
            completeSelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTransformResult();
                }
            });
        }
    }

    /**
     * init LocalMedia Loader
     */
    protected void initLoader() {
        if (config.isPageStrategy) {
            mLoader = new LocalMediaPageLoader(getContext(), config);
        } else {
            mLoader = new LocalMediaLoader(getContext(), config);
        }
    }

    private void initTitleBar() {
        if (PictureSelectionConfig.selectorStyle.getTitleBarStyle().isHideTitleBar()) {
            titleBar.setVisibility(View.GONE);
        }
        titleBar.setTitleBarStyle();
        titleBar.setOnTitleBarListener(new TitleBar.OnTitleBarListener() {
            @Override
            public void onBackPressed() {
                if (albumListPopWindow.isShowing()) {
                    albumListPopWindow.dismiss();
                } else {
                    if (ActivityCompatHelper.checkRootFragment(getActivity())) {
                        if (PictureSelectionConfig.resultCallListener != null) {
                            PictureSelectionConfig.resultCallListener.onCancel();
                        }
                    }
                    iBridgePictureBehavior.onFinish();
                }
            }

            @Override
            public void onShowAlbumPopWindow(View anchor) {
                albumListPopWindow.showAsDropDown(anchor);
            }
        });
    }

    /**
     * initAlbumListPopWindow
     */
    private void initAlbumListPopWindow() {
        albumListPopWindow = AlbumListPopWindow.buildPopWindow(getContext());
        albumListPopWindow.setOnPopupWindowStatusListener(new AlbumListPopWindow.OnPopupWindowStatusListener() {
            @Override
            public void onShowPopupWindow() {
                AnimUtils.rotateArrow(titleBar.getImageArrow(), true);
            }

            @Override
            public void onDismissPopupWindow() {
                AnimUtils.rotateArrow(titleBar.getImageArrow(), false);
            }
        });
        addAlbumPopWindowAction();
    }

    private void requestLoadData() {
        mAdapter.setDisplayCamera(isDisplayCamera);
        if (PictureSelectionConfig.permissionsEventListener != null) {
            PictureSelectionConfig.permissionsEventListener.onPermission(this,
                    PictureConfig.READ_WRITE_EXTERNAL_STORAGE, new OnCallbackListener<Boolean>() {
                        @Override
                        public void onCall(Boolean isResult) {
                            if (isResult) {
                                beginLoadData();
                            } else {
                                handlePermissionDenied();
                            }
                        }
                    });
        } else {
            PermissionChecker.getInstance().requestPermissions(this,
                    PictureConfig.READ_WRITE_EXTERNAL_STORAGE, new PermissionResultCallback() {
                        @Override
                        public void onGranted() {
                            beginLoadData();
                        }

                        @Override
                        public void onDenied() {
                            handlePermissionDenied();
                        }
                    });
        }
    }

    /**
     * 开始获取数据
     */
    private void beginLoadData() {
        showLoading();
        if (config.isOnlySandboxDir) {
            loadOnlyInAppDirectoryAllMedia();
        } else {
            loadAllAlbum();
        }
    }

    @Override
    public void handlePermissionSettingResult() {
        if (PermissionChecker.checkSelfPermission(getContext(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            beginLoadData();
        } else {
            Toast.makeText(getContext(), getString(R.string.ps_jurisdiction), Toast.LENGTH_LONG).show();
            iBridgePictureBehavior.onFinish();
        }
    }

    /**
     * 给AlbumListPopWindow添加事件
     */
    private void addAlbumPopWindowAction() {
        albumListPopWindow.setOnIBridgeAlbumWidget(new OnAlbumItemClickListener() {

            @Override
            public void onItemClick(int position, LocalMediaFolder curFolder) {
                isDisplayCamera = config.isDisplayCamera && curFolder.getBucketId() == PictureConfig.ALL;
                mAdapter.setDisplayCamera(isDisplayCamera);
                titleBar.setTitle(curFolder.getName());
                LocalMediaFolder lastFolder = SelectedManager.getCurrentLocalMediaFolder();
                long lastBucketId = lastFolder.getBucketId();
                if (config.isPageStrategy) {
                    if (curFolder.getBucketId() != lastBucketId) {
                        // 1、记录一下上一次相册数据加载到哪了，到时候切回来的时候要续上
                        lastFolder.setData(mAdapter.getData());
                        lastFolder.setCurrentDataPage(mPage);
                        lastFolder.setHasMore(mRecycler.isEnabledLoadMore());

                        // 2、判断当前相册是否请求过，如果请求过则不从MediaStore去拉取了
                        if (curFolder.getData().size() > 0) {
                            setAdapterData(curFolder.getData());
                            mPage = curFolder.getCurrentDataPage();
                            mRecycler.setEnabledLoadMore(curFolder.isHasMore());
                            mRecycler.smoothScrollToPosition(0);
                        } else {
                            // 3、从MediaStore拉取数据
                            mPage = 1;
                            showLoading();
                            mLoader.loadPageMediaData(curFolder.getBucketId(), mPage, config.pageSize,
                                    new OnQueryDataResultListener<LocalMedia>() {
                                        @Override
                                        public void onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore) {
                                            if (ActivityCompatHelper.isDestroy(getActivity())) {
                                                return;
                                            }
                                            dismissLoading();
                                            mRecycler.setEnabledLoadMore(isHasMore);
                                            if (result.size() == 0) {
                                                // 如果从MediaStore拉取都没有数据了，adapter里的可能是缓存所以也清除
                                                mAdapter.getData().clear();
                                            }
                                            setAdapterData(result);
                                            mRecycler.onScrolled(0, 0);
                                            mRecycler.smoothScrollToPosition(0);
                                        }
                                    });
                        }
                    }
                } else {
                    // 非分页模式直接导入该相册下的所有资源
                    setAdapterData(curFolder.getData());
                    mRecycler.smoothScrollToPosition(0);
                }
                SelectedManager.setCurrentLocalMediaFolder(curFolder);
                albumListPopWindow.dismiss();
            }
        });
    }


    private void initBottomNavBar() {
        bottomNarBar.setSelectedChange();
        bottomNarBar.setBottomNavBarStyle();
        bottomNarBar.setOnBottomNavBarListener(new BottomNavBar.OnBottomNavBarListener() {
            @Override
            public void onPreview() {
                onStartPreview(0, true);
            }

            @Override
            public void onCheckOriginalChange() {
                sendSelectedOriginalChangeEvent();
            }
        });
    }


    @Override
    public void loadAllAlbum() {
        mLoader.loadAllMedia(new OnQueryDataResultListener<LocalMediaFolder>() {

            @Override
            public void onComplete(List<LocalMediaFolder> result) {
                if (ActivityCompatHelper.isDestroy(getActivity())) {
                    return;
                }
                if (result.size() > 0) {
                    LocalMediaFolder firstFolder;
                    if (SelectedManager.getCurrentLocalMediaFolder() != null) {
                        firstFolder = SelectedManager.getCurrentLocalMediaFolder();
                    } else {
                        firstFolder = result.get(0);
                        SelectedManager.setCurrentLocalMediaFolder(firstFolder);
                    }
                    titleBar.setTitle(firstFolder.getName());
                    albumListPopWindow.bindAlbumData(result);
                    saveFirstImagePath(firstFolder.getFirstImagePath());
                    if (config.isPageStrategy) {
                        loadFirstPageMedia(firstFolder.getBucketId());
                    } else {
                        dismissLoading();
                        setAdapterData(firstFolder.getData());
                    }
                } else {
                    showDataNull();
                }
            }
        });
    }

    @Override
    public void loadFirstPageMedia(long firstBucketId) {
        mRecycler.setEnabledLoadMore(true);
        mLoader.loadFirstPageMedia(firstBucketId, mPage * config.pageSize,
                new OnQueryDataResultListener<LocalMedia>() {
                    @Override
                    public void onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore) {
                        if (ActivityCompatHelper.isDestroy(getActivity())) {
                            return;
                        }
                        dismissLoading();
                        mRecycler.setEnabledLoadMore(isHasMore);
                        if (mRecycler.isEnabledLoadMore() && result.size() == 0) {
                            // 如果isHasMore为true但result.size() = 0;
                            // 那么有可能是开启了某些条件过滤，实际上是还有更多资源的再强制请求
                            onRecyclerViewPreloadMore();
                        } else {
                            setAdapterData(result);
                        }
                        recoveryRecyclerPosition();
                    }
                });
    }

    @Override
    public void loadOnlyInAppDirectoryAllMedia() {
        mLoader.loadOnlyInAppDirectoryAllMedia(new OnQueryDataResultListener<LocalMediaFolder>() {
            @Override
            public void onComplete(LocalMediaFolder folder) {
                dismissLoading();
                if (!ActivityCompatHelper.isDestroy(getActivity())) {
                    if (folder != null) {
                        titleBar.setTitle(folder.getName());
                        SelectedManager.setCurrentLocalMediaFolder(folder);
                        setAdapterData(folder.getData());
                        recoveryRecyclerPosition();
                    } else {
                        showDataNull();
                    }
                }
            }
        });
    }

    /**
     * 内存不足时，恢复RecyclerView定位位置
     */
    private void recoveryRecyclerPosition() {
        if (currentPosition > 0) {
            mRecycler.post(new Runnable() {
                @Override
                public void run() {
                    mRecycler.scrollToPosition(currentPosition);
                    mRecycler.setLastVisiblePosition(currentPosition);
                }
            });
        }
    }

    /**
     * 缓存首个相册目录的首张封面，拍照时有用到
     *
     * @param firstImagePath
     */
    private void saveFirstImagePath(String firstImagePath) {
        if (getArguments() != null) {
            Log.i("YYY", "saveFirstImagePath: " + firstImagePath);
            getArguments().putString(PictureConfig.EXTRA_CURRENT_FIRST_PATH, firstImagePath);
        }
    }

    /**
     * 获取首个相册目录的首张封面，拍照时有用到
     */
    private String getFirstImagePath() {
        if (getArguments() != null) {
            return getArguments().getString(PictureConfig.EXTRA_CURRENT_FIRST_PATH, "");
        }
        return "";
    }

    private void initRecycler(View view) {
        mRecycler = view.findViewById(R.id.recycler);
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        int listBackgroundColor = selectorStyle.getSelectMainStyle().getMainListBackgroundColor();
        if (listBackgroundColor != 0) {
            mRecycler.setBackgroundColor(listBackgroundColor);
        }
        int imageSpanCount = config.imageSpanCount <= 0 ? PictureConfig.DEFAULT_SPAN_COUNT : config.imageSpanCount;
        mRecycler.addItemDecoration(new GridSpacingItemDecoration(imageSpanCount, DensityUtil.dip2px(view.getContext(), 2), true));
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), imageSpanCount));
        RecyclerView.ItemAnimator itemAnimator = mRecycler.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            mRecycler.setItemAnimator(null);
        }
        if (config.isPageStrategy) {
            mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD);
            mRecycler.setOnRecyclerViewPreloadListener(this);
        } else {
            mRecycler.setHasFixedSize(true);
        }
        mAdapter = new PictureImageGridAdapter(config);
        mAdapter.setDisplayCamera(isDisplayCamera);
        switch (config.animationMode) {
            case AnimationType.ALPHA_IN_ANIMATION:
                mRecycler.setAdapter(new AlphaInAnimationAdapter(mAdapter));
                break;
            case AnimationType.SLIDE_IN_BOTTOM_ANIMATION:
                mRecycler.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
                break;
            default:
                mRecycler.setAdapter(mAdapter);
                break;
        }
        addRecyclerAction();
    }

    private void addRecyclerAction() {
        mAdapter.setOnItemClickListener(new PictureImageGridAdapter.OnItemClickListener() {

            @Override
            public void openCameraClick() {
                openSelectedCamera();
            }

            @Override
            public int onSelected(View selectedView, int position, LocalMedia media) {
                int selectResultCode = confirmSelect(media, selectedView.isSelected());
                if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                    selectedView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in));
                }
                return selectResultCode;
            }

            @Override
            public void onItemClick(View selectedView, int position, LocalMedia media) {
                if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
                    SelectedManager.getSelectedResult().clear();
                    SelectedManager.getSelectedResult().add(media);
                    dispatchTransformResult();
                } else {
                    boolean isPreview = PictureMimeType.isHasImage(media.getMimeType()) && config.isEnablePreview
                            || config.isDirectReturnSingle
                            || PictureMimeType.isHasVideo(media.getMimeType()) && (config.isEnPreviewVideo
                            || config.selectionMode == SelectModeConfig.SINGLE)
                            || PictureMimeType.isHasAudio(media.getMimeType()) && (config.isEnablePreviewAudio
                            || config.selectionMode == SelectModeConfig.SINGLE);
                    if (isPreview) {
                        if (DoubleUtils.isFastDoubleClick()) {
                            return;
                        }
                        if (PictureMimeType.isHasAudio(media.getMimeType())) {
                            AudioPlayDialog.showPlayAudioDialog(getActivity(), media.getPath());
                        } else {
                            onStartPreview(position, false);
                        }
                    } else {
                        confirmSelect(media, selectedView.isSelected());
                    }
                }
            }
        });
    }

    /**
     * 预览图片
     *
     * @param position        预览图片下标
     * @param isBottomPreview true 底部预览模式 false列表预览模式
     */
    private void onStartPreview(int position, boolean isBottomPreview) {
        if (ActivityCompatHelper.checkFragmentNonExits(getActivity(), PictureSelectorPreviewFragment.TAG)) {
            List<LocalMedia> data;
            int totalNum;
            long currentBucketId = 0;
            if (isBottomPreview) {
                data = new ArrayList<>(SelectedManager.getSelectedResult());
                totalNum = data.size();
            } else {
                data = mAdapter.getData();
                totalNum = SelectedManager.getCurrentLocalMediaFolder().getImageNum();
                currentBucketId = SelectedManager.getCurrentLocalMediaFolder().getBucketId();
            }
            if (iBridgePictureBehavior != null) {
                PictureSelectorPreviewFragment previewFragment = PictureSelectorPreviewFragment.newInstance();
                previewFragment.setInternalPreviewData(isBottomPreview, titleBar.getTitleText(), mAdapter.isDisplayCamera(),
                        position, totalNum, mPage, currentBucketId, data);
                iBridgePictureBehavior.injectFragmentFromScreen(PictureSelectorPreviewFragment.TAG, previewFragment);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterData(List<LocalMedia> result) {
        subSelectPosition(false);
        mAdapter.setDataAndDataSetChanged(result);
        if (mAdapter.isDataEmpty()) {
            showDataNull();
        } else {
            hideDataNull();
        }
    }

    @Override
    public void onRecyclerViewPreloadMore() {
        loadMoreData();
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMoreData() {
        if (mRecycler.isEnabledLoadMore()) {
            mPage++;
            LocalMediaFolder localMediaFolder = SelectedManager.getCurrentLocalMediaFolder();
            long bucketId = localMediaFolder != null ? localMediaFolder.getBucketId() : 0;
            mLoader.loadPageMediaData(bucketId, mPage, getPageLimit(bucketId), new OnQueryDataResultListener<LocalMedia>() {
                @Override
                public void onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore) {
                    if (ActivityCompatHelper.isDestroy(getActivity())) {
                        return;
                    }
                    mRecycler.setEnabledLoadMore(isHasMore);
                    if (mRecycler.isEnabledLoadMore()) {
                        if (result.size() > 0) {
                            int positionStart = mAdapter.getData().size();
                            mAdapter.getData().addAll(result);
                            mAdapter.notifyItemRangeChanged(positionStart, mAdapter.getItemCount());
                        } else {
                            // 如果没数据这里在强制调用一下上拉加载更多，防止是因为某些条件过滤导致的假为0的情况
                            onRecyclerViewPreloadMore();
                        }
                        if (result.size() < PictureConfig.MIN_PAGE_SIZE) {
                            // 当数据量过少时强制触发一下上拉加载更多，防止没有自动触发加载更多
                            mRecycler.onScrolled(mRecycler.getScrollX(), mRecycler.getScrollY());
                        }
                    }
                }
            });
        }
    }


    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        Log.i("YYY", media.getPath() + "\n" + getFirstImagePath());
        if (TextUtils.equals(media.getPath(), getFirstImagePath())) {
            // 这种情况一般就是拍照时内存不足了，导致Fragment重新创建了，先走的loadAllData已经获取到了拍照生成的这张
            // 如果这里还往下手动添加则会导致重复一张，故只要把新拍的加入选择结果即可
            SelectedManager.getSelectedResult().add(media);
            saveFirstImagePath(media.getPath());
            mAdapter.notifyItemChanged(config.isDisplayCamera ? 1 : 0);
            if (config.isDirectReturnSingle) {
                dispatchTransformResult();
            }
            return;
        }
        int exitsTotalNum = albumListPopWindow.getFirstAlbumImageCount();
        if (!isAddSameImp(exitsTotalNum)) {
            mAdapter.getData().add(0, media);
            openCameraNumber++;
        }
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            if (config.isDirectReturnSingle) {
                SelectedManager.getSelectedResult().clear();
                SelectedManager.getSelectedResult().add(media);
                dispatchTransformResult();
            } else {
                List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
                boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(SelectedManager.getTopResultMimeType(), media.getMimeType());
                if (mimeTypeSame || selectedResult.size() == 0) {
                    if (selectedResult.size() > 0) {
                        LocalMedia exitsMedia = selectedResult.get(0);
                        int position = exitsMedia.getPosition();
                        selectedResult.clear();
                        mAdapter.notifyItemChanged(position);
                    }
                }
                SelectedManager.getSelectedResult().add(media);
                onSelectedChange(true, media);
            }
        } else {
            confirmSelect(media, false);
        }
        mAdapter.notifyItemInserted(config.isDisplayCamera ? 1 : 0);
        mAdapter.notifyItemRangeChanged(config.isDisplayCamera ? 1 : 0, mAdapter.getData().size());
        if (config.isOnlySandboxDir) {
            titleBar.setTitle(media.getParentFolderName());
        } else {
            mergeFolder(media);
        }
        allFolderSize = 0;
        if (mAdapter.getData().size() > 0 || config.isDirectReturnSingle) {
            hideDataNull();
        } else {
            showDataNull();
        }
    }

    /**
     * 拍照出来的合并到相应的专辑目录中去
     *
     * @param media
     */
    private void mergeFolder(LocalMedia media) {
        LocalMediaFolder allFolder;
        if (albumListPopWindow.getFolderCount() == 0) {
            // 1、没有相册时需要手动创建相机胶卷
            allFolder = new LocalMediaFolder();
            String folderName = config.chooseMode == SelectMimeType.ofAudio()
                    ? getString(R.string.ps_all_audio) : getString(R.string.ps_camera_roll);
            allFolder.setName(folderName);
            allFolder.setFirstImagePath("");
            allFolder.setBucketId(PictureConfig.ALL);
            albumListPopWindow.getAlbumList().add(0, allFolder);
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow.getFolder(0);
        }
        allFolder.setFirstImagePath(media.getPath());
        allFolder.setFirstMimeType(media.getMimeType());
        allFolder.setData(mAdapter.getData());
        allFolder.setBucketId(PictureConfig.ALL);
        allFolder.setImageNum(isAddSameImp(allFolder.getImageNum())
                ? allFolder.getImageNum() : allFolder.getImageNum() + 1);
        // 先查找Camera目录，没有找到则创建一个Camera目录
        LocalMediaFolder cameraFolder = null;
        List<LocalMediaFolder> albumList = albumListPopWindow.getAlbumList();
        for (int i = 0; i < albumList.size(); i++) {
            LocalMediaFolder exitsFolder = albumList.get(i);
            if (TextUtils.equals(exitsFolder.getName(), media.getParentFolderName())) {
                cameraFolder = exitsFolder;
                break;
            }
        }
        if (cameraFolder == null) {
            cameraFolder = new LocalMediaFolder();
        }
        cameraFolder.setImageNum(isAddSameImp(allFolder.getImageNum())
                ? cameraFolder.getImageNum() : cameraFolder.getImageNum() + 1);
        if (!config.isPageStrategy && !isAddSameImp(allFolder.getImageNum())) {
            cameraFolder.getData().add(0, media);
        }
        cameraFolder.setBucketId(media.getBucketId());
        cameraFolder.setFirstImagePath(config.cameraPath);
        cameraFolder.setFirstMimeType(media.getMimeType());
        albumListPopWindow.bindAlbumData(albumListPopWindow.getAlbumList());
    }

    /**
     * 数量是否一致
     */
    private boolean isAddSameImp(int totalNum) {
        if (totalNum == 0) {
            return false;
        }
        return allFolderSize > 0 && allFolderSize < totalNum;
    }

    /**
     * 获取Limit
     * 如果用户点击拍照并返回，则应动态调整限制
     *
     * @return
     */
    private int getPageLimit(long bucketId) {
        if (bucketId == -1) {
            int limit = openCameraNumber > 0 ? config.pageSize - openCameraNumber : config.pageSize;
            openCameraNumber = 0;
            return limit;
        }
        return config.pageSize;
    }

    /**
     * 显示数据为空提示
     */
    private void showDataNull() {
        if (tvDataEmpty.getVisibility() == View.GONE) {
            tvDataEmpty.setVisibility(View.VISIBLE);
        }
        tvDataEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ps_ic_no_data, 0, 0);
        int chooseMode = config.chooseMode;
        String tips = chooseMode == SelectMimeType.ofAudio()
                ? getString(R.string.ps_audio_empty) : getString(R.string.ps_empty);
        tvDataEmpty.setText(tips);
    }

    /**
     * 隐藏数据为空提示
     */
    private void hideDataNull() {
        if (tvDataEmpty.getVisibility() == View.VISIBLE) {
            tvDataEmpty.setVisibility(View.GONE);
        }
    }
}
