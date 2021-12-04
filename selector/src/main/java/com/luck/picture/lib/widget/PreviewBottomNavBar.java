package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：PreviewBottomNavBar
 */
public class PreviewBottomNavBar extends BottomNavBar {

    public PreviewBottomNavBar(Context context) {
        super(context);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        tvPreview.setVisibility(GONE);
        tvImageEditor.setOnClickListener(this);
        tvImageEditor.setVisibility(PictureSelectionConfig.editMediaEventListener != null ? View.VISIBLE : GONE);
    }

    public void isDisplayEditor(boolean isHasVideo) {
        tvImageEditor.setVisibility(PictureSelectionConfig.editMediaEventListener != null && !isHasVideo ? View.VISIBLE : GONE);
    }

    @Override
    public void setBottomNavBarStyle() {
        super.setBottomNavBarStyle();
        BottomNavBarStyle bottomBarStyle = PictureSelectionConfig.selectorStyle.getBottomBarStyle();
        int backgroundColor = bottomBarStyle.getBottomPreviewNarBarBackgroundColor();
        if (StyleUtils.checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.ps_tv_editor) {
            if (bottomNavBarListener != null) {
                bottomNavBarListener.onEditImage();
            }
        }
    }
}
