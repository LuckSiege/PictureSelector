package com.luck.picture.lib

import com.luck.picture.lib.adapter.MediaListNewAdapter
import com.luck.picture.lib.adapter.base.BaseMediaListAdapter
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.provider.TempDataProvider

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelector num template style
 */
class SelectorNumberMainFragment : SelectorMainFragment() {

    override fun getFragmentTag(): String {
        return SelectorNumberMainFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return config.layoutSource[LayoutSource.SELECTOR_NUMBER_MAIN]
            ?: R.layout.ps_fragment_number_selector
    }

    override fun createMediaAdapter(): BaseMediaListAdapter {
        return MediaListNewAdapter()
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        super.onSelectionResultChange(change)
        // Label the selection order
        val selectResult = getSelectResult()
        if (!selectResult.contains(change)) {
            val currentItem = mAdapter.getData().indexOf(change)
            if (currentItem >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) currentItem + 1 else currentItem)
            }
        }
        selectResult.forEach { media ->
            val position = mAdapter.getData().indexOf(media)
            if (position >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
        }
    }
}