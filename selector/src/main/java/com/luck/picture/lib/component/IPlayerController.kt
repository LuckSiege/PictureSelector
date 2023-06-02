package com.luck.picture.lib.component

import android.view.View
import android.widget.SeekBar
import android.widget.TextView

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player controller
 */
interface IPlayerController {
    fun getViewLoading(): View?
    fun getViewPlay(): View
    fun getViewFast(): View?
    fun getViewBack(): View?
    fun getSeekBar(): SeekBar?
    fun getViewDuration(): TextView?
    fun getViewCurrentDuration(): TextView?
}