package com.luck.picture.lib.tools;

import android.os.Build;

/**
 * @author：luck
 * @date：2019-07-17 15:12
 * @describe：Android Sdk版本判断
 */
public class SdkVersionUtils {
    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 判断是否是Android R版本
     *
     * @return
     */
    public static boolean isR() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }
}
