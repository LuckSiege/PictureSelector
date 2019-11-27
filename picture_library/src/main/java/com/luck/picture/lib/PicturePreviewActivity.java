package com.luck.picture.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.luck.picture.lib.adapter.PictureSimpleFragmentAdapter;
import com.luck.picture.lib.anim.OptAnimationLoader;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.VoiceUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2016/1/29 下午21:50
 * @描述:图片预览
 */
public class PicturePreviewActivity extends PictureBaseActivity implements
        View.OnClickListener, PictureSimpleFragmentAdapter.OnCallBackActivity {
    private ImageView picture_left_back;
    private TextView tv_img_num, tv_title, mTvPictureOk;
    private PreviewViewPager viewPager;
    private int position;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private TextView check;
    private PictureSimpleFragmentAdapter adapter;
    private Animation animation;
    private View btnCheck;
    private boolean refresh;
    private int index;
    private int screenWidth;
    private Handler mHandler;
    private RelativeLayout selectBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);

        BroadcastManager.getInstance(this).registerReceiver(commonBroadcastReceiver,
                BroadcastAction.ACTION_CLOSE_PREVIEW);
        mHandler = new Handler();
        screenWidth = ScreenUtils.getScreenWidth(this);
        animation = OptAnimationLoader.loadAnimation(this, R.anim.picture_anim_modal_in);
        picture_left_back = findViewById(R.id.picture_left_back);
        viewPager = findViewById(R.id.preview_pager);
        btnCheck = findViewById(R.id.btnCheck);
        check = findViewById(R.id.check);
        picture_left_back.setOnClickListener(this);
        mTvPictureOk = findViewById(R.id.tv_ok);
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
        initPictureSelectorStyle();

        selectImages = getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
        boolean is_bottom_preview = getIntent().
                getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        // 底部预览按钮过来
        images = is_bottom_preview ? getIntent().
                getParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST)
                : ImagesObservable.getInstance().readPreviewMediaData();
        initViewPageAdapterData();
        btnCheck.setOnClickListener(view -> {
            if (images != null && images.size() > 0) {
                LocalMedia image = images.get(viewPager.getCurrentItem());
                String mimeType = selectImages.size() > 0 ?
                        selectImages.get(0).getMimeType() : "";
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                    if (!mimeTypeSame) {
                        ToastUtils.s(mContext, getString(R.string.picture_rule));
                        return;
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
                if (selectImages.size() >= config.maxSelectNum && isChecked) {
                    ToastUtils.s(mContext, getString(R.string.picture_message_max_num, config.maxSelectNum));
                    check.setSelected(false);
                    return;
                }
                if (isChecked) {
                    VoiceUtils.playVoice(mContext, config.openClickSound);
                    // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                    if (config.selectionMode == PictureConfig.SINGLE) {
                        singleRadioMediaImage();
                    }
                    selectImages.add(image);
                    image.setNum(selectImages.size());
                    if (config.checkNumMode) {
                        check.setText(String.valueOf(image.getNum()));
                    }
                } else {
                    for (LocalMedia media : selectImages) {
                        if (media.getPath().equals(image.getPath())) {
                            selectImages.remove(media);
                            subSelectPosition();
                            notifyCheckChanged(media);
                            break;
                        }
                    }
                }
                onSelectNumChange(true);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isPreviewEggs(config.previewEggs, position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                tv_title.setText(position + 1 + "/" + images.size());
                LocalMedia media = images.get(position);
                index = media.getPosition();
                if (!config.previewEggs) {
                    if (config.checkNumMode) {
                        check.setText(media.getNum() + "");
                        notifyCheckChanged(media);
                    }
                    onImageChecked(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 动态设置相册主题
     */
    private void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureTitleTextColor != 0) {
                tv_title.setTextColor(config.style.pictureTitleTextColor);
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
        }
        tv_title.setBackgroundColor(colorPrimary);
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
     * 单选图片
     */
    private void singleRadioMediaImage() {
        LocalMedia media = selectImages != null && selectImages.size() > 0 ? selectImages.get(0) : null;
        if (media != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", media.getPosition());
            bundle.putParcelableArrayList("selectImages", (ArrayList<? extends Parcelable>) selectImages);
            BroadcastManager.getInstance(this)
                    .action(BroadcastAction.ACTION_SELECTED_DATA)
                    .extras(bundle)
                    .broadcast();
            selectImages.clear();
        }
    }

    /**
     * 初始化ViewPage数据
     */
    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new PictureSimpleFragmentAdapter(config, images, this, this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onSelectNumChange(false);
        onImageChecked(position);
        if (images.size() > 0) {
            LocalMedia media = images.get(position);
            index = media.getPosition();
            if (config.checkNumMode) {
                tv_img_num.setSelected(true);
                check.setText(media.getNum() + "");
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
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(imageBean.getPath())) {
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
    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新图片选择数量
     */

    public void onSelectNumChange(boolean isRefresh) {
        this.refresh = isRefresh;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            if (config.style != null && config.style.pictureCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureCompleteTextColor);
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
                mTvPictureOk.setText(getString(R.string.picture_completed));
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            if (config.style != null && config.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(config.style.pictureUnCompleteTextColor);
            }
            if (numComplete) {
                mTvPictureOk.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                tv_img_num.setVisibility(View.INVISIBLE);
                mTvPictureOk.setText(getString(R.string.picture_please_select));
            }
        }
        updateSelector(refresh);
    }

    /**
     * 更新图片列表选中效果
     *
     * @param isRefresh
     */
    private void updateSelector(boolean isRefresh) {
        if (isRefresh) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", index);
            bundle.putParcelableArrayList("selectImages", (ArrayList<? extends Parcelable>) selectImages);
            BroadcastManager.getInstance(this)
                    .action(BroadcastAction.ACTION_SELECTED_DATA)
                    .extras(bundle)
                    .broadcast();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.picture_left_back) {
            onBackPressed();
        }
        if (id == R.id.tv_ok || id == R.id.tv_img_num) {
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = selectImages.size();
            LocalMedia image = selectImages.size() > 0 ? selectImages.get(0) : null;
            String mimeType = image != null ? image.getMimeType() : "";
            if (config.minSelectNum > 0) {
                if (size < config.minSelectNum && config.selectionMode == PictureConfig.MULTIPLE) {
                    boolean eqImg = PictureMimeType.eqImage(mimeType);
                    String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastUtils.s(mContext, str);
                    return;
                }
            }
            if (config.enableCrop && PictureMimeType.eqImage(mimeType)) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getPath();
                    startCrop(originalPath);
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<CutInfo> cuts = new ArrayList<>();
                    int count = selectImages.size();
                    for (int i = 0; i < count; i++) {
                        LocalMedia media = selectImages.get(i);
                        CutInfo cutInfo = new CutInfo();
                        cutInfo.setPath(media.getPath());
                        cutInfo.setImageWidth(media.getWidth());
                        cutInfo.setImageHeight(media.getHeight());
                        cutInfo.setMimeType(media.getMimeType());
                        cutInfo.setAndroidQToPath(media.getAndroidQToPath());
                        cuts.add(cutInfo);
                    }
                    startCrop(cuts);
                }
            } else {
                onResult(selectImages);
            }
        }
    }

    @Override
    public void onResult(List<LocalMedia> images) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selectImages", (ArrayList<? extends Parcelable>) images);
        BroadcastManager.getInstance(this)
                .action(BroadcastAction.ACTION_PREVIEW_COMPRESSION)
                .extras(bundle)
                .broadcast();
        if (!config.isCompress) {
            onBackPressed();
        } else {
            showPleaseDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> list = UCropMulti.getOutput(data);
                    setResult(RESULT_OK, new Intent().putExtra(UCropMulti.EXTRA_OUTPUT_URI_LIST,
                            (Serializable) list));
                    finish();
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null) {
                        setResult(RESULT_OK, data);
                    }
                    finish();
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastUtils.s(mContext, throwable.getMessage());
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
    protected void onDestroy() {
        super.onDestroy();
        ImagesObservable.getInstance().clearPreviewMediaData();
        if (commonBroadcastReceiver != null) {
            BroadcastManager.getInstance(this).unregisterReceiver(commonBroadcastReceiver,
                    BroadcastAction.ACTION_CLOSE_PREVIEW);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }

    private BroadcastReceiver commonBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastAction.ACTION_CLOSE_PREVIEW:
                    // 压缩完后关闭预览界面
                    dismissDialog();
                    mHandler.postDelayed(() -> onBackPressed(), 150);
                    break;
                default:
                    break;
            }
        }
    };

}
