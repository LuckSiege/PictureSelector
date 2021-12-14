package com.luck.picture.lib;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.adapter.PictureWeChatPreviewGalleryAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
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
    private static final int GALLERY_MAX_COUNT = 5;
    /**
     * alpha duration
     */
    private final static int ALPHA_DURATION = 300;
    private RecyclerView mRvGallery;
    private View bottomLine;
    private TextView mTvSelected;
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
        mTvPictureRight.setVisibility(View.VISIBLE);
        mTvPictureRight.setText(getString(R.string.picture_send));
        mCbOriginal.setTextSize(16);
        mTvSelected = findViewById(R.id.tv_selected);
        mTvPictureRight.setOnClickListener(this);
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
            if (selectData.size() > position) {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    media.setChecked(false);
                }
                LocalMedia media = selectData.get(position);
                media.setChecked(true);
            }
        } else {
            int size = selectData.size();
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
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText != 0) {
                mTvPictureRight.setText(getString(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText));
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground != 0) {
                mTvPictureRight.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground);
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize != 0) {
                mTvPictureRight.setTextSize(PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedText != 0) {
                mTvSelected.setText(getString(PictureSelectionConfig.uiStyle.picture_bottom_selectedText));
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedTextSize != 0) {
                mTvSelected.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_selectedTextSize);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedTextColor != 0) {
                mTvSelected.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_selectedTextColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }

            mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));

            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedCheckStyle != 0) {
                check.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_bottom_selectedCheckStyle);
            } else {
                check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            }

            if (PictureSelectionConfig.uiStyle.picture_top_leftBack != 0) {
                pictureLeftBack.setImageResource(PictureSelectionConfig.uiStyle.picture_top_leftBack);
            } else {
                pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
            }

            if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_dividerColor != 0) {
                bottomLine.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_gallery_dividerColor);
            }

            if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_backgroundColor != 0) {
                mRvGallery.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_gallery_backgroundColor);
            }

            if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_height > 0) {
                ViewGroup.LayoutParams params = mRvGallery.getLayoutParams();
                params.height = PictureSelectionConfig.uiStyle.picture_bottom_gallery_height;
            }

            if (config.isEditorImage) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_preview_editorTextSize != 0) {
                    mPictureEditor.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_preview_editorTextSize);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_preview_editorTextColor != 0) {
                    mPictureEditor.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_preview_editorTextColor);
                }
            }

            if (config.isOriginalControl) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor);
                } else {
                    mCbOriginal.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle);
                } else {
                    mCbOriginal.setButtonDrawable(R.drawable.picture_original_wechat_checkbox);
                }
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                mTvPictureRight.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (PictureSelectionConfig.style.pictureRightTextSize != 0) {
                mTvPictureRight.setTextSize(PictureSelectionConfig.style.pictureRightTextSize);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureWeChatPreviewSelectedText)) {
                mTvSelected.setText(PictureSelectionConfig.style.pictureWeChatPreviewSelectedText);
            }
            if (PictureSelectionConfig.style.pictureWeChatPreviewSelectedTextSize != 0) {
                mTvSelected.setTextSize(PictureSelectionConfig.style.pictureWeChatPreviewSelectedTextSize);
            }
            if (PictureSelectionConfig.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.style.picturePreviewBottomBgColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }
            if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
            } else {
                if (PictureSelectionConfig.style.pictureCancelTextColor != 0) {
                    mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureCancelTextColor);
                } else {
                    mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
            }
            if (PictureSelectionConfig.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (PictureSelectionConfig.style.pictureWeChatChooseStyle != 0) {
                check.setBackgroundResource(PictureSelectionConfig.style.pictureWeChatChooseStyle);
            } else {
                check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            }
            if (config.isOriginalControl) {
                if (PictureSelectionConfig.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }

            if (config.isEditorImage) {
                if (PictureSelectionConfig.style.picturePreviewEditorTextSize != 0) {
                    mPictureEditor.setTextSize(PictureSelectionConfig.style.picturePreviewEditorTextSize);
                }
                if (PictureSelectionConfig.style.picturePreviewEditorTextColor != 0) {
                    mPictureEditor.setTextColor(PictureSelectionConfig.style.picturePreviewEditorTextColor);
                }
            }

            if (PictureSelectionConfig.style.pictureWeChatLeftBackStyle != 0) {
                pictureLeftBack.setImageResource(PictureSelectionConfig.style.pictureWeChatLeftBackStyle);
            } else {
                pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureRight.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            }
        } else {
            mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
            mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
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
        if (id == R.id.picture_right) {
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
    protected void onUpdateGalleryChange(LocalMedia media) {
        mGalleryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {
        if (isAddRemove) {
            media.setChecked(true);
            if (isBottomPreview) {
                LocalMedia localMedia = mGalleryAdapter.getItem(position);
                localMedia.setMaxSelectEnabledMask(false);
                mGalleryAdapter.notifyDataSetChanged();
            } else {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    mGalleryAdapter.addSingleMediaToData(media);
                }
            }
        } else {
            media.setChecked(false);
            if (isBottomPreview) {
                check.setSelected(false);
                LocalMedia localMedia = mGalleryAdapter.getItem(position);
                localMedia.setMaxSelectEnabledMask(true);
                mGalleryAdapter.notifyDataSetChanged();
            } else {
                mGalleryAdapter.removeMediaToData(media);
            }
        }
        int itemCount = mGalleryAdapter.getItemCount();
        if (itemCount > GALLERY_MAX_COUNT) {
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
        goneParent();
        boolean enable = selectData.size() != 0;
        if (enable) {
            initCompleteText(selectData.size());
            if (mRvGallery.getVisibility() == View.GONE) {
                mRvGallery.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                mRvGallery.setVisibility(View.VISIBLE);
                bottomLine.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                bottomLine.setVisibility(View.VISIBLE);
                if (isBottomPreview && mGalleryAdapter.getItemCount() > 0) {
                    // todo 预览模式就算勾选了取消但实际并没有从GalleryAdapter移除掉，所以可以忽略不操作
                    Log.i(TAG, "gallery adapter ignore...");
                } else {
                    // 重置一片内存区域 不然在其他地方添加也影响这里的数量
                    mGalleryAdapter.setNewData(selectData, isBottomPreview);
                }
            }
            if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                    mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
                }
                if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                    mTvPictureRight.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
                }
            } else {
                mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
        } else {
            if (PictureSelectionConfig.style != null && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureRight.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            } else {
                mTvPictureRight.setText(getString(R.string.picture_send));
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
        boolean isNotEmptyStyle = PictureSelectionConfig.style != null;
        if (config.isWithVideoImage) {
            // 混选模式
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (startCount <= 0) {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(), 1));
                    } else {
                        mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText,
                            selectData.size(), config.maxSelectNum));
                } else {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num, selectData.size(),
                            config.maxSelectNum));
                }
            }
        } else {
            String mimeType = selectData.size() > 0 ? selectData.get(0).getMimeType() : "";
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (startCount <= 0) {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(),
                                1));
                    } else {
                        mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(), maxSize));
                } else {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText
                            : getString(R.string.picture_send_num, selectData.size(), maxSize));
                }
            }
        }
    }
}
