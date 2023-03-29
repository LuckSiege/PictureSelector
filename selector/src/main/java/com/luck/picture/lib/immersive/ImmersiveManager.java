package com.luck.picture.lib.immersive;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.luck.picture.lib.utils.DensityUtil;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 沉浸式相关
 */

public class ImmersiveManager {

    /**
     * 注意：使用最好将布局xml 跟布局加入    android:fitsSystemWindows="true" ，这样可以避免有些手机上布局顶边的问题
     *
     * @param baseActivity        这个会留出来状态栏和底栏的空白
     * @param statusBarColor      状态栏的颜色
     * @param navigationBarColor  导航栏的颜色
     * @param isDarkStatusBarIcon 状态栏图标颜色是否是深（黑）色  false状态栏图标颜色为白色
     */
    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        immersiveAboveAPI23(baseActivity, false, false, statusBarColor, navigationBarColor, isDarkStatusBarIcon);
    }


    /**
     * @param baseActivity
     * @param statusBarColor     状态栏的颜色
     * @param navigationBarColor 导航栏的颜色
     */
    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        try {
            Window window = baseActivity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4版本及以上 5.0版本及以下
                if (isDarkStatusBarIcon) {
                    initBarBelowLOLLIPOP(baseActivity);
                } else {
                    window.setFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isMarginStatusBar && isMarginNavigationBar) {
                    //5.0版本及以上
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, true
                            , true
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isDarkStatusBarIcon) {
                        initBarBelowLOLLIPOP(baseActivity);
                    } else {
                        window.requestFeature(Window.FEATURE_NO_TITLE);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                        LightStatusBarUtils.setLightStatusBar(baseActivity, false
                                , false
                                , statusBarColor == Color.TRANSPARENT
                                , isDarkStatusBarIcon);

                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }
                } else if (!isMarginStatusBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, false
                            , true
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


    private final static String TAG_FAKE_STATUS_BAR_VIEW = "TAG_FAKE_STATUS_BAR_VIEW";
    private final static String TAG_MARGIN_ADDED = "TAG_MARGIN_ADDED";
    private final static String TAG_NAVIGATION_BAR_VIEW = "TAG_NAVIGATION_BAR_VIEW";

    /**
     * 透明状态栏
     *
     * @param activity
     * @param isDarkStatusBarBlack
     */
    public static void translucentStatusBar(Activity activity, boolean isDarkStatusBarBlack) {
        Window window = activity.getWindow();
        //添加Flag把状态栏设为可绘制模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //如果为全透明模式，取消设置Window半透明的Flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //设置状态栏为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        View decor = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //设置window的状态栏不可见,且状态栏字体是白色
            if (isDarkStatusBarBlack) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else {
            //初始化5.0以下，4.4以上沉浸式
            if (isDarkStatusBarBlack) {
                initBarBelowLOLLIPOP(activity);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            mChildView.setFitsSystemWindows(false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    private static void initBarBelowLOLLIPOP(Activity activity) {
        //透明状态栏
        Window mWindow = activity.getWindow();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //创建一个假的状态栏
        setupStatusBarView(activity);
        //判断是否存在导航栏，是否禁止设置导航栏
        if (DensityUtil.isNavBarVisible(activity)) {
            //透明导航栏，设置这个，如果有导航栏，底部布局会被导航栏遮住
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //创建一个假的导航栏
            setupNavBarView(activity);
        }
    }

    private static void setupStatusBarView(Activity activity) {
        Window mWindow = activity.getWindow();
        View statusBarView = mWindow.getDecorView().findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (statusBarView == null) {
            statusBarView = new View(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    DensityUtil.getStatusBarHeight(activity));
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setVisibility(View.VISIBLE);
            statusBarView.setTag(TAG_MARGIN_ADDED);
            ((ViewGroup) mWindow.getDecorView()).addView(statusBarView);
        }
        statusBarView.setBackgroundColor(Color.TRANSPARENT);
    }

    private static void setupNavBarView(Activity activity) {
        Window window = activity.getWindow();
        View navigationBarView = window.getDecorView().findViewWithTag(TAG_NAVIGATION_BAR_VIEW);
        if (navigationBarView == null) {
            navigationBarView = new View(activity);
            navigationBarView.setTag(TAG_NAVIGATION_BAR_VIEW);
            ((ViewGroup) window.getDecorView()).addView(navigationBarView);
        }

        FrameLayout.LayoutParams params;
        if (DensityUtil.isNavigationAtBottom(activity)) {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DensityUtil.getNavigationBarHeight(activity));
            params.gravity = Gravity.BOTTOM;
        } else {
            params = new FrameLayout.LayoutParams(DensityUtil.getNavigationBarWidth(activity), FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
        }
        navigationBarView.setLayoutParams(params);
        navigationBarView.setBackgroundColor(Color.TRANSPARENT);
        navigationBarView.setVisibility(View.VISIBLE);
    }
}
