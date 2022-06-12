package com.luck.lib.camerax.utils;

import android.os.SystemClock;

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：DoubleUtils
 */
public class DoubleUtils {
    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastClickTime;
    private final static long TIME = 800;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
