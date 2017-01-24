package com.luck.picture.lib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.R;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.yalantis.ucrop.MultiUCrop;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.dialog.OptAnimationLoader;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.ToolbarUtil;

import java.io.File;
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
public class PicturePreviewActivity extends PictureBaseActivity implements View.OnClickListener {
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
    private Handler mHandler = new Handler();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("app.activity.finish")) {
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
            } else if (action.equals("app.action.finish.preview")) {
                // 多图裁剪完关闭 预览界面，在图片列表中进行压缩，所以这里区分开来，不用统一的关闭activity
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_image_preview);
        registerReceiver(receiver, "app.activity.finish", "app.action.finish.preview");
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
        Animation animation = null;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            tv_ok.setEnabled(true);
            tv_ok.setAlpha(1.0f);
            animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_in);
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

        if (isRefresh) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(new Intent().setAction("app.action.refresh.data").putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectImages));
                }
            }, 100);
        }
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
        if (id == R.id.left_back) {
            finish();
        } else if (id == R.id.tv_ok) {
            if (selectMode == FunctionConfig.MODE_MULTIPLE && enableCrop && type == LocalMediaLoader.TYPE_IMAGE) {
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
        sendBroadcast(new Intent().setAction("app.action.crop_data").putExtra(UCrop.EXTRA_RESULT, (Serializable) result));
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
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
                case FunctionConfig.COPY_MODEL_DEFAULT:
                    options.withAspectRatio(0, 0);
                    break;
                case FunctionConfig.COPY_MODEL_1_1:
                    options.withAspectRatio(1, 1);
                    break;
                case FunctionConfig.COPY_MODEL_3_2:
                    options.withAspectRatio(3, 2);
                    break;
                case FunctionConfig.COPY_MODEL_3_4:
                    options.withAspectRatio(3, 4);
                    break;
                case FunctionConfig.COPY_MODEL_16_9:
                    options.withAspectRatio(16, 9);
                    break;
            }
            options.setLocalMedia(medias);
            options.setCompressionQuality(compressQuality);
            options.withMaxResultSize(cropW, cropH);
            options.background_color(backgroundColor);
            options.copyMode(copyMode);
            uCrop.withOptions(options);
            uCrop.start(PicturePreviewActivity.this);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
