package com.luck.picture.lib.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.luck.picture.lib.immersive.OSUtils;

/**
 * @author：luck
 * @date：2021/11/17 11:48 上午
 * @describe：DensityUtil
 */
public class DensityUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeightPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
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
        return result == 0 ? dip2px(context, 26) : result;
    }

    public static boolean hasNavigationBar(Activity activity) {
        return getNavigationBarHeight(activity) > 0;
    }

    @TargetApi(14)
    public static int getNavigationBarWidth(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                return getInternalDimensionSize(context, "navigation_bar_width");
            }
        }
        return result;
    }

    @TargetApi(14)
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                String key;
                if (mInPortrait) {
                    key = "navigation_bar_height";
                } else {
                    key = "navigation_bar_height_landscape";
                }
                return getInternalDimensionSize(context, key);
            }
        }
        return result;
    }

    private static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }

    @SuppressLint("NewApi")
    private static float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    public static boolean isNavigationAtBottom(Activity activity) {
        Resources res = activity.getResources();
        boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        return (getSmallestWidthDp(activity) >= 600 || mInPortrait);
    }

    @TargetApi(14)
    private static boolean hasNavBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //判断小米手机是否开启了全面屏，开启了，直接返回false
            if (Settings.Global.getInt(activity.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
                return false;
            }
            //判断华为手机是否隐藏了导航栏，隐藏了，直接返回false
            if (OSUtils.isEMUI()) {
                if (OSUtils.isEMUI3_x() || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    if (Settings.System.getInt(activity.getContentResolver(), "navigationbar_is_min", 0) != 0) {
                        return false;
                    }
                } else {
                    if (Settings.Global.getInt(activity.getContentResolver(), "navigationbar_is_min", 0) != 0) {
                        return false;
                    }
                }
            }
        }
        //其他手机根据屏幕真实高度与显示高度是否相同来判断
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
