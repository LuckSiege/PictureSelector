package com.luck.picture.lib.magical

import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @date：2021/12/17 1:19 下午
 * @describe：RecycleItemViewParams
 */
object RecycleItemViewParams {
    private val viewParams: MutableList<ViewParams> = ArrayList()

    fun clear() {
        if (viewParams.size > 0) {
            viewParams.clear()
        }
    }

    fun getItemViewParams(position: Int): ViewParams? {
        return if (viewParams.size > position) viewParams[position] else null
    }

    fun build(viewGroup: ViewGroup, statusBarHeight: Int) {
        val views: MutableList<View?> = ArrayList()
        val childCount: Int = when (viewGroup) {
            is RecyclerView -> {
                viewGroup.childCount
            }
            is ListView -> {
                viewGroup.childCount
            }
            else -> {
                throw IllegalArgumentException(viewGroup.javaClass.canonicalName
                        + " Must be " + RecyclerView::class.java + " or " + ListView::class.java)
            }
        }
        for (i in 0 until childCount) {
            val view = viewGroup.getChildAt(i) ?: continue
            views.add(view)
        }
        val firstPos: Int
        var lastPos: Int
        val totalCount: Int
        if (viewGroup is RecyclerView) {
            val layoutManager = viewGroup.layoutManager as GridLayoutManager? ?: return
            totalCount = layoutManager.itemCount
            firstPos = layoutManager.findFirstVisibleItemPosition()
            lastPos = layoutManager.findLastVisibleItemPosition()
        } else {
            val listAdapter = (viewGroup as ListView).adapter ?: return
            totalCount = listAdapter.count
            firstPos = viewGroup.firstVisiblePosition
            lastPos = viewGroup.lastVisiblePosition
        }
        lastPos = if (lastPos > totalCount) totalCount - 1 else lastPos
        fillPlaceHolder(views, totalCount, firstPos, lastPos)
        viewParams.clear()
        for (i in views.indices) {
            val view = views[i]
            val viewParam = ViewParams()
            if (view == null) {
                viewParam.left = 0
                viewParam.top = 0
                viewParam.width = 0
                viewParam.height = 0
            } else {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                viewParam.left = location[0]
                viewParam.top = location[1] - statusBarHeight
                viewParam.width = view.width
                viewParam.height = view.height
            }
            viewParams.add(viewParam)
        }
    }

    private fun fillPlaceHolder(
        originImageList: MutableList<View?>,
        totalCount: Int,
        firstPos: Int,
        lastPos: Int,
    ) {
        if (firstPos > 0) {
            for (i in firstPos downTo 1) {
                originImageList.add(0, null)
            }
        }
        if (lastPos < totalCount) {
            for (i in totalCount - 1 - lastPos downTo 1) {
                originImageList.add(null)
            }
        }
    }
}