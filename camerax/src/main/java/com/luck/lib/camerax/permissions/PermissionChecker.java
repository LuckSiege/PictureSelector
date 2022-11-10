package com.luck.lib.camerax.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.luck.lib.camerax.PictureCameraActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/18 10:07 上午
 * @describe：PermissionChecker
 */
public class PermissionChecker {
    /**
     * 权限设置
     */
    public static final int PERMISSION_SETTING_CODE = 1102;

    /**
     * 录音权限设置
     */
    public static final int PERMISSION_RECORD_AUDIO_SETTING_CODE = 1103;

    private static final int REQUEST_CODE = 10086;

    private static PermissionChecker mInstance;

    private PermissionChecker() {

    }

    public static PermissionChecker getInstance() {
        if (mInstance == null) {
            synchronized (PermissionChecker.class) {
                if (mInstance == null) {
                    mInstance = new PermissionChecker();
                }
            }
        }
        return mInstance;
    }


    public void requestPermissions(Activity activity, String[] permissionArray, PermissionResultCallback callback) {
        List<String[]> groupList = new ArrayList<>();
        groupList.add(permissionArray);
        requestPermissions(activity, groupList, REQUEST_CODE, callback);
    }

    public void requestPermissions(Activity activity, List<String[]> permissionGroupList, PermissionResultCallback callback) {
        requestPermissions(activity, permissionGroupList, REQUEST_CODE, callback);
    }

    private void requestPermissions(Activity activity, List<String[]> permissionGroupList, final int requestCode, PermissionResultCallback permissionResultCallback) {
        if (activity instanceof PictureCameraActivity) {
            if (Build.VERSION.SDK_INT < 23) {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
                return;
            }
            List<String> permissionList = new ArrayList<>();
            for (String[] permissionArray : permissionGroupList) {
                for (String permission : permissionArray) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permission);
                    }
                }
            }
            if (permissionList.size() > 0) {
                ((PictureCameraActivity) activity).setPermissionsResultAction(permissionResultCallback);
                String[] requestArray = new String[permissionList.size()];
                permissionList.toArray(requestArray);
                ActivityCompat.requestPermissions(activity, requestArray, requestCode);
            } else {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
            }
        }
    }

    public void onRequestPermissionsResult(int[] grantResults, PermissionResultCallback action) {
        if (SimpleXPermissionUtil.isAllGranted(grantResults)) {
            action.onGranted();
        } else {
            action.onDenied();
        }
    }


    /**
     * 检查是否有某个权限
     *
     * @param ctx
     * @param permissions
     * @return
     */
    public static boolean checkSelfPermission(Context ctx, String[] permissions) {
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(ctx.getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }
}
