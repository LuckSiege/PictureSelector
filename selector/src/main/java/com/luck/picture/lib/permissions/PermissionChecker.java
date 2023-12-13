package com.luck.picture.lib.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/18 10:07 上午
 * @describe：PermissionChecker
 */
public class PermissionChecker {

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


    public void requestPermissions(Fragment fragment, String[] permissionArray, PermissionResultCallback callback) {
        List<String[]> groupList = new ArrayList<>();
        groupList.add(permissionArray);
        requestPermissions(fragment, groupList, REQUEST_CODE, callback);
    }

    public void requestPermissions(Fragment fragment, List<String[]> permissionGroupList, PermissionResultCallback callback) {
        requestPermissions(fragment, permissionGroupList, REQUEST_CODE, callback);
    }

    private void requestPermissions(Fragment fragment, List<String[]> permissionGroupList, final int requestCode, PermissionResultCallback permissionResultCallback) {
        if (ActivityCompatHelper.isDestroy(fragment.getActivity())) {
            return;
        }
        if (fragment instanceof PictureCommonFragment) {
            if (Build.VERSION.SDK_INT < 23) {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
                return;
            }
            Activity activity = fragment.getActivity();
            List<String> permissionList = new ArrayList<>();
            for (String[] permissionArray : permissionGroupList) {
                for (String permission : permissionArray) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permission);
                    }
                }
            }
            if (permissionList.size() > 0) {
                ((PictureCommonFragment) fragment).setPermissionsResultAction(permissionResultCallback);
                String[] requestArray = new String[permissionList.size()];
                permissionList.toArray(requestArray);
                fragment.requestPermissions(requestArray, requestCode);
                ActivityCompat.requestPermissions(activity, requestArray, requestCode);
            } else {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
            }
        }
    }

    public void onRequestPermissionsResult(Context context,String[] permissions,int[] grantResults, PermissionResultCallback action) {
        Activity activity = (Activity) context;
        for (String permission : permissions) {
            boolean should = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            SpUtils.putBoolean(context, permission, should);
        }
        if (PermissionUtil.isAllGranted(context, permissions, grantResults)) {
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
     */
    public static boolean checkSelfPermission(Context ctx, String[] permissions) {
        boolean isAllGranted = true;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(ctx.getApplicationContext(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
        }
        return isAllGranted;
    }

    /**
     * 检查读写权限是否存在
     */
    public static boolean isCheckReadStorage(int chooseMode, Context context) {
        if (SdkVersionUtils.isTIRAMISU()) {
            if (chooseMode == SelectMimeType.ofImage()) {
                return PermissionChecker.isCheckReadImages(context);
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return PermissionChecker.isCheckReadVideo(context);
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return PermissionChecker.isCheckReadAudio(context);
            } else {
                return PermissionChecker.isCheckReadImages(context) && PermissionChecker.isCheckReadVideo(context);
            }
        } else {
            return PermissionChecker.isCheckReadExternalStorage(context);
        }
    }


    /**
     * 检查读取图片权限是否存在
     */
    @RequiresApi(api = 33)
    public static boolean isCheckReadImages(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{PermissionConfig.READ_MEDIA_IMAGES});
    }

    /**
     * 检查读取视频权限是否存在
     */
    @RequiresApi(api = 33)
    public static boolean isCheckReadVideo(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{PermissionConfig.READ_MEDIA_VIDEO});
    }

    /**
     * 检查读取音频权限是否存在
     */
    @RequiresApi(api = 33)
    public static boolean isCheckReadAudio(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{PermissionConfig.READ_MEDIA_AUDIO});
    }

    /**
     * 检查写入权限是否存在
     */
    public static boolean isCheckWriteExternalStorage(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    /**
     * 检查读取权限是否存在
     */
    public static boolean isCheckReadExternalStorage(Context context) {
        return PermissionChecker.checkSelfPermission(context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }


    /**
     * 检查相机权限是否存在
     */
    public static boolean isCheckCamera(Context context) {
        return PermissionChecker.checkSelfPermission(context, new String[]{Manifest.permission.CAMERA});
    }

    /**
     * 权限是否已申请
     */
    public static boolean isCheckSelfPermission(Context context, String[] permissions) {
        return PermissionChecker.checkSelfPermission(context, permissions);
    }
}
