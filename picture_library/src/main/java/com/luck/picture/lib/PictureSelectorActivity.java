package com.luck.picture.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.listener.OnAlbumItemClickListener;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.listener.OnPhotoSelectChangedListener;
import com.luck.picture.lib.listener.OnQueryDataResultListener;
import com.luck.picture.lib.listener.OnRecyclerViewPreloadMoreListener;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.LocalMediaPageLoader;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.BitmapUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.JumpUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.widget.FolderPopWindow;
import com.luck.picture.lib.widget.RecyclerPreloadView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.CutInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2018/1/27 19:12
 * @describe: PictureSelectorActivity
 */
public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener,
        OnAlbumItemClickListener, OnPhotoSelectChangedListener<LocalMedia>, OnItemClickListener,
        OnRecyclerViewPreloadMoreListener {
    private static final String TAG = PictureSelectorActivity.class.getSimpleName();
    protected ImageView mIvPictureLeftBack;
    protected ImageView mIvArrow;
    protected View titleViewBg;
    protected TextView mTvPictureTitle, mTvPictureRight, mTvPictureOk, mTvEmpty,
            mTvPictureImgNum, mTvPicturePreview, mTvPlayPause, mTvStop, mTvQuit,
            mTvMusicStatus, mTvMusicTotal, mTvMusicTime;
    protected RecyclerPreloadView mRecyclerView;
    protected RelativeLayout mBottomLayout;
    protected PictureImageGridAdapter mAdapter;
    protected FolderPopWindow folderWindow;
    protected Animation animation = null;
    protected boolean isStartAnimation = false;
    protected MediaPlayer mediaPlayer;
    protected SeekBar musicSeekBar;
    protected boolean isPlayAudio = false;
    protected PictureCustomDialog audioDialog;
    protected CheckBox mCbOriginal;
    protected int oldCurrentListSize;
    protected boolean isEnterSetting;
    private long intervalClickTime = 0;
    private int allFolderSize;
    private int mOpenCameraCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE);
            oldCurrentListSize = savedInstanceState.getInt(PictureConfig.EXTRA_OLD_CURRENT_LIST_SIZE, 0);
            selectionMedias = PictureSelector.obtainSelectorList(savedInstanceState);
            if (mAdapter != null) {
                isStartAnimation = true;
                mAdapter.bindSelectData(selectionMedias);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isEnterSetting) {
            if (PermissionChecker
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    PermissionChecker
                            .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (mAdapter.isDataEmpty()) {
                    readLocalMedia();
                }
            } else {
                showPermissionsDialog(false, getString(R.string.picture_jurisdiction));
            }
            isEnterSetting = false;
        }

        if (config.isOriginalControl) {
            if (mCbOriginal != null) {
                mCbOriginal.setChecked(config.isCheckOriginalImage);
            }
        }
    }

    @Override
    public int getResourceId() {
        return R.layout.picture_selector;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        container = findViewById(R.id.container);
        titleViewBg = findViewById(R.id.titleViewBg);
        mIvPictureLeftBack = findViewById(R.id.pictureLeftBack);
        mTvPictureTitle = findViewById(R.id.picture_title);
        mTvPictureRight = findViewById(R.id.picture_right);
        mTvPictureOk = findViewById(R.id.picture_tv_ok);
        mCbOriginal = findViewById(R.id.cb_original);
        mIvArrow = findViewById(R.id.ivArrow);
        mTvPicturePreview = findViewById(R.id.picture_id_preview);
        mTvPictureImgNum = findViewById(R.id.picture_tvMediaNum);
        mRecyclerView = findViewById(R.id.picture_recycler);
        mBottomLayout = findViewById(R.id.rl_bottom);
        mTvEmpty = findViewById(R.id.tv_empty);
        isNumComplete(numComplete);
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.picture_anim_modal_in);
        }
        mTvPicturePreview.setOnClickListener(this);
        if (config.isAutomaticTitleRecyclerTop) {
            titleViewBg.setOnClickListener(this);
        }
        mTvPicturePreview.setVisibility(config.chooseMode != PictureMimeType.ofAudio() && config.enablePreview ? View.VISIBLE : View.GONE);
        mBottomLayout.setVisibility(config.selectionMode == PictureConfig.SINGLE
                && config.isSingleDirectReturn ? View.GONE : View.VISIBLE);
        mIvPictureLeftBack.setOnClickListener(this);
        mTvPictureRight.setOnClickListener(this);
        mTvPictureOk.setOnClickListener(this);
        mTvPictureImgNum.setOnClickListener(this);
        mTvPictureTitle.setOnClickListener(this);
        mIvArrow.setOnClickListener(this);
        String title = config.chooseMode == PictureMimeType.ofAudio() ?
                getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
        mTvPictureTitle.setText(title);
        mTvPictureTitle.setTag(R.id.view_tag, -1);
        folderWindow = new FolderPopWindow(this, config);
        folderWindow.setArrowImageView(mIvArrow);
        folderWindow.setOnAlbumItemClickListener(this);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), config.imageSpanCount));
        if (!config.isPageStrategy) {
            mRecyclerView.setHasFixedSize(true);
        } else {
            mRecyclerView.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD);
            mRecyclerView.setOnRecyclerViewPreloadListener(PictureSelectorActivity.this);
        }
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            mRecyclerView.setItemAnimator(null);
        }
        loadAllMediaData();
        mTvEmpty.setText(config.chooseMode == PictureMimeType.ofAudio() ?
                getString(R.string.picture_audio_empty)
                : getString(R.string.picture_empty));
        StringUtils.tempTextFont(mTvEmpty, config.chooseMode);
        mAdapter = new PictureImageGridAdapter(getContext(), config);
        mAdapter.setOnPhotoSelectChangedListener(this);

        switch (config.animationMode) {
            case AnimationType
                    .ALPHA_IN_ANIMATION:
                mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mAdapter));
                break;
            case AnimationType
                    .SLIDE_IN_BOTTOM_ANIMATION:
                mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
                break;
            default:
                mRecyclerView.setAdapter(mAdapter);
                break;
        }
        if (config.isOriginalControl) {
            mCbOriginal.setVisibility(View.VISIBLE);
            mCbOriginal.setChecked(config.isCheckOriginalImage);
            mCbOriginal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                config.isCheckOriginalImage = isChecked;
            });
        }
    }

    @Override
    public void onRecyclerViewPreloadMore() {
        loadMoreData();
    }

    /**
     * getPageLimit
     * # If the user clicks to take a photo and returns, the Limit should be adjusted dynamically
     *
     * @return
     */
    private int getPageLimit() {
        int bucketId = ValueOf.toInt(mTvPictureTitle.getTag(R.id.view_tag));
        if (bucketId == -1) {
            int limit = mOpenCameraCount > 0 ? config.pageSize - mOpenCameraCount : config.pageSize;
            mOpenCameraCount = 0;
            return limit;
        }
        return config.pageSize;
    }

    /**
     * load more data
     */
    private void loadMoreData() {
        if (mAdapter != null) {
            if (isHasMore) {
                mPage++;
                long bucketId = ValueOf.toLong(mTvPictureTitle.getTag(R.id.view_tag));
                LocalMediaPageLoader.getInstance(getContext(), config).loadPageMediaData(bucketId, mPage, getPageLimit(),
                        (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                            if (!isFinishing()) {
                                this.isHasMore = isHasMore;
                                if (isHasMore) {
                                    hideDataNull();
                                    int size = result.size();
                                    if (size > 0) {
                                        int positionStart = mAdapter.getSize();
                                        mAdapter.getData().addAll(result);
                                        int itemCount = mAdapter.getItemCount();
                                        mAdapter.notifyItemRangeChanged(positionStart, itemCount);
                                    } else {
                                        onRecyclerViewPreloadMore();
                                    }
                                    if (size < PictureConfig.MIN_PAGE_SIZE) {
                                        mRecyclerView.onScrolled(mRecyclerView.getScrollX(), mRecyclerView.getScrollY());
                                    }
                                } else {
                                    boolean isEmpty = mAdapter.isDataEmpty();
                                    if (isEmpty) {
                                        showDataNull(bucketId == -1 ? getString(R.string.picture_empty) : getString(R.string.picture_data_null), R.drawable.picture_icon_no_data);
                                    }
                                }
                            }
                        });
            }
        }
    }

    /**
     * load All Data
     */
    private void loadAllMediaData() {
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            readLocalMedia();
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleDownResId != 0) {
                Drawable drawable = ContextCompat.getDrawable(this, config.style.pictureTitleDownResId);
                mIvArrow.setImageDrawable(drawable);
            }
            if (config.style.pictureTitleTextColor != 0) {
                mTvPictureTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureTitleTextSize != 0) {
                mTvPictureTitle.setTextSize(config.style.pictureTitleTextSize);
            }

            if (config.style.pictureRightDefaultTextColor != 0) {
                mTvPictureRight.setTextColor(config.style.pictureRightDefaultTextColor);
            } else {
                if (config.style.pictureCancelTextColor != 0) {
                    mTvPictureRight.setTextColor(config.style.pictureCancelTextColor);
                }
            }

            if (config.style.pictureRightTextSize != 0) {
                mTvPictureRight.setTextSize(config.style.pictureRightTextSize);
            }

            if (config.style.pictureLeftBackIcon != 0) {
                mIvPictureLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.pictureUnPreviewTextColor != 0) {
                mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
            }
            if (config.style.picturePreviewTextSize != 0) {
                mTvPicturePreview.setTextSize(config.style.picturePreviewTextSize);
            }
            if (config.style.pictureCheckNumBgStyle != 0) {
                mTvPictureImgNum.setBackgroundResource(config.style.pictureCheckNumBgStyle);
            }
            if (config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            if (config.style.pictureCompleteTextSize != 0) {
                mTvPictureOk.setTextSize(config.style.pictureCompleteTextSize);
            }
            if (config.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(config.style.pictureBottomBgColor);
            }
            if (config.style.pictureContainerBackgroundColor != 0) {
                container.setBackgroundColor(config.style.pictureContainerBackgroundColor);
            }
            if (!TextUtils.isEmpty(config.style.pictureRightDefaultText)) {
                mTvPictureRight.setText(config.style.pictureRightDefaultText);
            }
            if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mTvPictureOk.setText(config.style.pictureUnCompleteText);
            }
            if (!TextUtils.isEmpty(config.style.pictureUnPreviewText)) {
                mTvPicturePreview.setText(config.style.pictureUnPreviewText);
            }
        } else {
            if (config.downResId != 0) {
                Drawable drawable = ContextCompat.getDrawable(this, config.downResId);
                mIvArrow.setImageDrawable(drawable);
            }
            int pictureBottomBgColor = AttrsUtils.
                    getTypeValueColor(getContext(), R.attr.picture_bottom_bg);
            if (pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(pictureBottomBgColor);
            }
        }
        titleViewBg.setBackgroundColor(colorPrimary);

        if (config.isOriginalControl) {
            if (config.style != null) {
                if (config.style.pictureOriginalControlStyle != 0) {
                    mCbOriginal.setButtonDrawable(config.style.pictureOriginalControlStyle);
                } else {
                    mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                }
                if (config.style.pictureOriginalFontColor != 0) {
                    mCbOriginal.setTextColor(config.style.pictureOriginalFontColor);
                } else {
                    mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
                }
                if (config.style.pictureOriginalTextSize != 0) {
                    mCbOriginal.setTextSize(config.style.pictureOriginalTextSize);
                }
            } else {
                mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
            }
        }

        mAdapter.bindSelectData(selectionMedias);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            // Save the number of pictures or videos in the current list
            outState.putInt(PictureConfig.EXTRA_OLD_CURRENT_LIST_SIZE, mAdapter.getSize());
            // Save the number of Camera film and Camera folder files
            int size = folderWindow.getFolderData().size();
            if (size > 0) {
                outState.putInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE, folderWindow.getFolder(0).getImageNum());
            }
            if (mAdapter.getSelectedData() != null) {
                List<LocalMedia> selectedImages = mAdapter.getSelectedData();
                PictureSelector.saveSelectorList(outState, selectedImages);
            }
        }
    }

    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        if (numComplete) {
            initCompleteText(0);
        }
    }

    /**
     * init Text
     */
    @Override
    protected void initCompleteText(int startCount) {
        boolean isNotEmptyStyle = config.style != null;
        if (config.selectionMode == PictureConfig.SINGLE) {
            if (startCount <= 0) {
                mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                        ? config.style.pictureUnCompleteText : getString(R.string.picture_please_select));
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(config.style.pictureCompleteText, startCount, 1));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureCompleteText)
                            ? config.style.pictureCompleteText : getString(R.string.picture_done));
                }
            }

        } else {
            boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
            if (startCount <= 0) {
                mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                        ? config.style.pictureUnCompleteText : getString(R.string.picture_done_front_num,
                        startCount, config.maxSelectNum));
            } else {
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(config.style.pictureCompleteText, startCount, config.maxSelectNum));
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_done_front_num,
                            startCount, config.maxSelectNum));
                }
            }
        }
    }


    /**
     * get LocalMedia s
     */
    protected void readLocalMedia() {
        showPleaseDialog();
        if (config.isPageStrategy) {
            LocalMediaPageLoader.getInstance(getContext(), config).loadAllMedia(
                    (OnQueryDataResultListener<LocalMediaFolder>) (data, currentPage, isHasMore) -> {
                        if (!isFinishing()) {
                            this.isHasMore = true;
                            initPageModel(data);
                            synchronousCover();
                        }
                    });
        } else {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {

                @Override
                public List<LocalMediaFolder> doInBackground() {
                    return new LocalMediaLoader(getContext(), config).loadAllMedia();
                }

                @Override
                public void onSuccess(List<LocalMediaFolder> folders) {
                    initStandardModel(folders);
                }
            });
        }
    }

    /**
     * Page Model
     *
     * @param folders
     */
    private void initPageModel(List<LocalMediaFolder> folders) {
        if (folders != null) {
            folderWindow.bindFolder(folders);
            mPage = 1;
            LocalMediaFolder folder = folderWindow.getFolder(0);
            mTvPictureTitle.setTag(R.id.view_count_tag, folder != null ? folder.getImageNum() : 0);
            mTvPictureTitle.setTag(R.id.view_index_tag, 0);
            long bucketId = folder != null ? folder.getBucketId() : -1;
            mRecyclerView.setEnabledLoadMore(true);
            LocalMediaPageLoader.getInstance(getContext(), config).loadPageMediaData(bucketId, mPage,
                    (OnQueryDataResultListener<LocalMedia>) (data, currentPage, isHasMore) -> {
                        if (!isFinishing()) {
                            dismissDialog();
                            if (mAdapter != null) {
                                this.isHasMore = true;
                                // IsHasMore being true means that there's still data, but data being 0 might be a filter that's turned on and that doesn't happen to fit on the whole page
                                if (isHasMore && data.size() == 0) {
                                    onRecyclerViewPreloadMore();
                                    return;
                                }
                                int currentSize = mAdapter.getSize();
                                int resultSize = data.size();
                                oldCurrentListSize = oldCurrentListSize + currentSize;
                                if (resultSize >= currentSize) {
                                    // This situation is mainly caused by the use of camera memory, the Activity is recycled
                                    if (currentSize > 0 && currentSize < resultSize && oldCurrentListSize != resultSize) {
                                        if (isLocalMediaSame(data.get(0))) {
                                            mAdapter.bindData(data);
                                        } else {
                                            mAdapter.getData().addAll(data);
                                        }
                                    } else {
                                        mAdapter.bindData(data);
                                    }
                                }
                                boolean isEmpty = mAdapter.isDataEmpty();
                                if (isEmpty) {
                                    showDataNull(getString(R.string.picture_empty), R.drawable.picture_icon_no_data);
                                } else {
                                    hideDataNull();
                                }

                            }
                        }
                    });
        } else {
            showDataNull(getString(R.string.picture_data_exception), R.drawable.picture_icon_data_error);
            dismissDialog();
        }
    }

    /**
     * ofAll Page Model Synchronous cover
     */
    private void synchronousCover() {
        if (config.chooseMode == PictureMimeType.ofAll()) {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<Boolean>() {

                @Override
                public Boolean doInBackground() {
                    int size = folderWindow.getFolderData().size();
                    for (int i = 0; i < size; i++) {
                        LocalMediaFolder mediaFolder = folderWindow.getFolder(i);
                        if (mediaFolder == null) {
                            continue;
                        }
                        String firstCover = LocalMediaPageLoader
                                .getInstance(getContext(), config).getFirstCover(mediaFolder.getBucketId());
                        mediaFolder.setFirstImagePath(firstCover);
                    }
                    return true;
                }

                @Override
                public void onSuccess(Boolean result) {
                    // TODO Synchronous Success
                }
            });
        }
    }

    /**
     * Standard Model
     *
     * @param folders
     */
    private void initStandardModel(List<LocalMediaFolder> folders) {
        if (folders != null) {
            if (folders.size() > 0) {
                folderWindow.bindFolder(folders);
                LocalMediaFolder folder = folders.get(0);
                folder.setChecked(true);
                mTvPictureTitle.setTag(R.id.view_count_tag, folder.getImageNum());
                List<LocalMedia> result = folder.getData();
                if (mAdapter != null) {
                    int currentSize = mAdapter.getSize();
                    int resultSize = result.size();
                    oldCurrentListSize = oldCurrentListSize + currentSize;
                    if (resultSize >= currentSize) {
                        // This situation is mainly caused by the use of camera memory, the Activity is recycled
                        if (currentSize > 0 && currentSize < resultSize && oldCurrentListSize != resultSize) {
                            mAdapter.getData().addAll(result);
                            LocalMedia media = mAdapter.getData().get(0);
                            folder.setFirstImagePath(media.getPath());
                            folder.getData().add(0, media);
                            folder.setCheckedNum(1);
                            folder.setImageNum(folder.getImageNum() + 1);
                            updateMediaFolder(folderWindow.getFolderData(), media);
                        } else {
                            mAdapter.bindData(result);
                        }
                    }
                    boolean isEmpty = mAdapter.isDataEmpty();
                    if (isEmpty) {
                        showDataNull(getString(R.string.picture_empty), R.drawable.picture_icon_no_data);
                    } else {
                        hideDataNull();
                    }
                }
            } else {
                showDataNull(getString(R.string.picture_empty), R.drawable.picture_icon_no_data);
            }
        } else {
            showDataNull(getString(R.string.picture_data_exception), R.drawable.picture_icon_data_error);
        }
        dismissDialog();
    }

    /**
     * isSame
     *
     * @param newMedia
     * @return
     */
    private boolean isLocalMediaSame(LocalMedia newMedia) {
        LocalMedia oldMedia = mAdapter.getItem(0);
        if (oldMedia == null || newMedia == null) {
            return false;
        }
        if (oldMedia.getPath().equals(newMedia.getPath())) {
            return true;
        }
        // if Content:// type,determines whether the suffix id is consistent, mainly to solve the following two types of problems
        // content://media/external/images/media/5844
        // content://media/external/file/5844
        if (PictureMimeType.isContent(newMedia.getPath())
                && PictureMimeType.isContent(oldMedia.getPath())) {
            if (!TextUtils.isEmpty(newMedia.getPath()) && !TextUtils.isEmpty(oldMedia.getPath())) {
                String newId = newMedia.getPath().substring(newMedia.getPath().lastIndexOf("/") + 1);
                String oldId = oldMedia.getPath().substring(oldMedia.getPath().lastIndexOf("/") + 1);
                if (newId.equals(oldId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Open Camera
     */
    public void startCamera() {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (PictureSelectionConfig.onCustomCameraInterfaceListener != null) {
                if (config.chooseMode == PictureConfig.TYPE_ALL) {
                    PhotoItemSelectedDialog selectedDialog = PhotoItemSelectedDialog.newInstance();
                    selectedDialog.setOnItemClickListener(this);
                    selectedDialog.show(getSupportFragmentManager(), "PhotoItemSelectedDialog");
                } else {
                    PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, config.chooseMode);
                    config.cameraMimeType = config.chooseMode;
                }
                return;
            }
            if (config.isUseCustomCamera) {
                startCustomCamera();
                return;
            }
            switch (config.chooseMode) {
                case PictureConfig.TYPE_ALL:
                    PhotoItemSelectedDialog selectedDialog = PhotoItemSelectedDialog.newInstance();
                    selectedDialog.setOnItemClickListener(this);
                    selectedDialog.show(getSupportFragmentManager(), "PhotoItemSelectedDialog");
                    break;
                case PictureConfig.TYPE_IMAGE:
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_VIDEO:
                    startOpenCameraVideo();
                    break;
                case PictureConfig.TYPE_AUDIO:
                    startOpenCameraAudio();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Open Custom Camera
     */
    private void startCustomCamera() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            Intent intent = new Intent(this, PictureCustomCameraActivity.class);
            startActivityForResult(intent, PictureConfig.REQUEST_CAMERA);
            PictureWindowAnimationStyle windowAnimationStyle = config.windowAnimationStyle;
            overridePendingTransition(windowAnimationStyle != null &&
                    windowAnimationStyle.activityEnterAnimation != 0 ?
                    windowAnimationStyle.activityEnterAnimation :
                    R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
        } else {
            PermissionChecker
                    .requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pictureLeftBack || id == R.id.picture_right) {
            if (folderWindow != null && folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                onBackPressed();
            }
            return;
        }
        if (id == R.id.picture_title || id == R.id.ivArrow) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (!folderWindow.isEmpty()) {
                    folderWindow.showAsDropDown(titleViewBg);
                    if (!config.isSingleDirectReturn) {
                        List<LocalMedia> selectedImages = mAdapter.getSelectedData();
                        folderWindow.updateFolderCheckStatus(selectedImages);
                    }
                }
            }
            return;
        }

        if (id == R.id.picture_id_preview) {
            onPreview();
            return;
        }

        if (id == R.id.picture_tv_ok || id == R.id.picture_tvMediaNum) {
            onComplete();
            return;
        }

        if (id == R.id.titleViewBg) {
            if (config.isAutomaticTitleRecyclerTop) {
                int intervalTime = 500;
                if (SystemClock.uptimeMillis() - intervalClickTime < intervalTime) {
                    if (mAdapter.getItemCount() > 0) {
                        mRecyclerView.scrollToPosition(0);
                    }
                } else {
                    intervalClickTime = SystemClock.uptimeMillis();
                }
            }
        }
    }

    /**
     * Preview
     */
    private void onPreview() {
        List<LocalMedia> selectedImages = mAdapter.getSelectedData();
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
        bundle.putBoolean(PictureConfig.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
        bundle.putBoolean(PictureConfig.EXTRA_SHOW_CAMERA, mAdapter.isShowCamera());
        bundle.putString(PictureConfig.EXTRA_IS_CURRENT_DIRECTORY, mTvPictureTitle.getText().toString());
        JumpUtils.startPicturePreviewActivity(getContext(), config.isWeChatStyle, bundle,
                config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCrop.REQUEST_MULTI_CROP);

        overridePendingTransition(config.windowAnimationStyle != null
                        && config.windowAnimationStyle.activityPreviewEnterAnimation != 0
                        ? config.windowAnimationStyle.activityPreviewEnterAnimation : R.anim.picture_anim_enter,
                R.anim.picture_anim_fade_in);
    }

    /**
     * Complete
     */
    private void onComplete() {
        List<LocalMedia> result = mAdapter.getSelectedData();
        int size = result.size();
        LocalMedia image = result.size() > 0 ? result.get(0) : null;
        String mimeType = image != null ? image.getMimeType() : "";
        boolean isHasImage = PictureMimeType.isHasImage(mimeType);
        if (config.isWithVideoImage) {
            int videoSize = 0;
            int imageSize = 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = result.get(i);
                if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    videoSize++;
                } else {
                    imageSize++;
                }
            }
            if (config.selectionMode == PictureConfig.MULTIPLE) {
                if (config.minSelectNum > 0) {
                    if (imageSize < config.minSelectNum) {
                        showPromptDialog(getString(R.string.picture_min_img_num, config.minSelectNum));
                        return;
                    }
                }
                if (config.minVideoSelectNum > 0) {
                    if (videoSize < config.minVideoSelectNum) {
                        showPromptDialog(getString(R.string.picture_min_video_num, config.minVideoSelectNum));
                        return;
                    }
                }
            }
        } else {
            if (config.selectionMode == PictureConfig.MULTIPLE) {
                if (PictureMimeType.isHasImage(mimeType) && config.minSelectNum > 0 && size < config.minSelectNum) {
                    String str = getString(R.string.picture_min_img_num, config.minSelectNum);
                    showPromptDialog(str);
                    return;
                }
                if (PictureMimeType.isHasVideo(mimeType) && config.minVideoSelectNum > 0 && size < config.minVideoSelectNum) {
                    String str = getString(R.string.picture_min_video_num, config.minVideoSelectNum);
                    showPromptDialog(str);
                    return;
                }
            }
        }

        if (config.returnEmpty && size == 0) {
            if (config.selectionMode == PictureConfig.MULTIPLE) {
                if (config.minSelectNum > 0 && size < config.minSelectNum) {
                    String str = getString(R.string.picture_min_img_num, config.minSelectNum);
                    showPromptDialog(str);
                    return;
                }
                if (config.minVideoSelectNum > 0 && size < config.minVideoSelectNum) {
                    String str = getString(R.string.picture_min_video_num, config.minVideoSelectNum);
                    showPromptDialog(str);
                    return;
                }
            }
            if (PictureSelectionConfig.listener != null) {
                PictureSelectionConfig.listener.onResult(result);
            } else {
                Intent intent = PictureSelector.putIntentResult(result);
                setResult(RESULT_OK, intent);
            }
            closeActivity();
            return;
        }
        if (config.isCheckOriginalImage) {
            onResult(result);
            return;
        }
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            bothMimeTypeWith(isHasImage, result);
        } else {
            separateMimeTypeWith(isHasImage, result);
        }
    }


    /**
     * They are different types of processing
     *
     * @param isHasImage
     * @param images
     */
    private void bothMimeTypeWith(boolean isHasImage, List<LocalMedia> images) {
        LocalMedia image = images.size() > 0 ? images.get(0) : null;
        if (image == null) {
            return;
        }
        if (config.enableCrop) {
            if (config.selectionMode == PictureConfig.SINGLE && isHasImage) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath, image.getMimeType());
            } else {
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = images.size();
                int imageNum = 0;
                for (int i = 0; i < count; i++) {
                    LocalMedia media = images.get(i);
                    if (media == null
                            || TextUtils.isEmpty(media.getPath())) {
                        continue;
                    }
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        imageNum++;
                    }
                    CutInfo cutInfo = new CutInfo();
                    cutInfo.setId(media.getId());
                    cutInfo.setPath(media.getPath());
                    cutInfo.setImageWidth(media.getWidth());
                    cutInfo.setImageHeight(media.getHeight());
                    cutInfo.setMimeType(media.getMimeType());
                    cutInfo.setDuration(media.getDuration());
                    cutInfo.setRealPath(media.getRealPath());
                    cuts.add(cutInfo);
                }
                if (imageNum <= 0) {
                    onResult(images);
                } else {
                    startCrop(cuts);
                }
            }
        } else if (config.isCompress) {
            int size = images.size();
            int imageNum = 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = images.get(i);
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    imageNum++;
                    break;
                }
            }
            if (imageNum <= 0) {
                onResult(images);
            } else {
                compressImage(images);
            }
        } else {
            onResult(images);
        }
    }

    /**
     * Same type of image or video processing logic
     *
     * @param isHasImage
     * @param images
     */
    private void separateMimeTypeWith(boolean isHasImage, List<LocalMedia> images) {
        LocalMedia image = images.size() > 0 ? images.get(0) : null;
        if (image == null) {
            return;
        }
        if (config.enableCrop && isHasImage) {
            if (config.selectionMode == PictureConfig.SINGLE) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath, image.getMimeType());
            } else {
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = images.size();
                for (int i = 0; i < count; i++) {
                    LocalMedia media = images.get(i);
                    if (media == null
                            || TextUtils.isEmpty(media.getPath())) {
                        continue;
                    }
                    CutInfo cutInfo = new CutInfo();
                    cutInfo.setId(media.getId());
                    cutInfo.setPath(media.getPath());
                    cutInfo.setImageWidth(media.getWidth());
                    cutInfo.setImageHeight(media.getHeight());
                    cutInfo.setMimeType(media.getMimeType());
                    cutInfo.setDuration(media.getDuration());
                    cutInfo.setRealPath(media.getRealPath());
                    cuts.add(cutInfo);
                }
                startCrop(cuts);
            }
        } else if (config.isCompress
                && isHasImage) {
            compressImage(images);
        } else {
            onResult(images);
        }
    }

    /**
     * Play Audio
     *
     * @param path
     */
    private void AudioDialog(final String path) {
        if (!isFinishing()) {
            audioDialog = new PictureCustomDialog(getContext(), R.layout.picture_audio_dialog);
            if (audioDialog.getWindow() != null) {
                audioDialog.getWindow().setWindowAnimations(R.style.Picture_Theme_Dialog_AudioStyle);
            }
            mTvMusicStatus = audioDialog.findViewById(R.id.tv_musicStatus);
            mTvMusicTime = audioDialog.findViewById(R.id.tv_musicTime);
            musicSeekBar = audioDialog.findViewById(R.id.musicSeekBar);
            mTvMusicTotal = audioDialog.findViewById(R.id.tv_musicTotal);
            mTvPlayPause = audioDialog.findViewById(R.id.tv_PlayPause);
            mTvStop = audioDialog.findViewById(R.id.tv_Stop);
            mTvQuit = audioDialog.findViewById(R.id.tv_Quit);
            if (mHandler != null) {
                mHandler.postDelayed(() -> initPlayer(path), 30);
            }
            mTvPlayPause.setOnClickListener(new AudioOnClick(path));
            mTvStop.setOnClickListener(new AudioOnClick(path));
            mTvQuit.setOnClickListener(new AudioOnClick(path));
            musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            });
            audioDialog.setOnDismissListener(dialog -> {
                if (mHandler != null) {
                    mHandler.removeCallbacks(mRunnable);
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
            if (mHandler != null) {
                mHandler.post(mRunnable);
            }
            audioDialog.show();
        }
    }

    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    mTvMusicTime.setText(DateUtils.formatDurationTime(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    mTvMusicTotal.setText(DateUtils.formatDurationTime(mediaPlayer.getDuration()));
                    if (mHandler != null) {
                        mHandler.postDelayed(mRunnable, 200);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * init Player
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
     * Audio Click
     */
    public class AudioOnClick implements View.OnClickListener {
        private String path;

        public AudioOnClick(String path) {
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
                if (mHandler != null) {
                    mHandler.postDelayed(() -> stop(path), 30);
                    try {
                        if (audioDialog != null
                                && audioDialog.isShowing()) {
                            audioDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.removeCallbacks(mRunnable);
                }
            }
        }
    }

    /**
     * Play Audio
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
        if (!isPlayAudio) {
            if (mHandler != null) {
                mHandler.post(mRunnable);
            }
            isPlayAudio = true;
        }
    }

    /**
     * Audio Stop
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
     * Audio Pause
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
    public void onItemClick(int position, boolean isCameraFolder, long bucketId, String
            folderName, List<LocalMedia> data) {
        boolean camera = config.isCamera && isCameraFolder;
        mAdapter.setShowCamera(camera);
        mTvPictureTitle.setText(folderName);
        long currentBucketId = ValueOf.toLong(mTvPictureTitle.getTag(R.id.view_tag));
        mTvPictureTitle.setTag(R.id.view_count_tag, folderWindow.getFolder(position) != null
                ? folderWindow.getFolder(position).getImageNum() : 0);
        if (config.isPageStrategy) {
            if (currentBucketId != bucketId) {
                setLastCacheFolderData();
                boolean isCurrentCacheFolderData = isCurrentCacheFolderData(position);
                if (!isCurrentCacheFolderData) {
                    mPage = 1;
                    showPleaseDialog();
                    LocalMediaPageLoader.getInstance(getContext(), config).loadPageMediaData(bucketId, mPage,
                            (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                                this.isHasMore = isHasMore;
                                if (!isFinishing()) {
                                    if (result.size() == 0) {
                                        mAdapter.clear();
                                    }
                                    mAdapter.bindData(result);
                                    mRecyclerView.onScrolled(0, 0);
                                    mRecyclerView.smoothScrollToPosition(0);
                                    dismissDialog();
                                }
                            });
                }
            }
        } else {
            mAdapter.bindData(data);
            mRecyclerView.smoothScrollToPosition(0);
        }
        mTvPictureTitle.setTag(R.id.view_tag, bucketId);
        folderWindow.dismiss();
    }

    /**
     * Before switching directories, set the current directory cache
     */
    private void setLastCacheFolderData() {
        int oldPosition = ValueOf.toInt(mTvPictureTitle.getTag(R.id.view_index_tag));
        LocalMediaFolder lastFolder = folderWindow.getFolder(oldPosition);
        lastFolder.setData(mAdapter.getData());
        lastFolder.setCurrentDataPage(mPage);
        lastFolder.setHasMore(isHasMore);
    }

    /**
     * Does the current album have a cache
     *
     * @param position
     */
    private boolean isCurrentCacheFolderData(int position) {
        mTvPictureTitle.setTag(R.id.view_index_tag, position);
        LocalMediaFolder currentFolder = folderWindow.getFolder(position);
        if (currentFolder != null
                && currentFolder.getData() != null
                && currentFolder.getData().size() > 0) {
            mAdapter.bindData(currentFolder.getData());
            mPage = currentFolder.getCurrentDataPage();
            isHasMore = currentFolder.isHasMore();
            mRecyclerView.smoothScrollToPosition(0);

            return true;
        }
        return false;
    }

    @Override
    public void onTakePhoto() {
        // Check the permissions
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            if (PermissionChecker
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    PermissionChecker
                            .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startCamera();
            } else {
                PermissionChecker.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_CAMERA_STORAGE_PERMISSIONS_CODE);
            }
        } else {
            PermissionChecker
                    .requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onChange(List<LocalMedia> selectData) {
        changeImageNumber(selectData);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        if (config.selectionMode == PictureConfig.SINGLE && config.isSingleDirectReturn) {
            List<LocalMedia> list = new ArrayList<>();
            list.add(media);
            if (config.enableCrop && PictureMimeType.isHasImage(media.getMimeType()) && !config.isCheckOriginalImage) {
                mAdapter.bindSelectData(list);
                startCrop(media.getPath(), media.getMimeType());
            } else {
                handlerResult(list);
            }
        } else {
            List<LocalMedia> data = mAdapter.getData();
            startPreview(data, position);
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
        if (PictureMimeType.isHasVideo(mimeType)) {
            // video
            if (config.selectionMode == PictureConfig.SINGLE && !config.enPreviewVideo) {
                result.add(media);
                onResult(result);
            } else {
                if (PictureSelectionConfig.customVideoPlayCallback != null) {
                    PictureSelectionConfig.customVideoPlayCallback.startPlayVideo(media);
                } else {
                    bundle.putParcelable(PictureConfig.EXTRA_MEDIA_KEY, media);
                    JumpUtils.startPictureVideoPlayActivity(getContext(), bundle, PictureConfig.PREVIEW_VIDEO_CODE);
                }
            }
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            // audio
            if (config.selectionMode == PictureConfig.SINGLE) {
                result.add(media);
                onResult(result);
            } else {
                AudioDialog(media.getPath());
            }
        } else {
            // image
            List<LocalMedia> selectedData = mAdapter.getSelectedData();
            ImagesObservable.getInstance().savePreviewMediaData(new ArrayList<>(previewImages));
            bundle.putParcelableArrayList(PictureConfig.EXTRA_SELECT_LIST, (ArrayList<? extends Parcelable>) selectedData);
            bundle.putInt(PictureConfig.EXTRA_POSITION, position);
            bundle.putBoolean(PictureConfig.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
            bundle.putBoolean(PictureConfig.EXTRA_SHOW_CAMERA, mAdapter.isShowCamera());
            bundle.putLong(PictureConfig.EXTRA_BUCKET_ID, ValueOf.toLong(mTvPictureTitle.getTag(R.id.view_tag)));
            bundle.putInt(PictureConfig.EXTRA_PAGE, mPage);
            bundle.putParcelable(PictureConfig.EXTRA_CONFIG, config);
            bundle.putInt(PictureConfig.EXTRA_DATA_COUNT, ValueOf.toInt(mTvPictureTitle.getTag(R.id.view_count_tag)));
            bundle.putString(PictureConfig.EXTRA_IS_CURRENT_DIRECTORY, mTvPictureTitle.getText().toString());
            JumpUtils.startPicturePreviewActivity(getContext(), config.isWeChatStyle, bundle,
                    config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCrop.REQUEST_MULTI_CROP);
            overridePendingTransition(config.windowAnimationStyle != null
                    && config.windowAnimationStyle.activityPreviewEnterAnimation != 0
                    ? config.windowAnimationStyle.activityPreviewEnterAnimation : R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
        }
    }


    /**
     * change image selector state
     *
     * @param selectData
     */
    protected void changeImageNumber(List<LocalMedia> selectData) {
        boolean enable = selectData.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            if (config.style != null) {
                if (config.style.pictureCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(config.style.pictureCompleteTextColor);
                }
                if (config.style.picturePreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.picturePreviewTextColor);
                }
            }
            if (config.style != null && !TextUtils.isEmpty(config.style.picturePreviewText)) {
                mTvPicturePreview.setText(config.style.picturePreviewText);
            } else {
                mTvPicturePreview.setText(getString(R.string.picture_preview_num, selectData.size()));
            }
            if (numComplete) {
                initCompleteText(selectData.size());
            } else {
                if (!isStartAnimation) {
                    mTvPictureImgNum.startAnimation(animation);
                }
                mTvPictureImgNum.setVisibility(View.VISIBLE);
                mTvPictureImgNum.setText(String.valueOf(selectData.size()));
                if (config.style != null && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mTvPictureOk.setText(config.style.pictureCompleteText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_completed));
                }
                isStartAnimation = false;
            }
        } else {
            mTvPictureOk.setEnabled(config.returnEmpty);
            mTvPictureOk.setSelected(false);
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (config.style != null) {
                if (config.style.pictureUnCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
                }
                if (config.style.pictureUnPreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
                }
            }
            if (config.style != null && !TextUtils.isEmpty(config.style.pictureUnPreviewText)) {
                mTvPicturePreview.setText(config.style.pictureUnPreviewText);
            } else {
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            }
            if (numComplete) {
                initCompleteText(selectData.size());
            } else {
                mTvPictureImgNum.setVisibility(View.INVISIBLE);
                if (config.style != null && !TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                    mTvPictureOk.setText(config.style.pictureUnCompleteText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_please_select));
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.PREVIEW_VIDEO_CODE:
                    if (data != null) {
                        List<LocalMedia> list = data.getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
                        if (list != null && list.size() > 0) {
                            onResult(list);
                        }
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    singleCropHandleResult(data);
                    break;
                case UCrop.REQUEST_MULTI_CROP:
                    multiCropHandleResult(data);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    dispatchHandleCamera(data);
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            previewCallback(data);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            if (data != null) {
                Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
                if (throwable != null) {
                    ToastUtils.s(getContext(), throwable.getMessage());
                }
            }
        }
    }

    /**
     * Preview interface callback processing
     *
     * @param data
     */
    private void previewCallback(Intent data) {
        if (data == null) {
            return;
        }
        if (config.isOriginalControl) {
            config.isCheckOriginalImage = data.getBooleanExtra(PictureConfig.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
            mCbOriginal.setChecked(config.isCheckOriginalImage);
        }
        List<LocalMedia> list = data.getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
        if (mAdapter != null && list != null) {
            boolean isCompleteOrSelected = data.getBooleanExtra(PictureConfig.EXTRA_COMPLETE_SELECTED, false);
            if (isCompleteOrSelected) {
                onChangeData(list);
                if (config.isWithVideoImage) {
                    int size = list.size();
                    int imageSize = 0;
                    for (int i = 0; i < size; i++) {
                        LocalMedia media = list.get(i);
                        if (PictureMimeType.isHasImage(media.getMimeType())) {
                            imageSize++;
                            break;
                        }
                    }
                    if (imageSize <= 0 || !config.isCompress || config.isCheckOriginalImage) {
                        onResult(list);
                    } else {
                        compressImage(list);
                    }
                } else {
                    // Determine if the resource is of the same type
                    String mimeType = list.size() > 0 ? list.get(0).getMimeType() : "";
                    if (config.isCompress && PictureMimeType.isHasImage(mimeType)
                            && !config.isCheckOriginalImage) {
                        compressImage(list);
                    } else {
                        onResult(list);
                    }
                }
            } else {
                // Resources are selected on the preview page
                isStartAnimation = true;
            }
            mAdapter.bindSelectData(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Preview the callback
     *
     * @param list
     */
    protected void onChangeData(List<LocalMedia> list) {

    }

    /**
     * singleDirectReturn
     *
     * @param mimeType
     */
    private void singleDirectReturnCameraHandleResult(String mimeType) {
        boolean isHasImage = PictureMimeType.isHasImage(mimeType);
        if (config.enableCrop && isHasImage) {
            config.originalPath = config.cameraPath;
            startCrop(config.cameraPath, mimeType);
        } else if (config.isCompress && isHasImage) {
            List<LocalMedia> selectedImages = mAdapter.getSelectedData();
            compressImage(selectedImages);
        } else {
            onResult(mAdapter.getSelectedData());
        }
    }

    /**
     * Camera Handle
     *
     * @param intent
     */
    private void dispatchHandleCamera(Intent intent) {
        // If PictureSelectionConfig is not empty, synchronize it
        PictureSelectionConfig selectionConfig = intent != null ? intent.getParcelableExtra(PictureConfig.EXTRA_CONFIG) : null;
        if (selectionConfig != null) {
            config = selectionConfig;
        }
        boolean isAudio = config.chooseMode == PictureMimeType.ofAudio();
        config.cameraPath = isAudio ? getAudioPath(intent) : config.cameraPath;
        if (TextUtils.isEmpty(config.cameraPath)) {
            return;
        }
        showPleaseDialog();
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<LocalMedia>() {

            @Override
            public LocalMedia doInBackground() {
                LocalMedia media = new LocalMedia();
                String mimeType = isAudio ? PictureMimeType.MIME_TYPE_AUDIO : "";
                int[] newSize = new int[2];
                long duration = 0;
                if (!isAudio) {
                    if (PictureMimeType.isContent(config.cameraPath)) {
                        // content: Processing rules
                        String path = PictureFileUtils.getPath(getContext(), Uri.parse(config.cameraPath));
                        if (!TextUtils.isEmpty(path)) {
                            File cameraFile = new File(path);
                            mimeType = PictureMimeType.getMimeType(config.cameraMimeType);
                            media.setSize(cameraFile.length());
                        }
                        if (PictureMimeType.isHasImage(mimeType)) {
                            newSize = MediaUtils.getImageSizeForUrlToAndroidQ(getContext(), config.cameraPath);
                        } else if (PictureMimeType.isHasVideo(mimeType)) {
                            newSize = MediaUtils.getVideoSizeForUri(getContext(), Uri.parse(config.cameraPath));
                            duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), config.cameraPath);
                        }
                        int lastIndexOf = config.cameraPath.lastIndexOf("/") + 1;
                        media.setId(lastIndexOf > 0 ? ValueOf.toLong(config.cameraPath.substring(lastIndexOf)) : -1);
                        media.setRealPath(path);
                        // Custom photo has been in the application sandbox into the file
                        String mediaPath = intent != null ? intent.getStringExtra(PictureConfig.EXTRA_MEDIA_PATH) : null;
                        media.setAndroidQToPath(mediaPath);
                    } else {
                        File cameraFile = new File(config.cameraPath);
                        mimeType = PictureMimeType.getMimeType(config.cameraMimeType);
                        media.setSize(cameraFile.length());
                        if (PictureMimeType.isHasImage(mimeType)) {
                            int degree = PictureFileUtils.readPictureDegree(getContext(), config.cameraPath);
                            BitmapUtils.rotateImage(degree, config.cameraPath);
                            newSize = MediaUtils.getImageSizeForUrl(config.cameraPath);
                        } else if (PictureMimeType.isHasVideo(mimeType)) {
                            newSize = MediaUtils.getVideoSizeForUrl(config.cameraPath);
                            duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), config.cameraPath);
                        }
                        // Taking a photo generates a temporary id
                        media.setId(System.currentTimeMillis());
                    }
                    media.setPath(config.cameraPath);
                    media.setDuration(duration);
                    media.setMimeType(mimeType);
                    media.setWidth(newSize[0]);
                    media.setHeight(newSize[1]);
                    if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isHasVideo(media.getMimeType())) {
                        media.setParentFolderName(Environment.DIRECTORY_MOVIES);
                    } else {
                        media.setParentFolderName(PictureMimeType.CAMERA);
                    }
                    media.setChooseModel(config.chooseMode);
                    long bucketId = MediaUtils.getCameraFirstBucketId(getContext());
                    media.setBucketId(bucketId);
                    // The width and height of the image are reversed if there is rotation information
                    MediaUtils.setOrientationSynchronous(getContext(), media, config.isAndroidQChangeWH,config.isAndroidQChangeVideoWH);
                }
                return media;
            }

            @Override
            public void onSuccess(LocalMedia result) {
                dismissDialog();
                // Refresh the system library
                if (!SdkVersionUtils.checkedAndroid_Q()) {
                    if (config.isFallbackVersion3) {
                        new PictureMediaScannerConnection(getContext(), config.cameraPath);
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(config.cameraPath))));
                    }
                }
                // add data Adapter
                notifyAdapterData(result);
                // Solve some phone using Camera, DCIM will produce repetitive problems
                if (!SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isHasImage(result.getMimeType())) {
                    int lastImageId = MediaUtils.getDCIMLastImageId(getContext());
                    if (lastImageId != -1) {
                        MediaUtils.removeMedia(getContext(), lastImageId);
                    }
                }
            }
        });

    }

    /**
     * Update Adapter Data
     *
     * @param media
     */
    private void notifyAdapterData(LocalMedia media) {
        if (mAdapter != null) {
            boolean isAddSameImp = isAddSameImp(folderWindow.getFolder(0) != null ? folderWindow.getFolder(0).getImageNum() : 0);
            if (!isAddSameImp) {
                mAdapter.getData().add(0, media);
                mOpenCameraCount++;
            }
            if (checkVideoLegitimacy(media)) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    dispatchHandleSingle(media);
                } else {
                    dispatchHandleMultiple(media);
                }
            }
            mAdapter.notifyItemInserted(config.isCamera ? 1 : 0);
            mAdapter.notifyItemRangeChanged(config.isCamera ? 1 : 0, mAdapter.getSize());
            // Solve the problem that some mobile phones do not refresh the system library timely after using Camera
            if (config.isPageStrategy) {
                manualSaveFolderForPageModel(media);
            } else {
                manualSaveFolder(media);
            }
            mTvEmpty.setVisibility(mAdapter.getSize() > 0 || config.isSingleDirectReturn ? View.GONE : View.VISIBLE);
            // update all count
            if (folderWindow.getFolder(0) != null) {
                mTvPictureTitle.setTag(R.id.view_count_tag, folderWindow.getFolder(0).getImageNum());
            }
            allFolderSize = 0;
        }
    }

    /**
     * After using Camera, MultiSelect mode handles the logic
     *
     * @param media
     */
    private void dispatchHandleMultiple(LocalMedia media) {
        List<LocalMedia> selectedData = mAdapter.getSelectedData();
        int count = selectedData.size();
        String oldMimeType = count > 0 ? selectedData.get(0).getMimeType() : "";
        boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(oldMimeType, media.getMimeType());
        if (config.isWithVideoImage) {
            int videoSize = 0;
            for (int i = 0; i < count; i++) {
                LocalMedia item = selectedData.get(i);
                if (PictureMimeType.isHasVideo(item.getMimeType())) {
                    videoSize++;
                }
            }
            if (PictureMimeType.isHasVideo(media.getMimeType())) {
                if (config.maxVideoSelectNum <= 0) {
                    showPromptDialog(getString(R.string.picture_rule));
                } else {
                    if (selectedData.size() >= config.maxSelectNum) {
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                    } else {
                        if (videoSize < config.maxVideoSelectNum) {
                            selectedData.add(0, media);
                            mAdapter.bindSelectData(selectedData);
                        } else {
                            showPromptDialog(StringUtils.getMsg(getContext(), media.getMimeType(),
                                    config.maxVideoSelectNum));
                        }
                    }
                }
            } else {
                if (selectedData.size() < config.maxSelectNum) {
                    selectedData.add(0, media);
                    mAdapter.bindSelectData(selectedData);
                } else {
                    showPromptDialog(StringUtils.getMsg(getContext(), media.getMimeType(),
                            config.maxSelectNum));
                }
            }

        } else {
            if (PictureMimeType.isHasVideo(oldMimeType) && config.maxVideoSelectNum > 0) {
                if (count < config.maxVideoSelectNum) {
                    if (mimeTypeSame || count == 0) {
                        if (selectedData.size() < config.maxVideoSelectNum) {
                            selectedData.add(0, media);
                            mAdapter.bindSelectData(selectedData);
                        }
                    }
                } else {
                    showPromptDialog(StringUtils.getMsg(getContext(), oldMimeType,
                            config.maxVideoSelectNum));
                }
            } else {
                if (count < config.maxSelectNum) {
                    if (mimeTypeSame || count == 0) {
                        selectedData.add(0, media);
                        mAdapter.bindSelectData(selectedData);
                    }
                } else {
                    showPromptDialog(StringUtils.getMsg(getContext(), oldMimeType,
                            config.maxSelectNum));
                }
            }
        }
    }

    /**
     * After using the camera, the radio mode handles the logic
     *
     * @param media
     */
    private void dispatchHandleSingle(LocalMedia media) {
        if (config.isSingleDirectReturn) {
            List<LocalMedia> selectedData = mAdapter.getSelectedData();
            selectedData.add(media);
            mAdapter.bindSelectData(selectedData);
            singleDirectReturnCameraHandleResult(media.getMimeType());
        } else {
            List<LocalMedia> selectedData = mAdapter.getSelectedData();
            String mimeType = selectedData.size() > 0 ? selectedData.get(0).getMimeType() : "";
            boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, media.getMimeType());
            if (mimeTypeSame || selectedData.size() == 0) {
                singleRadioMediaImage();
                selectedData.add(media);
                mAdapter.bindSelectData(selectedData);
            }
        }
    }

    /**
     * Verify the validity of the video
     *
     * @param media
     * @return
     */
    private boolean checkVideoLegitimacy(LocalMedia media) {
        boolean isEnterNext = true;
        if (PictureMimeType.isHasVideo(media.getMimeType())) {
            if (config.videoMinSecond > 0 && config.videoMaxSecond > 0) {
                // The user sets the minimum and maximum video length to determine whether the video is within the interval
                if (media.getDuration() < config.videoMinSecond || media.getDuration() > config.videoMaxSecond) {
                    isEnterNext = false;
                    showPromptDialog(getString(R.string.picture_choose_limit_seconds, config.videoMinSecond / 1000, config.videoMaxSecond / 1000));
                }
            } else if (config.videoMinSecond > 0) {
                // The user has only set a minimum video length limit
                if (media.getDuration() < config.videoMinSecond) {
                    isEnterNext = false;
                    showPromptDialog(getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                }
            } else if (config.videoMaxSecond > 0) {
                // Only the maximum length of video is set
                if (media.getDuration() > config.videoMaxSecond) {
                    isEnterNext = false;
                    showPromptDialog(getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                }
            }
        }
        return isEnterNext;
    }

    /**
     * Single picture clipping callback
     *
     * @param data
     */
    private void singleCropHandleResult(Intent data) {
        if (data == null) {
            return;
        }
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri == null) {
            return;
        }
        List<LocalMedia> result = new ArrayList<>();
        String cutPath = resultUri.getPath();
        if (mAdapter != null) {
            List<LocalMedia> list = data.getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
            if (list != null) {
                mAdapter.bindSelectData(list);
                mAdapter.notifyDataSetChanged();
            }
            List<LocalMedia> mediaList = mAdapter.getSelectedData();
            LocalMedia media = mediaList != null && mediaList.size() > 0 ? mediaList.get(0) : null;
            if (media != null) {
                config.originalPath = media.getPath();
                media.setCutPath(cutPath);
                media.setChooseModel(config.chooseMode);
                boolean isCutPathEmpty = !TextUtils.isEmpty(cutPath);
                if (SdkVersionUtils.checkedAndroid_Q()
                        && PictureMimeType.isContent(media.getPath())) {
                    if (isCutPathEmpty) {
                        media.setSize(new File(cutPath).length());
                    } else {
                        media.setSize(!TextUtils.isEmpty(media.getRealPath()) ? new File(media.getRealPath()).length() : 0);
                    }
                    media.setAndroidQToPath(cutPath);
                } else {
                    media.setSize(isCutPathEmpty ? new File(cutPath).length() : 0);
                }
                media.setCut(isCutPathEmpty);
                result.add(media);
                handlerResult(result);
            } else {
                // Preview screen selects the image and crop the callback
                media = list != null && list.size() > 0 ? list.get(0) : null;
                if (media != null) {
                    config.originalPath = media.getPath();
                    media.setCutPath(cutPath);
                    media.setChooseModel(config.chooseMode);
                    boolean isCutPathEmpty = !TextUtils.isEmpty(cutPath);
                    if (SdkVersionUtils.checkedAndroid_Q()
                            && PictureMimeType.isContent(media.getPath())) {
                        if (isCutPathEmpty) {
                            media.setSize(new File(cutPath).length());
                        } else {
                            media.setSize(!TextUtils.isEmpty(media.getRealPath()) ? new File(media.getRealPath()).length() : 0);
                        }
                        media.setAndroidQToPath(cutPath);
                    } else {
                        media.setSize(isCutPathEmpty ? new File(cutPath).length() : 0);
                    }
                    media.setCut(isCutPathEmpty);
                    result.add(media);
                    handlerResult(result);
                }
            }
        }
    }

    /**
     * Multiple picture crop
     *
     * @param data
     */
    protected void multiCropHandleResult(Intent data) {
        if (data == null) {
            return;
        }
        List<CutInfo> mCuts = UCrop.getMultipleOutput(data);
        if (mCuts == null || mCuts.size() == 0) {
            return;
        }
        int size = mCuts.size();
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        List<LocalMedia> list = data.getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
        if (list != null) {
            mAdapter.bindSelectData(list);
            mAdapter.notifyDataSetChanged();
        }
        int oldSize = mAdapter != null ? mAdapter.getSelectedData().size() : 0;
        if (oldSize == size) {
            List<LocalMedia> result = mAdapter.getSelectedData();
            for (int i = 0; i < size; i++) {
                CutInfo c = mCuts.get(i);
                LocalMedia media = result.get(i);
                media.setCut(!TextUtils.isEmpty(c.getCutPath()));
                media.setPath(c.getPath());
                media.setMimeType(c.getMimeType());
                media.setCutPath(c.getCutPath());
                media.setWidth(c.getImageWidth());
                media.setHeight(c.getImageHeight());
                media.setAndroidQToPath(isAndroidQ ? c.getCutPath() : media.getAndroidQToPath());
                media.setSize(!TextUtils.isEmpty(c.getCutPath()) ? new File(c.getCutPath()).length() : media.getSize());
            }
            handlerResult(result);
        } else {
            // Fault-tolerant processing
            List<LocalMedia> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                CutInfo c = mCuts.get(i);
                LocalMedia media = new LocalMedia();
                media.setId(c.getId());
                media.setCut(!TextUtils.isEmpty(c.getCutPath()));
                media.setPath(c.getPath());
                media.setCutPath(c.getCutPath());
                media.setMimeType(c.getMimeType());
                media.setWidth(c.getImageWidth());
                media.setHeight(c.getImageHeight());
                media.setDuration(c.getDuration());
                media.setChooseModel(config.chooseMode);
                media.setAndroidQToPath(isAndroidQ ? c.getCutPath() : c.getAndroidQToPath());
                if (!TextUtils.isEmpty(c.getCutPath())) {
                    media.setSize(new File(c.getCutPath()).length());
                } else {
                    if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(c.getPath())) {
                        media.setSize(!TextUtils.isEmpty(c.getRealPath()) ? new File(c.getRealPath()).length() : 0);
                    } else {
                        media.setSize(new File(c.getPath()).length());
                    }
                }
                result.add(media);
            }
            handlerResult(result);
        }
    }

    /**
     * Just make sure you pick one
     */
    private void singleRadioMediaImage() {
        List<LocalMedia> selectData = mAdapter.getSelectedData();
        if (selectData != null
                && selectData.size() > 0) {
            LocalMedia media = selectData.get(0);
            int position = media.getPosition();
            selectData.clear();
            mAdapter.notifyItemChanged(position);
        }
    }

    /**
     * Manually add the photo to the list of photos and set it to select-paging mode
     *
     * @param media
     */
    private void manualSaveFolderForPageModel(LocalMedia media) {
        if (media == null) {
            return;
        }
        int count = folderWindow.getFolderData().size();
        LocalMediaFolder allFolder = count > 0 ? folderWindow.getFolderData().get(0) : new LocalMediaFolder();
        if (allFolder != null) {
            int totalNum = allFolder.getImageNum();
            allFolder.setFirstImagePath(media.getPath());
            allFolder.setImageNum(isAddSameImp(totalNum) ? allFolder.getImageNum() : allFolder.getImageNum() + 1);
            // Create All folder
            if (count == 0) {
                allFolder.setName(config.chooseMode == PictureMimeType.ofAudio() ?
                        getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll));
                allFolder.setOfAllType(config.chooseMode);
                allFolder.setCameraFolder(true);
                allFolder.setChecked(true);
                allFolder.setBucketId(-1);
                folderWindow.getFolderData().add(0, allFolder);
                // Create Camera
                LocalMediaFolder cameraFolder = new LocalMediaFolder();
                cameraFolder.setName(media.getParentFolderName());
                cameraFolder.setImageNum(isAddSameImp(totalNum) ? cameraFolder.getImageNum() : cameraFolder.getImageNum() + 1);
                cameraFolder.setFirstImagePath(media.getPath());
                cameraFolder.setBucketId(media.getBucketId());
                folderWindow.getFolderData().add(folderWindow.getFolderData().size(), cameraFolder);
            } else {
                boolean isCamera = false;
                String newFolder = SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isHasVideo(media.getMimeType())
                        ? Environment.DIRECTORY_MOVIES : PictureMimeType.CAMERA;
                for (int i = 0; i < count; i++) {
                    LocalMediaFolder cameraFolder = folderWindow.getFolderData().get(i);
                    if (cameraFolder.getName().startsWith(newFolder)) {
                        media.setBucketId(cameraFolder.getBucketId());
                        cameraFolder.setFirstImagePath(config.cameraPath);
                        cameraFolder.setImageNum(isAddSameImp(totalNum) ? cameraFolder.getImageNum() : cameraFolder.getImageNum() + 1);
                        if (cameraFolder.getData() != null && cameraFolder.getData().size() > 0) {
                            cameraFolder.getData().add(0, media);
                        }
                        isCamera = true;
                        break;
                    }
                }
                if (!isCamera) {
                    // There is no Camera folder locally. Create one
                    LocalMediaFolder cameraFolder = new LocalMediaFolder();
                    cameraFolder.setName(media.getParentFolderName());
                    cameraFolder.setImageNum(isAddSameImp(totalNum) ? cameraFolder.getImageNum() : cameraFolder.getImageNum() + 1);
                    cameraFolder.setFirstImagePath(media.getPath());
                    cameraFolder.setBucketId(media.getBucketId());
                    folderWindow.getFolderData().add(cameraFolder);
                    sortFolder(folderWindow.getFolderData());
                }
            }
            folderWindow.bindFolder(folderWindow.getFolderData());
        }
    }

    /**
     * Manually add the photo to the list of photos and set it to select
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            boolean isEmpty = folderWindow.isEmpty();
            int totalNum = folderWindow.getFolder(0) != null ? folderWindow.getFolder(0).getImageNum() : 0;
            LocalMediaFolder allFolder;
            if (isEmpty) {
                // All Folder
                createNewFolder(folderWindow.getFolderData());
                allFolder = folderWindow.getFolderData().size() > 0 ? folderWindow.getFolderData().get(0) : null;
                if (allFolder == null) {
                    allFolder = new LocalMediaFolder();
                    folderWindow.getFolderData().add(0, allFolder);
                }
            } else {
                // All Folder
                allFolder = folderWindow.getFolderData().get(0);
            }
            allFolder.setFirstImagePath(media.getPath());
            allFolder.setData(mAdapter.getData());
            allFolder.setBucketId(-1);
            allFolder.setImageNum(isAddSameImp(totalNum) ? allFolder.getImageNum() : allFolder.getImageNum() + 1);

            // Camera
            LocalMediaFolder cameraFolder = getImageFolder(media.getPath(), media.getRealPath(), folderWindow.getFolderData());
            if (cameraFolder != null) {
                cameraFolder.setImageNum(isAddSameImp(totalNum) ? cameraFolder.getImageNum() : cameraFolder.getImageNum() + 1);
                if (!isAddSameImp(totalNum)) {
                    cameraFolder.getData().add(0, media);
                }
                cameraFolder.setBucketId(media.getBucketId());
                cameraFolder.setFirstImagePath(config.cameraPath);
            }
            folderWindow.bindFolder(folderWindow.getFolderData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Is the quantity consistent
     *
     * @return
     */
    private boolean isAddSameImp(int totalNum) {
        if (totalNum == 0) {
            return false;
        }
        return allFolderSize > 0 && allFolderSize < totalNum;
    }

    /**
     * Update Folder
     *
     * @param imageFolders
     */
    private void updateMediaFolder(List<LocalMediaFolder> imageFolders, LocalMedia media) {
        File imageFile = new File(media.getRealPath());
        File folderFile = imageFile.getParentFile();
        if (folderFile == null) {
            return;
        }
        int size = imageFolders.size();
        for (int i = 0; i < size; i++) {
            LocalMediaFolder folder = imageFolders.get(i);
            String name = folder.getName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            if (name.equals(folderFile.getName())) {
                folder.setFirstImagePath(config.cameraPath);
                folder.setImageNum(folder.getImageNum() + 1);
                folder.setCheckedNum(1);
                folder.getData().add(0, media);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (config != null && PictureSelectionConfig.listener != null) {
            PictureSelectionConfig.listener.onCancel();
        }
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (mediaPlayer != null && mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case PhotoItemSelectedDialog.IMAGE_CAMERA:
                if (PictureSelectionConfig.onCustomCameraInterfaceListener != null) {
                    PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_IMAGE);
                    config.cameraMimeType = PictureMimeType.ofImage();
                } else {
                    startOpenCamera();
                }
                break;
            case PhotoItemSelectedDialog.VIDEO_CAMERA:
                if (PictureSelectionConfig.onCustomCameraInterfaceListener != null) {
                    PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_IMAGE);
                    config.cameraMimeType = PictureMimeType.ofVideo();
                } else {
                    startOpenCameraVideo();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // Store Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readLocalMedia();
                } else {
                    showPermissionsDialog(false, getString(R.string.picture_jurisdiction));
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // Camera Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    showPermissionsDialog(true, getString(R.string.picture_camera));
                }
                break;
            case PictureConfig.APPLY_CAMERA_STORAGE_PERMISSIONS_CODE:
                // Using the camera, retrieve the storage permission
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    showPermissionsDialog(false, getString(R.string.picture_jurisdiction));
                }
                break;
            case PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE:
                // Recording Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCustomCamera();
                } else {
                    showPermissionsDialog(false, getString(R.string.picture_audio));
                }
                break;
        }
    }

    @Override
    protected void showPermissionsDialog(boolean isCamera, String errorMsg) {
        if (isFinishing()) {
            return;
        }
        final PictureCustomDialog dialog =
                new PictureCustomDialog(getContext(), R.layout.picture_wind_base_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_commit = dialog.findViewById(R.id.btn_commit);
        btn_commit.setText(getString(R.string.picture_go_setting));
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        tvTitle.setText(getString(R.string.picture_prompt));
        tv_content.setText(errorMsg);
        btn_cancel.setOnClickListener(v -> {
            if (!isFinishing()) {
                dialog.dismiss();
            }
            if (!isCamera) {
                closeActivity();
            }
        });
        btn_commit.setOnClickListener(v -> {
            if (!isFinishing()) {
                dialog.dismiss();
            }
            PermissionChecker.launchAppDetailsSettings(getContext());
            isEnterSetting = true;
        });
        dialog.show();
    }


    /**
     * set Data Null
     *
     * @param msg
     */
    private void showDataNull(String msg, int topErrorResId) {
        if (mTvEmpty.getVisibility() == View.GONE || mTvEmpty.getVisibility() == View.INVISIBLE) {
            mTvEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, topErrorResId, 0, 0);
            mTvEmpty.setText(msg);
            mTvEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hidden
     */
    private void hideDataNull() {
        if (mTvEmpty.getVisibility() == View.VISIBLE) {
            mTvEmpty.setVisibility(View.GONE);
        }
    }
}
