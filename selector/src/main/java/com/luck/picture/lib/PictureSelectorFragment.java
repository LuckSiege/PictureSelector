package com.luck.picture.lib;

import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.animators.AlphaInAnimationAdapter;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.animators.SlideInBottomAnimationAdapter;
import com.luck.picture.lib.basic.FragmentInjectManager;
import com.luck.picture.lib.basic.IPictureSelectorEvent;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PermissionEvent;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.AlbumListPopWindow;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener;
import com.luck.picture.lib.interfaces.OnRequestPermissionListener;
import com.luck.picture.lib.loader.IBridgeMediaLoader;
import com.luck.picture.lib.loader.LocalMediaLoader;
import com.luck.picture.lib.loader.LocalMediaPageLoader;
import com.luck.picture.lib.magical.BuildRecycleItemViewParams;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.AnimUtils;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DoubleUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.luck.picture.lib.utils.ValueOf;
import com.luck.picture.lib.widget.BottomNavBar;
import com.luck.picture.lib.widget.CompleteSelectView;
import com.luck.picture.lib.widget.RecyclerPreloadView;
import com.luck.picture.lib.widget.SlideSelectTouchListener;
import com.luck.picture.lib.widget.SlideSelectionHandler;
import com.luck.picture.lib.widget.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelectorFragment
 */
public class PictureSelectorFragment extends PictureCommonFragment
        implements OnRecyclerViewPreloadMoreListener, IPictureSelectorEvent {
    public static final String TAG = PictureSelectorFragment.class.getSimpleName();
    private static final Object LOCK = new Object();
    /**
     * 这个时间对应的是R.anim.ps_anim_modal_in里面的
     */
    private static int SELECT_ANIM_DURATION = 135;
    private RecyclerPreloadView mRecycler;
    private TextView tvDataEmpty;
    private TitleBar titleBar;
    private BottomNavBar bottomNarBar;
    private CompleteSelectView completeSelectView;
    private TextView tvCurrentDataTime;
    private long intervalClickTime = 0;
    private int allFolderSize;
    private int currentPosition = -1;
    /**
     * Use camera to callback
     */
    private boolean isCameraCallback;
    /**
     * memory recycling
     */
    private boolean isMemoryRecycling;
    private boolean isDisplayCamera;

    private PictureImageGridAdapter mAdapter;

    private AlbumListPopWindow albumListPopWindow;

    private SlideSelectTouchListener mDragSelectTouchListener;

    public static PictureSelectorFragment newInstance() {
        PictureSelectorFragment fragment = new PictureSelectorFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        int layoutResourceId = InjectResourceSource.getLayoutResource(getContext(), InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE, selectorConfig);
        if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            return layoutResourceId;
        }
        return R.layout.ps_fragment_selector;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {
        bottomNarBar.setSelectedChange();
        completeSelectView.setSelectedChange(false);
        // 刷新列表数据
        if (checkNotifyStrategy(isAddRemove)) {
            mAdapter.notifyItemPositionChanged(currentMedia.position);
            mRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            }, SELECT_ANIM_DURATION);
        } else {
            mAdapter.notifyItemPositionChanged(currentMedia.position);
        }
        if (!isAddRemove) {
            sendChangeSubSelectPositionEvent(true);
        }
    }

    @Override
    public void onFixedSelectedChange(LocalMedia oldLocalMedia) {
        mAdapter.notifyItemPositionChanged(oldLocalMedia.position);
    }

    @Override
    public void sendChangeSubSelectPositionEvent(boolean adapterChange) {
        if (selectorConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
            for (int index = 0; index < selectorConfig.getSelectCount(); index++) {
                LocalMedia media = selectorConfig.getSelectedResult().get(index);
                media.setNum(index + 1);
                if (adapterChange) {
                    mAdapter.notifyItemPositionChanged(media.position);
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
        if (selectorConfig.isMaxSelectEnabledMask) {
            if (selectorConfig.isWithVideoImage) {
                if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
                    // ignore
                } else {
                    isNotifyAll = selectorConfig.getSelectCount() == selectorConfig.maxSelectNum
                            || (!isAddRemove && selectorConfig.getSelectCount() == selectorConfig.maxSelectNum - 1);
                }
            } else {
                if (selectorConfig.getSelectCount() == 0 || (isAddRemove && selectorConfig.getSelectCount() == 1)) {
                    // 首次添加或单选，选择数量变为0了，都notifyDataSetChanged
                    isNotifyAll = true;
                } else {
                    if (PictureMimeType.isHasVideo(selectorConfig.getResultFirstMimeType())) {
                        int maxSelectNum = selectorConfig.maxVideoSelectNum > 0
                                ? selectorConfig.maxVideoSelectNum : selectorConfig.maxSelectNum;
                        isNotifyAll = selectorConfig.getSelectCount() == maxSelectNum
                                || (!isAddRemove && selectorConfig.getSelectCount() == maxSelectNum - 1);
                    } else {
                        isNotifyAll = selectorConfig.getSelectCount() == selectorConfig.maxSelectNum
                                || (!isAddRemove && selectorConfig.getSelectCount() == selectorConfig.maxSelectNum - 1);
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
        if (mRecycler != null) {
            outState.putInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, mRecycler.getLastVisiblePosition());
        }
        if (mAdapter != null) {
            outState.putBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, mAdapter.isDisplayCamera());
            selectorConfig.addDataSource(mAdapter.getData());
        }
        if (albumListPopWindow != null) {
            selectorConfig.addAlbumDataSource(albumListPopWindow.getAlbumList());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reStartSavedInstance(savedInstanceState);
        isMemoryRecycling = savedInstanceState != null;
        tvDataEmpty = view.findViewById(R.id.tv_data_empty);
        completeSelectView = view.findViewById(R.id.ps_complete_select);
        titleBar = view.findViewById(R.id.title_bar);
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar);
        tvCurrentDataTime = view.findViewById(R.id.tv_current_data_time);
        onCreateLoader();
        initAlbumListPopWindow();
        initTitleBar();
        initComplete();
        initRecycler(view);
        initBottomNavBar();
        if (isMemoryRecycling) {
            recoverSaveInstanceData();
        } else {
            requestLoadData();
        }
    }


    @Override
    public void onFragmentResume() {
        setRootViewKeyListener(requireView());
    }

    @Override
    public void reStartSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE);
            mPage = savedInstanceState.getInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
            currentPosition = savedInstanceState.getInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition);
            isDisplayCamera = savedInstanceState.getBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, selectorConfig.isDisplayCamera);
        } else {
            isDisplayCamera = selectorConfig.isDisplayCamera;
        }
    }


    /**
     * 完成按钮
     */
    private void initComplete() {
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
            selectorConfig.selectorStyle.getTitleBarStyle().setHideCancelButton(false);
            titleBar.getTitleCancelView().setVisibility(View.VISIBLE);
            completeSelectView.setVisibility(View.GONE);
        } else {
            completeSelectView.setCompleteSelectViewStyle();
            completeSelectView.setSelectedChange(false);
            SelectMainStyle selectMainStyle = selectorConfig.selectorStyle.getSelectMainStyle();
            if (selectMainStyle.isCompleteSelectRelativeTop()) {
                if (completeSelectView.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                    ((ConstraintLayout.LayoutParams)
                            completeSelectView.getLayoutParams()).topToTop = R.id.title_bar;
                    ((ConstraintLayout.LayoutParams)
                            completeSelectView.getLayoutParams()).bottomToBottom = R.id.title_bar;
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
            completeSelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectorConfig.isEmptyResultReturn && selectorConfig.getSelectCount() == 0) {
                        onExitPictureSelector();
                    } else {
                        dispatchTransformResult();
                    }
                }
            });
        }
    }


    @Override
    public void onCreateLoader() {
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

    private void initTitleBar() {
        if (selectorConfig.selectorStyle.getTitleBarStyle().isHideTitleBar()) {
            titleBar.setVisibility(View.GONE);
        }
        titleBar.setTitleBarStyle();
        titleBar.setOnTitleBarListener(new TitleBar.OnTitleBarListener() {
            @Override
            public void onTitleDoubleClick() {
                if (selectorConfig.isAutomaticTitleRecyclerTop) {
                    int intervalTime = 500;
                    if (SystemClock.uptimeMillis() - intervalClickTime < intervalTime && mAdapter.getItemCount() > 0) {
                        mRecycler.scrollToPosition(0);
                    } else {
                        intervalClickTime = SystemClock.uptimeMillis();
                    }
                }
            }

            @Override
            public void onBackPressed() {
                if (albumListPopWindow.isShowing()) {
                    albumListPopWindow.dismiss();
                } else {
                    onKeyBackFragmentFinish();
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
        albumListPopWindow = AlbumListPopWindow.buildPopWindow(getContext(), selectorConfig);
        albumListPopWindow.setOnPopupWindowStatusListener(new AlbumListPopWindow.OnPopupWindowStatusListener() {
            @Override
            public void onShowPopupWindow() {
                if (!selectorConfig.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar.getImageArrow(), true);
                }
            }

            @Override
            public void onDismissPopupWindow() {
                if (!selectorConfig.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar.getImageArrow(), false);
                }
            }
        });
        addAlbumPopWindowAction();
    }

    private void recoverSaveInstanceData(){
        mAdapter.setDisplayCamera(isDisplayCamera);
        setEnterAnimationDuration(0);
        if (selectorConfig.isOnlySandboxDir) {
            handleInAppDirAllMedia(selectorConfig.currentLocalMediaFolder);
        } else {
            handleRecoverAlbumData(new ArrayList<>(selectorConfig.albumDataSource));
        }
    }


    private void handleRecoverAlbumData(List<LocalMediaFolder> albumData) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (albumData.size() > 0) {
            LocalMediaFolder firstFolder;
            if (selectorConfig.currentLocalMediaFolder != null) {
                firstFolder = selectorConfig.currentLocalMediaFolder;
            } else {
                firstFolder = albumData.get(0);
                selectorConfig.currentLocalMediaFolder = firstFolder;
            }
            titleBar.setTitle(firstFolder.getFolderName());
            albumListPopWindow.bindAlbumData(albumData);
            if (selectorConfig.isPageStrategy) {
                handleFirstPageMedia(new ArrayList<>(selectorConfig.dataSource), true);
            } else {
                setAdapterData(firstFolder.getData());
            }
        } else {
            showDataNull();
        }
    }


    private void requestLoadData() {
        mAdapter.setDisplayCamera(isDisplayCamera);
        if (PermissionChecker.isCheckReadStorage(selectorConfig.chooseMode, getContext())) {
            beginLoadData();
        } else {
            String[] readPermissionArray = PermissionConfig.getReadPermissionArray(getAppContext(), selectorConfig.chooseMode);
            onPermissionExplainEvent(true, readPermissionArray);
            if (selectorConfig.onPermissionsEventListener != null) {
                onApplyPermissionsEvent(PermissionEvent.EVENT_SOURCE_DATA, readPermissionArray);
            } else {
                PermissionChecker.getInstance().requestPermissions(this, readPermissionArray, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        beginLoadData();
                    }

                    @Override
                    public void onDenied() {
                        handlePermissionDenied(readPermissionArray);
                    }
                });
            }
        }
    }

    @Override
    public void onApplyPermissionsEvent(int event, String[] permissionArray) {
        if (event != PermissionEvent.EVENT_SOURCE_DATA) {
            super.onApplyPermissionsEvent(event, permissionArray);
        } else {
            selectorConfig.onPermissionsEventListener.requestPermission(this, permissionArray, new OnRequestPermissionListener() {
                @Override
                public void onCall(String[] permissionArray, boolean isResult) {
                    if (isResult) {
                        beginLoadData();
                    } else {
                        handlePermissionDenied(permissionArray);
                    }
                }
            });
        }
    }

    /**
     * 开始获取数据
     */
    private void beginLoadData() {
        onPermissionExplainEvent(false, null);
        if (selectorConfig.isOnlySandboxDir) {
            loadOnlyInAppDirectoryAllMediaData();
        } else {
            loadAllAlbumData();
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        if (permissions == null){
            return;
        }
        onPermissionExplainEvent(false, null);
        boolean isHasCamera = permissions.length > 0 && TextUtils.equals(permissions[0], PermissionConfig.CAMERA[0]);
        boolean isHasPermissions;
        if (selectorConfig.onPermissionsEventListener != null) {
            isHasPermissions = selectorConfig.onPermissionsEventListener.hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckSelfPermission(getContext(), permissions);
        }
        if (isHasPermissions) {
            if (isHasCamera) {
                openSelectedCamera();
            } else {
                beginLoadData();
            }
        } else {
            if (isHasCamera) {
                ToastUtils.showToast(getContext(), getString(R.string.ps_camera));
            } else {
                ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
                onKeyBackFragmentFinish();
            }
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }

    /**
     * 给AlbumListPopWindow添加事件
     */
    private void addAlbumPopWindowAction() {
        albumListPopWindow.setOnIBridgeAlbumWidget(new OnAlbumItemClickListener() {

            @Override
            public void onItemClick(int position, LocalMediaFolder curFolder) {
                isDisplayCamera = selectorConfig.isDisplayCamera && curFolder.getBucketId() == PictureConfig.ALL;
                mAdapter.setDisplayCamera(isDisplayCamera);
                titleBar.setTitle(curFolder.getFolderName());
                LocalMediaFolder lastFolder = selectorConfig.currentLocalMediaFolder;
                long lastBucketId = lastFolder.getBucketId();
                if (selectorConfig.isPageStrategy) {
                    if (curFolder.getBucketId() != lastBucketId) {
                        // 1、记录一下上一次相册数据加载到哪了，到时候切回来的时候要续上
                        lastFolder.setData(mAdapter.getData());
                        lastFolder.setCurrentDataPage(mPage);
                        lastFolder.setHasMore(mRecycler.isEnabledLoadMore());

                        // 2、判断当前相册是否请求过，如果请求过则不从MediaStore去拉取了
                        if (curFolder.getData().size() > 0 && !curFolder.isHasMore()) {
                            setAdapterData(curFolder.getData());
                            mPage = curFolder.getCurrentDataPage();
                            mRecycler.setEnabledLoadMore(curFolder.isHasMore());
                            mRecycler.smoothScrollToPosition(0);
                        } else {
                            // 3、从MediaStore拉取数据
                            mPage = 1;
                            if (selectorConfig.loaderDataEngine != null) {
                                selectorConfig.loaderDataEngine.loadFirstPageMediaData(getContext(),
                                        curFolder.getBucketId(), mPage, selectorConfig.pageSize,
                                        new OnQueryDataResultListener<LocalMedia>() {
                                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                                handleSwitchAlbum(result, isHasMore);
                                            }
                                        });
                            } else {
                                mLoader.loadPageMediaData(curFolder.getBucketId(), mPage, selectorConfig.pageSize,
                                        new OnQueryDataResultListener<LocalMedia>() {
                                            @Override
                                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                                handleSwitchAlbum(result, isHasMore);
                                            }
                                        });
                            }
                        }
                    }
                } else {
                    // 非分页模式直接导入该相册下的所有资源
                    if (curFolder.getBucketId() != lastBucketId) {
                        setAdapterData(curFolder.getData());
                        mRecycler.smoothScrollToPosition(0);
                    }
                }
                selectorConfig.currentLocalMediaFolder = curFolder;
                albumListPopWindow.dismiss();
                if (mDragSelectTouchListener != null && selectorConfig.isFastSlidingSelect) {
                    mDragSelectTouchListener.setRecyclerViewHeaderCount(mAdapter.isDisplayCamera() ? 1 : 0);
                }
            }
        });
    }

    private void handleSwitchAlbum(ArrayList<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (result.size() == 0) {
            // 如果从MediaStore拉取都没有数据了，adapter里的可能是缓存所以也清除
            mAdapter.getData().clear();
        }
        setAdapterData(result);
        mRecycler.onScrolled(0, 0);
        mRecycler.smoothScrollToPosition(0);
    }


    private void initBottomNavBar() {
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
        bottomNarBar.setSelectedChange();
    }


    @Override
    public void loadAllAlbumData() {
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadAllAlbumData(getContext(),
                    new OnQueryAllAlbumListener<LocalMediaFolder>() {
                        @Override
                        public void onComplete(List<LocalMediaFolder> result) {
                            handleAllAlbumData(false, result);
                        }
                    });
        } else {
            boolean isPreload = preloadPageFirstData();
            mLoader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {

                @Override
                public void onComplete(List<LocalMediaFolder> result) {
                    handleAllAlbumData(isPreload, result);
                }
            });
        }
    }

    private boolean preloadPageFirstData() {
        boolean isPreload = false;
        if (selectorConfig.isPageStrategy && selectorConfig.isPreloadFirst) {
            LocalMediaFolder firstFolder = new LocalMediaFolder();
            firstFolder.setBucketId(PictureConfig.ALL);
            if (TextUtils.isEmpty(selectorConfig.defaultAlbumName)) {
                titleBar.setTitle(selectorConfig.chooseMode == SelectMimeType.ofAudio() ? requireContext().getString(R.string.ps_all_audio) : requireContext().getString(R.string.ps_camera_roll));
            } else {
                titleBar.setTitle(selectorConfig.defaultAlbumName);
            }
            firstFolder.setFolderName(titleBar.getTitleText());
            selectorConfig.currentLocalMediaFolder = firstFolder;
            loadFirstPageMediaData(firstFolder.getBucketId());
            isPreload = true;
        }
        return isPreload;
    }

    private void handleAllAlbumData(boolean isPreload, List<LocalMediaFolder> result) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (result.size() > 0) {
            LocalMediaFolder firstFolder;
            if (isPreload) {
                firstFolder = result.get(0);
                selectorConfig.currentLocalMediaFolder = firstFolder;
            } else {
                if (selectorConfig.currentLocalMediaFolder != null) {
                    firstFolder = selectorConfig.currentLocalMediaFolder;
                } else {
                    firstFolder = result.get(0);
                    selectorConfig.currentLocalMediaFolder = firstFolder;
                }
            }
            titleBar.setTitle(firstFolder.getFolderName());
            albumListPopWindow.bindAlbumData(result);
            if (selectorConfig.isPageStrategy) {
                if (selectorConfig.isPreloadFirst) {
                    mRecycler.setEnabledLoadMore(true);
                } else {
                    loadFirstPageMediaData(firstFolder.getBucketId());
                }
            } else {
                setAdapterData(firstFolder.getData());
            }
        } else {
            showDataNull();
        }
    }

    @Override
    public void loadFirstPageMediaData(long firstBucketId) {
        mPage = 1;
        mRecycler.setEnabledLoadMore(true);
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadFirstPageMediaData(getContext(), firstBucketId,
                    mPage, mPage * selectorConfig.pageSize, new OnQueryDataResultListener<LocalMedia>() {

                        @Override
                        public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                            handleFirstPageMedia(result, isHasMore);
                        }
                    });
        } else {
            mLoader.loadPageMediaData(firstBucketId, mPage, mPage * selectorConfig.pageSize,
                    new OnQueryDataResultListener<LocalMedia>() {
                        @Override
                        public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                            handleFirstPageMedia(result, isHasMore);
                        }
                    });
        }
    }

    private void handleFirstPageMedia(ArrayList<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (mRecycler.isEnabledLoadMore() && result.size() == 0) {
            // 如果isHasMore为true但result.size() = 0;
            // 那么有可能是开启了某些条件过滤，实际上是还有更多资源的再强制请求
            onRecyclerViewPreloadMore();
        } else {
            setAdapterData(result);
        }
    }

    @Override
    public void loadOnlyInAppDirectoryAllMediaData() {
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadOnlyInAppDirAllMediaData(getContext(),
                    new OnQueryAlbumListener<LocalMediaFolder>() {
                        @Override
                        public void onComplete(LocalMediaFolder folder) {
                            handleInAppDirAllMedia(folder);
                        }
                    });
        } else {
            mLoader.loadOnlyInAppDirAllMedia(new OnQueryAlbumListener<LocalMediaFolder>() {
                @Override
                public void onComplete(LocalMediaFolder folder) {
                    handleInAppDirAllMedia(folder);
                }
            });
        }
    }

    private void handleInAppDirAllMedia(LocalMediaFolder folder) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            String sandboxDir = selectorConfig.sandboxDir;
            boolean isNonNull = folder != null;
            String folderName = isNonNull ? folder.getFolderName() : new File(sandboxDir).getName();
            titleBar.setTitle(folderName);
            if (isNonNull) {
                selectorConfig.currentLocalMediaFolder = folder;
                setAdapterData(folder.getData());
            } else {
                showDataNull();
            }
        }
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

    private void initRecycler(View view) {
        mRecycler = view.findViewById(R.id.recycler);
        PictureSelectorStyle selectorStyle = selectorConfig.selectorStyle;
        SelectMainStyle selectMainStyle = selectorStyle.getSelectMainStyle();
        int listBackgroundColor = selectMainStyle.getMainListBackgroundColor();
        if (StyleUtils.checkStyleValidity(listBackgroundColor)) {
            mRecycler.setBackgroundColor(listBackgroundColor);
        } else {
            mRecycler.setBackgroundColor(ContextCompat.getColor(getAppContext(), R.color.ps_color_black));
        }
        int imageSpanCount = selectorConfig.imageSpanCount <= 0 ? PictureConfig.DEFAULT_SPAN_COUNT : selectorConfig.imageSpanCount;
        if (mRecycler.getItemDecorationCount() == 0) {
            if (StyleUtils.checkSizeValidity(selectMainStyle.getAdapterItemSpacingSize())) {
                mRecycler.addItemDecoration(new GridSpacingItemDecoration(imageSpanCount,
                        selectMainStyle.getAdapterItemSpacingSize(), selectMainStyle.isAdapterItemIncludeEdge()));
            } else {
                mRecycler.addItemDecoration(new GridSpacingItemDecoration(imageSpanCount,
                        DensityUtil.dip2px(view.getContext(), 1), selectMainStyle.isAdapterItemIncludeEdge()));
            }
        }
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), imageSpanCount));
        RecyclerView.ItemAnimator itemAnimator = mRecycler.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            mRecycler.setItemAnimator(null);
        }
        if (selectorConfig.isPageStrategy) {
            mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD);
            mRecycler.setOnRecyclerViewPreloadListener(this);
        } else {
            mRecycler.setHasFixedSize(true);
        }
        mAdapter = new PictureImageGridAdapter(getContext(), selectorConfig);
        mAdapter.setDisplayCamera(isDisplayCamera);
        switch (selectorConfig.animationMode) {
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
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                openSelectedCamera();
            }

            @Override
            public int onSelected(View selectedView, int position, LocalMedia media) {
                int selectResultCode = confirmSelect(media, selectedView.isSelected());
                if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                    if (selectorConfig.onSelectAnimListener != null) {
                        long duration = selectorConfig.onSelectAnimListener.onSelectAnim(selectedView);
                        if (duration > 0) {
                            SELECT_ANIM_DURATION = (int) duration;
                        }
                    } else {
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
                        SELECT_ANIM_DURATION = (int) animation.getDuration();
                        selectedView.startAnimation(animation);
                    }
                }
                return selectResultCode;
            }

            @Override
            public void onItemClick(View selectedView, int position, LocalMedia media) {
                if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
                    selectorConfig.selectedResult.clear();
                    int selectResultCode = confirmSelect(media, false);
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        dispatchTransformResult();
                    }
                } else {
                    if (DoubleUtils.isFastDoubleClick()) {
                        return;
                    }
                    onStartPreview(position, false);
                }
            }

            @Override
            public void onItemLongClick(View itemView, int position) {
                if (mDragSelectTouchListener != null && selectorConfig.isFastSlidingSelect) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                    mDragSelectTouchListener.startSlideSelection(position);
                }
            }
        });

        mRecycler.setOnRecyclerViewScrollStateListener(new OnRecyclerViewScrollStateListener() {
            @Override
            public void onScrollFast() {
                if (selectorConfig.imageEngine != null) {
                    selectorConfig.imageEngine.pauseRequests(getContext());
                }
            }

            @Override
            public void onScrollSlow() {
                if (selectorConfig.imageEngine != null) {
                    selectorConfig.imageEngine.resumeRequests(getContext());
                }
            }
        });
        mRecycler.setOnRecyclerViewScrollListener(new OnRecyclerViewScrollListener() {
            @Override
            public void onScrolled(int dx, int dy) {
                setCurrentMediaCreateTimeText();
            }

            @Override
            public void onScrollStateChanged(int state) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                    showCurrentMediaCreateTimeUI();
                } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    hideCurrentMediaCreateTimeUI();
                }
            }
        });

        if (selectorConfig.isFastSlidingSelect) {
            HashSet<Integer> selectedPosition = new HashSet<>();
            SlideSelectionHandler slideSelectionHandler = new SlideSelectionHandler(new SlideSelectionHandler.ISelectionHandler() {
                @Override
                public HashSet<Integer> getSelection() {
                    for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                        LocalMedia media = selectorConfig.getSelectedResult().get(i);
                        selectedPosition.add(media.position);
                    }
                    return selectedPosition;
                }

                @Override
                public void changeSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                    ArrayList<LocalMedia> adapterData = mAdapter.getData();
                    if (adapterData.size() == 0 || start > adapterData.size()) {
                        return;
                    }
                    LocalMedia media = adapterData.get(start);
                    int selectResultCode = confirmSelect(media, selectorConfig.getSelectedResult().contains(media));
                    mDragSelectTouchListener.setActive(selectResultCode != SelectedManager.INVALID);
                }
            });
            mDragSelectTouchListener = new SlideSelectTouchListener()
                    .setRecyclerViewHeaderCount(mAdapter.isDisplayCamera() ? 1 : 0)
                    .withSelectListener(slideSelectionHandler);
            mRecycler.addOnItemTouchListener(mDragSelectTouchListener);
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private void setCurrentMediaCreateTimeText() {
        if (selectorConfig.isDisplayTimeAxis) {
            int position = mRecycler.getFirstVisiblePosition();
            if (position != RecyclerView.NO_POSITION) {
                ArrayList<LocalMedia> data = mAdapter.getData();
                if (data.size() > position && data.get(position).getDateAddedTime() > 0) {
                    tvCurrentDataTime.setText(DateUtils.getDataFormat(getContext(),
                            data.get(position).getDateAddedTime()));
                }
            }
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private void showCurrentMediaCreateTimeUI() {
        if (selectorConfig.isDisplayTimeAxis && mAdapter.getData().size() > 0) {
            if (tvCurrentDataTime.getAlpha() == 0F) {
                tvCurrentDataTime.animate().setDuration(150).alphaBy(1.0F).start();
            }
        }
    }

    /**
     * 隐藏当前资源时间轴
     */
    private void hideCurrentMediaCreateTimeUI() {
        if (selectorConfig.isDisplayTimeAxis && mAdapter.getData().size() > 0) {
            tvCurrentDataTime.animate().setDuration(250).alpha(0.0F).start();
        }
    }

    /**
     * 预览图片
     *
     * @param position        预览图片下标
     * @param isBottomPreview true 底部预览模式 false列表预览模式
     */
    private void onStartPreview(int position, boolean isBottomPreview) {
        if (ActivityCompatHelper.checkFragmentNonExits(getActivity(), PictureSelectorPreviewFragment.TAG)) {
            ArrayList<LocalMedia> data;
            int totalNum;
            long currentBucketId = 0;
            if (isBottomPreview) {
                data = new ArrayList<>(selectorConfig.getSelectedResult());
                totalNum = data.size();
            } else {
                data = new ArrayList<>(mAdapter.getData());
                LocalMediaFolder currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder;
                if (currentLocalMediaFolder != null) {
                    totalNum = currentLocalMediaFolder.getFolderTotalNum();
                    currentBucketId = currentLocalMediaFolder.getBucketId();
                } else {
                    totalNum = data.size();
                    currentBucketId = data.size() > 0 ? data.get(0).getBucketId() : PictureConfig.ALL;
                }
            }
            if (!isBottomPreview && selectorConfig.isPreviewZoomEffect) {
                BuildRecycleItemViewParams.generateViewParams(mRecycler,
                        selectorConfig.isPreviewFullScreenMode ? 0 : DensityUtil.getStatusBarHeight(getContext()));
            }
            if (selectorConfig.onPreviewInterceptListener != null) {
                selectorConfig.onPreviewInterceptListener
                        .onPreview(getContext(), position, totalNum, mPage, currentBucketId, titleBar.getTitleText(),
                                mAdapter.isDisplayCamera(), data, isBottomPreview);
            } else {
                if (ActivityCompatHelper.checkFragmentNonExits(getActivity(), PictureSelectorPreviewFragment.TAG)) {
                    PictureSelectorPreviewFragment previewFragment = PictureSelectorPreviewFragment.newInstance();
                    previewFragment.setInternalPreviewData(isBottomPreview, titleBar.getTitleText(), mAdapter.isDisplayCamera(),
                            position, totalNum, mPage, currentBucketId, data);
                    FragmentInjectManager.injectFragment(getActivity(), PictureSelectorPreviewFragment.TAG, previewFragment);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterData(ArrayList<LocalMedia> result) {
        // 这个地方有个时间差，主要是解决进场动画和查询数据同时进行导致动画有点卡顿问题，
        // 主要是针对添加PictureSelectorFragment方式下
        long enterAnimationDuration = getEnterAnimationDuration();
        if (enterAnimationDuration > 0) {
            requireView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAdapterDataComplete(result);
                }
            }, enterAnimationDuration);
        } else {
            setAdapterDataComplete(result);
        }
    }

    private void setAdapterDataComplete(ArrayList<LocalMedia> result) {
        setEnterAnimationDuration(0);
        sendChangeSubSelectPositionEvent(false);
        mAdapter.setDataAndDataSetChanged(result);
        selectorConfig.dataSource.clear();
        selectorConfig.albumDataSource.clear();
        recoveryRecyclerPosition();
        if (mAdapter.isDataEmpty()) {
            showDataNull();
        } else {
            hideDataNull();
        }
    }

    @Override
    public void onRecyclerViewPreloadMore() {
        if (isMemoryRecycling) {
            // 这里延迟是拍照导致的页面被回收，Fragment的重创会快于相机的onActivityResult的
            requireView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreMediaData();
                }
            }, 350);
        } else {
            loadMoreMediaData();
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMoreMediaData() {
        if (mRecycler.isEnabledLoadMore()) {
            mPage++;
            LocalMediaFolder localMediaFolder = selectorConfig.currentLocalMediaFolder;
            long bucketId = localMediaFolder != null ? localMediaFolder.getBucketId() : 0;
            if (selectorConfig.loaderDataEngine != null) {
                selectorConfig.loaderDataEngine.loadMoreMediaData(getContext(), bucketId, mPage,
                        selectorConfig.pageSize, selectorConfig.pageSize, new OnQueryDataResultListener<LocalMedia>() {
                            @Override
                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                handleMoreMediaData(result, isHasMore);
                            }
                        });
            } else {
                mLoader.loadPageMediaData(bucketId, mPage, selectorConfig.pageSize,
                        new OnQueryDataResultListener<LocalMedia>() {
                            @Override
                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                handleMoreMediaData(result, isHasMore);
                            }
                        });
            }
        }
    }

    private void handleMoreMediaData(List<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (mRecycler.isEnabledLoadMore()) {
            removePageCameraRepeatData(result);
            if (result.size() > 0) {
                int positionStart = mAdapter.getData().size();
                mAdapter.getData().addAll(result);
                mAdapter.notifyItemRangeChanged(positionStart, mAdapter.getItemCount());
                hideDataNull();
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

    private void removePageCameraRepeatData(List<LocalMedia> result) {
        try {
            if (selectorConfig.isPageStrategy && isCameraCallback) {
                synchronized (LOCK) {
                    Iterator<LocalMedia> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        if (mAdapter.getData().contains(iterator.next())) {
                            iterator.remove();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isCameraCallback = false;
        }
    }


    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        int exitsTotalNum = albumListPopWindow.getFirstAlbumImageCount();
        if (!isAddSameImp(exitsTotalNum)) {
            mAdapter.getData().add(0, media);
            isCameraCallback = true;
        }
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
            selectorConfig.selectedResult.clear();
            int selectResultCode = confirmSelect(media, false);
            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                dispatchTransformResult();
            }
        } else {
            confirmSelect(media, false);
        }
        mAdapter.notifyItemInserted(selectorConfig.isDisplayCamera ? 1 : 0);
        mAdapter.notifyItemRangeChanged(selectorConfig.isDisplayCamera ? 1 : 0, mAdapter.getData().size());
        if (selectorConfig.isOnlySandboxDir) {
            LocalMediaFolder currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder;
            if (currentLocalMediaFolder == null) {
                currentLocalMediaFolder = new LocalMediaFolder();
            }
            currentLocalMediaFolder.setBucketId(ValueOf.toLong(media.getParentFolderName().hashCode()));
            currentLocalMediaFolder.setFolderName(media.getParentFolderName());
            currentLocalMediaFolder.setFirstMimeType(media.getMimeType());
            currentLocalMediaFolder.setFirstImagePath(media.getPath());
            currentLocalMediaFolder.setFolderTotalNum(mAdapter.getData().size());
            currentLocalMediaFolder.setCurrentDataPage(mPage);
            currentLocalMediaFolder.setHasMore(false);
            currentLocalMediaFolder.setData(mAdapter.getData());
            mRecycler.setEnabledLoadMore(false);
            selectorConfig.currentLocalMediaFolder = currentLocalMediaFolder;
        } else {
            mergeFolder(media);
        }
        allFolderSize = 0;
        if (mAdapter.getData().size() > 0 || selectorConfig.isDirectReturnSingle) {
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
        List<LocalMediaFolder> albumList = albumListPopWindow.getAlbumList();
        if (albumListPopWindow.getFolderCount() == 0) {
            // 1、没有相册时需要手动创建相机胶卷
            allFolder = new LocalMediaFolder();
            String folderName;
            if (TextUtils.isEmpty(selectorConfig.defaultAlbumName)) {
                folderName = selectorConfig.chooseMode == SelectMimeType.ofAudio() ? getString(R.string.ps_all_audio) : getString(R.string.ps_camera_roll);
            } else {
                folderName = selectorConfig.defaultAlbumName;
            }
            allFolder.setFolderName(folderName);
            allFolder.setFirstImagePath("");
            allFolder.setBucketId(PictureConfig.ALL);
            albumList.add(0, allFolder);
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow.getFolder(0);
        }
        allFolder.setFirstImagePath(media.getPath());
        allFolder.setFirstMimeType(media.getMimeType());
        allFolder.setData(mAdapter.getData());
        allFolder.setBucketId(PictureConfig.ALL);
        allFolder.setFolderTotalNum(isAddSameImp(allFolder.getFolderTotalNum()) ? allFolder.getFolderTotalNum() : allFolder.getFolderTotalNum() + 1);
        LocalMediaFolder currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder;
        if (currentLocalMediaFolder == null || currentLocalMediaFolder.getFolderTotalNum() == 0) {
            selectorConfig.currentLocalMediaFolder = allFolder;
        }
        // 先查找Camera目录，没有找到则创建一个Camera目录
        LocalMediaFolder cameraFolder = null;
        for (int i = 0; i < albumList.size(); i++) {
            LocalMediaFolder exitsFolder = albumList.get(i);
            if (TextUtils.equals(exitsFolder.getFolderName(), media.getParentFolderName())) {
                cameraFolder = exitsFolder;
                break;
            }
        }
        if (cameraFolder == null) {
            // 还没有这个目录，创建一个
            cameraFolder = new LocalMediaFolder();
            albumList.add(cameraFolder);
        }
        cameraFolder.setFolderName(media.getParentFolderName());
        if (cameraFolder.getBucketId() == -1 || cameraFolder.getBucketId() == 0) {
            cameraFolder.setBucketId(media.getBucketId());
        }
        // 分页模式下，切换到Camera目录下时，会直接从MediaStore拉取
        if (selectorConfig.isPageStrategy) {
            cameraFolder.setHasMore(true);
        } else {
            // 非分页模式数据都是存在目录的data下，所以直接添加进去就行
            if (!isAddSameImp(allFolder.getFolderTotalNum())
                    || !TextUtils.isEmpty(selectorConfig.outPutCameraDir)
                    || !TextUtils.isEmpty(selectorConfig.outPutAudioDir)) {
                cameraFolder.getData().add(0, media);
            }
        }
        cameraFolder.setFolderTotalNum(isAddSameImp(allFolder.getFolderTotalNum())
                ? cameraFolder.getFolderTotalNum() : cameraFolder.getFolderTotalNum() + 1);
        cameraFolder.setFirstImagePath(selectorConfig.cameraPath);
        cameraFolder.setFirstMimeType(media.getMimeType());
        albumListPopWindow.bindAlbumData(albumList);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mDragSelectTouchListener != null) {
            mDragSelectTouchListener.stopAutoScroll();
        }
    }

    /**
     * 显示数据为空提示
     */
    private void showDataNull() {
        if (selectorConfig.currentLocalMediaFolder == null
                || selectorConfig.currentLocalMediaFolder.getBucketId() == PictureConfig.ALL) {
            if (tvDataEmpty.getVisibility() == View.GONE) {
                tvDataEmpty.setVisibility(View.VISIBLE);
            }
            tvDataEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ps_ic_no_data, 0, 0);
            String tips = selectorConfig.chooseMode == SelectMimeType.ofAudio() ? getString(R.string.ps_audio_empty) : getString(R.string.ps_empty);
            tvDataEmpty.setText(tips);
        }
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
