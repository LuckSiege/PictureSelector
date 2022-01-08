package com.luck.picture.lib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author：luck
 * @date：2022/1/8 3:29 下午
 * @describe：ToastUtils
 */
public class ToastUtils {
    /**
     * show toast content
     *
     * @param context
     * @param content
     */
    public static void showToast(Context context, String content) {
        Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }
}
