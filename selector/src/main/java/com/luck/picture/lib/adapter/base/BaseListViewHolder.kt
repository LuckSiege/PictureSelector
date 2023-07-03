package com.luck.picture.lib.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.interfaces.OnMediaItemClickListener
import com.luck.picture.lib.provider.SelectorProviders

/**
 * @author：luck
 * @date：2022/12/19 6:25 下午
 * @describe：BaseListViewHolder
 */
open class BaseListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var config = SelectorProviders.getInstance().getConfig()

    var mItemClickListener: OnMediaItemClickListener? = null

    fun setOnItemClickListener(listener: OnMediaItemClickListener?) {
        this.mItemClickListener = listener
    }

    var mGetSelectResultListener: BaseMediaListAdapter.OnGetSelectResultListener? = null

    fun setOnGetSelectResultListener(listener: BaseMediaListAdapter.OnGetSelectResultListener?) {
        this.mGetSelectResultListener = listener
    }
}