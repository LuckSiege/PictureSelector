package com.luck.picture.lib.player

import android.view.View

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayer Controller
 */
interface Controller {
    fun getVideoPlay(): View
    fun getVideoLoading(): View
}