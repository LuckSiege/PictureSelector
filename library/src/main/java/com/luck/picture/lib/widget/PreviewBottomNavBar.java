package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.luck.picture.lib.R;

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
        tvImageEditor.setVisibility(VISIBLE);
        tvImageEditor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.picture_tv_editor) {
            if (bottomNavBarListener != null) {
                bottomNavBarListener.onEditImage();
            }
        }
    }
}
