package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.listener.OnRecyclerViewPreloadMoreListener;

/**
 * @author：luck
 * @date：2020-04-14 18:43
 * @describe：RecyclerPreloadView
 */
public class RecyclerPreloadView extends RecyclerView {
    private static final String TAG = RecyclerPreloadView.class.getSimpleName();
    private static final int BOTTOM_DEFAULT = 1;
    public static final int BOTTOM_PRELOAD = 2;
    public boolean isInTheBottom = false;
    public boolean isScroll = true;
    /**
     * reachBottomRow = 1;(default)
     * mean : when the lastVisibleRow is lastRow , call the onReachBottom();
     * reachBottomRow = 2;
     * mean : when the lastVisibleRow is Penultimate Row , call the onReachBottom();
     * And so on
     */
    private int reachBottomRow = BOTTOM_DEFAULT;

    public RecyclerPreloadView(@NonNull Context context) {
        super(context);
    }

    public RecyclerPreloadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerPreloadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setReachBottomRow(int reachBottomRow) {
        if (reachBottomRow < 1)
            reachBottomRow = 1;
        this.reachBottomRow = reachBottomRow;
    }


    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (onRecyclerViewPreloadListener != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager == null) {
                throw new RuntimeException("LayoutManager is null,Please check it!");
            }
            Adapter adapter = getAdapter();
            if (adapter == null) {
                throw new RuntimeException("Adapter is null,Please check it!");
            }
            if (isScroll) {
                boolean isReachBottom = false;
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    int rowCount = adapter.getItemCount() / gridLayoutManager.getSpanCount();
                    int lastVisibleRowPosition = gridLayoutManager.findLastVisibleItemPosition() / gridLayoutManager.getSpanCount();
                    isReachBottom = (lastVisibleRowPosition >= rowCount - reachBottomRow);
                }

                if (!isReachBottom) {
                    isInTheBottom = false;
                } else if (!isInTheBottom) {
                    onRecyclerViewPreloadListener.onRecyclerViewPreloadMore();
                    if (dy > 0) {
                        isInTheBottom = true;
                    }
                } else {
                    // 属于首次进入屏幕未滑动且内容未超过一屏，用于确保分页数设置过小导致内容不足二次上拉加载...
                    if (dy == 0) {
                        isInTheBottom = false;
                    }
                }
            }
        }
    }


    private OnRecyclerViewPreloadMoreListener onRecyclerViewPreloadListener;

    public void setOnRecyclerViewPreloadListener(OnRecyclerViewPreloadMoreListener onRecyclerViewPreloadListener) {
        this.onRecyclerViewPreloadListener = onRecyclerViewPreloadListener;
    }

    public void setOnRecyclerViewPreloadListener(OnRecyclerViewPreloadMoreListener onRecyclerViewPreloadListener, boolean isScroll) {
        this.onRecyclerViewPreloadListener = onRecyclerViewPreloadListener;
        this.isScroll = isScroll;
    }
}
