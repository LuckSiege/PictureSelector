package com.yalantis.ucrop.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author：luck
 * @date：2019-12-18 16:36
 * @describe：ScreenUtils
 */
public class ScreenUtils {

    /**
     * dip to px
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) (0.5f + dpValue * context.getApplicationContext()
                .getResources().getDisplayMetrics().density);
    }

    /**
     * getScreenWidth
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }
}
