package com.luck.picture.lib.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.utils.SpUtils;

/**
 * @author：luck
 * @date：2021/11/18 10:12 上午
 * @describe：PermissionUtil
 */
public class PermissionUtil {
    /**
     * 默认未请求授权状态
     */
    public static final int DEFAULT = 0;
    /**
     * 获取权限成功
     */
    public static final int SUCCESS = 1;
    /**
     * 申请权限拒绝, 但是下次申请权限还会弹窗
     */
    public static final int REFUSE = 2;
    /**
     * 申请权限拒绝，并且是永久，不会再弹窗
     */
    public static final int REFUSE_PERMANENT = 3;

    /**
     * Activity Action: Show screen for controlling which apps have access to manage external
     * storage.
     * <p>
     * In some cases, a matching Activity may not exist, so ensure you safeguard against this.
     * <p>
     * If you want to control a specific app's access to manage external storage, use
     * {@link #ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION} instead.
     * <p>
     * Output: Nothing.
     *
     * @see #ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
     */
    public static final String ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION =
            "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION";


    public static boolean hasPermissions(@NonNull Context context, @Size(min = 1) @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static int getPermissionStatus(Activity activity, String permission) {
        int flag = ActivityCompat.checkSelfPermission(activity, permission);
        boolean should = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (should) {
            return REFUSE;
        }
        if (flag == PackageManager.PERMISSION_GRANTED) {
            return SUCCESS;
        }
        if (!SpUtils.contains(activity, permission)) {
            return DEFAULT;
        }
        return REFUSE_PERMANENT;
    }

    public static boolean isAllGranted(Context context, String[] permissions, int[] grantResults) {
        boolean isAllGranted = true;
        boolean skipPermissionReject = false;
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(context, PermissionConfig.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                skipPermissionReject = true;
            }
        }
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (skipPermissionReject) {
                        if (permissions[i].equals(PermissionConfig.READ_MEDIA_IMAGES) ||
                                permissions[i].equals(PermissionConfig.READ_MEDIA_VIDEO)) {
                            break;
                        }
                    }
                    isAllGranted = false;
                    break;
                }
            }
        } else {
            isAllGranted = false;
        }
        return isAllGranted;
    }


    /**
     * 跳转到系统设置页面
     */
    public static void goIntentSetting(Fragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
            intent.setData(uri);
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
