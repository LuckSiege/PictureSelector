package com.luck.picture.lib.magical;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author：luck
 * @date：2021/12/15 11:10 上午
 * @describe：MagicalViewWrapper
 */
public class MagicalViewWrapper {
    private final ViewGroup.MarginLayoutParams params;
    private final View viewWrapper;

    public MagicalViewWrapper(View view) {
        this.viewWrapper = view;
        params = (ViewGroup.MarginLayoutParams) viewWrapper.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).gravity = Gravity.START;
        }
    }

    public int getWidth() {
        return params.width;
    }

    public int getHeight() {
        return params.height;
    }

    public void setWidth(float width) {
        params.width = Math.round(width);
        viewWrapper.setLayoutParams(params);
    }

    public void setHeight(float height) {
        params.height = Math.round(height);
        viewWrapper.setLayoutParams(params);
    }

    public void setMarginTop(int m) {
        params.topMargin = m;
        viewWrapper.setLayoutParams(params);
    }

    public void setMarginBottom(int m) {
        params.bottomMargin = m;
        viewWrapper.setLayoutParams(params);
    }

    public int getMarginTop() {
        return params.topMargin;
    }

    public void setMarginRight(int mr) {
        params.rightMargin = mr;
        viewWrapper.setLayoutParams(params);
    }

    public void setMarginLeft(int mr) {
        params.leftMargin = mr;
        viewWrapper.setLayoutParams(params);
    }

    public int getMarginRight() {
        return params.rightMargin;
    }

    public int getMarginLeft() {
        return params.leftMargin;
    }

    public int getMarginBottom() {
        return params.bottomMargin;
    }
}
