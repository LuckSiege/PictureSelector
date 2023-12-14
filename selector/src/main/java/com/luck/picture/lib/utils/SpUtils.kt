package com.luck.picture.lib.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * @author：luck
 * @date：2022/3/15 6:26 下午
 * @describe：SpUtils
 */
object SpUtils {
    private var pictureSpUtils: SharedPreferences? = null
    private fun getSp(context: Context): SharedPreferences? {
        if (pictureSpUtils == null) {
            pictureSpUtils = context.getSharedPreferences("PictureSelector", Context.MODE_PRIVATE)
        }
        return pictureSpUtils
    }

    fun putBoolean(context: Context, key: String?, value: Boolean) {
        getSp(context)!!.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        return getSp(context)!!.getBoolean(key, defValue)
    }

    fun contains(context: Context, key: String): Boolean {
        return getSp(context)!!.contains(key)
    }
}