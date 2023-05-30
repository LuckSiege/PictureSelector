package com.luck.picture.lib.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build

/**
 * @author：luck
 * @date：2019-07-17 15:12
 * @describe：Android Sdk版本判断
 */
object SdkVersionUtils {

    const val R = 30

    const val TIRAMISU = 33

    /**
     * 判断是否是低于Android LOLLIPOP版本
     */
    fun isMinM(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    }

    /**
     * 判断是否是Android O版本
     */
    fun isO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }


    /**
     * 判断是否是Android N版本
     */
    fun isMaxN(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }


    /**
     * 判断是否是Android N版本
     */
    fun isN(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.N
    }

    /**
     * 判断是否是Android P版本
     */
    fun isP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * 判断是否是Android Q版本
     */
    fun isQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    /**
     * 判断是否是Android R版本
     */
    fun isR(): Boolean {
        return Build.VERSION.SDK_INT >= R
    }

    /**
     * 判断是否是Android TIRAMISU版本
     */
    fun isTIRAMISU(): Boolean {
        return Build.VERSION.SDK_INT >= TIRAMISU
    }

    /**
     * debug模式
     */
    fun isApkDebug(context: Context): Boolean {
        val info = context.applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}