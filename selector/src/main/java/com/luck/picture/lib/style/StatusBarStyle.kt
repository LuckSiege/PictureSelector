package com.luck.picture.lib.style

import androidx.annotation.ColorInt

/**
 * @author：luck
 * @date：2021/11/15 4:12 下午
 * @describe：StatusBarStyle
 */
class StatusBarStyle {
    /**
     * Status Bar Dark Mode
     */
    private var isDarkStatusBar = false

    /**
     * Status Bar Color
     */
    @ColorInt
    private var statusBarColor = 0

    /**
     * Bottom navigation bar color
     */
    @ColorInt
    private var navigationBarColor = 0

    fun isDarkStatusBar(): Boolean {
        return isDarkStatusBar
    }

    fun getStatusBarColor(): Int {
        return statusBarColor
    }

    fun getNavigationBarColor(): Int {
        return navigationBarColor
    }

    fun of(
        isDarkStatusBar: Boolean,
        @ColorInt statusBarColor: Int,
        @ColorInt navigationBarColor: Int
    ) {
        this.isDarkStatusBar = isDarkStatusBar
        this.statusBarColor = statusBarColor
        this.navigationBarColor = navigationBarColor
    }
}