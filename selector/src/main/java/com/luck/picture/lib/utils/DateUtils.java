package com.luck.picture.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.luck.picture.lib.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author：luck
 * @date：2017-5-25 23:30
 * @describe：DateUtils
 */

public class DateUtils {
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM");

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SDF_YEAR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static long getCurrentTimeMillis() {
        String timeToString = ValueOf.toString(System.currentTimeMillis());
        return ValueOf.toLong(timeToString.length() > 10 ? timeToString.substring(0, 10) : timeToString);
    }


    public static String getDataFormat(Context context, long time) {
        time = String.valueOf(time).length() > 10 ? time : time * 1000;
        if (isThisWeek(time)) {
            return context.getString(R.string.ps_current_week);
        } else if (isThisMonth(time)) {
            return context.getString(R.string.ps_current_month);
        } else {
            return SDF.format(time);
        }
    }

    public static String getYearDataFormat(long time) {
        time = String.valueOf(time).length() > 10 ? time : time * 1000;
        return SDF_YEAR.format(time);
    }

    private static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    public static boolean isThisMonth(long time) {
        Date date = new Date(time);
        String param = SDF.format(date);
        String now = SDF.format(new Date());
        return param.equals(now);
    }


    /**
     * millisecondToSecond
     *
     * @param duration millisecond
     * @return
     */
    public static long millisecondToSecond(long duration) {
        return (duration / 1000) * 1000;
    }

    /**
     * 判断两个时间戳相差多少秒
     *
     * @param d
     * @return
     */
    public static int dateDiffer(long d) {
        try {
            long l1 = getCurrentTimeMillis();
            long interval = l1 - d;
            return (int) Math.abs(interval);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 时间戳转换成时间格式
     *
     * @param timeMs
     * @return
     */
    public static String formatDurationTime(long timeMs) {
        String prefix = timeMs < 0 ? "-" : "";
        timeMs = Math.abs(timeMs);
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        return hours > 0
                ? String.format(Locale.getDefault(), "%s%d:%02d:%02d", prefix, hours, minutes, seconds)
                : String.format(Locale.getDefault(), "%s%02d:%02d", prefix, minutes, seconds);
    }


    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + SF.format(millis);
    }

    /**
     * 根据时间戳创建文件名
     *
     * @return
     */
    public static String getCreateFileName() {
        long millis = System.currentTimeMillis();
        return SF.format(millis);
    }

    /**
     * 计算两个时间间隔
     *
     * @param sTime
     * @param eTime
     * @return
     */
    public static String cdTime(long sTime, long eTime) {
        long diff = eTime - sTime;
        return diff > 1000 ? diff / 1000 + "秒" : diff + "毫秒";
    }

}
