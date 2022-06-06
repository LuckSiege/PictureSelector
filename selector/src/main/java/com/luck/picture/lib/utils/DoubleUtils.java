package com.luck.picture.lib.utils;

import android.os.SystemClock;

/**
 * @author：luck
 * @date：2021/12/10 10:07 上午
 * @describe：DoubleUtils
 */
public class DoubleUtils {
    private final static long TIME = 600;

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
