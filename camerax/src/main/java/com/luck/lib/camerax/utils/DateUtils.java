package com.luck.lib.camerax.utils;

import java.text.SimpleDateFormat;

/**
 * @author：luck
 * @date：2021/11/29 8:33 下午
 * @describe：DateUtils
 */
public class DateUtils {

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

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
}
