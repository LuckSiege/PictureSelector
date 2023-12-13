package com.luck.picture.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.luck.picture.lib.config.PictureConfig;

/**
 * @author：luck
 * @date：2022/3/15 6:26 下午
 * @describe：SpUtils
 */
public class SpUtils {
    private static SharedPreferences pictureSpUtils;

    private static SharedPreferences getSp(Context context) {
        if (pictureSpUtils == null) {
            pictureSpUtils = context.getSharedPreferences(PictureConfig.SP_NAME, Context.MODE_PRIVATE);
        }
        return pictureSpUtils;
    }

    public static void putString(Context context, String key, String value) {
        getSp(context).edit().putString(key, value).apply();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSp(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSp(context).getBoolean(key, defValue);
    }

    public static boolean contains(Context context, String key) {
        return getSp(context).contains(key);
    }
}
