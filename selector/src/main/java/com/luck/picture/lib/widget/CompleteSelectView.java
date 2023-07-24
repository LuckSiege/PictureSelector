package com.luck.picture.lib.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ValueOf;

/**
 * @author：luck
 * @date：2021/11/21 11:28 下午
 * @describe：CompleteSelectView
 */
public class CompleteSelectView extends LinearLayout {
    private TextView tvSelectNum;
    private TextView tvComplete;
    private Animation numberChangeAnimation;
    private SelectorConfig config;

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
        inflateLayout();
        setOrientation(LinearLayout.HORIZONTAL);
        tvSelectNum = findViewById(R.id.ps_tv_select_num);
        tvComplete = findViewById(R.id.ps_tv_complete);
        setGravity(Gravity.CENTER_VERTICAL);
        numberChangeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
        config = SelectorProviders.getInstance().getSelectorConfig();
    }

    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_complete_selected_layout, this);
    }

    /**
     * 完成选择按钮样式
     */
    public void setCompleteSelectViewStyle() {
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        SelectMainStyle selectMainStyle = selectorStyle.getSelectMainStyle();
        if (StyleUtils.checkStyleValidity(selectMainStyle.getSelectNormalBackgroundResources())) {
            setBackgroundResource(selectMainStyle.getSelectNormalBackgroundResources());
        }
        String selectNormalText = StyleUtils.checkStyleValidity(selectMainStyle.getSelectNormalTextResId())
                ? getContext().getString(selectMainStyle.getSelectNormalTextResId()) : selectMainStyle.getSelectNormalText();
        if (StyleUtils.checkTextValidity(selectNormalText)) {
            int formatCount = StyleUtils.getFormatCount(selectNormalText);
            if (formatCount == 1) {
                tvComplete.setText(String.format(selectNormalText, config.getSelectCount()));
            } else if (formatCount == 2) {
                tvComplete.setText(String.format(selectNormalText, config.getSelectCount(), config.maxSelectNum));
            } else {
                tvComplete.setText(selectNormalText);
            }
        }

        int selectNormalTextSize = selectMainStyle.getSelectNormalTextSize();
        if (StyleUtils.checkSizeValidity(selectNormalTextSize)) {
            tvComplete.setTextSize(selectNormalTextSize);
        }

        int selectNormalTextColor = selectMainStyle.getSelectNormalTextColor();
        if (StyleUtils.checkStyleValidity(selectNormalTextColor)) {
            tvComplete.setTextColor(selectNormalTextColor);
        }

        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();

        if (bottomBarStyle.isCompleteCountTips()) {
            int selectNumRes = bottomBarStyle.getBottomSelectNumResources();
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
    }

    /**
     * 选择结果发生变化
     */
    public void setSelectedChange(boolean isPreview) {
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        SelectMainStyle selectMainStyle = selectorStyle.getSelectMainStyle();
        if (config.getSelectCount() > 0) {
            setEnabled(true);
            int selectBackground = selectMainStyle.getSelectBackgroundResources();
            if (StyleUtils.checkStyleValidity(selectBackground)) {
                setBackgroundResource(selectBackground);
            } else {
                setBackgroundResource(R.drawable.ps_ic_trans_1px);
            }
            String selectText = StyleUtils.checkStyleValidity(selectMainStyle.getSelectTextResId())
                    ? getContext().getString(selectMainStyle.getSelectTextResId()) : selectMainStyle.getSelectText();
            if (StyleUtils.checkTextValidity(selectText)) {
                int formatCount = StyleUtils.getFormatCount(selectText);
                if (formatCount == 1) {
                    tvComplete.setText(String.format(selectText, config.getSelectCount()));
                } else if (formatCount == 2) {
                    tvComplete.setText(String.format(selectText, config.getSelectCount(), config.maxSelectNum));
                } else {
                    tvComplete.setText(selectText);
                }
            } else {
                tvComplete.setText(getContext().getString(R.string.ps_completed));
            }
            int selectTextSize = selectMainStyle.getSelectTextSize();
            if (StyleUtils.checkSizeValidity(selectTextSize)) {
                tvComplete.setTextSize(selectTextSize);
            }
            int selectTextColor = selectMainStyle.getSelectTextColor();
            if (StyleUtils.checkStyleValidity(selectTextColor)) {
                tvComplete.setTextColor(selectTextColor);
            } else {
                tvComplete.setTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_fa632d));
            }
            if (selectorStyle.getBottomBarStyle().isCompleteCountTips()) {
                if (tvSelectNum.getVisibility() == GONE || tvSelectNum.getVisibility() == INVISIBLE) {
                    tvSelectNum.setVisibility(VISIBLE);
                }
                if (TextUtils.equals(ValueOf.toString(config.getSelectCount()), tvSelectNum.getText())) {
                    // ignore
                } else {
                    tvSelectNum.setText(ValueOf.toString(config.getSelectCount()));
                    if (config.onSelectAnimListener != null) {
                        config.onSelectAnimListener.onSelectAnim(tvSelectNum);
                    } else {
                        tvSelectNum.startAnimation(numberChangeAnimation);
                    }
                }
            } else {
                tvSelectNum.setVisibility(GONE);
            }
        } else {
            if (isPreview && selectMainStyle.isCompleteSelectRelativeTop()) {
                setEnabled(true);
                int selectBackground = selectMainStyle.getSelectBackgroundResources();
                if (StyleUtils.checkStyleValidity(selectBackground)) {
                    setBackgroundResource(selectBackground);
                } else {
                    setBackgroundResource(R.drawable.ps_ic_trans_1px);
                }
                int selectTextColor = selectMainStyle.getSelectTextColor();
                if (StyleUtils.checkStyleValidity(selectTextColor)) {
                    tvComplete.setTextColor(selectTextColor);
                } else {
                    tvComplete.setTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                }
            } else {
                setEnabled(config.isEmptyResultReturn);
                int normalBackground = selectMainStyle.getSelectNormalBackgroundResources();
                if (StyleUtils.checkStyleValidity(normalBackground)) {
                    setBackgroundResource(normalBackground);
                } else {
                    setBackgroundResource(R.drawable.ps_ic_trans_1px);
                }
                int normalTextColor = selectMainStyle.getSelectNormalTextColor();
                if (StyleUtils.checkStyleValidity(normalTextColor)) {
                    tvComplete.setTextColor(normalTextColor);
                } else {
                    tvComplete.setTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
                }
            }

            tvSelectNum.setVisibility(GONE);
            String selectNormalText = StyleUtils.checkStyleValidity(selectMainStyle.getSelectNormalTextResId())
                    ? getContext().getString(selectMainStyle.getSelectNormalTextResId()) : selectMainStyle.getSelectNormalText();
            if (StyleUtils.checkTextValidity(selectNormalText)) {
                int formatCount = StyleUtils.getFormatCount(selectNormalText);
                if (formatCount == 1) {
                    tvComplete.setText(String.format(selectNormalText, config.getSelectCount()));
                } else if (formatCount == 2) {
                    tvComplete.setText(String.format(selectNormalText, config.getSelectCount(), config.maxSelectNum));
                } else {
                    tvComplete.setText(selectNormalText);
                }
            } else {
                tvComplete.setText(getContext().getString(R.string.ps_please_select));
            }
            int normalTextSize = selectMainStyle.getSelectNormalTextSize();
            if (StyleUtils.checkSizeValidity(normalTextSize)) {
                tvComplete.setTextSize(normalTextSize);
            }
        }
    }
}
