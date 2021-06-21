package com.luck.picture.lib;


import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AttrsUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 13:27
 * @describe：PictureSelector WeChatStyle
 */
public class PictureSelectorWeChatStyleActivity extends PictureSelectorActivity {
    private RelativeLayout rlAlbum;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_selector;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        rlAlbum = findViewById(R.id.rlAlbum);
        mTvPictureRight.setOnClickListener(this);
        mTvPictureRight.setText(getString(R.string.picture_send));
        mTvPicturePreview.setTextSize(16);
        mCbOriginal.setTextSize(16);
        boolean isChooseMode = config.selectionMode == PictureConfig.SINGLE && config.isSingleDirectReturn;
        mTvPictureRight.setVisibility(isChooseMode ? View.GONE : View.VISIBLE);
        mTvPictureRight.setOnClickListener(this);
        setAlbumLayoutParams(isChooseMode);
    }

    @Override
    public void initPictureSelectorStyle() {
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground != 0) {
                mTvPictureRight.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground);
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor != 0) {
                mBottomLayout.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextColor.length > 0) {
                ColorStateList colorStateList = AttrsUtils.getColorStateList(PictureSelectionConfig.uiStyle.picture_top_titleRightTextColor);
                if (colorStateList != null) {
                    mTvPictureRight.setTextColor(colorStateList);
                }
            } else {
                mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize != 0) {
                mTvPictureRight.setTextSize(PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize);
            }

            if (config.isOriginalControl) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize);
                }
            }
            if (PictureSelectionConfig.uiStyle.picture_container_backgroundColor != 0) {
                container.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_container_backgroundColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleAlbumBackground != 0) {
                rlAlbum.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleAlbumBackground);
            } else {
                rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            }

            if (PictureSelectionConfig.uiStyle.picture_album_horizontal) {
                setAlbumLayoutParams(true);
            }

            if (PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText !=0) {
                mTvPictureRight.setText(getString(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText));
            }

        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle != 0) {
                mTvPictureRight.setBackgroundResource(PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle);
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (PictureSelectionConfig.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(PictureSelectionConfig.style.pictureBottomBgColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
            } else {
                if (PictureSelectionConfig.style.pictureCancelTextColor != 0) {
                    mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureCancelTextColor);
                } else {
                    mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
            }
            if (PictureSelectionConfig.style.pictureRightTextSize != 0) {
                mTvPictureRight.setTextSize(PictureSelectionConfig.style.pictureRightTextSize);
            }
            if (PictureSelectionConfig.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (config.isOriginalControl) {
                if (PictureSelectionConfig.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
            if (PictureSelectionConfig.style.pictureContainerBackgroundColor != 0) {
                container.setBackgroundColor(PictureSelectionConfig.style.pictureContainerBackgroundColor);
            }

            if (PictureSelectionConfig.style.pictureWeChatTitleBackgroundStyle != 0) {
                rlAlbum.setBackgroundResource(PictureSelectionConfig.style.pictureWeChatTitleBackgroundStyle);
            } else {
                rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            }

            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureRight.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            }

        } else {
            mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            int pictureBottomBgColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_bottom_bg);
            mBottomLayout.setBackgroundColor(pictureBottomBgColor != 0
                    ? pictureBottomBgColor : ContextCompat.getColor(getContext(), R.color.picture_color_grey));

            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.picture_icon_wechat_down);
            mIvArrow.setImageDrawable(drawable);
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }
        super.initPictureSelectorStyle();
        goneParentView();
    }

    /**
     * setting album LayoutParams
     * @param isHorizontal
     */
    private void setAlbumLayoutParams(boolean isHorizontal){
        if (rlAlbum.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlAlbum.getLayoutParams();
            if (isHorizontal) {
                lp.addRule(RelativeLayout.RIGHT_OF, RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            } else {
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.pictureLeftBack);
            }
        }
    }

    /**
     * Hide views that are not needed by the parent container
     */
    private void goneParentView() {
        mTvPictureImgNum.setVisibility(View.GONE);
        mTvPictureOk.setVisibility(View.GONE);
    }

    @Override
    protected void changeImageNumber(List<LocalMedia> selectData) {
        int size = selectData.size();
        boolean enable = size != 0;
        if (enable) {
            mTvPictureRight.setEnabled(true);
            mTvPictureRight.setSelected(true);
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            initCompleteText(selectData);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground != 0) {
                    mTvPictureRight.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground);
                } else {
                    mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_previewTextColor.length > 0) {
                    ColorStateList colorStateList = AttrsUtils.getColorStateList(PictureSelectionConfig.uiStyle.picture_bottom_previewTextColor);
                    if (colorStateList != null) {
                        mTvPicturePreview.setTextColor(colorStateList);
                    }
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_previewNormalText !=0) {
                    if (PictureSelectionConfig.uiStyle.isCompleteReplaceNum) {
                        mTvPicturePreview.setText(String.format(getString(PictureSelectionConfig.uiStyle.picture_bottom_previewNormalText), size));
                    } else {
                        mTvPicturePreview.setText(PictureSelectionConfig.uiStyle.picture_bottom_previewNormalText);
                    }
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
                }
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                    mTvPictureRight.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
                } else {
                    mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                    mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
                } else {
                    mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (PictureSelectionConfig.style.picturePreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(PictureSelectionConfig.style.picturePreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (!TextUtils.isEmpty(PictureSelectionConfig.style.picturePreviewText)) {
                    mTvPicturePreview.setText(PictureSelectionConfig.style.picturePreviewText);
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
                }
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_bg);
                mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
            }
        } else {
            mTvPictureRight.setEnabled(false);
            mTvPictureRight.setSelected(false);
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground != 0) {
                    mTvPictureRight.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground);
                } else {
                    mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText !=0) {
                    mTvPictureRight.setText(getString(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText));
                } else {
                    mTvPictureRight.setText(getString(R.string.picture_send));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_previewDefaultText !=0) {
                    mTvPicturePreview.setText(getString(PictureSelectionConfig.uiStyle.picture_bottom_previewDefaultText));
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview));
                }
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle != 0) {
                    mTvPictureRight.setBackgroundResource(PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle);
                } else {
                    mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                    mTvPictureRight.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
                } else {
                    mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
                if (PictureSelectionConfig.style.pictureUnPreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(PictureSelectionConfig.style.pictureUnPreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
                if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                    mTvPictureRight.setText(PictureSelectionConfig.style.pictureUnCompleteText);
                } else {
                    mTvPictureRight.setText(getString(R.string.picture_send));
                }
                if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnPreviewText)) {
                    mTvPicturePreview.setText(PictureSelectionConfig.style.pictureUnPreviewText);
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview));
                }
            } else {
                mTvPictureRight.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                mTvPictureRight.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                mTvPicturePreview.setText(getString(R.string.picture_preview));
                mTvPictureRight.setText(getString(R.string.picture_send));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_right) {
            if (folderWindow != null
                    && folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                mTvPictureOk.performClick();
            }
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void onChangeData(List<LocalMedia> list) {
        super.onChangeData(list);
        initCompleteText(list);
    }

    @Override
    protected void initCompleteText(List<LocalMedia> list) {
        int size = list.size();
        boolean isNotEmptyStyle = PictureSelectionConfig.style != null;
        if (config.isWithVideoImage) {
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (size <= 0) {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, 1));
                    } else {
                        mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, config.maxSelectNum));
                } else {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num, size, config.maxSelectNum));
                }
            }
        } else {
            String mimeType = list.get(0).getMimeType();
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == PictureConfig.SINGLE) {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, 1));
                } else {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                            ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureRight.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, maxSize));
                } else {
                    mTvPictureRight.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num, size, maxSize));
                }
            }
        }
    }
}
