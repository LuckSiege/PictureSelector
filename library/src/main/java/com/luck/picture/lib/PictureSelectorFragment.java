package com.luck.picture.lib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.animators.AlphaInAnimationAdapter;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.animators.SlideInBottomAnimationAdapter;
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
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener;
import com.luck.picture.lib.loader.LocalMediaLoader;
import com.luck.picture.lib.loader.LocalMediaPageLoader;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.tools.AnimUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.SortUtils;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.widget.BottomNavBar;
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

    /**
     * open camera number
     */
    private int openCameraNumber;

    private int allFolderSize;

    private PictureImageGridAdapter mAdapter;

    private AlbumListPopWindow albumListPopWindow;

    public static PictureSelectorFragment newInstance() {

        return new PictureSelectorFragment();
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_fragment_selector;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {
        bottomNarBar.setSelectedChange();
        if (checkNotifyStrategy(isAddRemove)) {
            mAdapter.notifyDataSetChanged();
            Log.i("YYY", "刷新全部");
        } else {
            mAdapter.notifyItemChanged(currentMedia.position);
            Log.i("YYY", "刷新单个");
        }
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE);
        }
        tvDataEmpty = view.findViewById(R.id.tv_data_empty);
        titleBar = view.findViewById(R.id.title_bar);
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar);
        initLoader();
        initAlbumListPopWindow();
        initTitleBar();
        initRecycler(view);
        initBottomNavBar();
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
        loadAllAlbum();
        addAlbumPopWindowAction();
    }

    private void addAlbumPopWindowAction() {
        albumListPopWindow.setOnIBridgeAlbumWidget(new OnAlbumItemClickListener() {

            @Override
            public void onItemClick(int position, LocalMediaFolder curFolder) {
                boolean camera = config.isDisplayCamera && curFolder.isCameraFolder();
                mAdapter.setShowCamera(camera);
                titleBar.setTitle(curFolder.getName());
                LocalMediaFolder lastFolder = albumListPopWindow.getLastFolder();
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
                            mLoader.loadPageMediaData(curFolder.getBucketId(), mPage,
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
                albumListPopWindow.setLastFolder(curFolder);
                albumListPopWindow.dismiss();
            }
        });
    }


    private void initBottomNavBar() {
        bottomNarBar.setBottomNavBarStyle();
        bottomNarBar.setOnBottomNavBarListener(new BottomNavBar.OnBottomNavBarListener() {
            @Override
            public void onPreview() {
                onStartPreview(0, true);
            }

            @Override
            public void onComplete() {
                dispatchTransformResult();
            }

            @Override
            public void onEditImage() {

            }
        });
    }


    @Override
    public void loadAllAlbum() {
        PermissionChecker.getInstance().requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        showLoading();
                        mLoader.loadAllMedia(new OnQueryDataResultListener<LocalMediaFolder>() {

                            @Override
                            public void onComplete(List<LocalMediaFolder> result) {
                                if (ActivityCompatHelper.isDestroy(getActivity())) {
                                    return;
                                }
                                if (result.size() > 0) {
                                    LocalMediaFolder firstFolder = result.get(0);
                                    firstFolder.setChecked(true);
                                    titleBar.setTitle(firstFolder.getName());
                                    albumListPopWindow.setLastFolder(firstFolder);
                                    albumListPopWindow.bindAlbumData(result);
                                    loadAllMedia(firstFolder);
                                } else {
                                    showDataNull();
                                }
                            }
                        });
                    }

                    @Override
                    public void onDenied() {

                    }
                });
    }

    @Override
    public void loadAllMedia(LocalMediaFolder firstFolder) {
        if (config.isOnlySandboxDir) {
            loadOnlyInAppDirectoryAllMedia();
        } else {
            if (config.isPageStrategy) {
                mPage = 1;
                mRecycler.setEnabledLoadMore(true);
                mLoader.loadPageMediaData(firstFolder.getBucketId(), mPage, new OnQueryDataResultListener<LocalMedia>() {
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
                            result.addAll(0, firstFolder.getData());
                            SortUtils.sortLocalMediaAddedTime(result);
                            setAdapterData(result);
                        }
                    }
                });
            } else {
                dismissLoading();
                setAdapterData(firstFolder.getData());
            }
        }
    }

    @Override
    public void loadOnlyInAppDirectoryAllMedia() {
        mLoader.loadOnlyInAppDirectoryAllMedia(new OnQueryDataResultListener<LocalMediaFolder>() {
            @Override
            public void onComplete(LocalMediaFolder folder) {
                if (ActivityCompatHelper.isDestroy(getActivity())) {
                    return;
                }
                dismissLoading();
                if (folder != null) {
                    titleBar.setTitle(folder.getName());
                    setAdapterData(folder.getData());
                } else {
                    showDataNull();
                }
            }
        });
    }

    private void initRecycler(View view) {
        mRecycler = view.findViewById(R.id.recycler);
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        int listBackgroundColor = selectorStyle.getAdapterStyle().getAdapterListBackgroundColor();
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
                int resultCode = confirmSelect(media, selectedView.isSelected());
                if (resultCode == SelectedManager.ADD_SUCCESS) {
                    selectedView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in));
                }
                return resultCode;
            }

            @Override
            public void onItemClick(View selectedView, int position, LocalMedia media) {
                if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
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
            if (isBottomPreview) {
                data = new ArrayList<>(SelectedManager.getSelectedResult());
                totalNum = data.size();
            } else {
                data = mAdapter.getData();
                totalNum = albumListPopWindow.getLastFolder().getImageNum();
            }
            if (iBridgePictureBehavior != null) {
                PictureSelectorPreviewFragment previewFragment = PictureSelectorPreviewFragment.newInstance();
                previewFragment.setData(position, totalNum, data);
                iBridgePictureBehavior.injectFragmentFromScreen(PictureSelectorPreviewFragment.TAG, previewFragment);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterData(List<LocalMedia> result) {
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
            long bucketId = albumListPopWindow.getLastFolder().getBucketId();
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
        boolean isAddSameImp = isAddSameImp(albumListPopWindow.getFolderCount() > 0
                ? albumListPopWindow.getFolder(0).getImageNum() : 0);
        if (!isAddSameImp) {
            mAdapter.getData().add(0, media);
            openCameraNumber++;
        }
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            String exitsMimeType = SelectedManager.getTopResultMimeType();
            if (checkOnlyMimeTypeValidity(false, media.getMimeType(), exitsMimeType, media.getDuration())) {
                if (config.isDirectReturnSingle) {
                    SelectedManager.getSelectedResult().clear();
                } else {
                    List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(exitsMimeType, media.getMimeType());
                    if (mimeTypeSame || selectedResult.size() == 0) {
                        if (selectedResult.size() > 0) {
                            LocalMedia exitsMedia = selectedResult.get(0);
                            int position = exitsMedia.getPosition();
                            selectedResult.clear();
                            mAdapter.notifyItemChanged(position);
                        }
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
                    ? getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
            allFolder.setName(folderName);
            allFolder.setFirstImagePath("");
            allFolder.setCameraFolder(true);
            allFolder.setBucketId(-1);
            allFolder.setChecked(true);
            albumListPopWindow.getAlbumList().add(0, allFolder);
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow.getFolder(0);
        }
        allFolder.setFirstImagePath(media.getPath());
        allFolder.setFirstMimeType(media.getMimeType());
        allFolder.setData(mAdapter.getData());
        allFolder.setBucketId(-1);
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


    @Override
    public void onDestroy() {
        PictureSelectionConfig.destroy();
        super.onDestroy();
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
                ? getString(R.string.picture_audio_empty) : getString(R.string.picture_empty);
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
