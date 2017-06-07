package com.luck.picture.lib.config;


import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.luck.picture.lib.tools.DebugUtil;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureMimeType {
    public static int ofAll() {
        return PictureConfig.TYPE_ALL;
    }

    public static int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }

    public static int ofVideo() {
        return PictureConfig.TYPE_VIDEO;
    }


    public static int isPictureType(String pictureType) {
        switch (pictureType) {
            case "image/png":
            case "image/PNG":
            case "image/jpeg":
            case "image/JPEG":
            case "image/webp":
            case "image/WEBP":
            case "image/gif":
            case "image/GIF":
                return PictureConfig.TYPE_IMAGE;
            case "video/3gp":
            case "video/3gpp":
            case "video/avi":
            case "video/mp4":
                return PictureConfig.TYPE_VIDEO;
        }
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
        }
        return false;
    }

    /**
     * 是否是视频
     *
     * @param pictureType
     * @return
     */
    public static boolean isVideo(String pictureType) {
        switch (pictureType) {
            case "video/3gp":
            case "video/3gpp":
            case "video/avi":
            case "video/mp4":
                return true;
        }
        return false;
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (null != path) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    public static String fileToType(File file) {
        if (file != null) {
            String name = file.getName();
            DebugUtil.i("**** fileToType:", name);
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")) {
                return "image/jpeg";
            }
        }
        return "image/jpeg";
    }

    /**
     * is type Equal
     *
     * @param p1
     * @param p2
     * @return
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

    public static String createImageType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public static String createVideoType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
    }

    /**
     * Picture or video
     *
     * @return
     */
    public static int pictureToVideo(String pictureType) {
        if (pictureType.startsWith("video")) {
            return PictureConfig.TYPE_VIDEO;
        }
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * get Local video duration
     *
     * @return
     */
    public static int getLocalVideoDuration(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        int duration = Integer.parseInt(mmr.extractMetadata
                (MediaMetadataRetriever.METADATA_KEY_DURATION));
        return duration;
    }

    /**
     * image is Damage
     *
     * @param path
     * @return
     */
    public static int isDamage(String path) {
        BitmapFactory.Options options = null;
        if (options == null) options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); //filePath代表图片路径
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            //表示图片已损毁
            return -1;
        }
        return 0;
    }
}
