package com.luck.picture.lib.magical;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/12/15 2:15 下午
 * @describe：BuildViewParams
 */
public class BuildRecycleItemViewParams {

    private static final List<ViewParams> viewParamsData = new ArrayList<>();

    public static ViewParams getItemViewParams(int position) {
        ViewParams viewParams = null;
        if (viewParamsData.size() > position) {
            viewParams = viewParamsData.get(position);
        }
        if (viewParams == null) {
            viewParams = new ViewParams();
        }
        return viewParams;
    }


    public static void generateViewParams(RecyclerView recyclerView, @IdRes int viewId) {
        if (viewParamsData.size() > 0) {
            viewParamsData.clear();
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = recyclerView.getChildAt(i).findViewById(viewId);
            if (view == null) {
                continue;
            }
            ViewParams viewParams = new ViewParams();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            viewParams.left = location[0];
            viewParams.top = location[1];
            viewParams.width = view.getWidth();
            viewParams.height = view.getHeight();
            viewParamsData.add(viewParams);
        }
    }
}
