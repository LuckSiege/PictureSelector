package com.luck.picture.lib.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * @author：luck
 * @data：2018/3/28 下午4:10
 * @描述: Toast工具类
 */

public final class ToastUtils {
    public static void s(Context context, String s) {
        if (!isShowToast()) {
            Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastToastTime;
    private final static long TIME = 1500;

    public static boolean isShowToast() {
        long time = System.currentTimeMillis();
        if (time - lastToastTime < TIME) {
            return true;
        }
        lastToastTime = time;
        return false;
    }
}
