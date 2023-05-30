package com.luck.picture.lib.adapter

import android.annotation.SuppressLint
import com.luck.picture.lib.R
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/12/1 1:18 下午
 * @describe：MediaListNewAdapter
 */
class MediaListNewAdapter : MediaListAdapter() {

    override fun bindData(holder: ListMediaViewHolder, media: LocalMedia, position: Int) {
        super.bindData(holder, media, position)
        holder.tvSelectView.setBackgroundResource(R.drawable.ps_default_num_selector)
        notifySelectNumberStyle(holder, media)
    }

    @SuppressLint("SetTextI18n")
    private fun notifySelectNumberStyle(holder: ListMediaViewHolder, currentMedia: LocalMedia) {
        holder.tvSelectView.text = ""
        val selectResult = mGetSelectResultListener?.onSelectResult() ?: mutableListOf()
        val position = selectResult.indexOf(currentMedia)
        if (position >= 0) {
            holder.tvSelectView.text = "${position + 1}"
        }
    }
}