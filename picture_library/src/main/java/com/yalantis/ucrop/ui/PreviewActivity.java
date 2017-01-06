package com.yalantis.ucrop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.Constants;
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
    private LinearLayout id_ll_ok;
    private TextView tv_img_num, tv_title, tv_ok;
    private PreviewViewPager viewPager;
    private int position;
    private int maxSelectNum;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private CheckBox checkboxSelect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        left_back = (ImageButton) findViewById(R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        checkboxSelect = (CheckBox) findViewById(R.id.checkbox_select);
        left_back.setOnClickListener(this);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_ok.setOnClickListener(this);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(Constants.EXTRA_PREVIEW_LIST);
        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(Constants.EXTRA_PREVIEW_SELECT_LIST);
        position = getIntent().getIntExtra(Constants.EXTRA_POSITION, 0);
        maxSelectNum = getIntent().getIntExtra(Constants.EXTRA_MAX_SELECT_NUM, 0);
        viewPager.setAdapter(new SimpleFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(position);
        tv_title.setText(position + 1 + "/" + images.size());
        onSelectNumChange();
        onImageChecked(position);
        checkboxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 刷新图片列表中图片状态
                Intent intent = new Intent();
                boolean isChecked = checkboxSelect.isChecked();
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(PreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    checkboxSelect.setChecked(false);
                    return;
                }
                LocalMedia image = images.get(viewPager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                    intent.putExtra("media", image);
                    intent.setAction(Constants.ACTION_ADD_PHOTO);
                } else {
                    for (LocalMedia media : selectImages) {
                        if (media.getPath().equals(image.getPath())) {
                            selectImages.remove(media);
                            intent.putExtra("media", media);
                            intent.setAction(Constants.ACTION_REMOVE_PHOTO);
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
                onImageChecked(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        checkboxSelect.setChecked(isSelected(images.get(position)));
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
            animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_in);
            tv_img_num.startAnimation(animation);
            tv_img_num.setVisibility(View.VISIBLE);
            tv_img_num.setText(selectImages.size() + "");
            tv_ok.setText("已完成");
        } else {
            tv_ok.setEnabled(false);
            tv_ok.setAlpha(0.5f);
            if (selectImages.size() > 0) {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_out);
                tv_img_num.startAnimation(animation);
            }
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
            return ImagePreviewFragment.getInstance(images.get(position).getPath());
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
            ArrayList<String> result = new ArrayList<>();
            for (LocalMedia media : selectImages) {
                result.add(media.getPath());
            }
            if (result.size() > 0) {
                ArrayList<String> images = new ArrayList<>();
                for (LocalMedia media : selectImages) {
                    images.add(media.getPath());
                }
                setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_PREVIEW_SELECT_LIST, (Serializable) images));
                finish();
            }
        }
    }
}
