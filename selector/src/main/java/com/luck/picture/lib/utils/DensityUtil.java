package com.luck.picture.lib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author：luck
 * @date：2021/11/17 11:48 上午
 * @describe：DensityUtil
 */
public class DensityUtil {
    private static int mScreenWidth;
    private static int mScreenHeight;

    /**
     * 获取屏幕宽度
     */
    private static int getScreenWidthPixels(Context context) {
        if (mScreenWidth <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }
        return mScreenWidth;
    }

    /**
     * 获取屏幕高度
     */
    private static int getScreenHeightPixels(Context context) {
        if (mScreenHeight <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }
        return mScreenHeight;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getScreenWidthPixels(context);
    }

    /**
     * 获取屏幕高度(不包含状态栏高度)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getScreenHeightPixels(context) - getStatusBarHeight(context);
    }

    /**
     * 获取屏幕高度(包含状态栏高度)
     *
     * @param context
     * @return
     */
    public static int getAppInScreenHeight(Context context) {
        return getScreenHeightPixels(context) + getStatusBarHeight(context);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result == 0 ? dip2px(context, 25) : result;
    }

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}