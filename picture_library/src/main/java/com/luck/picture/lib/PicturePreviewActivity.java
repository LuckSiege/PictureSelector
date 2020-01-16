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
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.tools.PictureFileUtils;
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
    protected ImageView picture_left_back;
    protected TextView tv_img_num, tv_title, mTvPictureOk;
    protected PreviewViewPager viewPager;
    protected int position;
    protected boolean is_bottom_preview;
    protected List<LocalMedia> images = new ArrayList<>();
    protected List<LocalMedia> selectImages = new ArrayList<>();
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
    /**
     * 是否已完成选择
     */
    protected boolean isCompleteOrSelected;
    /**
     * 是否改变已选的数据
     */
    protected boolean isChangeSelectedData;


    @Override
    public int getResourceId() {
        return R.layout.picture_preview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // 防止内存不足时activity被回收，导致图片未选中
            selectImages = PictureSelector.obtainSelectorList(savedInstanceState);
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
        picture_left_back = findViewById(R.id.picture_left_back);
        viewPager = findViewById(R.id.preview_pager);
        btnCheck = findViewById(R.id.btnCheck);
        check = findViewById(R.id.check);
        picture_left_back.setOnClickListener(this);
        mTvPictureOk = findViewById(R.id.tv_ok);
        mCbOriginal = findViewById(R.id.cb_original);
        tv_img_num = findViewById(R.id.tv_img_num);
        selectBarLayout = findViewById(R.id.select_bar_layout);
        mTvPictureOk.setOnClickListener(this);
        tv_img_num.setOnClickListener(this);
        tv_title = findViewById(R.id.picture_title);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        mTvPictureOk.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_please_select));
        tv_img_num.setSelected(config.checkNumMode ? true : false);
        btnCheck.setOnClickListener(this);
        selectImages = getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
        is_bottom_preview = getIntent().
                getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        // 底部预览按钮过来
        images = is_bottom_preview ? getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST)
                : ImagesObservable.getInstance().readPreviewMediaData();
        initViewPageAdapterData();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getContext() instanceof PictureSelectorPreviewWeChatStyleActivity) {
                    //  不做处理
                } else {
                    isPreviewEggs(config.previewEggs, position, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                tv_title.setText(getString(R.string.picture_preview_image_num,
                        position + 1, images.size()));
                LocalMedia media = images.get(position);
                index = media.getPosition();
                if (!config.previewEggs) {
                    if (config.checkNumMode) {
                        check.setText(media.getNum() + "");
                        notifyCheckChanged(media);
                    }
                    onImageChecked(position);
                }

                if (config.isOriginalControl) {
                    boolean eqVideo = PictureMimeType.eqVideo(media.getMimeType());
                    mCbOriginal.setVisibility(eqVideo ? View.GONE : View.VISIBLE);
                    mCbOriginal.setChecked(config.isCheckOriginalImage);
                }

                onPageSelectedChange(media);
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
                tv_title.setTextColor(config.style.pictureTitleTextColor);
            }
            if (config.style.pictureTitleTextSize != 0) {
                tv_title.setTextSize(config.style.pictureTitleTextSize);
            }
            if (config.style.pictureLeftBackIcon != 0) {
                picture_left_back.setImageResource(config.style.pictureLeftBackIcon);
            }
            if (config.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(config.style.picturePreviewBottomBgColor);
            }
            if (config.style.pictureCheckNumBgStyle != 0) {
                tv_img_num.setBackgroundResource(config.style.pictureCheckNumBgStyle);
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
            if (images.size() > 0 && images != null) {
                LocalMedia media;
                int num;
                if (positionOffsetPixels < screenWidth / 2) {
                    media = images.get(position);
                    check.setSelected(isSelected(media));
                    if (config.checkNumMode) {
                        num = media.getNum();
                        check.setText(num + "");
                        notifyCheckChanged(media);
                        onImageChecked(position);
                    }
                } else {
                    media = images.get(position + 1);
                    check.setSelected(isSelected(media));
                    if (config.checkNumMode) {
                        num = media.getNum();
                        check.setText(num + "");
                        notifyCheckChanged(media);
                        onImageChecked(position + 1);
                    }
                }
            }
        }
    }

    /**
     * 初始化ViewPage数据
     */
    private void initViewPageAdapterData() {
        tv_title.setText(getString(R.string.picture_preview_image_num,
                position + 1, images.size()));
        adapter = new PictureSimpleFragmentAdapter(config, images, this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onImageChecked(position);
        if (images.size() > 0) {
            LocalMedia media = images.get(position);
            index = media.getPosition();
            if (config.checkNumMode) {
                tv_img_num.setSelected(true);
                check.setText(ValueOf.toString(media.getNum()));
                notifyCheckChanged(media);
            }
        }
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(LocalMedia imageBean) {
        if (config.checkNumMode) {
            check.setText("");
            int size = selectImages.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectImages.get(i);
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
        for (int index = 0, len = selectImages.size(); index < len; index++) {
            LocalMedia media = selectImages.get(index);
            media.setNum(index + 1);
        }
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        if (images != null && images.size() > 0) {
            LocalMedia media = images.get(position);
            check.setSelected(isSelected(media));
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
        int size = selectImages.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectImages.get(i);
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
        boolean enable = selectImages.size() != 0;
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
                mTvPictureOk.setText(getString(R.string.picture_done_front_num, selectImages.size(),
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (refresh) {
                    tv_img_num.startAnimation(animation);
                }
                tv_img_num.setVisibility(View.VISIBLE);
                tv_img_num.setText(String.valueOf(selectImages.size()));
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
                mTvPictureOk.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                tv_img_num.setVisibility(View.INVISIBLE);
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
        if (id == R.id.picture_left_back) {
            onBackPressed();
        } else if (id == R.id.tv_ok || id == R.id.tv_img_num) {
            onComplete();
        } else if (id == R.id.btnCheck) {
            onCheckedComplete();
        }
    }

    protected void onCheckedComplete() {
        if (images != null && images.size() > 0) {
            LocalMedia image = images.get(viewPager.getCurrentItem());
            String mimeType = selectImages.size() > 0 ?
                    selectImages.get(0).getMimeType() : "";
            int currentSize = selectImages.size();
            if (config.isWithVideoImage) {
                // 混选模式
                int videoSize = 0;
                int imageSize = 0;
                for (int i = 0; i < currentSize; i++) {
                    LocalMedia media = selectImages.get(i);
                    if (PictureMimeType.eqVideo(media.getMimeType())) {
                        videoSize++;
                    } else {
                        imageSize++;
                    }
                }
                if (PictureMimeType.eqVideo(image.getMimeType())) {
                    if (config.maxVideoSelectNum > 0
                            && videoSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果选择的是视频
                        ToastUtils.s(getContext(), StringUtils.getMsg(getContext(), image.getMimeType(), config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        ToastUtils.s(getContext(), getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        ToastUtils.s(getContext(),
                                getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                }
                if (PictureMimeType.eqImage(image.getMimeType()) && imageSize >= config.maxSelectNum && !check.isSelected()) {
                    ToastUtils.s(getContext(), StringUtils.getMsg(getContext(), image.getMimeType(), config.maxSelectNum));
                    return;
                }
            } else {
                // 非混选模式
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                    if (!mimeTypeSame) {
                        ToastUtils.s(getContext(), getString(R.string.picture_rule));
                        return;
                    }
                }
                if (PictureMimeType.eqVideo(mimeType)) {
                    if (config.maxVideoSelectNum > 0
                            && currentSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果先选择的是视频
                        ToastUtils.s(getContext(), StringUtils.getMsg(getContext(), mimeType, config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        ToastUtils.s(getContext(), getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        ToastUtils.s(getContext(),
                                getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                } else {
                    if (currentSize >= config.maxSelectNum && !check.isSelected()) {
                        ToastUtils.s(getContext(), StringUtils.getMsg(getContext(), mimeType, config.maxSelectNum));
                        return;
                    }
                    if (PictureMimeType.eqVideo(image.getMimeType())) {
                        if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                            // 视频小于最低指定的长度
                            ToastUtils.s(getContext(), getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                            return;
                        }

                        if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                            // 视频时长超过了指定的长度
                            ToastUtils.s(getContext(),
                                    getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
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
                    selectImages.clear();
                }
                if (!TextUtils.isEmpty(image.getRealPath()) && image.getPath().startsWith("content://")) {
                    image.setRealPath(PictureFileUtils.getPath(getContext(), Uri.parse(image.getPath())));
                }
                selectImages.add(image);
                onSelectedChange(true, image);
                image.setNum(selectImages.size());
                if (config.checkNumMode) {
                    check.setText(String.valueOf(image.getNum()));
                }
            } else {
                int size = selectImages.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectImages.get(i);
                    if (media.getPath().equals(image.getPath())
                            || media.getId() == image.getId()) {
                        selectImages.remove(media);
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

    protected void onComplete() {
        // 如果设置了图片最小选择数量，则判断是否满足条件
        int size = selectImages.size();
        LocalMedia image = selectImages.size() > 0 ? selectImages.get(0) : null;
        String mimeType = image != null ? image.getMimeType() : "";
        if (config.isWithVideoImage) {
            // 混选模式
            int videoSize = 0;
            int imageSize = 0;
            int currentSize = selectImages.size();
            for (int i = 0; i < currentSize; i++) {
                LocalMedia media = selectImages.get(i);
                if (PictureMimeType.eqVideo(media.getMimeType())) {
                    videoSize++;
                } else {
                    imageSize++;
                }
            }
            if (config.selectionMode == PictureConfig.MULTIPLE) {
                if (config.minSelectNum > 0) {
                    if (imageSize < config.minSelectNum) {
                        ToastUtils.s(getContext(), getString(R.string.picture_min_img_num, config.minSelectNum));
                        return;
                    }
                }
                if (config.minVideoSelectNum > 0) {
                    if (videoSize < config.minVideoSelectNum) {
                        ToastUtils.s(getContext(), getString(R.string.picture_min_video_num, config.minVideoSelectNum));
                        return;
                    }
                }
            }
        } else {
            // 单选模式(同类型)
            if (config.selectionMode == PictureConfig.MULTIPLE) {
                if (PictureMimeType.eqImage(mimeType) && config.minSelectNum > 0 && size < config.minSelectNum) {
                    String str = getString(R.string.picture_min_img_num, config.minSelectNum);
                    ToastUtils.s(getContext(), str);
                    return;
                }
                if (PictureMimeType.eqVideo(mimeType) && config.minVideoSelectNum > 0 && size < config.minVideoSelectNum) {
                    String str = getString(R.string.picture_min_video_num, config.minVideoSelectNum);
                    ToastUtils.s(getContext(), str);
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
            isCompleteOrSelected = false;
            boolean eqImage = PictureMimeType.eqImage(mimeType);
            if (config.selectionMode == PictureConfig.SINGLE && eqImage) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath);
            } else {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = selectImages.size();
                int imageNum = 0;
                for (int i = 0; i < count; i++) {
                    LocalMedia media = selectImages.get(i);
                    if (media == null
                            || TextUtils.isEmpty(media.getPath())) {
                        continue;
                    }
                    if (PictureMimeType.eqImage(media.getMimeType())) {
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
        if (config.enableCrop && PictureMimeType.eqImage(mimeType)) {
            isCompleteOrSelected = false;
            isCompleteOrSelected = false;
            if (config.selectionMode == PictureConfig.SINGLE) {
                config.originalPath = image.getPath();
                startCrop(config.originalPath);
            } else {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<CutInfo> cuts = new ArrayList<>();
                int count = selectImages.size();
                for (int i = 0; i < count; i++) {
                    LocalMedia media = selectImages.get(i);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_MULTI_CROP:
                    // 裁剪数据
                    List<CutInfo> list = UCrop.getMultipleOutput(data);
                    data.putParcelableArrayListExtra(UCrop.Options.EXTRA_OUTPUT_URI_LIST,
                            (ArrayList<? extends Parcelable>) list);
                    // 已选数量
                    data.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                            (ArrayList<? extends Parcelable>) selectImages);
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null) {
                        data.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                                (ArrayList<? extends Parcelable>) selectImages);
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
                    (ArrayList<? extends Parcelable>) selectImages);
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
        PictureSelector.saveSelectorList(outState, selectImages);
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
