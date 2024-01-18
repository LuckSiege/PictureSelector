package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * Created on 2024/1/17.
 *
 * @author wdeo3601@163.com
 * @description 安卓34+，READ_MEDIA_VISUAL_USER_SELECTED 授权后,提示用户重选媒体
 */
public class MediaReselectionTipView extends FrameLayout implements View.OnClickListener {

    protected TextView tvTip;
    protected TextView tvManage;
    protected SelectorConfig config;
    protected ConstraintLayout mediaReselectionTipLayout;

    public TextView getTitleManageView() {
        return tvManage;
    }

    public MediaReselectionTipView(Context context) {
        super(context);
        init();
    }

    public MediaReselectionTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaReselectionTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflateLayout();
        setClickable(true);
        setFocusable(true);
        config = SelectorProviders.getInstance().getSelectorConfig();
        mediaReselectionTipLayout = findViewById(R.id.cl_media_reselection_tip);
        tvTip = findViewById(R.id.ps_tv_title);
        tvManage = findViewById(R.id.ps_tv_manage);
        tvManage.setOnClickListener(this);
        mediaReselectionTipLayout.setOnClickListener(this);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
        handleLayoutUI();
    }

    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_media_reselection_tip_view, this);
    }

    protected void handleLayoutUI() {

    }

    public void setMediaReselectionTipViewStyle() {
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();

        int backgroundColor = bottomBarStyle.getBottomNarBarBackgroundColor();
        if (StyleUtils.checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor);
        }

        int previewNormalTextColor = bottomBarStyle.getBottomPreviewNormalTextColor();
        if (StyleUtils.checkSizeValidity(previewNormalTextColor)) {
            tvTip.setTextColor(previewNormalTextColor);
        }

        int previewSelectTextColor = bottomBarStyle.getBottomPreviewSelectTextColor();
        if (StyleUtils.checkStyleValidity(previewSelectTextColor)) {
            tvManage.setTextColor(previewSelectTextColor);
        }
        int previewNormalTextSize = bottomBarStyle.getBottomPreviewNormalTextSize();
        if (StyleUtils.checkSizeValidity(previewNormalTextSize)) {
            tvManage.setTextSize(previewNormalTextSize);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ps_tv_manage) {
            if (mediaReselectionListener != null) {
                mediaReselectionListener.onManageClick();
            }
        }
    }

    protected OnMediaReselectionListener mediaReselectionListener;

    /**
     * 功能事件回调
     *
     * @param listener
     */
    public void setOnMediaReselectionListener(OnMediaReselectionListener listener) {
        this.mediaReselectionListener = listener;
    }

    public static class OnMediaReselectionListener {
        public void onManageClick() {

        }
    }
}
