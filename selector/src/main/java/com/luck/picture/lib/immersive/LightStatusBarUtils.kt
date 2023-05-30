package com.luck.picture.lib.immersive

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * @author：luck
 * @data：2018/3/28 下午1:01
 * @描述: 沉浸式
 */
object LightStatusBarUtils {
    fun setLightStatusBarAboveAPI23(
        activity: Activity,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        isTransStatusBar: Boolean,
        dark: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setLightStatusBar(
                activity,
                isMarginStatusBar,
                isMarginNavigationBar,
                isTransStatusBar,
                dark
            )
        }
    }

    fun setLightStatusBar(activity: Activity, dark: Boolean) {
        setLightStatusBar(activity, false, false, false, dark)
    }

    fun setLightStatusBar(
        activity: Activity,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        isTransStatusBar: Boolean,
        dark: Boolean
    ) {
        when (RomUtils.lightStatusBarAvailableRomType) {
            RomUtils.AvailableRomType.MIUI -> if (RomUtils.mIUIVersionCode >= 7) {
                setAndroidNativeLightStatusBar(
                    activity,
                    isMarginStatusBar,
                    isMarginNavigationBar,
                    isTransStatusBar,
                    dark
                )
            } else {
                setMIUILightStatusBar(
                    activity,
                    isMarginStatusBar,
                    isMarginNavigationBar,
                    isTransStatusBar,
                    dark
                )
            }
            RomUtils.AvailableRomType.FLYME -> setFlymeLightStatusBar(
                activity,
                isMarginStatusBar,
                isMarginNavigationBar,
                isTransStatusBar,
                dark
            )
            RomUtils.AvailableRomType.ANDROID_NATIVE -> setAndroidNativeLightStatusBar(
                activity,
                isMarginStatusBar,
                isMarginNavigationBar,
                isTransStatusBar,
                dark
            )
            RomUtils.AvailableRomType.NA -> {}
        }
    }

    private fun setMIUILightStatusBar(
        activity: Activity,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        isTransStatusBar: Boolean,
        darkmode: Boolean
    ): Boolean {
        initStatusBarStyle(activity, isMarginStatusBar, isMarginNavigationBar)
        val clazz: Class<out Window> = activity.window.javaClass
        try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(activity.window, if (darkmode) darkModeFlag else 0, darkModeFlag)
            return true
        } catch (e: Exception) {
            setAndroidNativeLightStatusBar(
                activity,
                isMarginStatusBar,
                isMarginNavigationBar,
                isTransStatusBar,
                darkmode
            )
        }
        return false
    }

    private fun setFlymeLightStatusBar(
        activity: Activity?,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        isTransStatusBar: Boolean,
        dark: Boolean
    ): Boolean {
        var result = false
        if (activity != null) {
            initStatusBarStyle(activity, isMarginStatusBar, isMarginNavigationBar)
            try {
                val lp = activity.window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (dark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                activity.window.attributes = lp
                result = true
                if (RomUtils.flymeVersion >= 7) {
                    setAndroidNativeLightStatusBar(
                        activity,
                        isMarginStatusBar,
                        isMarginNavigationBar,
                        isTransStatusBar,
                        dark
                    )
                }
            } catch (e: Exception) {
                setAndroidNativeLightStatusBar(
                    activity,
                    isMarginStatusBar,
                    isMarginNavigationBar,
                    isTransStatusBar,
                    dark
                )
            }
        }
        return result
    }

    @TargetApi(11)
    private fun setAndroidNativeLightStatusBar(
        activity: Activity,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        isTransStatusBar: Boolean,
        isDarkStatusBarIcon: Boolean
    ) {
        try {
            if (isTransStatusBar) {
                val window = activity.window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isMarginStatusBar && isMarginNavigationBar) {
                        //5.0版本及以上
                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                        } else {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        }
                    } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.decorView.systemUiVisibility =
                                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                        } else {
                            window.decorView.systemUiVisibility =
                                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                        }
                    } else if (!isMarginStatusBar && isMarginNavigationBar) {
                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.decorView.systemUiVisibility =
                                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                        } else {
                            window.decorView.systemUiVisibility =
                                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                        }
                    } else {
                        //留出来状态栏 不留出来导航栏 没找到办法。。
                        return
                    }
                }
            } else {
                val decor = activity.window.decorView
                if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    // We want to change tint color to white again.
                    // You can also record the flags in advance so that you can turn UI back completely if
                    // you have set other flags before, such as translucent or full screen.
                    decor.systemUiVisibility = 0
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun initStatusBarStyle(
        activity: Activity, isMarginStatusBar: Boolean, isMarginNavigationBar: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (isMarginStatusBar && isMarginNavigationBar) {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                activity.window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            } else if (!isMarginStatusBar && isMarginNavigationBar) {
                activity.window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            } else {
                //留出来状态栏 不留出来导航栏 没找到办法。。
            }
        }
    }
}