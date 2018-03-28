package com.luck.picture.lib.immersive;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 沉浸式相关
 */

public class ImmersiveManage {
    /**
     * 注意：使用最好将布局xml 跟布局加入    android:fitsSystemWindows="true" ，这样可以避免有些手机上布局顶边的问题
     *
     * @param baseActivity        这个会留出来状态栏和底栏的空白
     * @param statusBarColor      状态栏的颜色
     * @param navigationBarColor  导航栏的颜色
     * @param isDarkStatusBarIcon 状态栏图标颜色是否是深（黑）色  false状态栏图标颜色为白色
     */
    public static void immersiveAboveAPI19(AppCompatActivity baseActivity, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        immersiveAboveAPI23(baseActivity, false, false, statusBarColor, navigationBarColor, isDarkStatusBarIcon);
    }


    /**
     * 注意：使用最好将布局xml 跟布局加入    android:fitsSystemWindows="true" ，这样可以避免有些手机上布局顶边的问题
     *
     * @param baseActivity        这个会留出来状态栏和底栏的空白
     * @param statusBarColor      状态栏的颜色
     * @param navigationBarColor  导航栏的颜色
     * @param isDarkStatusBarIcon 状态栏图标颜色是否是深（黑）色  false状态栏图标颜色为白色
     */
    public static void immersiveAboveAPI23(Activity baseActivity, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            immersiveAboveAPI23(baseActivity, false, false, statusBarColor, navigationBarColor, isDarkStatusBarIcon);
        }
    }


    /**
     * @param baseActivity
     * @param statusBarColor     状态栏的颜色
     * @param navigationBarColor 导航栏的颜色
     */
    public static void immersiveAboveAPI23(Activity baseActivity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        try {
            Window window = baseActivity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4版本及以上 5.0版本及以下
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isMarginStatusBar && isMarginNavigationBar) {
                    //5.0版本及以上
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                } else if (!isMarginStatusBar && isMarginNavigationBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                } else {
                    //留出来状态栏 不留出来导航栏 没找到办法。。
                    return;
                }

                window.setStatusBarColor(statusBarColor);
                window.setNavigationBarColor(navigationBarColor);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
