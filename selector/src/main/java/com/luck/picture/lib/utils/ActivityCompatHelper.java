package com.luck.picture.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @author：luck
 * @date：2021/11/17 4:42 下午
 * @describe：ActivityCompatHelper
 */
public class ActivityCompatHelper {
    private static final int MIN_FRAGMENT_COUNT = 1;

    public static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }


    /**
     * 验证Fragment是否已存在
     *
     * @param fragmentTag Fragment标签
     * @return
     */
    public static boolean checkFragmentNonExits(FragmentActivity activity, String fragmentTag) {
        if (isDestroy(activity)) {
            return false;
        }
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return fragment == null;
    }


    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper) {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity) {
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    /**
     * 验证当前是否是根Fragment
     *
     * @param activity
     * @return
     */
    public static boolean checkRootFragment(FragmentActivity activity) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return false;
        }
        return activity.getSupportFragmentManager().getBackStackEntryCount() == MIN_FRAGMENT_COUNT;
    }
}
