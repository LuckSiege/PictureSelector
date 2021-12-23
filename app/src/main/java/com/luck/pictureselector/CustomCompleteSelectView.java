package com.luck.pictureselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.luck.picture.lib.widget.CompleteSelectView;

/**
 * @author：luck
 * @date：2021/12/23 11:54 上午
 * @describe：CustomCompleteSelectView
 */
public class CustomCompleteSelectView extends CompleteSelectView {
    public CustomCompleteSelectView(Context context) {
        super(context);
    }

    public CustomCompleteSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCompleteSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_custom_complete_selected_layout, this);
    }

    @Override
    public void setCompleteSelectViewStyle() {
        super.setCompleteSelectViewStyle();
    }
}
