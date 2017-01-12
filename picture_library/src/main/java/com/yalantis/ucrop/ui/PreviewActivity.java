package com.yalantis.ucrop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.dialog.OptAnimationLoader;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.observable.ImagesObservable;
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
    private PreviewViewPager viewPager;
    private int position;
    private RelativeLayout rl_title;
    private LinearLayout ll_check;
    private int maxSelectNum;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private TextView check;
    private SimpleFragmentAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        left_back = (ImageButton) findViewById(R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        ll_check = (LinearLayout) findViewById(R.id.ll_check);
        check = (TextView) findViewById(R.id.check);
        left_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_ok.setOnClickListener(this);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        maxSelectNum = getIntent().getIntExtra(PictureConfig.EXTRA_MAX_SELECT_NUM, 0);
        backgroundColor = getIntent().getIntExtra(PictureConfig.BACKGROUND_COLOR, 0);
        cb_drawable = getIntent().getIntExtra(PictureConfig.CHECKED_DRAWABLE, 0);
        is_checked_num = getIntent().getBooleanExtra(PictureConfig.EXTRA_IS_CHECKED_NUM, false);
        rl_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        boolean is_bottom_preview = getIntent().getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        if (is_bottom_preview) {
            // 底部预览按钮过来
            images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_LIST);
        } else {
            images = ImagesObservable.getInstance().readLocalMedias();
        }

        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);

        initViewPageAdapterData();
        ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 刷新图片列表中图片状态
                Intent intent = new Intent();
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
                    intent.putExtra("media", image);
                    intent.setAction(PictureConfig.ACTION_ADD_PHOTO);
                    Log.i("", images.size() + "");
                } else {
                    for (LocalMedia media : selectImages) {
                        if (media.getPath().equals(image.getPath())) {
                            selectImages.remove(media);
                            subSelectPosition();
                            notifyCheckChanged(media);
                            intent.putExtra("media", media);
                            intent.setAction(PictureConfig.ACTION_REMOVE_PHOTO);
                            break;
                        }
                    }
                }
                onSelectNumChange();
                sendBroadcast(intent);
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
        adapter = new SimpleFragmentAdapter(getSupportFragmentManager(), images);
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
            if (selectImages.size() > 0) {
                animation = OptAnimationLoader.loadAnimation(mContext, R.anim.modal_out);
                tv_img_num.startAnimation(animation);
            }
            tv_img_num.setVisibility(View.INVISIBLE);
            tv_ok.setText("请选择");
        }
    }

    public class SimpleFragmentAdapter extends FragmentPagerAdapter {
        private List<LocalMedia> medias;

        public SimpleFragmentAdapter(FragmentManager fm, List<LocalMedia> medias) {
            super(fm);
            this.medias = medias;
        }

        public void setMedias(List<LocalMedia> medias) {
            this.medias = medias;
        }

        @Override
        public Fragment getItem(int position) {
            ImagePreviewFragment fragment = ImagePreviewFragment.getInstance(medias.get(position).getPath());
            return fragment;
        }

        @Override
        public int getCount() {
            return medias.size();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.left_back) {
            finish();
        } else if (id == R.id.tv_ok) {
            ArrayList<String> result = new ArrayList<>();
            for (LocalMedia media : selectImages) {
                result.add(media.getPath());
            }
            if (result.size() > 0) {
                ArrayList<String> images = new ArrayList<>();
                for (LocalMedia media : selectImages) {
                    images.add(media.getPath());
                }
                setResult(RESULT_OK, new Intent().putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) images));
                finish();
            }
        }
    }
}
