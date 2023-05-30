package com.luck.picture.lib.helper

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/12/6 1:28 下午
 * @describe：FragmentInjectManager
 */
object FragmentInjectManager {
    /**
     * inject fragment
     *
     * @param activity          root activity
     * @param targetFragmentTag fragment tag
     * @param targetFragment    target fragment
     */
    fun injectFragment(
        activity: FragmentActivity,
        targetFragmentTag: String?,
        targetFragment: Fragment
    ) {
        if (checkFragmentNonExits(activity, targetFragmentTag)) {
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, targetFragment, targetFragmentTag)
                .addToBackStack(targetFragmentTag)
                .commitAllowingStateLoss()
        }
    }

    /**
     * inject fragment
     *
     * @param activity          root activity
     * @param targetFragmentTag fragment tag
     * @param targetFragment    target fragment
     */
    fun injectSystemRoomFragment(
        activity: FragmentActivity,
        targetFragmentTag: String?,
        targetFragment: Fragment
    ) {
        if (checkFragmentNonExits(activity, targetFragmentTag)) {
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, targetFragment, targetFragmentTag)
                .addToBackStack(targetFragmentTag)
                .commitAllowingStateLoss()
        }
    }

    /**
     * inject fragment
     *
     * @param activity          root activity
     * @param containerViewId Optional identifier of the container this fragment is to be placed in.
     * If 0, it will not be placed in a container.fragment – The fragment to be added.
     * This fragment must not already be added to the activity.
     * @param targetFragmentTag fragment tag
     * @param targetFragment    target fragment
     */
    fun injectSystemRoomFragment(
        activity: FragmentActivity,
        @IdRes containerViewId: Int,
        targetFragmentTag: String?,
        targetFragment: Fragment
    ) {
        if (checkFragmentNonExits(activity, targetFragmentTag)) {
            activity.supportFragmentManager.beginTransaction()
                .add(containerViewId, targetFragment, targetFragmentTag)
                .addToBackStack(targetFragmentTag)
                .commitAllowingStateLoss()
        }
    }

    private fun checkFragmentNonExits(activity: FragmentActivity, fragmentTag: String?): Boolean {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return false
        }
        return activity.supportFragmentManager.findFragmentByTag(fragmentTag) == null
    }
}