package com.luck.picture.lib.utils;

import android.content.Context;
import android.widget.Toast;

import com.luck.picture.lib.app.PictureAppMaster;

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
        Context appContext = PictureAppMaster.getInstance().getAppContext();
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        Toast.makeText(appContext, content, Toast.LENGTH_SHORT).show();
    }
}
