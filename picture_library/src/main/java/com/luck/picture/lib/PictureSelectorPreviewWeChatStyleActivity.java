package com.luck.picture.lib;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.adapter.PictureWeChatPreviewGalleryAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;


/**
 * @author：luck
 * @date：2019-11-30 17:16
 * @describe：PictureSelector WeChatStyle
 */
public class PictureSelectorPreviewWeChatStyleActivity extends PicturePreviewActivity {
    /**
     * alpha duration
     */
    private final static int ALPHA_DURATION = 300;
    private TextView mPictureSendView;
    private RecyclerView mRvGallery;
    private TextView tvSelected;
    private View bottomLine;
    private PictureWeChatPreviewGalleryAdapter mGalleryAdapter;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_preview;
    }

    private void goneParent() {
        if (tvMediaNum.getVisibility() == View.VISIBLE) {
            tvMediaNum.setVisibility(View.GONE);
        }
        if (mTvPictureOk.getVisibility() == View.VISIBLE) {
            mTvPictureOk.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(check.getText())) {
            check.setText("");
        }
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        goneParent();
        mRvGallery = findViewById(R.id.rv_gallery);
        bottomLine = findViewById(R.id.bottomLine);
        tvSelected = findViewById(R.id.tv_selected);
        mPictureSendView = findViewById(R.id.picture_send);
        mPictureSendView.setOnClickListener(this);
        mPictureSendView.setText(getString(R.string.picture_send));
        mCbOriginal.setTextSize(16);
        mGalleryAdapter = new PictureWeChatPreviewGalleryAdapter(config);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
        mRvGallery.setLayoutManager(layoutManager);
        mRvGallery.addItemDecoration(new GridSpacingItemDecoration(Integer.MAX_VALUE,
                ScreenUtils.dip2px(this, 8), false));
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setItemClickListener((position, media, v) -> {
            if (viewPager != null && media != null) {
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    int newPosition = isBottomPreview ? position : isShowCamera ? media.position - 1 : media.position;
                    viewPager.setCurrentItem(newPosition);
                } else {
                    // TODO The picture is not in the album directory, click invalid
                }
            }
        });
        if (isBottomPreview) {
            if (selectData != null && selectData.size() > position) {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    media.setChecked(false);
                }
                LocalMedia media = selectData.get(position);
                media.setChecked(true);
            }
        } else {
            int size = selectData != null ? selectData.size() : 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectData.get(i);
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    media.setChecked(isShowCamera ? media.position - 1 == position : media.position == position);
                }
            }
        }
    }

    /**
     * Is it the same directory
     *
     * @param parentFolderName
     * @param currentDirectory
     * @return
     */
    private boolean isEqualsDirectory(String parentFolderName, String currentDirectory) {
        return isBottomPreview
                || TextUtils.isEmpty(parentFolderName)
                || TextUtils.isEmpty(currentDirectory)
                || currentDirectory.equals(getString(R.string.picture_camera_roll))
                || parentFolderName.equals(currentDirectory);
    }

    @Override
    public void initPictureSelectorStyle() {
        super.initPictureSelectorStyle();
        if (config.style != null) {
            if (config.style.pictureCompleteBackgroundStyle != 0) {
                mPictureSendView.setBackgroundResource(config.style.pictureCompleteBackgroundStyle);
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (config.style.pictureRightTextSize != 0) {
                mPictureSendView.setTextSize(config.style.pictureRightTextSize);
            }
            if (!TextUtils.isEmpty(config.style.pictureWeChatPreviewSelectedText)) {
                tvSelected.setText(config.style.pictureWeChatPreviewSelectedText);
            }
            if (config.style.pictureWeChatPreviewSelectedTextSize != 0) {
                tvSelected.setTextSize(config.style.pictureWeChatPreviewSelectedTextSize);
            }
            if (config.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(config.style.picturePreviewBottomBgColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }
            if (config.style.pictureCompleteTextColor != 0) {
                mPictureSendView.setTextColor(config.style.pictureCompleteTextColor);
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
                pictureLeftBack.setImageResource(config.style.pictureWeChatLeftBackStyle);
            } else {
                pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
            }
            if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mPictureSendView.setText(config.style.pictureUnCompleteText);
            }
        } else {
            mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
            mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
            selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }

        onSelectNumChange(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.picture_send) {
            boolean enable = selectData.size() != 0;
            if (enable) {
                mTvPictureOk.performClick();
            } else {
                btnCheck.performClick();
                boolean isNewEnableStatus = selectData.size() != 0;
                if (isNewEnableStatus) {
                    mTvPictureOk.performClick();
                }
            }
        }
    }

    @Override
    protected void onUpdateSelectedChange(LocalMedia media) {
        onChangeMediaStatus(media);
    }

    @Override
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {
        if (isAddRemove) {
            media.setChecked(true);
            if (config.selectionMode == PictureConfig.SINGLE) {
                mGalleryAdapter.addSingleMediaToData(media);
            }
        } else {
            media.setChecked(false);
            mGalleryAdapter.removeMediaToData(media);
            if (isBottomPreview) {
                if (selectData != null && selectData.size() > position) {
                    selectData.get(position).setChecked(true);
                }
                if (mGalleryAdapter.isDataEmpty()) {
                    onActivityBackPressed();
                } else {
                    int currentItem = viewPager.getCurrentItem();
                    adapter.remove(currentItem);
                    adapter.removeCacheView(currentItem);
                    position = currentItem;
                    tvTitle.setText(getString(R.string.picture_preview_image_num,
                            position + 1, adapter.getSize()));
                    check.setSelected(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        int itemCount = mGalleryAdapter.getItemCount();
        if (itemCount > 5) {
            mRvGallery.smoothScrollToPosition(itemCount - 1);
        }
    }

    @Override
    protected void onPageSelectedChange(LocalMedia media) {
        super.onPageSelectedChange(media);
        goneParent();
        if (!config.previewEggs) {
            onChangeMediaStatus(media);
        }
    }

    /**
     * onChangeMediaStatus
     *
     * @param media
     */
    private void onChangeMediaStatus(LocalMedia media) {
        if (mGalleryAdapter != null) {
            int itemCount = mGalleryAdapter.getItemCount();
            if (itemCount > 0) {
                boolean isChangeData = false;
                for (int i = 0; i < itemCount; i++) {
                    LocalMedia item = mGalleryAdapter.getItem(i);
                    if (item == null || TextUtils.isEmpty(item.getPath())) {
                        continue;
                    }
                    boolean isOldChecked = item.isChecked();
                    boolean isNewChecked = item.getPath().equals(media.getPath()) || item.getId() == media.getId();
                    if (!isChangeData) {
                        isChangeData = (isOldChecked && !isNewChecked) || (!isOldChecked && isNewChecked);
                    }
                    item.setChecked(isNewChecked);
                }
                if (isChangeData) {
                    mGalleryAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onSelectNumChange(boolean isRefresh) {
        if (mPictureSendView == null) {
            return;
        }
        goneParent();
        boolean enable = selectData.size() != 0;
        if (enable) {
            initCompleteText(selectData.size());
            if (mRvGallery.getVisibility() == View.GONE) {
                mRvGallery.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                mRvGallery.setVisibility(View.VISIBLE);
                bottomLine.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                bottomLine.setVisibility(View.VISIBLE);
                // 重置一片内存区域 不然在其他地方添加也影响这里的数量
                mGalleryAdapter.setNewData(selectData);
            }
            if (config.style != null) {
                if (config.style.pictureCompleteTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureCompleteTextColor);
                }
                if (config.style.pictureCompleteBackgroundStyle != 0) {
                    mPictureSendView.setBackgroundResource(config.style.pictureCompleteBackgroundStyle);
                }
            } else {
                mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
        } else {
            if (config.style != null && !TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mPictureSendView.setText(config.style.pictureUnCompleteText);
            } else {
                mPictureSendView.setText(getString(R.string.picture_send));
            }
            mRvGallery.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
            mRvGallery.setVisibility(View.GONE);
            bottomLine.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
            bottomLine.setVisibility(View.GONE);
        }
    }


    /**
     * initCompleteText
     */
    @Override
    protected void initCompleteText(int startCount) {
        boolean isNotEmptyStyle = config.style != null;
        if (config.isWithVideoImage) {
            // 混选模式
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (startCount <= 0) {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                        mPictureSendView.setText(String.format(config.style.pictureCompleteText, selectData.size(), 1));
                    } else {
                        mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureCompleteText)
                                ? config.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mPictureSendView.setText(String.format(config.style.pictureCompleteText,
                            selectData.size(), config.maxSelectNum));
                } else {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send_num, selectData.size(),
                            config.maxSelectNum));
                }
            }
        } else {
            String mimeType = selectData.get(0).getMimeType();
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (startCount <= 0) {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                        mPictureSendView.setText(String.format(config.style.pictureCompleteText, selectData.size(),
                                1));
                    } else {
                        mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureCompleteText)
                                ? config.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mPictureSendView.setText(String.format(config.style.pictureCompleteText, selectData.size(), maxSize));
                } else {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText
                            : getString(R.string.picture_send_num, selectData.size(), maxSize));
                }
            }
        }
    }
}
