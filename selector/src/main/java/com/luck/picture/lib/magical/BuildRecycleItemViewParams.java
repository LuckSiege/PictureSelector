package com.luck.picture.lib.magical;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/12/17 1:19 下午
 * @describe：BuildRecycleItemViewParams
 */
public class BuildRecycleItemViewParams {

    private static final List<ViewParams> viewParams = new ArrayList<>();

    public static void clear() {
        if (viewParams.size() > 0) {
            viewParams.clear();
        }
    }

    public static ViewParams getItemViewParams(int position) {
        return viewParams.size() > position ? viewParams.get(position) : null;
    }

    public static void generateViewParams(ViewGroup viewGroup, int statusBarHeight) {
        List<View> views = new ArrayList<>();
        int childCount;
        if (viewGroup instanceof RecyclerView) {
            childCount = ((RecyclerView) viewGroup).getChildCount();
        } else if (viewGroup instanceof ListView) {
            childCount = ((ListView) viewGroup).getChildCount();
        } else {
            throw new IllegalArgumentException(viewGroup.getClass().getCanonicalName()
                    + " Must be " + RecyclerView.class + " or " + ListView.class);
        }
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view == null) {
                continue;
            }
            views.add(view);
        }
        int firstPos;
        int lastPos;
        int totalCount;
        if (viewGroup instanceof RecyclerView) {
            GridLayoutManager layoutManager = (GridLayoutManager) ((RecyclerView) viewGroup).getLayoutManager();
            if (layoutManager == null) {
                return;
            }
            totalCount = layoutManager.getItemCount();
            firstPos = layoutManager.findFirstVisibleItemPosition();
            lastPos = layoutManager.findLastVisibleItemPosition();
        } else {
            ListAdapter listAdapter = ((ListView) viewGroup).getAdapter();
            if (listAdapter == null) {
                return;
            }
            totalCount = listAdapter.getCount();
            firstPos = ((ListView) viewGroup).getFirstVisiblePosition();
            lastPos = ((ListView) viewGroup).getLastVisiblePosition();
        }
        lastPos = lastPos > totalCount ? totalCount - 1 : lastPos;
        fillPlaceHolder(views, totalCount, firstPos, lastPos);
        viewParams.clear();
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            ViewParams viewParam = new ViewParams();
            if (view == null) {
                viewParam.setLeft(0);
                viewParam.setTop(0);
                viewParam.setWidth(0);
                viewParam.setHeight(0);
            } else {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                viewParam.setLeft(location[0]);
                viewParam.setTop(location[1] - statusBarHeight);
                viewParam.setWidth(view.getWidth());
                viewParam.setHeight(view.getHeight());
            }
            viewParams.add(viewParam);
        }
    }

    private static void fillPlaceHolder(List<View> originImageList, int totalCount, int firstPos, int lastPos) {
        if (firstPos > 0) {
            for (int i = firstPos; i >= 1; i--) {
                originImageList.add(0, null);
            }
        }

        if (lastPos < totalCount) {
            for (int i = totalCount - 1 - lastPos; i >= 1; i--) {
                originImageList.add(null);
            }
        }
    }
}
