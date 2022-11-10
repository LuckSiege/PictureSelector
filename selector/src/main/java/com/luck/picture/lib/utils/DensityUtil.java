package com.luck.picture.lib.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.luck.picture.lib.immersive.RomUtils;

/**
 * @author：luck
 * @date：2021/11/17 11:48 上午
 * @describe：DensityUtil
 */
public class DensityUtil {
    /**
     * 获取屏幕真实宽度
     *
     * @param context
     * @return
     */
    public static int getRealScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    /**
     * 获取屏幕真实高度
     *
     * @param context
     * @return
     */
    public static int getRealScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }


    /**
     * 获取屏幕高度(不包含状态栏高度)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getRealScreenHeight(context) - getStatusNavigationBarHeight(context);
    }

    /**
     * 获取状态栏和导航栏高度
     *
     * @param context
     * @return
     */
    private static int getStatusNavigationBarHeight(Context context) {
        if (isNavBarVisible(context)) {
            return getStatusBarHeight(context) + getNavigationBarHeight(context);
        } else {
            return getStatusBarHeight(context);
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        try {
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = resources.getDimensionPixelSize(resourceId);
                if (sizeTwo >= sizeOne) {
                    result = sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = resources.getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    result = f >= 0 ? (int) (f + 0.5f) : (int) (f - 0.5f);
                }
            }
        } catch (Exception ignored) {
            result = getStatusBarHeight();
        }
        return result == 0 ? dip2px(context, 26) : result;
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * Return whether the navigation bar visible.
     * <p>Call it in onWindowFocusChanged will get right result.</p>
     *
     * @param window The window.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(Context context) {
        boolean isVisible = false;
        if (!(context instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) context;
        Window window = activity.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = getResNameById(activity, id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    isVisible = true;
                    break;
                }
            }
        }
        if (isVisible) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，
            // 导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入法之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复
            if (RomUtils.isSamsung()
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                try {
                    return Settings.Global.getInt(activity.getContentResolver(), "navigationbar_hide_bar_enabled") == 0;
                } catch (Exception ignore) {
                }
            }

            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }

        return isVisible;
    }

    /**
     * getResNameById
     *
     * @param context
     * @param id
     * @return
     */
    private static String getResNameById(Context context, int id) {
        try {
            return context.getResources().getResourceEntryName(id);
        } catch (Exception ignore) {
            return "";
        }
    }


    /**
     * 获取导航栏宽度
     *
     * @param context
     * @return
     */
    @TargetApi(14)
    public static int getNavigationBarWidth(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (isNavBarVisible(context)) {
                return getInternalDimensionSize(context, "navigation_bar_width");
            }
        }
        return result;
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    @TargetApi(14)
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        if (isNavBarVisible(context)) {
            String key;
            if (mInPortrait) {
                key = "navigation_bar_height";
            } else {
                key = "navigation_bar_height_landscape";
            }
            return getInternalDimensionSize(context, key);
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

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
