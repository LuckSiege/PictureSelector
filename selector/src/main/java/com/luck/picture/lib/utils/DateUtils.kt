package com.luck.picture.lib.utils

import android.content.Context
import com.luck.picture.lib.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @author：luck
 * @date：2022/11/30 3:33 下午
 * @describe：DateUtils
 */
object DateUtils {
    val sf = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH)
    private val sdfYear = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val sfYm = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)

    /**
     * 时间戳转换成时间格式
     *
     * @param timeMs
     */
    fun formatDurationTime(timeMs: Long): String {
        return formatDurationTime(timeMs, true)
    }

    /**
     * 时间戳转换成时间格式
     *
     * @param timeMs
     */
    fun formatDurationTime(timeMs: Long, isAdjust: Boolean): String {
        if (isAdjust) {
            if (timeMs < 1000) {
                return String.format(
                    Locale.getDefault(), "%s%02d:%02d", "", 0, 1
                )
            }
        }
        val prefix = if (timeMs < 0) "-" else ""
        val absTimeMs = abs(timeMs)
        val totalSeconds = absTimeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format(
            Locale.getDefault(),
            "%s%d:%02d:%02d",
            prefix,
            hours,
            minutes,
            seconds
        ) else String.format(
            Locale.getDefault(), "%s%02d:%02d", prefix, minutes, seconds
        )
    }

    fun getYearDataFormat(time: Long): String {
        val newTime = if (time.toString().length > 10) time else time * 1000
        return sdfYear.format(newTime)
    }

    fun getDataFormat(context: Context, time: Long): String? {
        val newTime = if (time.toString().length > 10) time else time * 1000
        return when {
            isThisWeek(newTime) -> {
                context.getString(R.string.ps_current_week)
            }
            isThisMonth(newTime) -> {
                context.getString(R.string.ps_current_month)
            }
            else -> {
                sfYm.format(newTime)
            }
        }
    }

    private fun isThisWeek(time: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar[Calendar.WEEK_OF_YEAR]
        calendar.time = Date(time)
        val paramWeek = calendar[Calendar.WEEK_OF_YEAR]
        return paramWeek == currentWeek
    }

    private fun isThisMonth(time: Long): Boolean {
        val date = Date(time)
        val param: String = sfYm.format(date)
        val now: String = sfYm.format(Date())
        return param == now
    }

    fun dateDiffer(duration: Long): Boolean {
        return abs(getCurrentTimeMillis() - duration).toInt() <= 1
    }

    private fun getCurrentTimeMillis(): Long {
        val timeToString = System.currentTimeMillis().toString()
        return if (timeToString.length > 10) timeToString.substring(0, 10)
            .toLong() else timeToString.toLong()
    }
}