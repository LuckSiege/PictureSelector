package com.luck.picture.lib.tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 动态获取attrs
 */

public class AttrsUtils {

    /**
     * get attrs color
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getTypeValueColor(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int color = array.getColor(0, 0);
            array.recycle();
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get attrs color
     *
     * @param context
     * @param attr
     * @return
     */
    public static ColorStateList getTypeValueColorStateList(Context context, int attr) {
        ColorStateList colorStateList = null;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            colorStateList = array.getColorStateList(0);
            array.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colorStateList;
    }

    /**
     * attrs status color or black
     *
     * @param context
     * @param attr
     * @return
     */
    public static boolean getTypeValueBoolean(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            boolean statusFont = array.getBoolean(0, false);
            array.recycle();
            return statusFont;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * attrs drawable
     *
     * @param context
     * @param attr
     * @param defaultResId
     * @return
     */
    public static Drawable getTypeValueDrawable(Context context, int attr, int defaultResId) {
        Drawable drawable = null;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            drawable = array.getDrawable(0);
            array.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable == null ? ContextCompat.getDrawable(context, defaultResId) : drawable;
    }
}
