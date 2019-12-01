package com.luck.picture.lib;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.adapter.PictureWeChatPreviewGalleryAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.decoration.GridSpacingItemNotBothDecoration;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;


/**
 * @author：luck
 * @date：2019-11-30 17:16
 * @describe：PictureSelector 预览微信风格
 */
public class PictureSelectorPreviewWeChatStyleActivity extends PicturePreviewActivity {
    private TextView mPictureSendView;
    private RecyclerView mRvGallery;
    private View bottomLine;
    private PictureWeChatPreviewGalleryAdapter mGalleryAdapter;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_preview;
    }

    private void goneParent() {
        if (tv_img_num.getVisibility() == View.VISIBLE) {
            tv_img_num.setVisibility(View.GONE);
        }
        if (mTvPictureOk.getVisibility() == View.VISIBLE) {
            mTvPictureOk.setVisibility(View.GONE);
        }
        check.setText("");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        goneParent();
        mRvGallery = findViewById(R.id.rv_gallery);
        bottomLine = findViewById(R.id.bottomLine);
        mPictureSendView = findViewById(R.id.picture_send);
        mPictureSendView.setOnClickListener(this);
        mPictureSendView.setText(getString(R.string.picture_send));
        mCbOriginal.setTextSize(16);
        mGalleryAdapter = new PictureWeChatPreviewGalleryAdapter(config);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
        mRvGallery.setLayoutManager(layoutManager);
        mRvGallery.addItemDecoration(new GridSpacingItemNotBothDecoration(Integer.MAX_VALUE,
                ScreenUtils.dip2px(this, 8), true, true));
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setItemClickListener((position, media, v) -> {
            if (viewPager != null && media != null) {
                int newPosition = is_bottom_preview ? position : media.position - 1;
                viewPager.setCurrentItem(newPosition);
            }
        });
        if (is_bottom_preview) {
            if (selectImages != null && selectImages.size() > position) {
                selectImages.get(position).setChecked(true);
            }
        } else {
            int size = selectImages != null ? selectImages.size() : 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectImages.get(i);
                media.setChecked(media.position - 1 == position);
            }
        }
        onSelectNumChange(false);
    }

    @Override
    public void initPictureSelectorStyle() {
        super.initPictureSelectorStyle();
        if (config.style != null) {
            if (config.style.pictureRightBackgroundStyle != 0) {
                mPictureSendView.setBackgroundResource(config.style.pictureRightBackgroundStyle);
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (config.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(config.style.picturePreviewBottomBgColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }
            if (config.style.pictureRightDefaultTextColor != 0) {
                mPictureSendView.setTextColor(config.style.pictureRightSelectedTextColor);
            } else {
                if (config.style.pictureCancelTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureCancelTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
            }
            if (config.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (config.style.pictureWeChatChooseStyle != 0) {
                check.setBackgroundResource(config.style.pictureWeChatChooseStyle);
            } else {
                check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            }
            if (config.isOriginalControl) {
                if (config.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
            if (config.style.pictureWeChatLeftBackStyle != 0) {
                picture_left_back.setImageResource(config.style.pictureWeChatLeftBackStyle);
            } else {
                picture_left_back.setImageResource(R.drawable.picture_icon_back);
            }
        } else {
            mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
            mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
            selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            picture_left_back.setImageResource(R.drawable.picture_icon_back);
            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.picture_send) {
            boolean enable = selectImages.size() != 0;
            if (enable) {
                // 如果已有勾选则走完成逻辑
                mTvPictureOk.performClick();
            } else {
                // 没有勾选走选中逻辑并完成
                btnCheck.performClick();
                boolean newEnableStatus = selectImages.size() != 0;
                if (newEnableStatus) {
                    // 如果已有勾选则走完成逻辑
                    mTvPictureOk.performClick();
                }
            }
        }
    }

    @Override
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {
        super.onSelectedChange(isAddRemove, media);
        if (isAddRemove) {
            // 添加
            media.setChecked(true);
            if (config.selectionMode == PictureConfig.SINGLE) {
                mGalleryAdapter.addSingleMediaToData(media);
            }
        } else {
            // 移除
            media.setChecked(false);
            mGalleryAdapter.removeMediaToData(media);
        }
    }

    @Override
    protected void onPageSelectedChange(LocalMedia media) {
        super.onPageSelectedChange(media);
        goneParent();
        if (mGalleryAdapter != null) {
            int itemCount = mGalleryAdapter.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                LocalMedia item = mGalleryAdapter.getItem(i);
                if (item == null || TextUtils.isEmpty(item.getPath())) {
                    continue;
                }
                item.setChecked(item.getPath().equals(media.getPath()));
            }
            mGalleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSelectNumChange(boolean isRefresh) {
        if (mPictureSendView == null) {
            return;
        }
        goneParent();
        boolean enable = selectImages.size() != 0;
        if (enable) {
            mPictureSendView.setText(config.selectionMode == PictureConfig.SINGLE ? getString(R.string.picture_send) :
                    getString(R.string.picture_send_num, selectImages.size(),
                            config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            if (mRvGallery.getVisibility() == View.GONE) {
                mRvGallery.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);
                // 重置一片内存区域 不然在其他地方添加也影响这里的数量
                mGalleryAdapter.setNewData(selectImages);
            }
        } else {
            mPictureSendView.setText(getString(R.string.picture_send));
            mRvGallery.setVisibility(View.GONE);
            bottomLine.setVisibility(View.GONE);
        }
        updateSelector(isRefresh);
    }

    @Override
    protected void updateSelector(boolean isRefresh) {
        super.updateSelector(isRefresh);
        // 调用父类的刷新方法，不可注释掉
    }
}
