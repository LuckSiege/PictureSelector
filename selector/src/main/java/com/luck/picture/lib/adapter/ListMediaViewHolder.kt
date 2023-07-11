package com.luck.picture.lib.adapter

import android.graphics.ColorFilter
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BaseListViewHolder
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.StyleUtils
import com.luck.picture.lib.widget.StyleTextView

/**
 * @author：luck
 * @date：2022/12/19 6:25 下午
 * @describe：ListMediaViewHolder
 */
open class ListMediaViewHolder(itemView: View) : BaseListViewHolder(itemView) {
    var tvSelectView: StyleTextView = itemView.findViewById(R.id.ps_tv_check)
    var ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    var defaultColorFilter: ColorFilter =
        StyleUtils.getColorFilter(itemView.context, R.color.ps_color_20)!!
    var selectColorFilter: ColorFilter =
        StyleUtils.getColorFilter(itemView.context, R.color.ps_color_80)!!
    var maskWhiteColorFilter: ColorFilter =
        StyleUtils.getColorFilter(itemView.context, R.color.ps_color_half_white)!!


    open fun bindData(media: LocalMedia, position: Int) {
        tvSelectView.visibility =
            if (config.selectionMode == SelectionMode.ONLY_SINGLE) View.GONE else View.VISIBLE

        mGetSelectResultListener?.onSelectResult()?.let { result ->
            isSelectedMedia(result.contains(media))
        }
        if (MediaUtils.hasMimeTypeOfAudio(media.mimeType)) {
            ivCover.setImageResource(R.drawable.ps_audio_placeholder)
        } else {
            config.imageEngine?.loadListImage(ivCover.context, media.getAvailablePath(), ivCover)
        }
        tvSelectView.setOnClickListener {
            if (media.isEnabledMask) {
                return@setOnClickListener
            }
            if (media.id == SelectorConstant.INVALID_DATA) {
                return@setOnClickListener
            }
            val resultCode =
                mItemClickListener?.onSelected(
                    tvSelectView.isSelected,
                    position,
                    media
                )
            if (resultCode == SelectedState.INVALID) {
                return@setOnClickListener
            }

            if (config.mListenerInfo.onCustomAnimationListener?.onItemCheckBoxAnimation(
                    resultCode == SelectedState.SUCCESS,
                    tvSelectView
                ) != true
            ) {
                if (resultCode == SelectedState.SUCCESS) {
                    tvSelectView.startAnimation(
                        AnimationUtils.loadAnimation(
                            it.context,
                            R.anim.ps_anim_modal_in
                        )
                    )
                }
            }

            if (config.mListenerInfo.onCustomAnimationListener?.onClickItemAnimation(
                    resultCode == SelectedState.SUCCESS,
                    ivCover
                ) != true
            ) {
                ivCover.startAnimation(
                    AnimationUtils.loadAnimation(
                        it.context,
                        if (resultCode == SelectedState.SUCCESS) R.anim.ps_zoom_anim_in else R.anim.ps_zoom_anim_out
                    )
                )
            }

            mGetSelectResultListener?.onSelectResult()?.let { result ->
                isSelectedMedia(result.contains(media))
            }
        }

        if (config.isMaxSelectEnabledMask && config.selectionMode != SelectionMode.ONLY_SINGLE) {
            isDisplayMask(media)
        }

        itemView.setOnClickListener {
            if (media.isEnabledMask) {
                return@setOnClickListener
            }
            if (media.id == SelectorConstant.INVALID_DATA) {
                return@setOnClickListener
            }
            val isPreview = when {
                MediaUtils.hasMimeTypeOfImage(media.mimeType) -> {
                    config.isEnablePreviewImage
                }
                MediaUtils.hasMimeTypeOfVideo(media.mimeType) -> {
                    config.isEnablePreviewVideo
                }
                MediaUtils.hasMimeTypeOfAudio(media.mimeType) -> {
                    config.isEnablePreviewAudio
                }
                else -> {
                    false
                }
            }
            when {
                isPreview -> {
                    mItemClickListener?.onItemClick(tvSelectView, position, media)
                }
                config.selectionMode == SelectionMode.ONLY_SINGLE -> {
                    mItemClickListener?.onComplete(
                        tvSelectView.isSelected, position,
                        media
                    )
                }
                else -> {
                    tvSelectView.performClick()
                }
            }
        }
        itemView.setOnLongClickListener { v ->
            mItemClickListener?.onItemLongClick(v, position, media)
            false
        }
    }

    private fun isDisplayMask(media: LocalMedia) {
        var isDisplayMask = false
        val selectResult = mGetSelectResultListener?.onSelectResult()
        if (selectResult != null && selectResult.isNotEmpty() && !selectResult.contains(media)) {
            isDisplayMask = if (config.isAllWithImageVideo) {
                selectResult.size == config.getSelectCount()
            } else {
                if (MediaUtils.hasMimeTypeOfVideo(selectResult.first().mimeType)) {
                    selectResult.size == config.maxVideoSelectNum || MediaUtils.hasMimeTypeOfImage(
                        media.mimeType
                    )
                } else {
                    selectResult.size == config.totalCount || MediaUtils.hasMimeTypeOfVideo(
                        media.mimeType
                    )
                }
            }
        }
        media.isEnabledMask = isDisplayMask
        if (media.isEnabledMask) {
            ivCover.colorFilter = maskWhiteColorFilter
        }
    }

    /**
     * selectedMedia
     *
     * @param isSelected
     */
    private fun isSelectedMedia(isSelected: Boolean) {
        if (config.selectionMode == SelectionMode.ONLY_SINGLE) {
            ivCover.colorFilter = defaultColorFilter
        } else {
            if (tvSelectView.isSelected != isSelected) {
                tvSelectView.isSelected = isSelected
            }
            ivCover.colorFilter = if (isSelected) selectColorFilter else defaultColorFilter
        }
    }
}