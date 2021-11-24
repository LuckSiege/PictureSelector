package com.luck.picture.lib.tools;

import android.annotation.SuppressLint;
import android.content.Context;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;

import java.util.regex.Pattern;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: String Utils
 */
public class StringUtils {

    /**
     * 匹配数值
     *
     * @param str
     * @return
     */
    public static int stringToInt(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");
        return pattern.matcher(str).matches() ? ValueOf.toInt(str) : 0;
    }

    /**
     * 根据类型获取相应的Toast文案
     *
     * @param context
     * @param mimeType
     * @param maxSelectNum
     * @return
     */
    @SuppressLint("StringFormatMatches")
    public static String getMsg(Context context, String mimeType, int maxSelectNum) {
        if (PictureMimeType.isHasVideo(mimeType)) {
            return context.getString(R.string.picture_message_video_max_num, maxSelectNum);
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            return context.getString(R.string.picture_message_audio_max_num, maxSelectNum);
        } else {
            return context.getString(R.string.picture_message_max_num, maxSelectNum);
        }
    }

    /**
     * 重命名相册拍照
     *
     * @param fileName
     * @return
     */
    public static String rename(String fileName) {
        try {
            String temp = fileName.substring(0, fileName.lastIndexOf("."));
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            return temp + "_" + DateUtils.getCreateFileName() + suffix;
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 重命名后缀
     *
     * @param fileName 文件名
     * @return
     */
    public static String renameSuffix(String fileName, String suffix) {
        try {
            String temp = fileName.substring(0, fileName.lastIndexOf("."));
            return temp + suffix;
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
