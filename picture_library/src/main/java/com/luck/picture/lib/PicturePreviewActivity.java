package com.luck.picture.lib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.anim.OptAnimationLoader;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.rxbus2.Subscribe;
import com.luck.picture.lib.rxbus2.ThreadMode;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.LightStatusBarUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToolbarUtil;
import com.luck.picture.lib.tools.VoiceUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PicturePreviewActivity extends PictureBaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private ImageView picture_left_back;
    private TextView tv_img_num, tv_title, tv_ok;
    private PreviewViewPager viewPager;
    private LinearLayout id_ll_ok;
    private int position;
    private LinearLayout ll_check;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private TextView check;
    private SimpleFragmentAdapter adapter;
    private Animation animation;
    private boolean refresh;
    private int index;
    private int preview_complete_textColor;
    private int screenWidth;
    private LayoutInflater inflater;

    //EventBus 3.0 回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.CLOSE_PREVIEW_FLAG:
                // 压缩完后关闭预览界面
                dismissDialog();
                finish();
                overridePendingTransition(0, R.anim.a3);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        inflater = LayoutInflater.from(this);
        screenWidth = ScreenUtils.getScreenWidth(this);
        int status_color = AttrsUtils.getTypeValueColor(this, R.attr.picture_status_color);
        ToolbarUtil.setColorNoTranslucent(this, status_color);
        preview_complete_textColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_preview_textColor);
        LightStatusBarUtils.setLightStatusBar(this, previewStatusFont);
        animation = OptAnimationLoader.loadAnimation(this, R.anim.modal_in);
        animation.setAnimationListener(this);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        ll_check = (LinearLayout) findViewById(R.id.ll_check);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        check = (TextView) findViewById(R.id.check);
        picture_left_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        id_ll_ok.setOnClickListener(this);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.picture_title);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num, 0, config.maxSelectNum)
                : getString(R.string.picture_please_select));

        tv_img_num.setSelected(config.checkNumMode ? true : false);

        selectImages = (List<LocalMedia>) getIntent().
                getSerializableExtra(PictureConfig.EXTRA_SELECT_LIST);
        boolean is_bottom_preview = getIntent().
                getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        if (is_bottom_preview) {
            // 底部预览按钮过来
            images = (List<LocalMedia>) getIntent().
                    getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        } else {
            images = ImagesObservable.getInstance().readLocalMedias();
        }
        initViewPageAdapterData();
        ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images != null && images.size() > 0) {
                    LocalMedia image = images.get(viewPager.getCurrentItem());
                    String pictureType = selectImages.size() > 0 ?
                            selectImages.get(0).getPictureType() : "";
                    if (!TextUtils.isEmpty(pictureType)) {
                        boolean toEqual = PictureMimeType.
                                mimeToEqual(pictureType, image.getPictureType());
                        if (!toEqual) {
                            showToast(getString(R.string.picture_rule));
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
                        showToast(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        check.setSelected(false);
                        return;
                    }
                    if (isChecked) {
                        VoiceUtils.playVoice(mContext, config.openClickSound);
                        selectImages.add(image);
                        image.setNum(selectImages.size());
                        if (config.checkNumMode) {
                            check.setText(image.getNum() + "");
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

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter();
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
            tv_ok.setTextColor(preview_complete_textColor);
            id_ll_ok.setEnabled(true);
            if (numComplete) {
                tv_ok.setText(getString(R.string.picture_done_front_num, selectImages.size(), config.maxSelectNum));
            } else {
                if (refresh) {
                    tv_img_num.startAnimation(animation);
                }
                tv_img_num.setVisibility(View.VISIBLE);
                tv_img_num.setText(selectImages.size() + "");
                tv_ok.setText(getString(R.string.picture_completed));
            }
        } else {
            id_ll_ok.setEnabled(false);
            tv_ok.setTextColor(ContextCompat.getColor(this, R.color.tab_color_false));
            if (numComplete) {
                tv_ok.setText(getString(R.string.picture_done_front_num, 0, config.maxSelectNum));
            } else {
                tv_img_num.setVisibility(View.INVISIBLE);
                tv_ok.setText(getString(R.string.picture_please_select));
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
            EventEntity obj = new EventEntity(PictureConfig.UPDATE_FLAG, selectImages, index);
            RxBus.getDefault().post(obj);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        updateSelector(refresh);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    public class SimpleFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = inflater.inflate(R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.preview_image);
            // 长图控件
            final SubsamplingScaleImageView longImg = (SubsamplingScaleImageView) contentView.findViewById(R.id.longImg);

            ImageView iv_play = (ImageView) contentView.findViewById(R.id.iv_play);
            LocalMedia media = images.get(position);
            if (media != null) {
                final String pictureType = media.getPictureType();
                boolean eqVideo = pictureType.startsWith(PictureConfig.VIDEO);
                iv_play.setVisibility(eqVideo ? View.VISIBLE : View.GONE);
                final String path;
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = media.getPath();
                }
                boolean isGif = PictureMimeType.isGif(pictureType);
                final boolean eqLongImg = PictureMimeType.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                // 压缩过的gif就不是gif了
                if (isGif && !media.isCompressed()) {
                    RequestOptions gifOptions = new RequestOptions()
                            .override(480, 800)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(PicturePreviewActivity.this)
                            .asGif()
                            .load(path)
                            .apply(gifOptions)
                            .into(imageView);
                } else {
                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(PicturePreviewActivity.this)
                            .asBitmap()
                            .load(path)
                            .apply(options)
                            .into(new SimpleTarget<Bitmap>(480, 800) {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    if (eqLongImg) {
                                        displayLongPic(resource, longImg);
                                    } else {
                                        // 适配有些手机 图片不能充满全屏
                                        if (resource.getWidth() > 480 && resource.getHeight() > 1080) {
                                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        }
                                        imageView.setImageBitmap(resource);
                                    }
                                }
                            });
                }
                imageView.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                        overridePendingTransition(0, R.anim.a3);
                    }
                });
                longImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(0, R.anim.a3);
                    }
                });
                iv_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("video_path", path);
                        startActivity(PictureVideoPlayActivity.class, bundle);
                    }
                });
            }
            (container).addView(contentView, 0);
            return contentView;
        }
    }

    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.picture_left_back) {
            finish();
            overridePendingTransition(0, R.anim.a3);
        }
        if (id == R.id.id_ll_ok) {
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = selectImages.size();
            String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
            if (config.minSelectNum > 0) {
                if (size < config.minSelectNum && config.selectionMode == PictureConfig.MULTIPLE) {
                    boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
                    String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    showToast(str);
                    return;
                }
            }
            if (config.enableCrop && pictureType.startsWith(PictureConfig.IMAGE)
                    && config.selectionMode == PictureConfig.MULTIPLE) {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<String> cuts = new ArrayList<>();
                for (LocalMedia media : selectImages) {
                    cuts.add(media.getPath());
                }
                startCrop(cuts);
            } else {
                onResult(selectImages);
            }
        }
    }

    public void onResult(List<LocalMedia> images) {
        RxBus.getDefault().post(new EventEntity(PictureConfig.PREVIEW_DATA_FLAG, images));
        // 如果开启了压缩，先不关闭此页面，PictureImageGridActivity压缩完在通知关闭
        if (!config.isCompress) {
            DebugUtil.i("**** not compress finish");
            finish();
            overridePendingTransition(0, R.anim.a3);
        } else {
            DebugUtil.i("**** loading compress");
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
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            showToast(throwable.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
