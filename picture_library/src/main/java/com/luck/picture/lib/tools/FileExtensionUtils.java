package com.luck.picture.lib.tools;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by xiaosong on 2017/10/28.
 */

public class FileExtensionUtils {

    /**
     * 获取文件后缀名
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int extenPosi = filePath.lastIndexOf(".");
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }
}
