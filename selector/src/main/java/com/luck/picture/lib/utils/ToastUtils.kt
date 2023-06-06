package com.luck.picture.lib.utils

import android.content.Context
import android.widget.Toast
import com.luck.picture.lib.helper.ActivityCompatHelper

/**
 * @author：luck
 * @date：2019-07-17 15:12
 * @describe：Toast Utils
 */
object ToastUtils {
    fun showMsg(context: Context, msg: String) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}