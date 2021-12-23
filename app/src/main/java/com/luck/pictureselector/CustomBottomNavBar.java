package com.luck.pictureselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.luck.picture.lib.widget.BottomNavBar;

/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：CustomBottomNavBar
 */
public class CustomBottomNavBar extends BottomNavBar implements View.OnClickListener {

    public CustomBottomNavBar(Context context) {
        super(context);
    }

    public CustomBottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_custom_bottom_nav_bar, this);
    }
}
