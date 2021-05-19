package com.luck.picture.lib.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author：luck
 * @date：2017-5-25 23:30
 * @describe：DateUtils
 */

public class DateUtils {
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static long getCurrentTimeMillis() {
        String timeToString = ValueOf.toString(System.currentTimeMillis());
        return ValueOf.toLong(timeToString.length() > 10 ? timeToString.substring(0, 10) : timeToString);
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
     * @param duration
     * @return
     */
    public static String formatDurationTime(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }


    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

    /**
     * 根据时间戳创建文件名
     *
     * @return
     */
    public static String getCreateFileName() {
        long millis = System.currentTimeMillis();
        return sf.format(millis);
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
