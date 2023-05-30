package com.luck.picture.lib.interfaces

import android.view.View

/**
 * @author：luck
 * @date：2022/8/12 7:19 下午
 * @describe：OnCustomAnimationListener
 */
interface OnCustomAnimationListener {

    /**
     * Media list item click animation
     * @param isChecked
     * @param cover Cover ImageView
     * @return Return true to use user-defined animation, otherwise use default
     */
    fun onClickItemAnimation(isChecked: Boolean, cover: View): Boolean

    /**
     * Media list item checkbox animation
     * @param isChecked
     * @param selectView [SelectStyleView] View
     * @return Return true to use user-defined animation, otherwise use default
     */
    fun onItemCheckBoxAnimation(isChecked: Boolean, selectView: View): Boolean
}