package com.luck.picture.lib.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.R;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.yalantis.ucrop.MultiUCrop;
import com.yalantis.ucrop.dialog.OptAnimationLoader;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.EventEntity;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.ToolbarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
    private RelativeLayout select_bar_layout;
    private PreviewViewPager viewPager;
    private int position;
    private RelativeLayout rl_title;
    private LinearLayout ll_check;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private TextView check;
    private SimpleFragmentAdapter adapter;
    private Animation animation;
    private boolean refresh;
    private SweetAlertDialog dialog;

    //EventBus 3.0 回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case FunctionConfig.CLOSE_FLAG:
                dismiss();
                closeActivity();
                break;
            case FunctionConfig.CLOSE_PREVIEW_FLAG:
                closeActivity();
                break;
        }
    }

    // 关闭activity
    protected void closeActivity() {
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_image_preview);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        ll_check = (LinearLayout) findViewById(R.id.ll_check);
        select_bar_layout = (RelativeLayout) findViewById(R.id.select_bar_layout);
        check = (TextView) findViewById(R.id.check);
        picture_left_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_ok.setOnClickListener(this);
        position = getIntent().getIntExtra(FunctionConfig.EXTRA_POSITION, 0);
        rl_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        tv_ok.setTextColor(completeColor);
        select_bar_layout.setBackgroundColor(previewBottomBgColor);
        animation = OptAnimationLoader.loadAnimation(mContext, R.anim.modal_in);
        animation.setAnimationListener(this);
        boolean is_bottom_preview = getIntent().getBooleanExtra(FunctionConfig.EXTRA_BOTTOM_PREVIEW, false);
        if (is_bottom_preview) {
            // 底部预览按钮过来
            images = (List<LocalMedia>) getIntent().getSerializableExtra(FunctionConfig.EXTRA_PREVIEW_LIST);
        } else {
            images = ImagesObservable.getInstance().readLocalMedias();
        }

        if (is_checked_num) {
            tv_img_num.setBackgroundResource(R.drawable.message_oval_blue);
        }

        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST);

        initViewPageAdapterData();
        ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(PicturePreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    check.setSelected(false);
                    return;
                }
                LocalMedia image = images.get(viewPager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                    image.setNum(selectImages.size());
                    if (is_checked_num) {
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
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(position + 1 + "/" + images.size());
                if (is_checked_num) {
                    LocalMedia media = images.get(position);
                    check.setText(media.getNum() + "");
                    notifyCheckChanged(media);
                }
                onImageChecked(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter(getSupportFragmentManager());
        check.setBackgroundResource(cb_drawable);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onSelectNumChange(false);
        onImageChecked(position);
        if (is_checked_num) {
            tv_img_num.setBackgroundResource(R.drawable.message_oval_blue);
            LocalMedia media = images.get(position);
            check.setText(media.getNum() + "");
            notifyCheckChanged(media);
        }
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(LocalMedia imageBean) {
        if (is_checked_num) {
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
        check.setSelected(isSelected(images.get(position)));
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
            tv_ok.setEnabled(true);
            tv_img_num.setVisibility(View.VISIBLE);
            tv_img_num.startAnimation(animation);
            tv_img_num.setText(selectImages.size() + "");
            tv_ok.setText(getString(R.string.ok));
        } else {
            tv_ok.setEnabled(false);
            tv_img_num.setVisibility(View.INVISIBLE);
            tv_ok.setText(getString(R.string.please_select));
            updateSelector(refresh);
        }
    }

    /**
     * 更新图片列表选中效果
     *
     * @param isRefresh
     */
    private void updateSelector(boolean isRefresh) {
        if (isRefresh) {
            EventEntity obj = new EventEntity(FunctionConfig.UPDATE_FLAG, selectImages);
            EventBus.getDefault().post(obj);
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


    public class SimpleFragmentAdapter extends FragmentPagerAdapter {

        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PictureImagePreviewFragment fragment = PictureImagePreviewFragment.getInstance(images.get(position).getPath(), selectImages);
            return fragment;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.picture_left_back) {
            finish();
        } else if (id == R.id.tv_ok) {
            if (selectMode == FunctionConfig.MODE_MULTIPLE && enableCrop && type == FunctionConfig.TYPE_IMAGE) {
                // 是图片和选择压缩并且是多张，调用批量压缩
                startMultiCopy(selectImages);
            } else {
                onResult(selectImages);
            }
        }
    }

    public void onResult(List<LocalMedia> images) {
        // 因为这里是单一实例的结果集，重新用变量接收一下在返回，不然会产生结果集被单一实例清空的问题
        List<LocalMedia> result = new ArrayList<>();
        for (LocalMedia media : images) {
            result.add(media);
        }
        EventEntity obj = new EventEntity(FunctionConfig.CROP_FLAG, result);
        EventBus.getDefault().post(obj);
        // 如果开启了压缩，先不关闭此页面，PictureImageGridActivity压缩完在通知关闭
        if (!isCompress) {
            finish();
            overridePendingTransition(0, R.anim.slide_bottom_out);
        } else {
            showDialog("请稍候...");
        }
    }

    /**
     * 多图裁剪
     *
     * @param medias
     */
    protected void startMultiCopy(List<LocalMedia> medias) {
        if (medias != null && medias.size() > 0) {
            LocalMedia media = medias.get(0);
            String path = media.getPath();
            // 去裁剪
            MultiUCrop uCrop = MultiUCrop.of(Uri.parse(path), Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")));
            MultiUCrop.Options options = new MultiUCrop.Options();
            switch (copyMode) {
                case FunctionConfig.CROP_MODEL_DEFAULT:
                    options.withAspectRatio(0, 0);
                    break;
                case FunctionConfig.CROP_MODEL_1_1:
                    options.withAspectRatio(1, 1);
                    break;
                case FunctionConfig.CROP_MODEL_3_2:
                    options.withAspectRatio(3, 2);
                    break;
                case FunctionConfig.CROP_MODEL_3_4:
                    options.withAspectRatio(3, 4);
                    break;
                case FunctionConfig.CROP_MODEL_16_9:
                    options.withAspectRatio(16, 9);
                    break;
            }
            options.setLocalMedia(medias);
            options.setCompressionQuality(compressQuality);
            options.withMaxResultSize(cropW, cropH);
            options.background_color(backgroundColor);
            options.setIsCompress(isCompress);
            options.copyMode(copyMode);
            uCrop.withOptions(options);
            uCrop.start(PicturePreviewActivity.this);
        }

    }

    private void showDialog(String msg) {
        if (!isFinishing()) {
            dialog = new SweetAlertDialog(PicturePreviewActivity.this);
            dialog.setTitleText(msg);
            dialog.show();
        }
    }

    private void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
