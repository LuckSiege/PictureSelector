package com.luck.picture.lib;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AttrsUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 13:27
 * @describe：PictureSelector 微信风格
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
        adapter.bindSelectImages(selectionMedias);
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
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.picture_left_back);
            }
        }
    }

    @Override
    public void initPictureSelectorStyle() {
        super.initPictureSelectorStyle();
        goneParentView();
        if (config.style != null) {
            if (config.style.pictureRightDefaultBackgroundStyle != 0) {
                mPictureSendView.setBackgroundResource(config.style.pictureRightDefaultBackgroundStyle);
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (config.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(config.style.pictureBottomBgColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (config.style.pictureRightDefaultTextColor != 0) {
                mPictureSendView.setTextColor(config.style.pictureRightDefaultTextColor);
            } else {
                if (config.style.pictureCancelTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureCancelTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
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
    }

    /**
     * 隐藏父容器不需要的View
     */
    private void goneParentView() {
        mTvPictureRight.setVisibility(View.GONE);
        mTvPictureImgNum.setVisibility(View.GONE);
        mTvPictureOk.setVisibility(View.GONE);
    }

    @Override
    protected void changeImageNumber(List<LocalMedia> selectImages) {
        if (mPictureSendView == null) {
            return;
        }
        int size = selectImages.size();
        boolean enable = size != 0;
        if (enable) {
            // 可发送
            mPictureSendView.setEnabled(true);
            mPictureSendView.setSelected(true);
            mPictureSendView.setText(config.selectionMode == PictureConfig.SINGLE ? getString(R.string.picture_send)
                    : getString(R.string.picture_send_num, size, config.maxSelectNum));
            // 可预览
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            mTvPicturePreview.setText(getString(R.string.picture_preview_num, size));
            if (config.style != null) {
                if (config.style.pictureRightBackgroundStyle != 0) {
                    mPictureSendView.setBackgroundResource(config.style.pictureRightBackgroundStyle);
                } else {
                    mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                if (config.style.pictureRightSelectedTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureRightSelectedTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                if (config.style.picturePreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.picturePreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_bg);
                mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
            }
        } else {
            // 未选择
            mPictureSendView.setEnabled(false);
            mPictureSendView.setSelected(false);
            // 不可预览
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (config.style != null) {
                if (config.style.pictureRightDefaultBackgroundStyle != 0) {
                    mPictureSendView.setBackgroundResource(config.style.pictureRightDefaultBackgroundStyle);
                } else {
                    mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (config.style.pictureRightDefaultTextColor != 0) {
                    mPictureSendView.setTextColor(config.style.pictureRightDefaultTextColor);
                } else {
                    mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
                if (config.style.pictureUnPreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(config.style.pictureUnPreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
            } else {
                mPictureSendView.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                mPictureSendView.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
            }
            mTvPicturePreview.setText(getString(R.string.picture_preview));
            mPictureSendView.setText(getString(R.string.picture_send));
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
                // 发送 走父类的已完成流程
                mTvPictureOk.performClick();
            }
        }
    }
}
