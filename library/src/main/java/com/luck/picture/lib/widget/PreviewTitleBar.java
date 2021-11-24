package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2021/11/19 4:38 下午
 * @describe：PreviewTitleBar
 */
public class PreviewTitleBar extends TitleBar {
    public PreviewTitleBar(Context context) {
        super(context);
    }

    public PreviewTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        ivArrow.setVisibility(GONE);
        viewAlbumClickArea.setVisibility(GONE);
        rlAlbumBg.setOnClickListener(null);
        viewAlbumClickArea.setOnClickListener(null);
        tvRightMenu.setText("");
        tvRightMenu.setTextSize(0);
        tvRightMenu.setBackgroundResource(R.drawable.ps_checkbox_selector);
    }

    public TextView getSelectedView() {
        return tvRightMenu;
    }

    public View getSelectedClickView() {
        if (viewChecked.getVisibility() == GONE) {
            viewChecked.setVisibility(View.VISIBLE);
        }
        return viewChecked;
    }
}
