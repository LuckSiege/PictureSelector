package com.luck.picture.lib.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * @author：luck
 * @data：2018/3/28 下午4:10
 * @描述: Toast工具类
 */
public final class ToastManage {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void s(Context mContext, String s) {
        if (toast == null) {
            toast = Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.show();
    }
}