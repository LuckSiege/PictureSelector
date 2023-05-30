package com.luck.picture.lib.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:GridSpacingItemDecoration
 */
class GridSpacingItemDecoration constructor(
    var spanCount: Int,
    var spacing: Int,
    var includeEdge: Boolean
) :
    ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
        }
        if (position < spanCount) {
            outRect.top = spacing
        }
        outRect.bottom = spacing
    }


}