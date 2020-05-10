package com.luck.picture.lib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.luck.picture.lib.adapter.PictureSimpleFragmentAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnQueryDataResultListener;
import com.luck.picture.lib.model.LocalMediaPageLoader;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.tools.VoiceUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.CutInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2016/1/29 下午21:50
 * @描述:图片预览
 */
public class PicturePreviewActivity extends PictureBaseActivity implements
        View.OnClickListener, PictureSimpleFragmentAdapter.OnCallBackActivity {
    private static final String TAG = PicturePreviewActivity.class.getSimpleName();
    protected ImageView pictureLeftBack;
    protected TextView tvMediaNum, tvTitle, mTvPictureOk;
    protected PreviewViewPager viewPager;
    protected int position;
    protected boolean isBottomPreview;
    private int totalNumber;
    protected List<LocalMedia> selectData = new ArrayList<>();
    protected PictureSimpleFragmentAdapter adapter;
    protected Animation animation;
    protected TextView check;
    protected View btnCheck;
    protected boolean refresh;
    protected int index;
    protected int screenWidth;
    protected Handler mHandler;
    protected RelativeLayout selectBarLayout;
    protected CheckBox mCbOriginal;
    protected View titleViewBg;
    protected boolean isShowCamera;
    protected String currentDirectory;
    /**
     * 是否已完成选择
     */
    protected boolean isCompleteOrSelected;
    /**
     * 是否改变已选的数据
     */
    protected boolean isChangeSelectedData;

    /**
     * 分页码
     */
    private int mPage = 0;


    @Override
    public int getResourceId() {
        return R.layout.picture_preview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // 防止内存不足时activity被回收，导致图片未选中
            selectData = PictureSelector.obtainSelectorList(savedInstanceState);
            isCompleteOrSelected = savedInstanceState.getBoolean(PictureConfig.EXTRA_COMPLETE_SELECTED, false);
            isChangeSelectedData = savedInstanceState.getBoolean(PictureConfig.EXTRA_CHANGE_SELECTED_DATA, false);
            onImageChecked(position);
            onSelectNumChange(false);
        }
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mHandler = new Handler();
        titleViewBg = findViewById(R.id.titleViewBg);
        screenWidth = ScreenUtils.getScreenWidth(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.picture_anim_modal_in);
        pictureLeftBack = findViewById(R.id.pictureLeftBack);
        viewPager = findViewById(R.id.preview_pager);
        btnCheck = findViewById(R.id.btnCheck);
        check = findViewById(R.id.check);
        pictureLeftBack.setOnClickListener(this);
        mTvPictureOk = findViewById(R.id.tv_ok);
        mCbOriginal = findViewById(R.id.cb_original);
        tvMediaNum = findViewById(R.id.tvMediaNum);
        selectBarLayout = findViewById(R.id.select_bar_layout);
        mTvPictureOk.setOnClickListener(this);
        tvMediaNum.setOnClickListener(this);
        tvTitle = findViewById(R.id.picture_title);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        if (numComplete) {
            initCompleteText(0);
        }
        tvMediaNum.setSelected(config.checkNumMode);
        btnCheck.setOnClickListener(this);
        selectData = getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
        isBottomPreview = getIntent().
                getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        isShowCamera = getIntent().getBooleanExtra(PictureConfig.EXTRA_SHOW_CAMERA, config.isCamera);
        // 当前目录
        currentDirectory = getIntent().getStringExtra(PictureConfig.EXTRA_IS_CURRENT_DIRECTORY);
        List<LocalMedia> data;
        if (isBottomPreview) {
            // 底部预览模式
            data = getIntent().
                    getParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
            initViewPageAdapterData(data);
        } else {
            data = ImagesObservable.getInstance().readPreviewMediaData();
            boolean isEmpty = data.size() == 0;
            totalNumber = getIntent().getIntExtra(PictureConfig.EXTRA_DATA_COUNT, 0);
            if (config.isPageStrategy) {
                // 分页模式
                if (isEmpty) {
                    // 这种情况有可能是单例被回收了导致readPreviewMediaData();返回的数据为0，那就从第一页开始加载吧
                    setNewTitle();
                } else {
                    mPage = getIntent().getIntExtra(PictureConfig.EXTRA_PAGE, 0);
                }
                initViewPageAdapterData(data);
                loadData();
                setTitle();
            } else {
                // 普通模式
                initViewPageAdapterData(data);
                if (isEmpty) {
                    // 这种情况有可能是单例被回收了导致readPreviewMediaData();返回的数据为0，暂时自动切换成分页模式去获取数据
                    config.isPageStrategy = true;
                    setNewTitle();
                    loadData();
                }
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isPreviewEggs(config.previewEggs, position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                setTitle();
                LocalMedia media = adapter.getItem(position);
                if (media == null) {
                    return;
                }
                index = media.getPosition();
                if (!config.previewEggs) {
                    if (config.checkNumMode) {
                        check.setText(ValueOf.toString(media.getNum()));
                        notifyCheckChanged(media);
                    }
                    onImageChecked(position);
                }

                if (config.isOriginalControl) {
                    boolean isHasVideo = PictureMimeType.isHasVideo(media.getMimeType());
                    mCbOriginal.setVisibility(isHasVideo ? View.GONE : View.VISIBLE);
                    mCbOriginal.setChecked(config.isCheckOriginalImage);
                }
                onPageSelectedChange(media);

                if (config.isPageStrategy && !isBottomPreview) {
                    if (isHasMore) {
                        // 滑到adapter.getSize() - PictureConfig.MIN_PAGE_SIZE时或最后一条时预加载
                        if (position == (adapter.getSize() - 1) - PictureConfig.MIN_PAGE_SIZE || position == adapter.getSize() - 1) {
                            loadMoreData();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // 原图
        if (config.isOriginalControl) {
            boolean isCheckOriginal = getIntent()
                    .getBooleanExtra(PictureConfig.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
            mCbOriginal.setVisibility(View.VISIBLE);
            config.isCheckOriginalImage = isCheckOriginal;
            mCbOriginal.setChecked(config.isCheckOriginalImage);
            mCbOriginal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                config.isCheckOriginalImage = isChecked;
            });
        }
    }

    /**
     * 从本地获取数据
     */
    private void loadData() {
        long bucketId = getIntent().getLongExtra(PictureConfig.EXTRA_BUCKET_ID, -1);
        mPage++;
        LocalMediaPageLoader.getInstance(getContext(), config).loadPageMediaData(bucketId, mPage, config.pageSize,
                (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                    if (!isFinishing()) {
                        this.isHasMore = isHasMore;
                        if (isHasMore) {
                            int size = result.size();
                            if (size > 0 && adapter != null) {
                                adapter.getData().addAll(result);
                                adapter.notifyDataSetChanged();
                            } else {
                                // 这种情况就是开启过滤损坏文件刚好导致某一页全是损坏的虽然result为0，但还要请求下一页数据
                                loadMoreData();
                            }
                        }
                    }
                });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        long bucketId = getIntent().getLongExtra(PictureConfig.EXTRA_BUCKET_ID, -1);
        mPage++;
        LocalMediaPageLoader.getInstance(getContext(), config).loadPageMediaData(bucketId, mPage, config.pageSize,
                (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                    if (!isFinishing()) {
                        this.isHasMore = isHasMore;
                        if (isHasMore) {
                            int size = result.size();
                            if (size > 0 && adapter != null) {
                                adapter.getData().addAll(result);
                                adapter.notifyDataSetChanged();
                            } else {
                                // 这种情况就是开启过滤损坏文件刚好导致某一页全是损坏的虽然result为0，但还要请求下一页数据
                                loadMoreData();
                            }
                        }
                    }
                });
    }

    @Override
    protected void initCompleteText(int startCount) {
        boolean isNotEmptyStyle = config.style != null;
        if (config.selectionMode == PictureConfig.SINGLE) {
            if (startCount <= 0) {
                // 未选择任何图片
                mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                        ? config.style.pictureUnCompleteText : getString(R.string.picture_please_select));
            } else {
                // 已选择
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
                // 未选择任何图片
                mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                        ? config.style.pictureUnCompleteText : getString(R.string.picture_done_front_num,
                        startCount, config.maxSelectNum));
            } else {
                // 已选择
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
     * ViewPage滑动数据变化回调
     *
     * @param media
     */
    protected void onPageSelectedChange(LocalMedia media) {

    }

    /**
     * 动态设置相册主题
     */
    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleTextColor != 0) {
                tvTitle.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureTitleTextSize != 0) {
                tvTitle.setTextSize(config.style.pictureTitleTextSize);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                pictureLeftBack.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(config.style.picturePreviewBottomBgColor);
            }
            if (config.style.pictureCheckNumBgStyle != 0) {
                tvMediaNum.setBackgroundResource(config.style.pictureCheckNumBgStyle);
            }
            if (config.style.pictureCheckedStyle != 0) {
                check.setBackgroundResource(config.style.pictureCheckedStyle);
            }
            if (config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mTvPictureOk.setText(config.style.pictureUnCompleteText);
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

        onSelectNumChange(false);
    }

    /**
     * 这里没实际意义，好处是预览图片时 滑动到屏幕一半以上可看到下一张图片是否选中了
     *
     * @param previewEggs          是否显示预览友好体验
     * @param positionOffsetPixels 滑动偏移量
     */
    private void isPreviewEggs(boolean previewEggs, int position, int positionOffsetPixels) {
        if (previewEggs) {
            if (adapter.getSize() > 0) {
                LocalMedia media;
                int num;
                if (positionOffsetPixels < screenWidth / 2) {
                    media = adapter.getItem(position);
                    if (media != null) {
                        check.setSelected(isSelected(media));
                        if (config.isWeChatStyle) {
                            onUpdateSelectedChange(media);
                        } else {
                            if (config.checkNumMode) {
                                num = media.getNum();
                                check.setText(ValueOf.toString(num));
                                notifyCheckChanged(media);
                                onImageChecked(position);
                            }
                        }
                    }
                } else {
                    media = adapter.getItem(position + 1);
                    if (media != null) {
                        check.setSelected(isSelected(media));
                        if (config.isWeChatStyle) {
                            onUpdateSelectedChange(media);
                        } else {
                            if (config.checkNumMode) {
                                num = media.getNum();
                                check.setText(ValueOf.toString(num));
                                notifyCheckChanged(media);
                                onImageChecked(position + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化ViewPage数据
     *
     * @param list
     */
    private void initViewPageAdapterData(List<LocalMedia> list) {
        adapter = new PictureSimpleFragmentAdapter(config, this);
        adapter.bindData(list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        setTitle();
        onImageChecked(position);
        LocalMedia media = adapter.getItem(position);
        if (media != null) {
            index = media.getPosition();
            if (config.checkNumMode) {
                tvMediaNum.setSelected(true);
                check.setText(ValueOf.toString(media.getNum()));
                notifyCheckChanged(media);
            }
        }
    }

    /**
     * 重置标题栏和分页码
     */
    private void setNewTitle() {
        mPage = 0;
        position = 0;
        setTitle();
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (config.isPageStrategy && !isBottomPreview) {
            tvTitle.setText(getString(R.string.picture_preview_image_num,
                    position + 1, totalNumber));
        } else {
            tvTitle.setText(getString(R.string.picture_preview_image_num,
                    position + 1, adapter.getSize()));
        }
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(LocalMedia imageBean) {
        if (config.checkNumMode) {
            check.setText("");
            int size = selectData.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectData.get(i);
                if (media.getPath().equals(imageBean.getPath())
                        || media.getId() == imageBean.getId()) {
                    imageBean.setNum(media.getNum());
                    check.setText(String.valueOf(imageBean.getNum()));
                }
            }
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = selectData.size(); index < len; index++) {
            LocalMedia media = selectData.get(index);
            media.setNum(index + 1);
        }
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        if (adapter.getSize() > 0) {
            LocalMedia media = adapter.getItem(position);
            if (media != null) {
                check.setSelected(isSelected(media));
            }
        } else {
            check.setSelected(false);
        }
    }

    /**
     * 当前图片是否选中
     *
     * @param image
     * @return
     */
    protected boolean isSelected(LocalMedia image) {
        int size = selectData.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectData.get(i);
            if (media.getPath().equals(image.getPath()) || media.getId() == image.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新图片选择数量
     */

    protected void onSelectNumChange(boolean isRefresh) {
        this.refresh = isRefresh;
        boolean enable = selectData.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            if (config.style != null) {
                if (config.style.pictureCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(config.style.pictureCompleteTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_fa632d));
                }
            }
            if (numComplete) {
                initCompleteText(selectData.size());
            } else {
                if (refresh) {
                    tvMediaNum.startAnimation(animation);
                }
                tvMediaNum.setVisibility(View.VISIBLE);
                tvMediaNum.setText(String.valueOf(selectData.size()));
                if (config.style != null && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mTvPictureOk.setText(config.style.pictureCompleteText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_completed));
                }
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            if (config.style != null) {
                if (config.style.pictureUnCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
            }
            if (numComplete) {
                initCompleteText(0);
            } else {
                tvMediaNum.setVisibility(View.INVISIBLE);
                if (config.style != null && !TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                    mTvPictureOk.setText(config.style.pictureUnCompleteText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_please_select));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pictureLeftBack) {
            onBackPressed();
        } else if (id == R.id.tv_ok || id == R.id.tvMediaNum) {
            onComplete();
        } else if (id == R.id.btnCheck) {
            onCheckedComplete();
        }
    }

    protected void onCheckedComplete() {
        if (adapter.getSize() > 0) {
            LocalMedia image = adapter.getItem(viewPager.getCurrentItem());
            String mimeType = selectData.size() > 0 ?
                    selectData.get(0).getMimeType() : "";
            int currentSize = selectData.size();
            if (config.isWithVideoImage) {
                // 混选模式
                int videoSize = 0;
                for (int i = 0; i < currentSize; i++) {
                    LocalMedia media = selectData.get(i);
                    if (PictureMimeType.isHasVideo(media.getMimeType())) {
                        videoSize++;
                    }
                }
                if (image != null && PictureMimeType.isHasVideo(image.getMimeType())) {
                    if (config.maxVideoSelectNum <= 0) {
                        // 如果视频可选数量是0
                        showPromptDialog(getString(R.string.picture_rule));
                        return;
                    }

                    if (selectData.size() >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        return;
                    }

                    if (videoSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果选择的是视频
                        showPromptDialog(StringUtils.getMsg(getContext(), image.getMimeType(), config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                }
                if (image != null && PictureMimeType.isHasImage(image.getMimeType())) {
                    if (selectData.size() >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        return;
                    }
                }
            } else {
                // 非混选模式
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                    if (!mimeTypeSame) {
                        showPromptDialog(getString(R.string.picture_rule));
                        return;
                    }
                }
                if (PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0) {
                    if (currentSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果先选择的是视频
                        showPromptDialog(StringUtils.getMsg(getContext(), mimeType, config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                } else {
                    if (currentSize >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(StringUtils.getMsg(getContext(), mimeType, config.maxSelectNum));
                        return;
                    }
                    if (PictureMimeType.isHasVideo(image.getMimeType())) {
                        if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                            // 视频小于最低指定的长度
                            showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                            return;
                        }

                        if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                            // 视频时长超过了指定的长度
                            showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                            return;
                        }
                    }
                }
            }
            // 刷新图片列表中图片状态
            boolean isChecked;
            if (!check.isSelected()) {
                isChecked = true;
                check.setSelected(true);
                check.startAnimation(animation);
            } else {
                isChecked = false;
                check.setSelected(false);
            }
            isChangeSelectedData = true;
            if (isChecked) {
                VoiceUtils.getInstance().play();
                // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                if (config.selectionMode == PictureConfig.SINGLE) {
                    selectData.clear();
                }

                // 如果宽高为0，重新获取宽高
                if (image.getWidth() == 0 || image.getHeight() == 0) {
                    int width = 0, height = 0;
                    image.setOrientation(-1);
                    if (PictureMimeType.isContent(image.getPath())) {
                        if (PictureMimeType.isHasVideo(image.getMimeType())) {
                            int[] size = MediaUtils.getVideoSizeForUri(getContext(), Uri.parse(image.getPath()));
                            width = size[0];
                            height = size[1];
                        } else if (PictureMimeType.isHasImage(image.getMimeType())) {
                            int[] size = MediaUtils.getImageSizeForUri(getContext(), Uri.parse(image.getPath()));
                            width = size[0];
                            height = size[1];
                        }
                    } else {
                        if (PictureMimeType.isHasVideo(image.getMimeType())) {
                            int[] size = MediaUtils.getVideoSizeForUrl(image.getPath());
                            width = size[0];
                            height = size[1];
                        } else if (PictureMimeType.isHasImage(image.getMimeType())) {
                            int[] size = MediaUtils.getImageSizeForUrl(image.getPath());
                            width = size[0];
                            height = size[1];
                        }
                    }
                    image.setWidth(width);
                    image.setHeight(height);
                }

                // 如果有旋转信息图片宽高则是相反
                MediaUtils.setOrientationAsynchronous(getContext(), image, config.isAndroidQChangeWH, config.isAndroidQChangeVideoWH, null);
                selectData.add(image);
                onSelectedChange(true, image);
                image.setNum(selectData.size());
                if (config.checkNumMode) {
                    check.setText(String.valueOf(image.getNum()));
                }
            } else {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    if (media.getPath().equals(image.getPath())
                            || media.getId() == image.getId()) {
                        selectData.remove(media);
                        onSelectedChange(false, image);
                        subSelectPosition();
                        notifyCheckChanged(media);
                        break;
                    }
                }
            }
            onSelectNumChange(true);
        }
    }

    /**
     * 选中或是移除
     *
     * @param isAddRemove
     * @param media
     */
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {

    }

    /**
     * 更新选中或是移除状态
     *
     * @param media
     */
    protected void onUpdateSelectedChange(LocalMedia media) {

    }

    protected void onComplete() {
        // 如果设置了图片最小选择数量，则判断是否满足条件
        int size = selectData.size();
        LocalMedia image = selectData.size() > 0 ? selectData.get(0) : null;
        String mimeType = image != null ? image.getMimeType() : "";
        if (config.isWithVideoImage) {
            // 混选模式
            int videoSize = 0;
            int imageSize = 0;
            int currentSize = selectData.size();
            for (int i = 0; i < currentSize; i++) {
                LocalMedia media = selectData.get(i);
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
            // 单选模式(同类型)
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
        isCompleteOrSelected = true;
        isChangeSelectedData = true;
        if (config.isCheckOriginalImage) {
            onBackPressed();
            return;
        }
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            bothMimeTypeWith(mimeType, image);
        } else {
            separateMimeTypeWith(mimeType, image);
        }
    }

    /**
     * 两者不同类型的处理方式
     *
     * @param mimeType
     * @param image
     */
    private void bothMimeTypeWith(String mimeType, LocalMedia image) {
        if (config.enableCrop) {
            isCompleteOrSelected = false;
            boolean isHasImage = PictureMimeType.isHasImage(mimeType);
            if (config.selectionMode == PictureConfig.SINGLE && isHasImage) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath, image.getMimeType());
            } else {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = selectData.size();
                int imageNum = 0;
                for (int i = 0; i < count; i++) {
                    LocalMedia media = selectData.get(i);
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
                    cutInfo.setAndroidQToPath(media.getAndroidQToPath());
                    cutInfo.setId(media.getId());
                    cutInfo.setDuration(media.getDuration());
                    cutInfo.setRealPath(media.getRealPath());
                    cuts.add(cutInfo);
                }
                if (imageNum <= 0) {
                    // 全是视频
                    isCompleteOrSelected = true;
                    onBackPressed();
                } else {
                    // 图片和视频共存
                    startCrop(cuts);
                }
            }
        } else {
            onBackPressed();
        }
    }

    /**
     * 同一类型的图片或视频处理逻辑
     *
     * @param mimeType
     * @param image
     */
    private void separateMimeTypeWith(String mimeType, LocalMedia image) {
        if (config.enableCrop && PictureMimeType.isHasImage(mimeType)) {
            isCompleteOrSelected = false;
            if (config.selectionMode == PictureConfig.SINGLE) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath, image.getMimeType());
            } else {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = selectData.size();
                for (int i = 0; i < count; i++) {
                    LocalMedia media = selectData.get(i);
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
                    cutInfo.setAndroidQToPath(media.getAndroidQToPath());
                    cutInfo.setId(media.getId());
                    cutInfo.setDuration(media.getDuration());
                    cutInfo.setRealPath(media.getRealPath());
                    cuts.add(cutInfo);
                }
                startCrop(cuts);
            }
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_MULTI_CROP:
                    // 裁剪数据
                    List<CutInfo> list = UCrop.getMultipleOutput(data);
                    data.putParcelableArrayListExtra(UCrop.Options.EXTRA_OUTPUT_URI_LIST,
                            (ArrayList<? extends Parcelable>) list);
                    // 已选数量
                    data.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                            (ArrayList<? extends Parcelable>) selectData);
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null) {
                        data.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                                (ArrayList<? extends Parcelable>) selectData);
                        setResult(RESULT_OK, data);
                    }
                    finish();
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastUtils.s(getContext(), throwable.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        updateResult();
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

    /**
     * 更新选中数据
     */
    private void updateResult() {
        Intent intent = new Intent();
        if (isChangeSelectedData) {
            intent.putExtra(PictureConfig.EXTRA_COMPLETE_SELECTED, isCompleteOrSelected);
            intent.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                    (ArrayList<? extends Parcelable>) selectData);
        }
        // 把是否原图标识返回，主要用于开启了开发者选项不保留活动或内存不足时 原图选中状态没有全局同步问题
        if (config.isOriginalControl) {
            intent.putExtra(PictureConfig.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
        }
        setResult(RESULT_CANCELED, intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PictureConfig.EXTRA_COMPLETE_SELECTED, isCompleteOrSelected);
        outState.putBoolean(PictureConfig.EXTRA_CHANGE_SELECTED_DATA, isChangeSelectedData);
        PictureSelector.saveSelectorList(outState, selectData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isOnSaveInstanceState) {
            ImagesObservable.getInstance().clearPreviewMediaData();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (adapter != null) {
            adapter.clear();
        }
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }

}
