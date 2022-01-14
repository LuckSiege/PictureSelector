package com.luck.pictureselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.widget.TitleBar;

/**
 * @author：luck
 * @date：2021/11/17 10:45 上午
 * @describe：CustomTitleBar
 */
public class CustomTitleBar extends TitleBar implements View.OnClickListener {


    public TextView getTitleCancelView() {
        return tvCancel;
    }

    public CustomTitleBar(Context context) {
        super(context);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_custom_title_bar, this);
    }

    @Override
    public void setTitleBarStyle() {
        super.setTitleBarStyle();
    }
}
