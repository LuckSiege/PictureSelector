package com.yalantis.ucrop.util;

import android.os.Build;

/**
 * @author：luck
 * @date：2020-01-08 20:53
 * @describe：SdkUtils
 */
public class SdkUtils {
    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
