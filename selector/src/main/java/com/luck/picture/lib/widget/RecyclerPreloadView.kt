package com.luck.picture.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener
import kotlin.math.abs

/**
 * @author：luck
 * @date：2022/11/30 10:23 上午
 * @describe：RecyclerView loads more
 */
class RecyclerPreloadView : RecyclerView {
    companion object {
        const val BOTTOM_DEFAULT = 1
        const val BOTTOM_PRELOAD = 2
        const val LIMIT = 150
    }

    private var isInTheBottom = false
    private var isEnabledLoadMore = false
    private var mFirstVisiblePosition = 0
    private var mLastVisiblePosition: Int = 0

    /**
     * reachBottomRow = 1;(default)
     * mean : when the lastVisibleRow is lastRow , call the onReachBottom();
     * reachBottomRow = 2;
     * mean : when the lastVisibleRow is Penultimate Row , call the onReachBottom();
     * And so on
     */
    private var reachBottomRow = BOTTOM_DEFAULT

    private var onRecyclerViewScrollListener: OnRecyclerViewScrollListener? = null
    private var onRecyclerViewPreloadListener: OnRecyclerViewPreloadMoreListener? = null
    private var onRecyclerViewScrollStateListener: OnRecyclerViewScrollStateListener? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {

    }

    fun setReachBottomRow(reachBottomRow: Int) {
        this.reachBottomRow =
            if (reachBottomRow < BOTTOM_DEFAULT) BOTTOM_DEFAULT else reachBottomRow
    }

    /**
     * Whether to load more
     *
     * @param isEnabledLoadMore
     */
    fun setEnabledLoadMore(isEnabledLoadMore: Boolean) {
        this.isEnabledLoadMore = isEnabledLoadMore
    }

    /**
     * Whether to load more
     */
    fun isEnabledLoadMore(): Boolean {
        return isEnabledLoadMore
    }

    /**
     * Gets the first visible position index
     */
    fun getFirstVisiblePosition(): Int {
        return mFirstVisiblePosition
    }

    /**
     * Gets the last visible position index
     */
    fun getLastVisiblePosition(): Int {
        return mLastVisiblePosition
    }

    fun setLastVisiblePosition(position: Int) {
        mLastVisiblePosition = position
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        val layoutManager = layoutManager
            ?: throw RuntimeException("LayoutManager is null,Please check it!")
        setLayoutManagerPosition(layoutManager)
        if (onRecyclerViewPreloadListener != null) {
            if (isEnabledLoadMore) {
                val adapter = adapter ?: throw RuntimeException("Adapter is null,Please check it!")
                var isReachBottom = false
                if (layoutManager is GridLayoutManager) {
                    val rowCount = adapter.itemCount / layoutManager.spanCount
                    val lastVisibleRowPosition =
                        layoutManager.findLastVisibleItemPosition() / layoutManager.spanCount
                    isReachBottom = lastVisibleRowPosition >= rowCount - reachBottomRow
                }
                if (!isReachBottom) {
                    isInTheBottom = false
                } else if (!isInTheBottom) {
                    onRecyclerViewPreloadListener?.onPreloadMore()
                    if (dy > 0) {
                        isInTheBottom = true
                    }
                } else {
                    if (dy == 0) {
                        isInTheBottom = false
                    }
                }
            }
        }
        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener?.onScrolled(dx, dy)
        }
        if (onRecyclerViewScrollStateListener != null) {
            if (abs(dy) < LIMIT) {
                onRecyclerViewScrollStateListener?.onScrollSlow()
            } else {
                onRecyclerViewScrollStateListener?.onScrollFast()
            }
        }
    }

    private fun setLayoutManagerPosition(layoutManager: LayoutManager?) {
        if (layoutManager is GridLayoutManager) {
            mFirstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            mLastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            mFirstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            mLastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        }
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_DRAGGING) {
            setLayoutManagerPosition(layoutManager)
        }
        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener?.onScrollStateChanged(state)
        }
        if (state == SCROLL_STATE_IDLE) {
            if (onRecyclerViewScrollStateListener != null) {
                onRecyclerViewScrollStateListener?.onScrollSlow()
            }
        }
    }

    /**
     * RecyclerView loads more
     */
    fun setOnRecyclerViewPreloadListener(listener: OnRecyclerViewPreloadMoreListener?) {
        onRecyclerViewPreloadListener = listener
    }

    /**
     * RecyclerView Start and Pause Sliding
     */
    fun setOnRecyclerViewScrollStateListener(listener: OnRecyclerViewScrollStateListener?) {
        onRecyclerViewScrollStateListener = listener
    }

    /**
     * RecyclerView sliding status callback
     */
    fun setOnRecyclerViewScrollListener(listener: OnRecyclerViewScrollListener?) {
        onRecyclerViewScrollListener = listener
    }
}