package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/21 11:28 下午
 * @describe：CompleteSelectView
 */
public class CompleteSelectView extends LinearLayout {
    private TextView tvSelectNum;
    private TextView tvComplete;
    private Animation numberChangeAnimation;
    private PictureSelectionConfig config;

    public CompleteSelectView(Context context) {
        super(context);
        init();
    }

    public CompleteSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompleteSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ps_complete_selected_layout, this);
        setOrientation(LinearLayout.HORIZONTAL);
        tvSelectNum = findViewById(R.id.picture_tv_select_num);
        tvComplete = findViewById(R.id.picture_tv_complete);
        setGravity(Gravity.CENTER_VERTICAL);
        numberChangeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
        config = PictureSelectionConfig.getInstance();
    }

    public void setCompleteSelectViewStyle() {
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();
        String selectNormalText = bottomBarStyle.getBottomSelectNormalText();
        if (StyleUtils.checkTextValidity(selectNormalText)) {
            if (StyleUtils.checkTextTwoFormatValidity(selectNormalText)) {
                tvComplete.setText(String.format(selectNormalText, SelectedManager.getCount(), config.maxSelectNum));
            } else {
                tvComplete.setText(selectNormalText);
            }
        }

        int selectNormalTextSize = bottomBarStyle.getBottomSelectNormalTextSize();
        if (StyleUtils.checkSizeValidity(selectNormalTextSize)) {
            tvComplete.setTextSize(selectNormalTextSize);
        }

        int selectNormalTextColor = bottomBarStyle.getBottomSelectNormalTextColor();
        if (StyleUtils.checkStyleValidity(selectNormalTextColor)) {
            tvComplete.setTextColor(selectNormalTextColor);
        }

        int selectNumRes = bottomBarStyle.getBottomSelectNumRes();
        if (StyleUtils.checkStyleValidity(selectNumRes)) {
            tvSelectNum.setBackgroundResource(selectNumRes);
        }
        int selectNumTextSize = bottomBarStyle.getBottomSelectNumTextSize();
        if (StyleUtils.checkSizeValidity(selectNumTextSize)) {
            tvSelectNum.setTextSize(selectNumTextSize);
        }

        int selectNumTextColor = bottomBarStyle.getBottomSelectNumTextColor();
        if (StyleUtils.checkStyleValidity(selectNumTextColor)) {
            tvSelectNum.setTextColor(selectNumTextColor);
        }
    }

    /**
     * 选择结果发生变化
     */
    public void setSelectedChange() {
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();
        if (SelectedManager.getCount() > 0) {
            setEnabled(true);
            tvSelectNum.setVisibility(bottomBarStyle.isSelectNumVisible() ? VISIBLE : INVISIBLE);
            String selectText = bottomBarStyle.getBottomSelectText();
            if (StyleUtils.checkTextValidity(selectText)) {
                if (StyleUtils.checkTextTwoFormatValidity(selectText)) {
                    tvComplete.setText(String.format(selectText, SelectedManager.getCount(), config.maxSelectNum));
                } else {
                    tvComplete.setText(selectText);
                }
            } else {
                tvComplete.setText(getContext().getString(R.string.picture_completed));
            }

            int selectTextSize = bottomBarStyle.getBottomSelectTextSize();
            if (StyleUtils.checkSizeValidity(selectTextSize)) {
                tvComplete.setTextSize(selectTextSize);
            }
            int selectTextColor = bottomBarStyle.getBottomSelectTextColor();
            if (StyleUtils.checkStyleValidity(selectTextColor)) {
                tvComplete.setTextColor(selectTextColor);
            } else {
                tvComplete.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_fa632d));
            }
            tvSelectNum.setText(ValueOf.toString(SelectedManager.getCount()));
            tvSelectNum.startAnimation(numberChangeAnimation);
        } else {
            setEnabled(false);
            tvSelectNum.setVisibility(INVISIBLE);
            String selectNormalText = bottomBarStyle.getBottomSelectNormalText();
            if (StyleUtils.checkTextValidity(selectNormalText)) {
                if (StyleUtils.checkTextTwoFormatValidity(selectNormalText)) {
                    tvComplete.setText(String.format(selectNormalText, SelectedManager.getCount(), config.maxSelectNum));
                } else {
                    tvComplete.setText(selectNormalText);
                }
            } else {
                tvComplete.setText(getContext().getString(R.string.picture_please_select));
            }
            int normalTextSize = bottomBarStyle.getBottomSelectNormalTextSize();
            if (StyleUtils.checkSizeValidity(normalTextSize)) {
                tvComplete.setTextSize(normalTextSize);
            }
            int normalTextColor = bottomBarStyle.getBottomSelectNormalTextColor();
            if (StyleUtils.checkStyleValidity(normalTextColor)) {
                tvComplete.setTextColor(normalTextColor);
            } else {
                tvComplete.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
            }
        }
    }
}
