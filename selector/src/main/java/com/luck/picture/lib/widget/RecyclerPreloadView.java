package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener;
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener;

/**
 * @author：luck
 * @date：2020-04-14 18:43
 * @describe：RecyclerPreloadView
 */
public class RecyclerPreloadView extends RecyclerView {
    private static final String TAG = RecyclerPreloadView.class.getSimpleName();
    private static final int BOTTOM_DEFAULT = 1;
    public static final int BOTTOM_PRELOAD = 2;
    private static final int LIMIT = 150;
    private boolean isInTheBottom = false;
    private boolean isEnabledLoadMore = false;
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

    public void setLastVisiblePosition(int position) {
        this.mLastVisiblePosition = position;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            throw new RuntimeException("LayoutManager is null,Please check it!");
        }
        setLayoutManagerPosition(layoutManager);
        if (onRecyclerViewPreloadListener != null) {
            if (isEnabledLoadMore) {
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

        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener.onScrolled(dx, dy);
        }

        if (onRecyclerViewScrollStateListener != null) {
            if (Math.abs(dy) < LIMIT) {
                onRecyclerViewScrollStateListener.onScrollSlow();
            } else {
                onRecyclerViewScrollStateListener.onScrollFast();
            }
        }
    }

    private void setLayoutManagerPosition(LayoutManager layoutManager) {

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            mFirstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            mLastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mFirstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            mLastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
        }
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_DRAGGING) {
            setLayoutManagerPosition(getLayoutManager());
        }

        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener.onScrollStateChanged(state);
        }

        if (state == SCROLL_STATE_IDLE) {
            if (onRecyclerViewScrollStateListener != null) {
                onRecyclerViewScrollStateListener.onScrollSlow();
            }
        }
    }


    private OnRecyclerViewPreloadMoreListener onRecyclerViewPreloadListener;

    public void setOnRecyclerViewPreloadListener(OnRecyclerViewPreloadMoreListener listener) {
        this.onRecyclerViewPreloadListener = listener;
    }

    private OnRecyclerViewScrollStateListener onRecyclerViewScrollStateListener;

    public void setOnRecyclerViewScrollStateListener(OnRecyclerViewScrollStateListener listener) {
        this.onRecyclerViewScrollStateListener = listener;
    }

    private OnRecyclerViewScrollListener onRecyclerViewScrollListener;

    public void setOnRecyclerViewScrollListener(OnRecyclerViewScrollListener listener) {
        this.onRecyclerViewScrollListener = listener;
    }
}
