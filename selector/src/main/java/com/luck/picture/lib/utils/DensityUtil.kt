package com.luck.picture.lib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.luck.picture.lib.immersive.RomUtils.isSamsung
import kotlin.math.min

object DensityUtil {
    /**
     * dp2px
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 获取屏幕真实宽度
     *
     * @param context
     * @return
     */
    fun getRealScreenWidth(context: Context): Int {
        val wm =
            context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.x
    }

    /**
     * 获取屏幕真实高度
     *
     * @param context
     */
    fun getRealScreenHeight(context: Context): Int {
        val wm =
            context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }


    /**
     * 获取屏幕高度(不包含状态栏高度)
     *
     * @param context
     */
    fun getScreenHeight(context: Context): Int {
        return getRealScreenHeight(context) - getStatusNavigationBarHeight(context)
    }

    /**
     * 获取状态栏和导航栏高度
     *
     * @param context
     */
    private fun getStatusNavigationBarHeight(context: Context): Int {
        return if (isNavBarVisible(context)) {
            getStatusBarHeight(context) + getNavigationBarHeight(context)
        } else {
            getStatusBarHeight(context)
        }
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        try {
            if (resourceId > 0) {
                val sizeOne = context.resources.getDimensionPixelSize(resourceId)
                val sizeTwo = resources.getDimensionPixelSize(resourceId)
                result = if (sizeTwo >= sizeOne) {
                    sizeTwo
                } else {
                    val densityOne = context.resources.displayMetrics.density
                    val densityTwo = resources.displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    if (f >= 0) (f + 0.5f).toInt() else (f - 0.5f).toInt()
                }
            }
        } catch (ignored: Exception) {
            result = getStatusBarHeight()
        }
        return if (result == 0) dip2px(context, 26f) else result
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    private fun getStatusBarHeight(): Int {
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun isNavigationAtBottom(activity: Activity): Boolean {
        val res = activity.resources
        val mInPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return getSmallestWidthDp(activity) >= 600 || mInPortrait
    }

    @SuppressLint("NewApi")
    private fun getSmallestWidthDp(activity: Activity): Float {
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        } else {
            activity.windowManager.defaultDisplay.getMetrics(metrics)
        }
        val widthDp = metrics.widthPixels / metrics.density
        val heightDp = metrics.heightPixels / metrics.density
        return min(widthDp, heightDp)
    }

    /**
     * 获取导航栏宽度
     *
     * @param context
     * @return
     */
    @TargetApi(14)
    fun getNavigationBarWidth(context: Context?): Int {
        val result = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (isNavBarVisible(context)) {
                return getInternalDimensionSize(context!!, "navigation_bar_width")
            }
        }
        return result
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    @TargetApi(14)
    fun getNavigationBarHeight(context: Context): Int {
        val result = 0
        val res = context.resources
        val mInPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (isNavBarVisible(context)) {
            val key: String = if (mInPortrait) {
                "navigation_bar_height"
            } else {
                "navigation_bar_height_landscape"
            }
            return getInternalDimensionSize(context, key)
        }
        return result
    }

    private fun getInternalDimensionSize(context: Context, key: String): Int {
        val result = 0
        try {
            val resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android")
            if (resourceId > 0) {
                val sizeOne = context.resources.getDimensionPixelSize(resourceId)
                val sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId)
                return if (sizeTwo >= sizeOne) {
                    sizeTwo
                } else {
                    val densityOne = context.resources.displayMetrics.density
                    val densityTwo = Resources.getSystem().displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    (if (f >= 0) f + 0.5f else f - 0.5f).toInt()
                }
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }
        return result
    }

    /**
     * Return whether the navigation bar visible.
     *
     * Call it in onWindowFocusChanged will get right result.
     *
     * @param window The window.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isNavBarVisible(context: Context?): Boolean {
        var isVisible = false
        if (context !is Activity) {
            return false
        }
        val window = context.window
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName: String = getResNameById(context, id).toString()
                if ("navigationBarBackground" == resourceEntryName && child.visibility == View.VISIBLE) {
                    isVisible = true
                    break
                }
            }
            i++
        }
        if (isVisible) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，
            // 导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入法之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复
            if (isSamsung
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                try {
                    return Settings.Global.getInt(
                        context.contentResolver,
                        "navigationbar_hide_bar_enabled"
                    ) == 0
                } catch (ignore: java.lang.Exception) {
                }
            }
            val visibility = decorView.systemUiVisibility
            isVisible = visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0
        }
        return isVisible
    }

    /**
     * getResNameById
     *
     * @param context
     * @param id
     */
    private fun getResNameById(context: Context, id: Int): String? {
        return try {
            context.resources?.getResourceEntryName(id)
        } catch (ignore: java.lang.Exception) {
            ""
        }
    }
}