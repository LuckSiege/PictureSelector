package com.luck.picture.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * @author：luck
 * @date：2021/11/17 11:48 上午
 * @describe：DensityUtil
 */
public class DensityUtil {
    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Return the width of screen, in pixel.
     *
     * @return the width of screen, in pixel
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }


    public static int getAppScreenHeight(Context context) {
        return getScreenHeight(context) - getNavigationBarHeight(context);
    }

    public static int getNavigationBarHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            Resources resources = context.getResources();
            int navigationBarIsMin = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                navigationBarIsMin = Settings.System.getInt(context.getContentResolver(),
                        "navigationbar_is_min", 0);
            } else {
                navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
                        "navigationbar_is_min", 0);
            }
            if (navigationBarIsMin == 0) {
                return 0;
            }
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            int height = resources.getDimensionPixelSize(resourceId);
            if ((realSize.y - size.y) > (height - 10)) {
                return height;
            }
            return 0;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return 0;
            } else {
                Resources resources = context.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                int height = resources.getDimensionPixelSize(resourceId);
                return height;
            }
        }
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
}
