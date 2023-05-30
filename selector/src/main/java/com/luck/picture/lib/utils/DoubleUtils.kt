package com.luck.picture.lib.utils

import android.os.SystemClock

/**
 * @author：luck
 * @date：2021/12/10 10:07 上午
 * @describe：DoubleUtils
 */
object DoubleUtils {
    private const val TIME: Long = 600

    private var lastClickTime: Long = 0

    fun isFastDoubleClick(): Boolean {
        val time = SystemClock.elapsedRealtime()
        if (time - lastClickTime < TIME) {
            return true
        }
        lastClickTime = time
        return false
    }
}