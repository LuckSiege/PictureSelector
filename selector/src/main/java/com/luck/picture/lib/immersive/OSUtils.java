package com.luck.picture.lib.immersive;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * @author：luck
 * @date：2021/12/28 9:43 上午
 * @describe：OSUtils
 */
public class OSUtils {
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";

    public static String getEMUIVersion() {
        return isEMUI() ? getSystemProperty() : "";
    }

    public static boolean isEMUI() {
        String property = getSystemProperty();
        return !TextUtils.isEmpty(property);
    }

    public static boolean isEMUI3_x() {
        return isEMUI3_0() || isEMUI3_1();
    }

    public static boolean isEMUI3_1() {
        String property = getEMUIVersion();
        return "EmotionUI 3".equals(property) || property.contains("EmotionUI_3.1");
    }

    /**
     * 判断是否为emui3.0版本
     * Is emui 3 1 boolean.
     *
     * @return the boolean
     */
    public static boolean isEMUI3_0() {
        String property = getEMUIVersion();
        return property.contains("EmotionUI_3.0");
    }

    private static String getSystemProperty() {
        try {
            @SuppressLint("PrivateApi") Class<?> clz = Class.forName("android.os.SystemProperties");
            Method method = clz.getMethod("get", String.class, String.class);
            return (String) method.invoke(clz, KEY_EMUI_VERSION_NAME, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
