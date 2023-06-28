package com.luck.picture.lib.style

import androidx.annotation.AnimRes

/**
 * @author：luck
 * @date：2021/11/15 4:12 下午
 * @describe：WindowAnimStyle
 */
class WindowAnimStyle {

    /**
     * Window enter animation
     */
    @AnimRes
    private var windowEnterAnimRes = 0

    /**
     *Window exit animation
     */
    @AnimRes
    private var windowExitAnimRes = 0

    fun getEnterAnimRes(): Int {
        return windowEnterAnimRes
    }

    fun getExitAnimRes(): Int {
        return windowExitAnimRes
    }

    fun of(@AnimRes enter: Int, @AnimRes exit: Int) {
        this.windowEnterAnimRes = enter
        this.windowExitAnimRes = exit
    }

}