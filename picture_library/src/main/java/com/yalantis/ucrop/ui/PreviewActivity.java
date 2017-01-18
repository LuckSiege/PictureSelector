package com.yalantis.ucrop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.compress.CompressConfig;
import com.yalantis.ucrop.compress.CompressImageOptions;
import com.yalantis.ucrop.compress.CompressInterface;
import com.yalantis.ucrop.dialog.OptAnimationLoader;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.observable.ImagesObservable;
import com.yalantis.ucrop.util.FunctionConfig;
import com.yalantis.ucrop.util.LocalMediaLoader;
import com.yalantis.ucrop.util.PictureConfig;
import com.yalantis.ucrop.util.ToolbarUtil;
import com.yalantis.ucrop.widget.PreviewViewPager;

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
public class PreviewActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton left_back;
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
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        left_back = (ImageButton) findViewById(R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        ll_check = (LinearLayout) findViewById(R.id.ll_check);
        select_bar_layout = (RelativeLayout) findViewById(R.id.select_bar_layout);
        check = (TextView) findViewById(R.id.check);
        left_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_ok.setOnClickListener(this);
        position = getIntent().getIntExtra(FunctionConfig.EXTRA_POSITION, 0);
        rl_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        tv_ok.setTextColor(completeColor);
        select_bar_layout.setBackgroundColor(previewBottomBgColor);
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
                    Animation animation = OptAnimationLoader.loadAnimation(mContext, R.anim.modal_in);
                    check.startAnimation(animation);
                } else {
                    isChecked = false;
                    check.setSelected(false);
                }
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(PreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
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
                onSelectNumChange();
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
        onSelectNumChange();
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
    public void onSelectNumChange() {
        Animation animation = null;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            tv_ok.setEnabled(true);
            tv_ok.setAlpha(1.0f);
            animation = OptAnimationLoader.loadAnimation(mContext, R.anim.modal_in);
            tv_img_num.startAnimation(animation);
            tv_img_num.setVisibility(View.VISIBLE);
            tv_img_num.setText(selectImages.size() + "");
            tv_ok.setText("已完成");
        } else {
            tv_ok.setEnabled(false);
            tv_ok.setAlpha(0.5f);
            tv_img_num.setVisibility(View.INVISIBLE);
            tv_ok.setText("请选择");
        }
    }


    public class SimpleFragmentAdapter extends FragmentPagerAdapter {

        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ImagePreviewFragment fragment = ImagePreviewFragment.getInstance(images.get(position).getPath(),selectImages);
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
        if (id == R.id.left_back) {
            activityFinish();
        } else if (id == R.id.tv_ok) {
            if (isCompress && type == LocalMediaLoader.TYPE_IMAGE) {
                compressImage(selectImages);
            } else {
                onResult(selectImages);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                activityFinish();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void activityFinish() {
        setResult(RESULT_OK, new Intent().putExtra("type", 1).putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectImages));
        finish();
    }

    /**
     * 处理图片压缩
     */
    private void compressImage(List<LocalMedia> result) {
        showDialog("处理中...");
        CompressConfig config = CompressConfig.ofDefaultConfig();
        CompressImageOptions.compress(this, config, result, new CompressInterface.CompressListener() {
            @Override
            public void onCompressSuccess(List<LocalMedia> images) {
                // 压缩成功回调
                onResult(images);
                if (dialog != null && dialog.isShowing()) {
                    dialog.cancel();
                }
            }

            @Override
            public void onCompressError(List<LocalMedia> images, String msg) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        }).compress();
    }

    public void onResult(List<LocalMedia> images) {
        // 因为这里是单一实例的结果集，重新用变量接收一下在返回，不然会产生结果集被单一实例清空的问题
        List<LocalMedia> result = new ArrayList<>();
        for (LocalMedia media : images) {
            result.add(media);
        }
        PictureConfig.OnSelectResultCallback resultCallback = PictureConfig.getResultCallback();
        if (resultCallback != null) {
            resultCallback.onSelectSuccess(result);
        }
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
        sendBroadcast("app.activity.finish");
    }


    private void showDialog(String msg) {
        dialog = new SweetAlertDialog(PreviewActivity.this);
        dialog.setTitleText(msg);
        dialog.show();
    }
}
