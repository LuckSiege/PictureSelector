package com.luck.lib.camerax.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.luck.lib.camerax.CustomCameraConfig;

/**
 * @author：luck
 * @date：2022/3/15 6:26 下午
 * @describe：SimpleXSpUtils
 */
public class SimpleXSpUtils {
    private static SharedPreferences pictureSpUtils;

    private static SharedPreferences getSp(Context context) {
        if (pictureSpUtils == null) {
            pictureSpUtils = context.getSharedPreferences(CustomCameraConfig.SP_NAME, Context.MODE_PRIVATE);
        }
        return pictureSpUtils;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSp(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSp(context).getBoolean(key, defValue);
    }
}
