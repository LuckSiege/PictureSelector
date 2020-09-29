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
    public boolean isEnabledLoadMore = false;
    private int mFirstVisiblePosition, mLastVisiblePosition;
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

    /**
     * Whether to load more
     *
     * @param isEnabledLoadMore
     */
    public void setEnabledLoadMore(boolean isEnabledLoadMore) {
        this.isEnabledLoadMore = isEnabledLoadMore;
    }

    /**
     * Whether to load more
     */
    public boolean isEnabledLoadMore() {
        return isEnabledLoadMore;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_DRAGGING) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager linearManager = (GridLayoutManager) layoutManager;
                mFirstVisiblePosition = linearManager.findFirstVisibleItemPosition();
                mLastVisiblePosition = linearManager.findLastVisibleItemPosition();
            }
        }
    }

    /**
     * Gets the first visible position index
     *
     * @return
     */
    public int getFirstVisiblePosition() {
        return mFirstVisiblePosition;
    }

    /**
     * Gets the last visible position index
     *
     * @return
     */
    public int getLastVisiblePosition() {
        return mLastVisiblePosition;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (onRecyclerViewPreloadListener != null) {
            if (isEnabledLoadMore) {
                LayoutManager layoutManager = getLayoutManager();
                if (layoutManager == null) {
                    throw new RuntimeException("LayoutManager is null,Please check it!");
                }
                Adapter adapter = getAdapter();
                if (adapter == null) {
                    throw new RuntimeException("Adapter is null,Please check it!");
                }
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
}
