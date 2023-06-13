package com.luck.picture.lib.interfaces

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * @author：luck
 * @date：2022/6/4 12:56 下午
 * @describe：OnFragmentLifecycleListener
 */
interface OnFragmentLifecycleListener {
    /**
     * onViewCreated
     *
     * @param fragment
     * @param view
     * @param savedInstanceState
     */
    fun onViewCreated(fragment: Fragment, view: View?, savedInstanceState: Bundle?)

    /**
     * onDestroy
     *
     * @param fragment
     */
    fun onDestroy(fragment: Fragment)
}