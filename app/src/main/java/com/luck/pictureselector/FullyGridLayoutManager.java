package com.luck.pictureselector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */

public class FullyGridLayoutManager extends GridLayoutManager {
    public FullyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FullyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;
        int count = getItemCount();
        int span = getSpanCount();
        for (int i = 0; i < count; i++) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension);

            if (getOrientation() == HORIZONTAL) {
                if (i % span == 0) {
                    width = width + mMeasuredDimension[0];
                }
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                if (i % span == 0) {
                    height = height + mMeasuredDimension[1];
                }
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }

        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        setMeasuredDimension(width, height);
    }

    final RecyclerView.State mState = new RecyclerView.State();

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension) {
        int itemCount = mState.getItemCount();
        if (position < itemCount) {
            try {
                View view = recycler.getViewForPosition(0);
                if (view != null) {
                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                            getPaddingLeft() + getPaddingRight(), p.width);
                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                            getPaddingTop() + getPaddingBottom(), p.height);
                    view.measure(childWidthSpec, childHeightSpec);
                    measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}