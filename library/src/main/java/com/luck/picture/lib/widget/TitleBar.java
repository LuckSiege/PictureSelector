package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/17 10:45 上午
 * @describe：TitleBar
 */
public class TitleBar extends RelativeLayout implements View.OnClickListener {

    protected RelativeLayout rlAlbumBg;
    protected ImageView ivLeftBack;
    protected ImageView ivArrow;
    protected MarqueeTextView tvTitle;
    protected TextView tvRightMenu;
    protected View viewChecked;
    protected View viewAlbumClickArea;

    public TitleBar(Context context) {
        super(context);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflate(getContext(), R.layout.ps_title_bar, this);
        ivLeftBack = findViewById(R.id.picture_iv_left_back);
        rlAlbumBg = findViewById(R.id.picture_rl_album_bg);
        viewAlbumClickArea = findViewById(R.id.picture_rl_album_click);
        tvTitle = findViewById(R.id.picture_tv_title);
        ivArrow = findViewById(R.id.picture_iv_arrow);
        tvRightMenu = findViewById(R.id.picture_tv_right_menu);
        viewChecked = findViewById(R.id.view_checked);
        ivLeftBack.setOnClickListener(this);
        tvRightMenu.setOnClickListener(this);
        rlAlbumBg.setOnClickListener(this);
        viewAlbumClickArea.setOnClickListener(this);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
    }

    public ImageView getImageArrow() {
        return ivArrow;
    }

    /**
     * Set title
     *
     * @param title
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitleBarStyle() {
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
        int titleBarHeight = titleBarStyle.getTitleBarHeight();
        if (StyleUtils.checkSizeValidity(titleBarHeight)) {
            getLayoutParams().height = titleBarHeight;
        } else {
            getLayoutParams().height = DensityUtil.dip2px(getContext(), 48);
        }
        int backgroundColor = titleBarStyle.getTitleBackgroundColor();
        if (StyleUtils.checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor);
        }
        int backResId = titleBarStyle.getTitleLeftBackRes();
        if (StyleUtils.checkStyleValidity(backResId)) {
            ivLeftBack.setImageResource(backResId);
        }
        String titleDefaultText = titleBarStyle.getTitleDefaultText();
        if (StyleUtils.checkTextValidity(titleDefaultText)) {
            tvTitle.setText(titleDefaultText);
        }
        int titleTextSize = titleBarStyle.getTitleTextSize();
        if (StyleUtils.checkSizeValidity(titleTextSize)) {
            tvTitle.setTextSize(titleTextSize);
        }
        int titleTextColor = titleBarStyle.getTitleTextColor();
        if (StyleUtils.checkStyleValidity(titleTextColor)) {
            tvTitle.setTextColor(titleTextColor);
        }
        int arrowResId = titleBarStyle.getTitleDrawableRightRes();
        if (StyleUtils.checkStyleValidity(arrowResId)) {
            ivArrow.setImageResource(arrowResId);
        }
        int albumBackgroundRes = titleBarStyle.getTitleAlbumBackgroundRes();
        if (StyleUtils.checkStyleValidity(albumBackgroundRes)) {
            rlAlbumBg.setBackgroundResource(albumBackgroundRes);
        }
        boolean isGravityLeft = titleBarStyle.isTitleGravityLeft();
        if (isGravityLeft) {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) rlAlbumBg.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.END_OF, R.id.picture_iv_left_back);
        }
        int rightBackgroundRes = titleBarStyle.getTitleRightBackgroundRes();
        if (StyleUtils.checkStyleValidity(rightBackgroundRes)) {
            tvRightMenu.setBackgroundResource(rightBackgroundRes);
        }
        String rightNormalText = titleBarStyle.getTitleRightNormalText();
        if (StyleUtils.checkTextValidity(rightNormalText)) {
            tvRightMenu.setText(rightNormalText);
        }
        int rightTextColor = titleBarStyle.getTitleRightTextColor();
        if (StyleUtils.checkStyleValidity(rightTextColor)) {
            tvRightMenu.setTextColor(rightTextColor);
        }
        int rightTextSize = titleBarStyle.getTitleRightTextSize();
        if (StyleUtils.checkSizeValidity(rightTextSize)) {
            tvRightMenu.setTextSize(rightTextSize);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.picture_iv_left_back || id == R.id.picture_tv_right_menu) {
            if (titleBarListener != null) {
                titleBarListener.onBackPressed();
            }
        } else if (id == R.id.picture_rl_album_bg || id == R.id.picture_rl_album_click) {
            if (titleBarListener != null) {
                titleBarListener.onShowAlbumPopWindow(this);
            }
        }
    }

    protected OnTitleBarListener titleBarListener;

    /**
     * TitleBar的功能事件回调
     *
     * @param listener
     */
    public void setOnTitleBarListener(OnTitleBarListener listener) {
        this.titleBarListener = listener;
    }

    public static class OnTitleBarListener {
        /**
         * 关闭页面
         */
        public void onBackPressed() {

        }

        /**
         * 显示专辑列表
         */
        public void onShowAlbumPopWindow(View anchor) {

        }
    }
}
