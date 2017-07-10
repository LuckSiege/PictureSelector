package com.luck.picture.lib.config;


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

    public static int ofAudio() {
        return PictureConfig.TYPE_AUDIO;
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
            case "video/mov":
            case "video/x-msvideo":
                return PictureConfig.TYPE_VIDEO;
            case "audio/mpeg":
            case "audio/x-ms-wma":
            case "audio/x-wav":
            case "audio/amr":
            case "audio/wav":
            case "audio/aac":
            case "audio/mp4":
            case "audio/quicktime":
                return PictureConfig.TYPE_AUDIO;
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
            case "video/mov":
            case "video/x-msvideo":
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
        if (!TextUtils.isEmpty(path)) {
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
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac")) {
                return "audio/mpeg";
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
        if (!TextUtils.isEmpty(pictureType)) {
            if (pictureType.startsWith("video")) {
                return PictureConfig.TYPE_VIDEO;
            } else if (pictureType.startsWith("audio")) {
                return PictureConfig.TYPE_AUDIO;
            }
        }
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * get Local video duration
     *
     * @return
     */
    public static int getLocalVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }
}
