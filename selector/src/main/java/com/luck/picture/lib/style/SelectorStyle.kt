package com.luck.picture.lib.style

import android.graphics.Color
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/11/15 4:12 下午
 * @describe：SelectorStyle
 */
class SelectorStyle {
    init {
        defaultStyle()
    }

    /**
     * Status Bar Style
     */
    private lateinit var statusBar: StatusBar

    /**
     * Window enters and exits the animation
     */
    private lateinit var windowAnimation: WindowAnimation

    /**
     * Default UI Style
     */
    fun defaultStyle() {
        statusBar = StatusBar().apply {
            of(false, Color.parseColor("#393a3e"), Color.parseColor("#393a3e"))
        }
        windowAnimation = WindowAnimation().apply {
            of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
        }
    }


    fun getWindowAnimation(): WindowAnimation {
        return windowAnimation
    }

    fun getStatusBar(): StatusBar {
        return statusBar
    }

    class WindowAnimation {
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

        fun getEnterAnim(): Int {
            return windowEnterAnimRes
        }

        fun getExitAnim(): Int {
            return windowExitAnimRes
        }

        fun of(@AnimRes enter: Int, @AnimRes exit: Int) {
            this.windowEnterAnimRes = enter
            this.windowExitAnimRes = exit
        }

    }

    class StatusBar {
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
}