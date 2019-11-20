package com.luck.picture.lib.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author：luck
 * @date：2019-11-20 19:07
 * @describe：权限检查
 */
public class PermissionChecker {

    /**
     * 检查是否有某个权限
     *
     * @param ctx
     * @param permission
     * @return
     */
    public static boolean checkSelfPermission(Context ctx, String permission) {
        return ContextCompat.checkSelfPermission(ctx.getApplicationContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 动态申请多个权限
     *
     * @param activity
     * @param code
     */
    public static void requestPermissions(Activity activity, @NonNull String[] permissions, int code) {
        ActivityCompat.requestPermissions(activity, permissions, code);
    }
}
