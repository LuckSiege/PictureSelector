package com.luck.picture.lib.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * @author：luck
 * @date：2021/11/17 4:42 下午
 * @describe：ActivityCompatHelper
 */
object ActivityCompatHelper {

    fun isDestroy(activity: Activity?): Boolean {
        return if (activity == null) {
            true
        } else activity.isFinishing || activity.isDestroyed
    }

    fun assertValidRequest(context: Context?): Boolean {
        if (context is Activity) {
            return !isDestroy(context)
        } else if (context is ContextWrapper) {
            if (context.baseContext is Activity) {
                val activity = context.baseContext as Activity
                return !isDestroy(activity)
            }
        }
        return true
    }

}