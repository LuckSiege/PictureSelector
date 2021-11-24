package com.luck.picture.lib.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:ViewPage2ItemDecoration
 */

public class ViewPage2ItemDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;

    public ViewPage2ItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        outRect.left = spacing - column * spacing / spanCount;
        outRect.right = (column + 1) * spacing / spanCount;
    }
}