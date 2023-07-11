package com.luck.picture.lib.widget

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @data：2016/12/27 下午23:50
 * @describe:WrapContentGridLayoutManager
 */
open class WrapContentGridLayoutManager(context: Context, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}