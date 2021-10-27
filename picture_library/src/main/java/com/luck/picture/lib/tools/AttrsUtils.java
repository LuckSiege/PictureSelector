package com.luck.picture.lib.tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureSelectionConfig;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 动态获取attrs
 */

public class AttrsUtils {

    /**
     * get attrs size
     *
     * @param context
     * @param attr
     * @return
     */
    public static float getTypeValueSize(Context context, int attr) {
        float textSize = 0;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            textSize = array.getDimensionPixelSize(0, 0);
            array.recycle();
            return textSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textSize;
    }

    /**
     * get attrs size
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getTypeValueSizeForInt(Context context, int attr) {
        int textSize = 0;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            textSize = array.getDimensionPixelSize(0, 0);
            array.recycle();
            return textSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textSize;
    }

    /**
     * get attrs color
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getTypeValueColor(Context context, int attr) {
        int color = 0;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            color = array.getColor(0, 0);
            array.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
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
        boolean flag = false;
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            flag = array.getBoolean(0, false);
            array.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
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

    /**
     * getColorStateList
     *
     * @param colors
     * @return
     */
    public static ColorStateList getColorStateList(int[] colors) {
        try {
            if (colors.length == 2) {
                int[][] states = new int[2][];
                states[0] = new int[]{-android.R.attr.state_selected};
                states[1] = new int[]{android.R.attr.state_selected};
                return new ColorStateList(states, colors);
            } else {
                return ColorStateList.valueOf(colors[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
