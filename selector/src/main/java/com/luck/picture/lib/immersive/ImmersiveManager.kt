package com.luck.picture.lib.immersive

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.luck.picture.lib.utils.DensityUtil

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 沉浸式相关
 */
object ImmersiveManager {
    /**
     * 注意：使用最好将布局xml 跟布局加入    android:fitsSystemWindows="true" ，这样可以避免有些手机上布局顶边的问题
     *
     * @param baseActivity        这个会留出来状态栏和底栏的空白
     * @param statusBarColor      状态栏的颜色
     * @param navigationBarColor  导航栏的颜色
     * @param isDarkStatusBarIcon 状态栏图标颜色是否是深（黑）色  false状态栏图标颜色为白色
     */
    fun immersiveAboveAPI23(
        baseActivity: AppCompatActivity,
        statusBarColor: Int,
        navigationBarColor: Int,
        isDarkStatusBarIcon: Boolean
    ) {
        immersiveAboveAPI23(
            baseActivity,
            isMarginStatusBar = false,
            isMarginNavigationBar = false,
            statusBarColor = statusBarColor,
            navigationBarColor = navigationBarColor,
            isDarkStatusBarIcon = isDarkStatusBarIcon
        )
    }


    /**
     * @param baseActivity
     * @param statusBarColor     状态栏的颜色
     * @param navigationBarColor 导航栏的颜色
     */
    fun immersiveAboveAPI23(
        baseActivity: AppCompatActivity,
        isMarginStatusBar: Boolean,
        isMarginNavigationBar: Boolean,
        statusBarColor: Int,
        navigationBarColor: Int,
        isDarkStatusBarIcon: Boolean
    ) {
        try {
            val window = baseActivity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4版本及以上 5.0版本及以下
                if (isDarkStatusBarIcon) {
                    initBarBelowLOLLIPOP(baseActivity)
                } else {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isMarginStatusBar && isMarginNavigationBar) {
                    //5.0版本及以上
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    )
                    LightStatusBarUtils.setLightStatusBar(
                        baseActivity,
                        isMarginStatusBar = true,
                        isMarginNavigationBar = true,
                        isTransStatusBar = statusBarColor == Color.TRANSPARENT,
                        dark = isDarkStatusBarIcon
                    )
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isDarkStatusBarIcon) {
                        initBarBelowLOLLIPOP(baseActivity)
                    } else {
                        window.requestFeature(Window.FEATURE_NO_TITLE)
                        window.clearFlags(
                            (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                        )
                        LightStatusBarUtils.setLightStatusBar(
                            baseActivity,
                            isMarginStatusBar = false,
                            isMarginNavigationBar = false,
                            isTransStatusBar = statusBarColor == Color.TRANSPARENT,
                            dark = isDarkStatusBarIcon
                        )
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    }
                } else if (!isMarginStatusBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE)
                    window.clearFlags(
                        (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                    )
                    LightStatusBarUtils.setLightStatusBar(
                        baseActivity,
                        isMarginStatusBar = false,
                        isMarginNavigationBar = true,
                        isTransStatusBar = statusBarColor == Color.TRANSPARENT,
                        dark = isDarkStatusBarIcon
                    )
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                } else {
                    //留出来状态栏 不留出来导航栏 没找到办法。。
                    return
                }
                window.statusBarColor = statusBarColor
                window.navigationBarColor = navigationBarColor
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private const val TAG_FAKE_STATUS_BAR_VIEW = "TAG_FAKE_STATUS_BAR_VIEW"
    private const val TAG_MARGIN_ADDED = "TAG_MARGIN_ADDED"
    private const val TAG_NAVIGATION_BAR_VIEW = "TAG_NAVIGATION_BAR_VIEW"

    /**
     * 透明状态栏
     *
     * @param activity
     * @param isDarkStatusBarBlack
     */
    fun translucentStatusBar(activity: Activity, isDarkStatusBarBlack: Boolean) {
        val window = activity.window
        //添加Flag把状态栏设为可绘制模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        //如果为全透明模式，取消设置Window半透明的Flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //设置状态栏为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        fitsNotchScreen(activity)
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //设置window的状态栏不可见,且状态栏字体是白色
            if (isDarkStatusBarBlack) {
                decor.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        } else {
            //初始化5.0以下，4.4以上沉浸式
            if (isDarkStatusBarBlack) {
                initBarBelowLOLLIPOP(activity)
            } else {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
        //view不根据系统窗口来调整自己的布局
        val mContentView = window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            mChildView.fitsSystemWindows = false
            ViewCompat.requestApplyInsets(mChildView)
        }
    }

    private fun fitsNotchScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val lp: WindowManager.LayoutParams = activity.window.attributes
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                activity.window.attributes = lp
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun initBarBelowLOLLIPOP(activity: Activity) {
        //透明状态栏
        val mWindow = activity.window
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //创建一个假的状态栏
        setupStatusBarView(activity)
        //判断是否存在导航栏，是否禁止设置导航栏
        if (DensityUtil.isNavBarVisible(activity)) {
            //透明导航栏，设置这个，如果有导航栏，底部布局会被导航栏遮住
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            //创建一个假的导航栏
            setupNavBarView(activity)
        }
    }

    private fun setupStatusBarView(activity: Activity) {
        val mWindow = activity.window
        var statusBarView = mWindow.decorView.findViewWithTag<View>(TAG_FAKE_STATUS_BAR_VIEW)
        if (statusBarView == null) {
            statusBarView = View(activity)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.getStatusBarHeight(activity)
            )
            params.gravity = Gravity.TOP
            statusBarView.layoutParams = params
            statusBarView.visibility = View.VISIBLE
            statusBarView.tag = TAG_MARGIN_ADDED
            (mWindow.decorView as ViewGroup).addView(statusBarView)
        }
        statusBarView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupNavBarView(activity: Activity) {
        val window = activity.window
        var navigationBarView = window.decorView.findViewWithTag<View>(TAG_NAVIGATION_BAR_VIEW)
        if (navigationBarView == null) {
            navigationBarView = View(activity)
            navigationBarView.tag = TAG_NAVIGATION_BAR_VIEW
            (window.decorView as ViewGroup).addView(navigationBarView)
        }
        val params: FrameLayout.LayoutParams
        if (DensityUtil.isNavigationAtBottom(activity)) {
            params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.getNavigationBarHeight(activity)
            )
            params.gravity = Gravity.BOTTOM
        } else {
            params = FrameLayout.LayoutParams(
                DensityUtil.getNavigationBarWidth(activity),
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.END
        }
        navigationBarView.layoutParams = params
        navigationBarView.setBackgroundColor(Color.TRANSPARENT)
        navigationBarView.visibility = View.VISIBLE
    }
}