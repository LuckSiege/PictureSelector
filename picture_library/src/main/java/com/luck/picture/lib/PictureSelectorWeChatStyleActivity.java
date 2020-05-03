package com.luck.picture.lib;


import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AttrsUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 13:27
 * @describe：PictureSelector WeChatStyle
 */
public class PictureSelectorWeChatStyleActivity extends PictureSelectorActivity {
    private TextView mPictureSendView;
    private RelativeLayout rlAlbum;


    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_selector;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        rlAlbum = findViewById(R.id.rlAlbum);
        mPictureSendView = findViewById(R.id.picture_send);
        mPictureSendView.setOnClickListener(this);
        mPictureSendView.setText(getString(R.string.picture_send));
        mTvPicturePreview.setTextSize(16);
        mCbOriginal.setTextSize(16);
        boolean isChooseMode = config.selectionMode ==
                PictureConfig.SINGLE && config.isSingleDirectReturn;
        mPictureSendView.setVisibility(isChooseMode ? View.GONE : View.VISIBLE);
        if (rlAlbum.getLayoutParams() != null
                && rlAlbum.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlAlbum.getLayoutParams();
            if (isChooseMode) {
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            } else {
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.pictureLeftBack);
            }
        }
    }

    @Override
    public void initPictureSelectorStyle() {
        if (config.style != null) {
            if (config.style.pictureUnCompleteBackgroundStyle != 0) {
                mPictureSendView.setBackgroundResource(config.style.pictureUnCompleteBackgroundStyle);
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (config.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(config.style.pictureBottomBgColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (config.style.pictureUnCompleteTextColor != 0) {
                mPictureSendView.setTextColor(config.style.pictureUnCompleteTextColor);
            } else {
                if (config.style.pictureCancelTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureCancelTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
            }
            if (config.style.pictureRightTextSize != 0) {
                mPictureSendView.setTextSize(config.style.pictureRightTextSize);
            }
            if (config.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (config.isOriginalControl) {
                if (config.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
            if (config.style.pictureContainerBackgroundColor != 0) {
                container.setBackgroundColor(config.style.pictureContainerBackgroundColor);
            }

            if (config.style.pictureWeChatTitleBackgroundStyle != 0) {
                rlAlbum.setBackgroundResource(config.style.pictureWeChatTitleBackgroundStyle);
            } else {
                rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            }

            if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                mPictureSendView.setText(config.style.pictureUnCompleteText);
            }

        } else {
            mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
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
     * Hide views that are not needed by the parent container
     */
    private void goneParentView() {
        mTvPictureRight.setVisibility(View.GONE);
        mTvPictureImgNum.setVisibility(View.GONE);
        mTvPictureOk.setVisibility(View.GONE);
    }

    @Override
    protected void changeImageNumber(List<LocalMedia> selectData) {
        if (mPictureSendView == null) {
            return;
        }
        int size = selectData.size();
        boolean enable = size != 0;
        if (enable) {
            mPictureSendView.setEnabled(true);
            mPictureSendView.setSelected(true);
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            initCompleteText(selectData);
            if (config.style != null) {
                if (config.style.pictureCompleteBackgroundStyle != 0) {
                    mPictureSendView.setBackgroundResource(config.style.pictureCompleteBackgroundStyle);
                } else {
                    mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                if (config.style.pictureCompleteTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureCompleteTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (config.style.picturePreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.picturePreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (!TextUtils.isEmpty(config.style.picturePreviewText)) {
                    mTvPicturePreview.setText(config.style.picturePreviewText);
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
                }
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
                mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
            }
        } else {
            mPictureSendView.setEnabled(false);
            mPictureSendView.setSelected(false);
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (config.style != null) {
                if (config.style.pictureUnCompleteBackgroundStyle != 0) {
                    mPictureSendView.setBackgroundResource(config.style.pictureUnCompleteBackgroundStyle);
                } else {
                    mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (config.style.pictureUnCompleteTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureUnCompleteTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
                if (config.style.pictureUnPreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
                if (!TextUtils.isEmpty(config.style.pictureUnCompleteText)) {
                    mPictureSendView.setText(config.style.pictureUnCompleteText);
                } else {
                    mPictureSendView.setText(getString(R.string.picture_send));
                }
                if (!TextUtils.isEmpty(config.style.pictureUnPreviewText)) {
                    mTvPicturePreview.setText(config.style.pictureUnPreviewText);
                } else {
                    mTvPicturePreview.setText(getString(R.string.picture_preview));
                }
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                mTvPicturePreview.setText(getString(R.string.picture_preview));
                mPictureSendView.setText(getString(R.string.picture_send));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.picture_send) {
            if (folderWindow != null
                    && folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                mTvPictureOk.performClick();
            }
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
        boolean isNotEmptyStyle = config.style != null;
        if (config.isWithVideoImage) {
            if (config.selectionMode == PictureConfig.SINGLE) {
                if (size <= 0) {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                        mPictureSendView.setText(String.format(config.style.pictureCompleteText, size, 1));
                    } else {
                        mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureCompleteText)
                                ? config.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mPictureSendView.setText(String.format(config.style.pictureCompleteText, size, config.maxSelectNum));
                } else {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send_num, size, config.maxSelectNum));
                }
            }
        } else {
            String mimeType = list.get(0).getMimeType();
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == PictureConfig.SINGLE) {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mPictureSendView.setText(String.format(config.style.pictureCompleteText, size, 1));
                } else {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureCompleteText)
                            ? config.style.pictureCompleteText : getString(R.string.picture_send));
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && config.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(config.style.pictureCompleteText)) {
                    mPictureSendView.setText(String.format(config.style.pictureCompleteText, size, maxSize));
                } else {
                    mPictureSendView.setText(isNotEmptyStyle && !TextUtils.isEmpty(config.style.pictureUnCompleteText)
                            ? config.style.pictureUnCompleteText : getString(R.string.picture_send_num, size, maxSize));
                }
            }
        }
    }
}
