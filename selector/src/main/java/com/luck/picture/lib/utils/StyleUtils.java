package com.luck.picture.lib.utils;

import android.content.Context;
import android.graphics.ColorFilter;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author：luck
 * @date：2021/11/20 3:27 下午
 * @describe：StyleUtils
 */
public class StyleUtils {
    private static final int INVALID = 0;

    /**
     * 验证样式资源的合法性
     *
     * @param resource
     * @return
     */
    public static boolean checkStyleValidity(int resource) {
        return resource != INVALID;
    }

    /**
     * 验证文本的合法性
     *
     * @param text
     * @return
     */
    public static boolean checkTextValidity(String text) {
        return !TextUtils.isEmpty(text);
    }

    /**
     * 验证文本是否有2个动态匹配符
     *
     * @param text
     * @return
     */
    public static int getFormatCount(String text) {
        String pattern = "%[^%]*\\d";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 验证大小的合法性
     *
     * @param size
     * @return
     */
    public static boolean checkSizeValidity(int size) {
        return size > INVALID;
    }

    /**
     * 验证数组的合法性
     *
     * @param size
     * @return
     */
    public static boolean checkArrayValidity(int[] array) {
        return array != null && array.length > 0;
    }

    /**
     * getColorFilter
     *
     * @param context
     * @param color
     * @return
     */
    public static ColorFilter getColorFilter(Context context, int color) {
        return BlendModeColorFilterCompat.createBlendModeColorFilterCompat
                (ContextCompat.getColor(context, color), BlendModeCompat.SRC_ATOP);
    }
}
